package consulting.gazman.security.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class ReactController {
    @GetMapping(value = {"/oauth-callback", "/dashboard", "/users/**", "/other-react-route/**"})
    public String handleReactRoutes() {
        return "forward:/index.html";
    }
}
