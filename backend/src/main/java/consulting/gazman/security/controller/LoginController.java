package consulting.gazman.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@RequestParam(value = "client_id", required = false) String clientId, Model model) {
        model.addAttribute("client_id", clientId); // Add client_id to the model
        return "login";  // This will render your login.html template
    }
}
