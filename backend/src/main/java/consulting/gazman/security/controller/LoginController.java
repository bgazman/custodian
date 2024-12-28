package consulting.gazman.security.controller;

import consulting.gazman.security.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@RequestParam(value = "client_id", required = false) String clientId, Model model) {
        log.info("Login page accessed with client_id: {}", clientId);

        if (clientId == null || clientId.isEmpty()) {
            log.error("Missing client_id in login request");
//            throw  AppException.badRequest( "client_id is required");
        }

        model.addAttribute("clientId", clientId);
        return "login";
    }
}
