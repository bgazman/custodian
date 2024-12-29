package consulting.gazman.security.service.impl;


import consulting.gazman.security.dto.*;
import consulting.gazman.security.entity.GroupMembership;
import consulting.gazman.security.entity.OAuthClient;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.service.AuthService;
import consulting.gazman.security.service.JwtService;
import consulting.gazman.security.service.OAuthClientService;
import consulting.gazman.security.service.OAuthService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        return null;
    }

    @Override
    public AuthorizeResponse generateAuthCode(AuthorizeRequest request) {

        String code = authCodeService.generateSecureCode();
        // Store code with user/client mapping
        return AuthorizeResponse.builder()
                .code(code)
                .state(request.getState())
                .build();
    }

//    @Transactional
//    @Override
//    public LoginResponse login(LoginRequest request) {
//        TokenResponse authResult = authService.login(
//                LoginRequest.builder()
//                        .email(request.getEmail())
//                        .password(request.getPassword())
//                        .build()
//        );
//
//
//
//        String code = authCodeService.generateCode(
//                request.getEmail(),
//                request.getClientId()
//
//        );
//
//        return LoginResponse.builder()
//                .code(code)
//                .redirectUri(request.getRedirectUri())
//                .state(request.getState())
//                .build();
//    }

    @Override
    public TokenResponse exchangeToken(TokenRequest request) {
        authCodeService.validateCode(request.getCode());
        User user = authCodeService.getUserFromCode(request.getCode());
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
}