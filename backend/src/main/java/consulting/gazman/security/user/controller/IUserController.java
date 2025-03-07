package consulting.gazman.security.user.controller;

import consulting.gazman.security.user.dto.*;
import consulting.gazman.security.user.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/secure/users")
public interface IUserController {
    @GetMapping
    ResponseEntity<List<UserBasicDTO>> getAllUsers();

    @GetMapping("/{id}")
    ResponseEntity<UserDetailsDTO> getUser(@PathVariable Long id);

    @PostMapping
    ResponseEntity<UserDetailsDTO> createUser(@RequestBody UserCreateRequest request);

    @PutMapping("/{id}")
    ResponseEntity<UserDetailsDTO> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteUser(@PathVariable Long id);

    @PutMapping("/{id}/status")
    ResponseEntity<UserStatusDTO> updateUserStatus(
            @PathVariable Long id,
            @RequestBody UserStatusUpdateRequest request
    );

    @PutMapping("/{id}/security")
    ResponseEntity<UserSecurityDTO> updateUserSecurity(
            @PathVariable Long id,
            @RequestBody UserSecurityUpdateRequest request
    );
    @PutMapping("/{id}/access")
    ResponseEntity<UserAccessDTO> updateUserAccess(
            @PathVariable Long id,
            @RequestBody UserAccessUpdateRequest request
    );

    @GetMapping("/{id}/access")
    ResponseEntity<UserAccessDTO> getUserAccess(@PathVariable Long id);

    @GetMapping("/{id}/profile")
    ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable Long id);

    @GetMapping("/email/{email}")
    ResponseEntity<UserBasicDTO> getUserByEmail(@PathVariable String email);

    @GetMapping("/{id}/basic")
    ResponseEntity<UserBasicDTO> getUserBasic(@PathVariable Long id);


    @GetMapping("/{id}/status")
    ResponseEntity<UserStatusDTO> getUserStatus(@PathVariable Long id);


}