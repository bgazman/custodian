package consulting.gazman.security.auth.service;

import consulting.gazman.security.auth.dto.UserRegistrationRequest;
import consulting.gazman.security.auth.dto.UserRegistrationResponse;

public interface AuthService {



    void registerUser(UserRegistrationRequest userRegistrationRequest);


    UserRegistrationResponse createUser(UserRegistrationRequest userRegistartionRequest);

}




