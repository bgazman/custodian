package consulting.gazman.security.auth.controller;

import consulting.gazman.security.common.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "client_id", required = false) String clientId,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "redirect_uri", required = false) String redirectUri,
            @RequestParam(value = "response_type", required = false) String responseType,
            @RequestParam(value = "scope", required = false) String scope,
            Model model
    ) {
        log.info("Login page accessed with client_id: {}, state: {}, redirect_uri: {}", clientId, state, redirectUri);

        // Validate required parameters
        if (clientId == null || clientId.isEmpty()) {
            log.error("Missing client_id in login request");
            throw AppException.badRequest("client_id is required");
        }
        if (state == null || state.isEmpty()) {
            log.error("Missing state in login request");
            throw AppException.badRequest("state is required");
        }
        if (redirectUri == null || redirectUri.isEmpty()) {
            log.error("Missing redirect_uri in login request");
            throw AppException.badRequest("redirect_uri is required");
        }

        model.addAttribute("clientId", clientId);
        model.addAttribute("state", state);
        model.addAttribute("redirectUri", redirectUri);
        model.addAttribute("scope", scope);
        model.addAttribute("responseType", responseType);

        return "login"; // This renders the login.html template
    }

}
