package consulting.gazman.security.idp.auth.service;

import consulting.gazman.security.idp.auth.dto.UserRegistrationRequest;
import consulting.gazman.security.idp.auth.dto.UserRegistrationResponse;

public interface AuthService {



    void registerUser(UserRegistrationRequest userRegistrationRequest);


    UserRegistrationResponse createUser(UserRegistrationRequest userRegistartionRequest);

}




