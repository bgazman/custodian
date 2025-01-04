package consulting.gazman.security.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import consulting.gazman.security.entity.User;
import consulting.gazman.security.exception.AppException;
import consulting.gazman.security.service.NotificationService;
import consulting.gazman.security.service.MfaService;
import consulting.gazman.security.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MfaServiceImpl implements MfaService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final long TOKEN_EXPIRY = 300; // 5 minutes
    private final ObjectMapper objectMapper;


    @Autowired
    UserService userService;

    @Autowired
    private NotificationService notificationService; // For sending SMS or Email

    public MfaServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void initiateMfaChallenge(String email, String method) {
        String token = generateRandomCode(); // 6-digit OTP
        if ("SMS".equalsIgnoreCase(method)) {
            String phoneNumber = userService.getPhoneNumber(email);
            notificationService.sendSms(phoneNumber, "Your OTP is: " + token);
        } else if ("EMAIL".equalsIgnoreCase(method)) {
            notificationService.sendEmail(email, "Your OTP", "Your OTP is: " + token);
        }
        // Store the token in Redis with a short expiration (e.g., 5 minutes)
        redisTemplate.opsForValue().set("mfa:" + email, token, 5, TimeUnit.MINUTES);
    }



    @Override
    public String generateMfaSecret(String email) {
        return "";
    }



    @Override
    public boolean validateMfaToken(String email, String token, String method) {
        if (email == null || token == null || method == null) {
            log.error("Null parameters provided: email={}, token={}, method={}", email, token, method);
            return false;
        }

        if ("TOTP".equalsIgnoreCase(method)) {
//            String secret = userService.getTotpSecret(email); // Actually fetch from user profile
//            if (secret == null) {
//                log.error("No TOTP secret found for user: {}", email);
//                return false;
//            }
//            return validateTotp(secret, token);
            return true;
        } else {
            String redisKey = "mfa:" + email;
            String storedToken = redisTemplate.opsForValue().get(redisKey);

            if (storedToken == null) {
                return false;
            }

            boolean matches = storedToken.equals(token);
            if (!matches) {
                log.debug("Token mismatch for user {}: expected={}, received={}",
                        email, storedToken, token);
            }else{
                redisTemplate.delete(redisKey);

            }
            return matches;
        }
    }

    @Override
    public String generateTotpSecret(String email) {
        // Generate and return a TOTP secret
        return "simulated-totp-secret";
    }

    @Override
    public String generateRandomCode() {
        return String.format("%06d", new SecureRandom().nextInt(999999));
    }

    @Override
    public boolean resendMfaCode(String email) {
        // Check if user exists
        User user = userService.findByEmail(email)
                .orElseThrow(() -> AppException.userNotFound("No user found with email: " + email));

        // Check resend attempts using Redis
        String resendKey = "mfa:resend:" + email;
        String attemptsStr = redisTemplate.opsForValue().get(resendKey);
        int attempts = attemptsStr != null ? Integer.parseInt(attemptsStr) : 0;

        // Check if max resend attempts reached (e.g., 3 attempts within 5 minutes)
        if (attempts >= 3) {
            throw new AppException("MAX_RESEND_ATTEMPTS", "Maximum resend attempts reached. Please try again later.");
        }

        try {
            // Generate and send new code based on user's MFA method
            String newToken = generateRandomCode();
            String method = user.getMfaMethod();

            if ("SMS".equalsIgnoreCase(method)) {
                String phoneNumber = user.getPhoneNumber();
                if (phoneNumber == null || phoneNumber.isEmpty()) {
                    throw new AppException("INVALID_PHONE", "No phone number registered for this account");
                }
                notificationService.sendSms(phoneNumber, "Your verification code is: " + newToken);
            } else if ("EMAIL".equalsIgnoreCase(method)) {
                notificationService.sendEmail(email, "Verification Code", "Your verification code is: " + newToken);
            } else {
                throw new AppException("INVALID_MFA_METHOD", "Unsupported MFA method: " + method);
            }

            // Store new token in Redis with expiration
            String tokenKey = "mfa:token:" + email;
            redisTemplate.opsForValue().set(tokenKey, newToken, TOKEN_EXPIRY, TimeUnit.SECONDS);

            // Increment resend attempts counter
            redisTemplate.opsForValue().increment(resendKey);
            // Set expiry for resend attempts counter if it's first attempt
            if (attempts == 0) {
                redisTemplate.expire(resendKey, TOKEN_EXPIRY, TimeUnit.SECONDS);
            }

            return true;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException("RESEND_FAILED", "Failed to resend verification code: " + e.getMessage());
        }
    }

    @Override
    public boolean validateBackupCode(String email, String token) {
        // Check if user exists
        User user = userService.findByEmail(email)
                .orElseThrow(() -> AppException.userNotFound("No user found with email: " + email));


        // Check if account is locked
        String lockKey = "mfa:locked:" + email;
        Boolean isLocked = redisTemplate.hasKey(lockKey);
        if (Boolean.TRUE.equals(isLocked)) {
            throw new AppException("ACCOUNT_LOCKED", "Account is temporarily locked due to too many failed attempts");
        }

        try {
            // Get backup codes from user (assuming it's stored as a JSON array in the database)
            String backupCodesJson = user.getMfaBackupCodes();
            if (backupCodesJson == null || backupCodesJson.equals("[]")) {
                throw new AppException("NO_BACKUP_CODES", "No backup codes available");
            }

            List<String> backupCodes = objectMapper.readValue(backupCodesJson, new TypeReference<List<String>>() {});

            // Check if the provided token matches any backup code
            if (backupCodes.contains(token)) {
                // Remove the used backup code
                backupCodes.remove(token);

                // Update the user's backup codes in the database
                user.setMfaBackupCodes(objectMapper.writeValueAsString(backupCodes));
                userService.updateUser(user);

                // Clear any failed attempts
                String attemptsKey = "mfa:attempts:" + email;
                redisTemplate.delete(attemptsKey);

                return true;
            } else {
                // Increment failed attempts
                String attemptsKey = "mfa:attempts:" + email;
                Long attempts = redisTemplate.opsForValue().increment(attemptsKey);

                // Set expiry for attempts counter if it's first attempt
                if (attempts != null && attempts == 1) {
                    redisTemplate.expire(attemptsKey, 1, TimeUnit.HOURS);
                }

                // Lock account after 5 failed attempts
                if (attempts != null && attempts >= 5) {
                    redisTemplate.opsForValue().set(lockKey, "true", 1, TimeUnit.HOURS);
                    throw new AppException("ACCOUNT_LOCKED", "Account has been locked due to too many failed attempts");
                }

                return false;
            }
        } catch (AppException e) {
            throw e;
        } catch (JsonProcessingException e) {
            throw new AppException("INVALID_BACKUP_CODES", "Error processing backup codes");
        } catch (Exception e) {
            throw new AppException("VALIDATION_FAILED", "Failed to validate backup code: " + e.getMessage());
        }
    }

    private boolean validateTotp(String secret, String token) {
        // Simulated TOTP validation (replace with library validation)
        return "123456".equals(token); // Replace with real validation logic
    }
}

