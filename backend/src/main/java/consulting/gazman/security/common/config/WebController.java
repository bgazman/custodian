package consulting.gazman.security.common.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/login")
    public String login() {
        return "forward:/index.html";
    }

    @GetMapping("/mfa")
    public String forgotPassword() {
        return "forward:/index.html";
    }
}