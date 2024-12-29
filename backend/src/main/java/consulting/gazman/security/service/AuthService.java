package consulting.gazman.security.service;

import consulting.gazman.security.dto.*;

public interface AuthService {


    LoginResponse login(LoginRequest loginRequest);


    void registerUser(UserRegistrationRequest userRegistrationRequest);


    UserRegistrationResponse createUser(UserRegistrationRequest userRegistartionRequest);

    TokenResponse refresh(RefreshTokenRequest refreshRequest);
}




