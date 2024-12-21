package consulting.gazman.security.controller;

import consulting.gazman.security.dto.AuthRequest;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    @Autowired
    private UserService userService;

//    @GetMapping
//    public List<User> getAllUsers() {
//        return userService.findAll();
//    }
    @GetMapping()
    public ResponseEntity<?> getAllUsers(@RequestBody String str) {
        return ResponseEntity.ok(str);
    }
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping
    public User createUser( @RequestBody User user) {
        return userService.save(user);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id,  @RequestBody User user) {
        return userService.update(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.delete(id);
    }
}