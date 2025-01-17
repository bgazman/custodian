package consulting.gazman.security.idp.oauth.controller;

import consulting.gazman.security.idp.oauth.dto.ClientRegistrationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/client")
public interface IClientRegistrationController {
    @PostMapping("/register")
    ResponseEntity<?> registerClient(@RequestBody ClientRegistrationRequest request);
}
