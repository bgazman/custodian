package consulting.gazman.security.service.impl;


import consulting.gazman.security.dto.*;
import consulting.gazman.security.entity.*;

import consulting.gazman.security.service.AuthService;

import consulting.gazman.security.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import consulting.gazman.security.exception.AppException;
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private  UserServiceImpl userService;
    @Autowired
    RoleServiceImpl roleService;
    @Autowired
    GroupPermissionServiceImpl groupPermissionService;
    @Autowired
    GroupServiceImpl groupService;
    @Autowired
    GroupMembershipServiceImpl groupMembershipService;
    @Autowired
    OAuthClientServiceImpl oAuthClientService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private JwtServiceImpl jwtService;
    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 15;


    public TokenResponse login(LoginRequest loginRequest) {

        Optional<User> optionalUser = userService.findByEmail(loginRequest.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (isAccountLocked(user)) {
                Duration remainingLockTime = Duration.between(LocalDateTime.now(), user.getLockedUntil());
                String message = "Account is locked. Try again in " + remainingLockTime.toMinutes() + " minutes.";
                throw AppException.accountLocked(message);
            }

            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                handleFailedLoginAttempt(user);
                int remainingAttempts = MAX_ATTEMPTS - user.getFailedLoginAttempts();
                throw remainingAttempts > 0
                        ? AppException.invalidCredentials("Invalid credentials. " + remainingAttempts + " attempts remaining.")
                        : AppException.accountLocked("Account locked due to attempts");
            }

            resetLoginAttempts(user);
            return createTokenResponse(user, loginRequest.getClientId());
        }
        else {
            // Return response indicating that the user was not found
            throw AppException.userNotFound("User not found for subject: " + loginRequest.getEmail());
        }
        }



    @Override
    public UserRegistrationResponse register(UserRegistartionRequest userRegistartionRequest) {
        log.info("Attempting registration for email: {}", userRegistartionRequest.getEmail());

        // Check if the email is already registered
        if (userService.existsByEmail(userRegistartionRequest.getEmail())) {
            throw  AppException.userAlreadyExists("Email is already registered.");
        }
        Role userRole = roleService.findByName("USER")
                .orElseThrow(() ->  AppException.userNotFound("Role 'USER' does not exist"));

        // Create and save the user
        User newUser = new User();
        newUser.setName(userRegistartionRequest.getName());
        newUser.setEmail(userRegistartionRequest.getEmail());
        log.info("Password before encoding: {}", userRegistartionRequest.getPassword());
        String encodedPassword = passwordEncoder.encode(userRegistartionRequest.getPassword());
        log.info("Password after encoding: {}", encodedPassword);
        newUser.setPassword(encodedPassword);
        UserRole newUserRole = new UserRole();
        newUserRole.setUser(newUser);
        newUserRole.setRole(userRole);
        newUser.getUserRoles().add(newUserRole);
        newUser.setEnabled(true);

        User savedUser = userService.save(newUser);
        String clientId = userRegistartionRequest.getClientId();
        OAuthClient oAuthClient = oAuthClientService.getClientByClientId(clientId)
                .orElseThrow(() -> AppException.resourceNotFound("Couldn't find oauth config for:  " + clientId));

        // 2. Get user's groups
        List<GroupMembership> groupMemberships = groupMembershipService.getGroupsForUser(newUser.getId());

        // 3. Retrieve permissions for each group
        Map<Long, List<String>> permissions = groupMemberships.stream()
                .collect(Collectors.toMap(
                        groupMembership -> groupMembership.getGroup().getId(), // Map group ID
                        groupMembership -> groupPermissionService.getGroupPermissions(groupMembership.getGroup().getId()) // Retrieve permissions as names
                ));
        String accessToken = jwtService.generateAccessToken(newUser, oAuthClient.getClientId(), groupMemberships, permissions);
        String refreshToken = jwtService.generateRefreshToken(newUser, oAuthClient.getClientId());
        return UserRegistrationResponse.builder()
                .user(UserRegistrationResponse.UserDetails.builder()
                        .id(newUser.getId())
                        .name(newUser.getName())
                        .email(newUser.getEmail())
                        .build())
                .tokens(UserRegistrationResponse.Tokens.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build())
                .build();    }

    // Refresh token implementation
    @Override
    public TokenResponse refresh(RefreshTokenRequest refreshRequest) {
        log.info("Attempting token refresh");

        String refreshToken = refreshRequest.getRefreshToken();

        // Validate the refresh token
        if (!jwtService.validateToken(refreshToken).equals("success")) {
            throw  AppException.invalidToken("Invalid or expired refresh token.");
        }

        try {
            // Extract user email from token
            String userEmail = JwtUtils.extractSubject(refreshToken);
            String clientId = JwtUtils.extractClientId(refreshToken);
            Optional<User> optionalUser = userService.findByEmail("");
            if(optionalUser.isPresent()) {
                User user = optionalUser.get();            // Create and return the new auth response
                return createTokenResponse(user, clientId);
            }
             else {
                // Return response indicating that the user was not found
                throw AppException.userNotFound("Email doesn't exist");
            }

        } catch(ExpiredJwtException e){
            throw AppException.tokenExpired("Refresh token expired.");
        } catch(JwtException e){
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

    private TokenResponse createTokenResponse(User user, String clientId) {
        // 1. Retrieve token configuration for the app
        OAuthClient oAuthClient = oAuthClientService.getClientByClientId(clientId)
                .orElseThrow(() -> AppException.resourceNotFound("Couldn't find oauth config for:  " + clientId));

        // 2. Get user's groups
        List<GroupMembership> groupMemberships = groupMembershipService.getGroupsForUser(user.getId());

        // 3. Retrieve permissions for each group
        Map<Long, List<String>> permissions = groupMemberships.stream()
                .collect(Collectors.toMap(
                        groupMembership -> groupMembership.getGroup().getId(), // Map group ID
                        groupMembership -> groupPermissionService.getGroupPermissions(groupMembership.getGroup().getId()) // Retrieve permissions as names
                ));
        String accessToken = jwtService.generateAccessToken(user, oAuthClient.getClientId(), groupMemberships, permissions);
        String refreshToken = jwtService.generateRefreshToken(user, oAuthClient.getClientId());
        String idToken = jwtService.generateIdToken(user, oAuthClient.getClientId());
        // 4. Generate tokens and return the response
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .idToken(idToken)
                .tokenType("Bearer")  // Set token type
                .expiresIn(3600L)     // Set expiration time (e.g., 1 hour)
                .build();
    }




    public User findByEmail(String email) {
        return userService.findByEmail(email)
                .orElseThrow(() ->  AppException.userNotFound("User not found for subject: " + email));
    }

}
