package consulting.gazman.security.service.impl;


import consulting.gazman.security.dto.*;
import consulting.gazman.security.entity.GroupMembership;
import consulting.gazman.security.entity.OAuthClient;
import consulting.gazman.security.entity.Token;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.exception.AppException;
import consulting.gazman.security.service.*;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import consulting.gazman.security.utils.TokenUtils;
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
    TokenService tokenService;

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 15;

    @Transactional
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

        Optional<User> optionalUser = userService.findByEmailOptional(loginRequest.getEmail());
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
            String authorizationCode =authCodeService.generateCode(loginRequest.getEmail(), loginRequest.getClientId());

            return LoginResponse.builder()
                    .code(authorizationCode)
                    .state(loginRequest.getState())
                    .redirectUri(loginRequest.getRedirectUri())
                    .mfaMethod(user.getMfaMethod())
                    .build();        }
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

    @Transactional
    @Override
    public TokenResponse exchangeToken(TokenRequest request) {
        // Validate the authorization code
        String value = authCodeService.validateCode(request.getCode());

        // Extract email and clientId from the value
        String[] parts = value.split(":");
        String email = parts[0];
        String clientId = parts[1];

        // Fetch OAuthClient using clientId
        OAuthClient oAuthClient = oAuthClientService.getClientByClientId(clientId)
                .orElseThrow(() -> AppException.invalidClientId("Invalid clientId: " + clientId));

        // Fetch the user by email
        User user = userService.findByEmail(email);
        // Retrieve groups and permissions
        List<GroupMembership> groupMemberships = groupMembershipService.getGroupsForUser(user.getId());
        Map<Long, List<String>> permissions = groupMemberships.stream()
                .collect(Collectors.toMap(
                        groupMembership -> groupMembership.getGroup().getId(),
                        groupMembership -> groupPermissionService.getGroupPermissions(groupMembership.getGroup().getId())
                ));

        // Generate a new refresh token
        String rawRefreshToken = TokenUtils.generateOpaqueToken();
        LocalDateTime refreshTokenExpiresAt = LocalDateTime.now().plusSeconds(oAuthClient.getRefreshTokenExpirySeconds());
        tokenService.createToken("refresh_token", rawRefreshToken, refreshTokenExpiresAt, user, oAuthClient);

        // Build the token response
        return TokenResponse.builder()
                .accessToken(jwtService.generateAccessToken(user, oAuthClient, groupMemberships, permissions))
                .refreshToken(rawRefreshToken) // Return raw token to client
                .idToken(jwtService.generateIdToken(user, oAuthClient))
                .build();
    }


    @Transactional
    @Override
    public TokenResponse refreshToken(TokenRequest request) {
        // Validate the refresh token and find the associated token record
        Token existingToken = tokenService.findToken(request.getRefreshToken())
                .orElseThrow(() -> AppException.invalidRefreshToken("The refresh token is invalid or expired"));

        if (!tokenService.isTokenValid(request.getRefreshToken())) {
            throw AppException.invalidRefreshToken("The refresh token has expired");
        }

        // Fetch the associated OAuthClient
        OAuthClient oAuthClient = existingToken.getClient();

        // Fetch the associated user
        User user = existingToken.getUser();

        // Retrieve groups and permissions
        List<GroupMembership> groupMemberships = groupMembershipService.getGroupsForUser(user.getId());
        Map<Long, List<String>> permissions = groupMemberships.stream()
                .collect(Collectors.toMap(
                        groupMembership -> groupMembership.getGroup().getId(),
                        groupMembership -> groupPermissionService.getGroupPermissions(groupMembership.getGroup().getId())
                ));

        // Rotate the refresh token
        String rawNewRefreshToken = TokenUtils.generateOpaqueToken();
        LocalDateTime newRefreshTokenExpiresAt = LocalDateTime.now().plusSeconds(oAuthClient.getRefreshTokenExpirySeconds());
        tokenService.rotateRefreshToken(existingToken, rawNewRefreshToken, newRefreshTokenExpiresAt);

        // Build the token response
        return TokenResponse.builder()
                .accessToken(jwtService.generateAccessToken(user, oAuthClient, groupMemberships, permissions))
                .refreshToken(rawNewRefreshToken) // Provide new refresh token
                .idToken(jwtService.generateIdToken(user, oAuthClient))
                .build();
    }
    @Transactional
    @Override
    public IntrospectResponse introspectToken(String token) {
        // Step 1: Validate the token
        Claims tokenClaims = jwtService.validateToken(token);



        String clientId = tokenClaims.get("client_id", String.class);
        String userId = tokenClaims.getSubject(); // Standard "sub" claim
        String scope = tokenClaims.get("scope", String.class);
        String tokenType = tokenClaims.get("typ", String.class);
        LocalDateTime issuedAt = LocalDateTime.ofInstant(
                tokenClaims.getIssuedAt().toInstant(), ZoneId.systemDefault());
        LocalDateTime expiresAt = LocalDateTime.ofInstant(
                tokenClaims.getExpiration().toInstant(), ZoneId.systemDefault());
        LocalDateTime notBefore = LocalDateTime.ofInstant(
                tokenClaims.getNotBefore().toInstant(), ZoneId.systemDefault());
        String issuer = tokenClaims.getIssuer(); // Standard "iss" claim
        String tokenId = tokenClaims.getId(); // Standard "jti" claim

        // Step 3: Fetch OAuthClient and User information
        OAuthClient oAuthClient = oAuthClientService.getClientByClientId(clientId)
                .orElseThrow(() -> AppException.invalidClientId("Invalid clientId: " + clientId));

        User user = userService.findByEmail(userId);

        // Step 4: Retrieve groups and permissions
        List<GroupMembership> groupMemberships = groupMembershipService.getGroupsForUser(user.getId());
        Map<Long, List<String>> permissions = groupMemberships.stream()
                .collect(Collectors.toMap(
                        groupMembership -> groupMembership.getGroup().getId(),
                        groupMembership -> groupPermissionService.getGroupPermissions(groupMembership.getGroup().getId())
                ));

        // Step 5: Build and return the IntrospectResponse
        return IntrospectResponse.builder()
                .active(true)
                .scope(scope)
                .tokenType(tokenType)
                .clientId(clientId)
                .username(user.getEmail())
                .exp(expiresAt.toEpochSecond(ZoneOffset.UTC))
                .iat(issuedAt.toEpochSecond(ZoneOffset.UTC))
                .nbf(notBefore.toEpochSecond(ZoneOffset.UTC))
                .sub(userId)
                .iss(issuer)
                .jti(tokenId)
                .build();
    }

    @Override
    public UserInfoResponse getUserInfo(String bearerToken) {
        return null;
    }


    @Transactional
    @Override
    public void revokeToken(String token) {
        Token existingToken = tokenService.findToken(token)
                .orElseThrow(() -> AppException.invalidRefreshToken("The refresh token is invalid or expired"));

        tokenService.revokeToken(existingToken.getId());



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