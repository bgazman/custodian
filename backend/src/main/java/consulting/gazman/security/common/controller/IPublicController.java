package consulting.gazman.security.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping("/api/public")
public interface IPublicController {
    @GetMapping("/data")
    ResponseEntity<Map<String, String>> getPublicData();
}
