package consulting.gazman.security.service.impl;


import consulting.gazman.security.dto.AuthRequest;
import consulting.gazman.security.dto.AuthResponse;
import consulting.gazman.security.entity.GroupMembership;
import consulting.gazman.security.entity.TokenConfiguration;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.exception.*;

import consulting.gazman.security.service.AuthService;

import consulting.gazman.security.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import consulting.gazman.security.exception.AppException;

@Slf4j
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    @Autowired
    UserServiceImpl userService;
    @Autowired
    GroupPermissionServiceImpl groupPermissionService;
    @Autowired
    GroupServiceImpl groupService;
    @Autowired
    GroupMembershipServiceImpl groupMembershipService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    TokenConfigurationServiceImpl tokenConfigService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private JwtServiceImpl jwtService;
    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 15;

    @Override
    public AuthResponse login(AuthRequest loginRequest) {
        log.info("Attempting login for email: {}", loginRequest.getEmail());

        // Find user by email
        User user = userService.findByEmail(loginRequest.getEmail());

        // Check if account is locked
        if (isAccountLocked(user)) {
            Duration remainingLockTime = Duration.between(LocalDateTime.now(), user.getLockedUntil());
            throw AppException.accountLocked("Account is locked. Try again in " + remainingLockTime.toMinutes() + " minutes.");
        }

        // Validate password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            handleFailedLoginAttempt(user);
            int remainingAttempts = MAX_ATTEMPTS - user.getFailedLoginAttempts();
            if (remainingAttempts > 0) {
                throw  AppException.invalidCredentials("Invalid credentials. " + remainingAttempts + " attempts remaining.");
            } else {
                throw  AppException.accountLocked("Account is locked due to too many failed attempts. Try again later.");
            }
        }

        // Reset failed attempts and update login time
        resetLoginAttempts(user);
        return createAuthResponse(user, loginRequest.getAppName());
    }



    @Override
    public AuthResponse register(AuthRequest registerRequest) {
        log.info("Attempting registration for email: {}", registerRequest.getEmail());

        // Check if the email is already registered
        if (userService.existsByEmail(registerRequest.getEmail())) {
            throw  AppException.userAlreadyExists("Email is already registered.");
        }

        // Create and save the user
        User newUser = new User();
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setRole("USER");
        newUser.setEnabled(true);

        User savedUser = userService.save(newUser);
        return createAuthResponse(savedUser,registerRequest.getAppName());
    }

    // Refresh token implementation
    @Override
    public AuthResponse refresh(AuthRequest refreshRequest) {
        log.info("Attempting token refresh");

        String refreshToken = refreshRequest.getRefreshToken();

        // Validate the refresh token
        if (!jwtService.validateToken(refreshToken).equals("success")) {
            throw  AppException.invalidToken("Invalid or expired refresh token.");
        }

        try {
            // Extract user email from token
            String userEmail = JwtUtils.extractSubject(refreshToken);
            String appName = JwtUtils.extractAppName(refreshToken);
            User user = userService.findByEmail(userEmail);
            // Create and return the new auth response
            return createAuthResponse(user,appName);
        } catch (ExpiredJwtException e) {
            throw AppException.tokenExpired("Refresh token expired.");
        } catch (JwtException e) {
            throw AppException.jwtProcessingFailed("Error processing JWT: " + e.getMessage());
        }
    }

    // Helper: Check if account is locked
    private boolean isAccountLocked(User user) {
        return user.getLockedUntil() != null && LocalDateTime.now().isBefore(user.getLockedUntil());
    }

    // Helper: Handle failed login attempts
    private void handleFailedLoginAttempt(User user) {
        int newFailedAttempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(newFailedAttempts);

        if (newFailedAttempts >= MAX_ATTEMPTS) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
            log.warn("User account locked due to too many failed attempts: {}", user.getEmail());
        }

        userService.save(user);
    }

    // Helper: Reset login attempts
    private void resetLoginAttempts(User user) {
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user.setLastLoginTime(LocalDateTime.now());
        userService.save(user);
    }

    private AuthResponse createAuthResponse(User user, String appName) {
        // 1. Retrieve token configuration for the app
        TokenConfiguration config = tokenConfigService.findByAppName(appName);

        // 2. Get user's groups
        List<GroupMembership> groupMemberships = groupMembershipService.getGroupsForUser(user.getId());

        // 3. Retrieve permissions for each group
        Map<Long, List<String>> permissions = groupMemberships.stream()
                .collect(Collectors.toMap(
                        groupMembership -> groupMembership.getGroup().getId(), // Map group ID
                        groupMembership -> groupPermissionService.getGroupPermissions(groupMembership.getGroup().getId()) // Retrieve permissions as names
                ));

        // 4. Generate tokens and return the response
        return new AuthResponse(
                jwtService.generateAccessToken(user, appName, groupMemberships, permissions), // Access token
                jwtService.generateRefreshToken(user, appName)// Refresh token
        );
    }




    public User findByEmail(String email) {
       return userService.findByEmail(email);
    }


}
