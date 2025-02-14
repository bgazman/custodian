package consulting.gazman.security.idp.auth.service.impl;


import consulting.gazman.security.client.user.service.UserRoleService;
import consulting.gazman.security.client.user.service.impl.*;
import consulting.gazman.security.idp.auth.dto.LoginRequest;
import consulting.gazman.security.idp.auth.dto.LoginResponse;
import consulting.gazman.security.idp.auth.dto.UserRegistrationRequest;
import consulting.gazman.security.idp.auth.dto.UserRegistrationResponse;

import consulting.gazman.security.idp.auth.service.AuthService;

import consulting.gazman.security.idp.auth.service.EmailVerificationService;
import consulting.gazman.security.idp.oauth.entity.OAuthClient;
import consulting.gazman.security.idp.oauth.service.AuthCodeService;
import consulting.gazman.security.idp.oauth.service.TokenService;
import consulting.gazman.security.idp.oauth.service.impl.JwtServiceImpl;
import consulting.gazman.security.idp.oauth.service.impl.OAuthClientServiceImpl;
import consulting.gazman.security.idp.oauth.utils.JwtUtils;
import consulting.gazman.security.idp.oauth.utils.TokenUtils;
import consulting.gazman.security.client.user.entity.GroupMembership;
import consulting.gazman.security.client.user.entity.Role;
import consulting.gazman.security.client.user.entity.User;
import consulting.gazman.security.client.user.entity.UserRole;
import jakarta.transaction.Transactional;
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

import consulting.gazman.security.common.exception.AppException;
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    EmailVerificationService emailVerificationService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    RoleServiceImpl roleService;
    @Autowired
    UserRoleService userRoleService;
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
    @Autowired
    AuthCodeService authCodeService;
    @Autowired
    TokenService tokenService;




    private void validateUserRegistrationRequest(UserRegistrationRequest request) {

        if (request.getEmail() == null || !request.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw AppException.badRequest("INVALID_EMAIL_FORMAT");
        }
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw AppException.badRequest("WEAK_PASSWORD");
        }
        if(userService.existsByEmail(request.getEmail())){
            throw AppException.userAlreadyExists("Email already registered: " + request.getEmail());
        }
    }
    @Transactional
    public void registerUser(UserRegistrationRequest request) {
        // Step 1: Validate input
        validateUserRegistrationRequest(request);

        createUser(request);

        // Step 3: Generate a temporary email verification token using EmailVerificationService
        String verificationToken = emailVerificationService.generateVerificationToken(request.getEmail());

        // Step 4: Send verification email using EmailVerificationService
        emailVerificationService.sendVerificationEmailAsync(request.getEmail(), verificationToken)
                .exceptionally(throwable -> {
                    // Handle any async failures here
                    log.error("Async email sending failed", throwable);
                    return null;
                });    }


@Transactional(dontRollbackOn  = AppException.class)
@Override
public LoginResponse login(LoginRequest loginRequest) {

            User user = userService.findByEmailOptional(loginRequest.getEmail())
                    .orElseThrow(() -> AppException.userNotFound("User not found"));

            if (isAccountLocked(user)) {
                Duration remainingLockTime = Duration.between(LocalDateTime.now(), user.getLockedUntil());
                throw AppException.accountLocked("Account locked. Try again in " + remainingLockTime.toMinutes() + " minutes");
            }

            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                handleFailedLoginAttempt(user);
                int remainingAttempts = MAX_ATTEMPTS - user.getFailedLoginAttempts();
                throw remainingAttempts > 0
                        ? AppException.invalidCredentials("Invalid credentials. " + remainingAttempts + " attempts remaining")
                        : AppException.accountLocked("Account locked due to attempts");
            }

            resetLoginAttempts(user);

            return LoginResponse.builder()
                    .mfaMethod(user.getMfaMethod())
                    .mfaEnabled(user.isMfaEnabled())
                    .build();
    }
