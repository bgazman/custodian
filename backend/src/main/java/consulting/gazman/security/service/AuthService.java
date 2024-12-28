package consulting.gazman.security.service;

import consulting.gazman.security.dto.*;

public interface AuthService {


    TokenResponse login(LoginRequest loginRequest);


    void registerUser(UserRegistrationRequest userRegistrationRequest);


    UserRegistrationResponse createUser(UserRegistrationRequest userRegistartionRequest);

    TokenResponse refresh(RefreshTokenRequest refreshRequest);
}




