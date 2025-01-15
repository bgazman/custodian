package consulting.gazman.security.oauth.service;

import consulting.gazman.security.user.entity.User;
import consulting.gazman.security.oauth.dto.AuthorizeRequest;

public interface AuthCodeService {
    String generateCode(String email, String clientId);

    String validateCode(String code);

    User getUserFromCode(String code);


    void validateRequest(AuthorizeRequest request);
}