//    public LoginResponse loginWrapped(LoginRequest loginRequest) {
//        Optional<User> optionalUser = userService.findByEmailOptional(loginRequest.getEmail());
//        if (optionalUser.isPresent()) {
//            User user = optionalUser.get();
//
//            if (isAccountLocked(user)) {
//                Duration remainingLockTime = Duration.between(LocalDateTime.now(), user.getLockedUntil());
//                String message = "Account is locked. Try again in " + remainingLockTime.toMinutes() + " minutes.";
//                throw AppException.accountLocked(message);
//            }
//
//            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
//                handleFailedLoginAttempt(user);
//                int remainingAttempts = MAX_ATTEMPTS - user.getFailedLoginAttempts();
//                throw remainingAttempts > 0
//                        ? AppException.invalidCredentials("Invalid credentials. " + remainingAttempts + " attempts remaining.")
//                        : AppException.accountLocked("Account locked due to attempts");
//            }
//
//            resetLoginAttempts(user);
//            String authorizationCode = authCodeService.generateCode(loginRequest.getEmail(), loginRequest.getClientId());
//
//            return LoginResponse.builder()
//                    .code(authorizationCode)
//                    .state(loginRequest.getState())
//                    .redirectUri(loginRequest.getRedirectUri())
//                    .mfaMethod(user.getMfaMethod())
//                    .mfaEnabled(user.isMfaEnabled())
//                    .build();
//        } else {
//            throw AppException.userNotFound("User not found for subject: " + loginRequest.getEmail());
//        }
//    }
    private boolean isAccountLocked(User user) {
        return user.getLockedUntil() != null && LocalDateTime.now().isBefore(user.getLockedUntil());
    }

    private void handleFailedLoginAttempt(User user) {
        int newFailedAttempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(newFailedAttempts);

        if (newFailedAttempts >= MAX_ATTEMPTS) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
        }

        userService.save(user);
    }

    private void resetLoginAttempts(User user) {
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user.setLastLoginTime(LocalDateTime.now());
        userService.save(user);
    }
    @Override
    public UserRegistrationResponse createUser(UserRegistrationRequest userRegistrationRequest) {
        log.info("Attempting registration for email: {}", userRegistrationRequest.getEmail());

        // Check if the email is already registered
        if (userService.existsByEmail(userRegistrationRequest.getEmail())) {
            throw AppException.userAlreadyExists("Email is already registered.");
        }

        // Fetch the default role (e.g., "VIEWER")
        Role userRole = roleService.findByName("VIEWER")
                .orElseThrow(() -> AppException.userNotFound("Role 'VIEWER' does not exist"));

        // Create and save the user
        User newUser = new User();
        newUser.setName(userRegistrationRequest.getName());
        newUser.setEmail(userRegistrationRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(userRegistrationRequest.getPassword()));

        // Assign the default role to the new user
        UserRole newUserRole = new UserRole();
        newUserRole.setUser(newUser);
        newUserRole.setRole(userRole);
        newUser.getUserRoles().add(newUserRole);
        newUser.setEnabled(true);

        // Save the user to the database
        User savedUser = userService.save(newUser);

        // Fetch OAuth client configuration by clientId
        String clientId = userRegistrationRequest.getClientId();
        OAuthClient oAuthClient = oAuthClientService.getClientByClientId(clientId)
                .orElseThrow(() -> AppException.resourceNotFound("Couldn't find OAuth config for clientId: " + clientId));

        // Retrieve user's group memberships
        List<GroupMembership> groupMemberships = groupMembershipService.getGroupsForUser(savedUser.getId());

        // Retrieve permissions for each group
        Map<Long, List<String>> permissions = groupMemberships.stream()
                .collect(Collectors.toMap(
                        groupMembership -> groupMembership.getGroup().getId(),
                        groupMembership -> groupPermissionService.getGroupPermissions(groupMembership.getGroup().getId())
                ));
        List<UserRole> roles = userRoleService.getRolesForUser(savedUser.getId());
        // Generate access token
                String accessToken = jwtService.generateAccessToken(savedUser, oAuthClient, groupMemberships, permissions,roles);

        // Generate refresh token
        String rawRefreshToken = TokenUtils.generateOpaqueToken();
        LocalDateTime refreshTokenExpiresAt = LocalDateTime.now().plusSeconds(oAuthClient.getRefreshTokenExpirySeconds());
        tokenService.createToken("refresh_token", rawRefreshToken, refreshTokenExpiresAt, savedUser, oAuthClient);

        // Build and return the response
        return UserRegistrationResponse.builder()
                .user(UserRegistrationResponse.UserDetails.builder()
                        .id(savedUser.getId())
                        .name(savedUser.getName())
                        .email(savedUser.getEmail())
                        .build())
                .tokens(UserRegistrationResponse.Tokens.builder()
                        .accessToken(accessToken)
                        .refreshToken(rawRefreshToken) // Return raw token to client
                        .build())
                .build();
    }



    // Refresh token implementation








    public User findByEmail(String email) {
        return userService.findByEmail(email);
    }

}
