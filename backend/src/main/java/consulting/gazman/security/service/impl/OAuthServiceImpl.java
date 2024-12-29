package consulting.gazman.security.service.impl;


import consulting.gazman.common.dto.ApiError;
import consulting.gazman.security.dto.*;
import consulting.gazman.security.entity.GroupMembership;
import consulting.gazman.security.entity.OAuthClient;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.exception.AppException;
import consulting.gazman.security.service.*;
import consulting.gazman.security.utils.JwtUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class OAuthServiceImpl implements OAuthService {
    @Autowired private AuthCodeService authCodeService;
    @Autowired private AuthService authService;
    @Autowired private OAuthClientService clientService;
    @Autowired private JwtService jwtService;
    @Autowired private GroupMembershipServiceImpl groupMembershipService;
    @Autowired private GroupPermissionServiceImpl groupPermissionService;
    @Autowired
    UserService userService;
    @Autowired
    EmailVerificationService emailVerificationService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    RoleServiceImpl roleService;

    @Autowired
    GroupServiceImpl groupService;

    @Autowired
    OAuthClientServiceImpl oAuthClientService;
    @Autowired
    private JwtUtils jwtUtils;

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 15;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        LoginResponse response = null;
        try {
            response = loginWrapped(loginRequest);

        } catch (Exception e) {
            return LoginResponse.builder().error(e.getMessage()).build();

        }
        return response;
    }
    public LoginResponse loginWrapped(LoginRequest loginRequest) {

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
//            String authorizationCode =authCodeService.generateCode(loginRequest.getEmail(), loginRequest.getClientId());

            return LoginResponse.builder().build();        }
        else {
            // Return response indicating that the user was not found
            throw AppException.userNotFound("User not found for subject: " + loginRequest.getEmail());
        }

    }

    @Override
    public AuthorizeResponse generateAuthCode(AuthorizeRequest request) {

        String code = authCodeService.generateCode(request.getEmail(),request.getClientId());
        // Store code with user/client mapping
        return AuthorizeResponse.builder()
                .code(code)
                .state(request.getState())
                .build();
    }


    @Override
    public TokenResponse exchangeToken(TokenRequest request) {

        String value = authCodeService.validateCode(request.getCode());

        // Split the value to get email and clientId
        String[] parts = value.split(":");
        String email = parts[0];
        String clientId = parts[1];
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new AppException("USER_NOT_FOUND", "No user found with email: " + email));

        List<GroupMembership> groupMemberships = groupMembershipService.getGroupsForUser(user.getId());

        // 3. Retrieve permissions for each group
        Map<Long, List<String>> permissions = groupMemberships.stream()
                .collect(Collectors.toMap(
                        groupMembership -> groupMembership.getGroup().getId(), // Map group ID
                        groupMembership -> groupPermissionService.getGroupPermissions(groupMembership.getGroup().getId()) // Retrieve permissions as names
                ));
        return TokenResponse.builder()
                .accessToken(jwtService.generateAccessToken(user, request.getClientId(), groupMemberships, permissions))
                .refreshToken(jwtService.generateRefreshToken(user, request.getClientId()))
                .idToken(jwtService.generateIdToken(user, request.getClientId()))
                .build();
    }

    @Override
    public TokenResponse refreshToken(TokenRequest request) {
        User user = jwtService.validateRefreshToken(request.getRefreshToken());
        List<GroupMembership> groupMemberships = groupMembershipService.getGroupsForUser(user.getId());

        // 3. Retrieve permissions for each group
        Map<Long, List<String>> permissions = groupMemberships.stream()
                .collect(Collectors.toMap(
                        groupMembership -> groupMembership.getGroup().getId(), // Map group ID
                        groupMembership -> groupPermissionService.getGroupPermissions(groupMembership.getGroup().getId()) // Retrieve permissions as names
                ));
        return TokenResponse.builder()
                .accessToken(jwtService.generateAccessToken(user, request.getClientId(), groupMemberships, permissions))
                .refreshToken(jwtService.generateRefreshToken(user, request.getClientId()))
                .idToken(jwtService.generateIdToken(user, request.getClientId()))
                .build();
    }

    @Override
    public UserInfoResponse getUserInfo(String bearerToken) {
        User user = jwtService.validateAccessToken(bearerToken);
        return UserInfoResponse.builder()
                .sub(user.getEmail())
                .email(user.getEmail())
                .emailVerified(user.isEmailVerified())
                .build();
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
//            log.warn("User account locked due to too many failed attempts: {}", user.getEmail());
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
}