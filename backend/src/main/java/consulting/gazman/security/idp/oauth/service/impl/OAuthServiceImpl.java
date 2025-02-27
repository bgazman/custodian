package consulting.gazman.security.idp.oauth.service.impl;

import consulting.gazman.security.user.entity.User;
import consulting.gazman.security.user.entity.UserRole;
import consulting.gazman.security.idp.auth.service.AuthService;
import consulting.gazman.security.idp.auth.service.impl.EmailVerificationServiceImpl;
import consulting.gazman.security.idp.oauth.dto.*;
import consulting.gazman.security.user.entity.GroupMembership;
import consulting.gazman.security.idp.oauth.entity.OAuthClient;
import consulting.gazman.security.idp.oauth.entity.Token;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.idp.oauth.service.*;
import consulting.gazman.security.user.service.*;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import consulting.gazman.security.idp.oauth.utils.TokenUtils;

@Service
@Transactional
public class OAuthServiceImpl implements OAuthService {
    @Autowired private AuthCodeService authCodeService;
    @Autowired private AuthService authService;
    @Autowired private OAuthClientService clientService;
    @Autowired private JwtService jwtService;
    @Autowired private GroupMembershipService groupMembershipService;
    @Autowired private GroupPermissionService groupPermissionService;
    @Autowired private UserRoleService userRoleService;
    @Autowired private RolePermissionService rolePermissionService;
    @Autowired UserService userService;
    @Autowired EmailVerificationServiceImpl emailVerificationServiceImpl;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired
    RoleService roleService;
    @Autowired
    GroupService groupService;
    @Autowired OAuthClientService oAuthClientService;
    @Autowired TokenService tokenService;


    @Override
    public AuthorizeResponse generateAuthCode(AuthorizeRequest request) {
        String code = authCodeService.generateCode(request.getEmail(), request.getClientId());
        return AuthorizeResponse.builder()
                .code(code)
                .state(request.getState())
                .build();
    }

    @Transactional
    @Override
    public TokenResponse exchangeToken(TokenRequest request) {
        String value = authCodeService.validateCode(request.getCode());
        String[] parts = value.split(":");
        String email = parts[0];
        String clientId = parts[1];

        OAuthClient oAuthClient = oAuthClientService.getClientByClientId(clientId)
                .orElseThrow(() -> AppException.invalidClientId("Invalid clientId: " + clientId));

        User user = userService.findByEmail(email);
        List<GroupMembership> groupMemberships = groupMembershipService.getGroupsForUser(user.getId());
        List<UserRole> userRoles = userRoleService.getRolesForUser(user.getId());

        Map<Long, List<String>> rolePermissions = userRoles.stream()
                .collect(Collectors.toMap(
                        ur -> ur.getRole().getId(),
                        ur -> rolePermissionService.findByRoleId(ur.getRole().getId())
                                .stream()
                                .map(rp -> rp.getPermission().getName())
                                .collect(Collectors.toList())
                ));

        Map<Long, List<String>> groupPermissions = groupMemberships.stream()
                .collect(Collectors.toMap(
                        gm -> gm.getGroup().getId(),
                        gm -> groupPermissionService.getGroupPermissions(gm.getGroup().getId())
                ));

        Map<Long, List<String>> permissions = new HashMap<>();
        permissions.putAll(rolePermissions);
        permissions.putAll(groupPermissions);

        String rawRefreshToken = TokenUtils.generateOpaqueToken();
        LocalDateTime refreshTokenExpiresAt = LocalDateTime.now().plusSeconds(oAuthClient.getRefreshTokenExpirySeconds());
        tokenService.createToken("refresh_token", rawRefreshToken, refreshTokenExpiresAt, user, oAuthClient);

        return TokenResponse.builder()
                .accessToken(jwtService.generateAccessToken(user, oAuthClient, groupMemberships, permissions, userRoles))
                .refreshToken(rawRefreshToken)
                .idToken(jwtService.generateIdToken(user, oAuthClient))
                .build();
    }

    @Transactional
    @Override
    public TokenResponse refreshToken(TokenRequest request) {
        Token existingToken = tokenService.findToken(request.getRefreshToken())
                .orElseThrow(() -> AppException.invalidRefreshToken("The refresh token is invalid or expired"));

        if (!tokenService.isTokenValid(request.getRefreshToken())) {
            throw AppException.invalidRefreshToken("The refresh token has expired");
        }

        OAuthClient oAuthClient = existingToken.getClient();
        User user = existingToken.getUser();

        List<GroupMembership> groupMemberships = groupMembershipService.getGroupsForUser(user.getId());
        List<UserRole> userRoles = userRoleService.getRolesForUser(user.getId());

        Map<Long, List<String>> rolePermissions = userRoles.stream()
                .collect(Collectors.toMap(
                        ur -> ur.getRole().getId(),
                        ur -> rolePermissionService.findByRoleId(ur.getRole().getId())
                                .stream()
                                .map(rp -> rp.getPermission().getName())
                                .collect(Collectors.toList())
                ));

        Map<Long, List<String>> groupPermissions = groupMemberships.stream()
                .collect(Collectors.toMap(
                        gm -> gm.getGroup().getId(),
                        gm -> groupPermissionService.getGroupPermissions(gm.getGroup().getId())
                ));

        Map<Long, List<String>> permissions = new HashMap<>();
        permissions.putAll(rolePermissions);
        permissions.putAll(groupPermissions);

        String rawNewRefreshToken = TokenUtils.generateOpaqueToken();
        LocalDateTime newRefreshTokenExpiresAt = LocalDateTime.now().plusSeconds(oAuthClient.getRefreshTokenExpirySeconds());
        tokenService.rotateRefreshToken(existingToken, rawNewRefreshToken, newRefreshTokenExpiresAt);

        return TokenResponse.builder()
                .accessToken(jwtService.generateAccessToken(user, oAuthClient, groupMemberships, permissions, userRoles))
                .refreshToken(rawNewRefreshToken)
                .idToken(jwtService.generateIdToken(user, oAuthClient))
                .build();
    }

    @Transactional
    @Override
    public IntrospectResponse introspectToken(String token) {
        Claims tokenClaims = jwtService.validateToken(token);

        String clientId = tokenClaims.get("client_id", String.class);
        String userId = tokenClaims.getSubject();
        String scope = tokenClaims.get("scope", String.class);
        String tokenType = tokenClaims.get("typ", String.class);
        LocalDateTime issuedAt = LocalDateTime.ofInstant(
                tokenClaims.getIssuedAt().toInstant(), ZoneId.systemDefault());
        LocalDateTime expiresAt = LocalDateTime.ofInstant(
                tokenClaims.getExpiration().toInstant(), ZoneId.systemDefault());
        LocalDateTime notBefore = LocalDateTime.ofInstant(
                tokenClaims.getNotBefore().toInstant(), ZoneId.systemDefault());
        String issuer = tokenClaims.getIssuer();
        String tokenId = tokenClaims.getId();

        OAuthClient oAuthClient = oAuthClientService.getClientByClientId(clientId)
                .orElseThrow(() -> AppException.invalidClientId("Invalid clientId: " + clientId));

        User user = userService.findByEmail(userId);

        List<GroupMembership> groupMemberships = groupMembershipService.getGroupsForUser(user.getId());
        Map<Long, List<String>> permissions = groupMemberships.stream()
                .collect(Collectors.toMap(
                        groupMembership -> groupMembership.getGroup().getId(),
                        groupMembership -> groupPermissionService.getGroupPermissions(groupMembership.getGroup().getId())
                ));

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


}