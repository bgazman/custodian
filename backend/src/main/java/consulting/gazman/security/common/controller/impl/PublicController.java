package consulting.gazman.security.common.controller.impl;

import consulting.gazman.security.common.controller.IPublicController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


import java.util.Map;

@RestController
public class PublicController implements IPublicController {


    @Override
    public ResponseEntity<Map<String, String>> getPublicData() {
        return null;
    }
}