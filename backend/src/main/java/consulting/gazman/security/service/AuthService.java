package consulting.gazman.security.service;

import common.dto.ApiError;
import common.dto.ApiResponse;
import consulting.gazman.security.dto.AuthRequest;
import consulting.gazman.security.dto.AuthResponse;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.exception.ResourceNotFoundException;
import consulting.gazman.security.repository.UserRepository;
import consulting.gazman.security.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    public ApiResponse<AuthResponse> login(@RequestBody AuthRequest loginRequest) {
        // Find user by email
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);

        // Check if user exists
        if (user == null) {
            return ApiResponse.error(
                            HttpStatus.BAD_REQUEST,
                            "Invalid credentials",
                            ApiError.of("INVALID_CREDENTIALS",
                                    "Invalid credentials")
                    );         }

        // Check if account is locked
        if (user.getLockedUntil() != null && LocalDateTime.now().isBefore(user.getLockedUntil())) {
            Duration remainingLockTime = Duration.between(LocalDateTime.now(), user.getLockedUntil());
            return ApiResponse.error(
                            HttpStatus.BAD_REQUEST,
                            "Account is locked. Try again in " + remainingLockTime.toMinutes() + " minutes.",
                            ApiError.of("ACCOUNT_LOCKED",
                                   "Account locked")
                    );        }

        // Check password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            int newFailedAttempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(newFailedAttempts);

            if (newFailedAttempts >= maxAttempts) {
                user.setLockedUntil(LocalDateTime.now().plusMinutes(lockDurationMinutes));
                user.setFailedLoginAttempts(0); // Reset failed attempts after lock
                userRepository.save(user);
                return ApiResponse.error(
                                HttpStatus.BAD_REQUEST,
                                "Account locked due to too many failed attempts. Try again in "
                                        + lockDurationMinutes + " minutes.",
                                ApiError.of("ACCOUNT_LOCKED",
                                        "Account locked"
                                ));
            }

            userRepository.save(user);
            return ApiResponse.error(
                            HttpStatus.BAD_REQUEST,
                            "Invalid credentials. " + (maxAttempts - newFailedAttempts) + " attempts remaining.",
                            ApiError.of("INVALID_CREDENTIALS",
                                   "Invalid Credentials")
                    );        }

        // Successful login: Reset failed attempts and update login time
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
        AuthResponse authResponse = createAuthResponse(user);

        return ApiResponse.success(
                        HttpStatus.OK,
                        authResponse,
                        "Successful Login!"
                );    }
;


    @Transactional
    public ApiResponse<AuthResponse> register(@RequestBody AuthRequest registerRequest) {
        try {
            // Check if email already exists
            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                return ApiResponse.error(
                        HttpStatus.BAD_REQUEST,
                        "Email already registered",
                        ApiError.of("EMAIL_EXISTS", "Email exists")
                );           }


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
            return ApiResponse.success(
                            HttpStatus.OK,
                            authResponse,
                            "User registered successfully"
                    );
        } catch (DataIntegrityViolationException ex) {
            // Handle database constraint violations (e.g., unique constraint)
            return ApiResponse.error(
                            HttpStatus.BAD_REQUEST,
                            "DatabaseError",
                            ApiError.of("BAD_REQUEST",
                                    ex.getMessage())
                    );
        } catch (Exception ex) {
            // Handle other unexpected exceptions
            return ApiResponse.error(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Internal server error",
                            ApiError.of("INTERNAL_SERVER_ERROR",
                                    ex.getMessage())
                    );        }
    }


    public ApiResponse<AuthResponse> refresh(@RequestBody AuthRequest refreshRequest) {
        String refreshToken = refreshRequest.getRefreshToken();

        if (!jwtUtils.validateRefreshToken(refreshToken, "GLOBAL")) {
            return ApiResponse.error(
                            HttpStatus.BAD_REQUEST,
                            "Invalid refresh token",
                            ApiError.of("BAD_REQUEST", "Invalid or malformed refresh token")
                    );
        }

        try {
            String userEmail = jwtUtils.extractSubject(refreshToken, "GLOBAL");
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Generate new tokens
            String newAccessToken = jwtUtils.generateAccessToken(user,"GLOBAL");
            String newRefreshToken = jwtUtils.generateRefreshToken(user,"GLOBAL");

            AuthResponse authResponse = createAuthResponse(user);

            return ApiResponse.success(
                            HttpStatus.OK,
                            authResponse,
                            "Token refreshed successfully"
                           );

        } catch (ExpiredJwtException e) {
            return ApiResponse.error(
                            HttpStatus.BAD_REQUEST,
                            "Token expired",
                            ApiError.of("TOKEN_EXPIRED", e.getMessage())
                    );
        } catch (JwtException e) {
            return ApiResponse.error(
                            HttpStatus.BAD_REQUEST,
                            "JWT processing error",
                            ApiError.of("JWT_ERROR", e.getMessage())
                    );
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


