package consulting.gazman.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReactController {
    @GetMapping(value = {"/oauth-callback", "/dashboard", "/profile", "/other-react-route/**"})
    public String handleReactRoutes() {
        return "forward:/index.html";
    }
}
