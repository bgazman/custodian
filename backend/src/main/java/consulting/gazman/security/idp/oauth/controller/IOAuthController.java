package consulting.gazman.security.idp.oauth.controller;

import consulting.gazman.security.idp.oauth.dto.AuthorizeRequest;
import consulting.gazman.security.idp.oauth.dto.TokenRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/oauth")
public interface IOAuthController {

    @GetMapping("/authorize")
    ResponseEntity<?> authorize(
            @RequestParam String response_type,
            @RequestParam String client_id,
            @RequestParam String redirect_uri,
            @RequestParam String scope,
            @RequestParam String state,
            @RequestParam(required = false) String code_challenge,
            @RequestParam(required = false) String code_challenge_method,
            @CookieValue(name = "OAUTH_SESSION", required = false) String sessionToken
    );

    @PostMapping("/token")
    ResponseEntity<?> token(@RequestBody TokenRequest request,
                            @CookieValue(name = "OAUTH_SESSION", required = false) String sessionToken
    );


    @GetMapping("/introspect")
    ResponseEntity<?> introspect(@RequestBody String bearerToken);

    @PostMapping("/revoke")
    ResponseEntity<?> revokeToken(@RequestBody String refreshToken);

    @GetMapping("/userinfo")
    ResponseEntity<?> userinfo(@RequestHeader("Authorization") String bearerToken);

}
