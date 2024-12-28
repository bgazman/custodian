package consulting.gazman.security.service;

import consulting.gazman.security.dto.*;

public interface AuthService {

    /**
     * Handles user login by validating credentials and generating tokens.
     *
     * @param loginRequest The login request containing email and password.
     * @return ApiResponse<AuthResponse> containing the authentication response or an error.
     */
    TokenResponse login(LoginRequest loginRequest);

    /**
     * Registers a new user with the given details.
     *
     * @param userRegistartionRequest The registration request containing email and password.
     * @return ApiResponse<AuthResponse> containing the newly registered user data or an error.
     */
    UserRegistrationResponse register(UserRegistartionRequest userRegistartionRequest);

    /**
     * Refreshes authentication tokens using a refresh token.
     *
     * @param refreshRequest The refresh request containing the refresh token.
     * @return ApiResponse<AuthResponse> containing the refreshed tokens or an error.
     */
    TokenResponse refresh(RefreshTokenRequest refreshRequest);
}




