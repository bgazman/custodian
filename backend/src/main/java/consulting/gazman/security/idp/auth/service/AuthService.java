package consulting.gazman.security.idp.auth.service;

import consulting.gazman.security.idp.auth.dto.LoginRequest;
import consulting.gazman.security.idp.auth.dto.LoginResponse;
import consulting.gazman.security.idp.auth.dto.UserRegistrationRequest;
import consulting.gazman.security.idp.auth.dto.UserRegistrationResponse;

public interface AuthService {



    void registerUser(UserRegistrationRequest userRegistrationRequest);

    LoginResponse login(LoginRequest loginRequest);  // Add this

    UserRegistrationResponse createUser(UserRegistrationRequest userRegistartionRequest);

}




