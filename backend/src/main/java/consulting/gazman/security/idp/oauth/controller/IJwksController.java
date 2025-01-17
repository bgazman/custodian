package consulting.gazman.security.idp.oauth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/.well-known")
public interface IJwksController {
    @GetMapping("/jwks.json")
    ResponseEntity<?> getJwks();

    @GetMapping("/openid-configuration")
    ResponseEntity<?> getOpenIdConfiguration();
}
