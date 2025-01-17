package consulting.gazman.security.client.user.controller;

import consulting.gazman.security.client.user.entity.Permission;
import consulting.gazman.security.idp.oauth.entity.Secret;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/secure/secrets")
public interface ISecretController {
    @GetMapping
    ResponseEntity<?> getAllSecrets();

    @GetMapping("/{id}")
    ResponseEntity<?> getSecretById(@PathVariable Long id);

    @PostMapping
    ResponseEntity<?> createSecret(@RequestBody Secret secret);

    @PutMapping("/{id}")
    ResponseEntity<?> updateSecret(@PathVariable Long id, @RequestBody Secret secret);

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteSecret(@PathVariable Long id);

}