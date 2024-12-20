package consulting.gazman.security.service;

import consulting.gazman.security.ApiResponse;
import consulting.gazman.security.dto.AuthRequest;
import consulting.gazman.security.dto.AuthResponse;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.exception.ResourceNotFoundException;
import consulting.gazman.security.repository.UserRepository;
import consulting.gazman.security.utils.JwtUtils;
import consulting.gazman.security.utils.ResponseMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;

    @Value("${security.login.max-attempts}")
    private int maxAttempts;

    @Value("${security.login.lock-duration-minutes}")
    private int lockDurationMinutes;

    public ApiResponse<Object> login(@RequestBody AuthRequest loginRequest) {
        // Find user by email
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);

        // Check if user exists
        if (user == null) {
            return ResponseMapper.badRequest("Invalid credentials.");
        }

        // Check if account is locked
        if (user.getLockedUntil() != null && LocalDateTime.now().isBefore(user.getLockedUntil())) {
            Duration remainingLockTime = Duration.between(LocalDateTime.now(), user.getLockedUntil());
            return ResponseMapper.badRequest("Account is locked. Try again in " + remainingLockTime.toMinutes() + " minutes.");
        }

        // Check password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            int newFailedAttempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(newFailedAttempts);

            if (newFailedAttempts >= maxAttempts) {
                user.setLockedUntil(LocalDateTime.now().plusMinutes(lockDurationMinutes));
                user.setFailedLoginAttempts(0); // Reset failed attempts after lock
                userRepository.save(user);
                return ResponseMapper.badRequest("Account locked due to too many failed attempts. Try again in "
                        + lockDurationMinutes + " minutes.");
            }

            userRepository.save(user);
            return ResponseMapper.badRequest("Invalid credentials. " + (maxAttempts - newFailedAttempts) + " attempts remaining.");
        }

        // Successful login: Reset failed attempts and update login time
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);

        return ResponseMapper.success(createAuthResponse(user), "Login successful.");
    }
;


    @Transactional
    public ApiResponse<AuthResponse> register(@RequestBody AuthRequest registerRequest) {
        try {
            // Check if email already exists
            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                return ResponseMapper.badRequest("Email already exists.");
            }


            // Create new user
            User newUser = new User();
            newUser.setEmail(registerRequest.getEmail());
            newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            newUser.setRole("USER"); // Assign the default role as a string
            newUser.setEnabled(true); // Default enabled for new users

            // Save user
            User savedUser = userRepository.save(newUser);

            // Generate auth response
            AuthResponse authResponse = createAuthResponse(savedUser);
            return ResponseMapper.success(authResponse, "User registered successfully.");

        } catch (DataIntegrityViolationException ex) {
            // Handle database constraint violations (e.g., unique constraint)
            return ResponseMapper.badRequest("Database error: " + ex.getMostSpecificCause().getMessage());
        } catch (Exception ex) {
            // Handle other unexpected exceptions
            return ResponseMapper.internalServerError("An unexpected error occurred: " , ex.getMessage());
        }
    }


    public ApiResponse<Object> refresh(@RequestBody AuthRequest refreshRequest) {
        String refreshToken = refreshRequest.getRefreshToken();

        if (!jwtUtils.validateRefreshToken(refreshToken,"GLOBAL")) {
            return ResponseMapper.badRequest("Invalid refresh token");
        }

        // Extract user email from refresh token
        String userEmail = jwtUtils.extractSubject(refreshToken,"GLOBAL");

        // Find user
        User user = userRepository.findByEmail(userEmail).orElse(null);

        if (user == null) {
            return ResponseMapper.badRequest("Bad refresh token");
        }

        try {
            return ResponseMapper.success(createAuthResponse(user), "Token refreshed successfully.");


        } catch (ExpiredJwtException e) {
            return ResponseMapper.badRequest("Token expired");


        } catch (JwtException e) {
            return ResponseMapper.badRequest("Jwt exception");

        }
    }

    public User findByEmail(String email) {
        try {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }
    private AuthResponse createAuthResponse(User user) {
        return new AuthResponse(
                jwtUtils.generateAccessToken(user,"GLOBAL"),
                jwtUtils.generateRefreshToken(user,"GLOBAL"),
                user.getEmail(),
                user.getRole()
        );
    }


}


