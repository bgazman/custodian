package consulting.gazman.security.service.impl;

import consulting.gazman.common.dto.ApiError;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.security.dto.AuthRequest;
import consulting.gazman.security.dto.AuthResponse;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.exception.ResourceNotFoundException;
import consulting.gazman.security.repository.UserRepository;
import consulting.gazman.security.service.AuthService;
import consulting.gazman.security.service.JwtService;
import consulting.gazman.security.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Duration;
import java.time.LocalDateTime;
@Slf4j
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private JwtServiceImpl jwtService;
    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 15;

    // Login implementation
    @Override
    public ApiResponse<AuthResponse> login(AuthRequest loginRequest) {
        log.info("Attempting login for email: {}", loginRequest.getEmail());

        // Find user by email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElse(null);

        // Validate user
        if (user == null) {
            return buildErrorResponse("INVALID_CREDENTIALS", "Invalid credentials.");
        }

        // Check if account is locked
        if (isAccountLocked(user)) {
            Duration remainingLockTime = Duration.between(LocalDateTime.now(), user.getLockedUntil());
            return buildErrorResponse("ACCOUNT_LOCKED",
                    "Account is locked. Try again in " + remainingLockTime.toMinutes() + " minutes.");
        }

        // Validate password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            handleFailedLoginAttempt(user);
            int remainingAttempts = MAX_ATTEMPTS - user.getFailedLoginAttempts() ;
            if (remainingAttempts > 0) {
                return buildErrorResponse("INVALID_CREDENTIALS",
                        "Invalid credentials. " + remainingAttempts + " attempts remaining.");
            } else {
                return buildErrorResponse("ACCOUNT_LOCKED",
                        "Account is locked due to too many failed attempts. Try again later.");
            }


//            return buildErrorResponse("INVALID_CREDENTIALS",
//                    "Invalid credentials. " + remainingAttempts + " attempts remaining.");
        }

        // Reset failed attempts and update login time
        resetLoginAttempts(user);
        AuthResponse authResponse = createAuthResponse(user);

        return ApiResponse.success(authResponse, "Login successful.");
    }

    // Register implementation
    @Override
    public ApiResponse<AuthResponse> register(AuthRequest registerRequest) {
        log.info("Attempting registration for email: {}", registerRequest.getEmail());

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return buildErrorResponse("EMAIL_EXISTS", "Email is already registered.");
        }

        // Create and save the user
        User newUser = new User();
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setRole("USER");
        newUser.setEnabled(true);

        User savedUser = userRepository.save(newUser);
        AuthResponse authResponse = createAuthResponse(savedUser);

        return ApiResponse.success(authResponse, "User registered successfully.");
    }

    // Refresh token implementation
    @Override
    public ApiResponse<AuthResponse> refresh(AuthRequest refreshRequest) {
        log.info("Attempting token refresh");

        String refreshToken = refreshRequest.getRefreshToken();
        if (!jwtUtils.validateRefreshToken(refreshToken, "GLOBAL")) {
            return buildErrorResponse("INVALID_TOKEN", "Invalid or expired refresh token.");
        }

        try {
            String userEmail = jwtUtils.extractSubject(refreshToken, "GLOBAL");
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            AuthResponse authResponse = createAuthResponse(user);
            return ApiResponse.success(authResponse, "Token refreshed successfully.");
        } catch (ExpiredJwtException e) {
            return buildErrorResponse("TOKEN_EXPIRED", "Refresh token expired.");
        } catch (JwtException e) {
            return buildErrorResponse("JWT_ERROR", "Error processing JWT: " + e.getMessage());
        }
    }

    // Helper: Check if account is locked
    private boolean isAccountLocked(User user) {
        return user.getLockedUntil() != null && LocalDateTime.now().isBefore(user.getLockedUntil());
    }

    // Helper: Handle failed login attempts
    private void handleFailedLoginAttempt(User user) {
        int newFailedAttempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(newFailedAttempts);

        if (newFailedAttempts >= MAX_ATTEMPTS) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
            log.warn("User account locked due to too many failed attempts: {}", user.getEmail());
        }

        userRepository.save(user);
    }

    // Helper: Reset login attempts
    private void resetLoginAttempts(User user) {
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
    }

    // Helper: Create AuthResponse
    private AuthResponse createAuthResponse(User user) {
        return new AuthResponse(
                jwtService.generateAccessToken(user, "GLOBAL"),
                jwtService.generateRefreshToken(user, "GLOBAL"),
                user.getEmail(),
                user.getRole()
        );
    }

    // Helper: Build error response
    private ApiResponse<AuthResponse> buildErrorResponse(String code, String message) {
        return ApiResponse.error(
                "error",
                message,
                ApiError.of(code, message)
        );
    }

    public User findByEmail(String email) {
        try {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }
}
