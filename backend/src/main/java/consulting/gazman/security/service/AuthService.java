package consulting.gazman.security.service;

import consulting.gazman.security.dto.*;

public interface AuthService {



    void registerUser(UserRegistrationRequest userRegistrationRequest);


    UserRegistrationResponse createUser(UserRegistrationRequest userRegistartionRequest);

}




