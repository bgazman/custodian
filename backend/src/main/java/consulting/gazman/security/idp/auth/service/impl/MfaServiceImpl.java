package consulting.gazman.security.idp.auth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import consulting.gazman.security.client.user.entity.User;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.common.service.NotificationService;
import consulting.gazman.security.idp.auth.service.MfaService;
import consulting.gazman.security.client.user.service.UserService;
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
    public void initiateMfaChallenge(String email, String clientId) {
        User user = userService.findByEmail(email);
        String method = user.getMfaMethod();
        String token = generateRandomCode(); // 6-digit OTP
        if ("SMS".equalsIgnoreCase(method)) {
            String phoneNumber = userService.getPhoneNumber(email);
            notificationService.sendSms(phoneNumber, "Your OTP is: " + token);
        } else if ("EMAIL".equalsIgnoreCase(method)) {
            notificationService.sendEmail(email, "Your OTP", "Your OTP is: " + token);
        }
        // Store the token in Redis with a short expiration (e.g., 5 minutes)
        redisTemplate.opsForValue().set("mfa:token:" + email, token, 5, TimeUnit.MINUTES);
    }

    @Override
    public String generateMfaSecret(String email) {
        return "";
    }

    @Override
    public boolean validateMfaToken(String email, String token, String clientId) {
        if (email == null || token == null || clientId == null) {
            log.error("Null parameters provided: email={}, token={}, clientId={}", email, token, clientId);
            return false;
        }
        User user = userService.findByEmail(email);
        String method = user.getMfaMethod();

        if ("TOTP".equalsIgnoreCase(method)) {
            return true; // TOTP validation logic
        }

        String tokenKey = "mfa:token:" + email;
        String storedToken = redisTemplate.opsForValue().get(tokenKey);

        if (storedToken == null) {
            return false;
        }

        boolean matches = storedToken.equals(token);
        if (matches) {
            redisTemplate.delete(tokenKey);
            redisTemplate.delete("mfa:resend:" + email); // Clear resend attempts
        } else {
            log.debug("Token mismatch for user {}", email);
        }
        return matches;
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
    public boolean resendMfaCode(String email, String clientId) {
        User user = userService.findByEmail(email);
        String method = user.getMfaMethod();
        String resendKey = "mfa:resend:" + email;
        String attemptsStr = redisTemplate.opsForValue().get(resendKey);
        int attempts = attemptsStr != null ? Integer.parseInt(attemptsStr) : 0;

        if (attempts >= 3) {
            throw new AppException("MAX_RESEND_ATTEMPTS", "Maximum resend attempts reached. Please try again later.");
        }

        try {
            String newToken = generateRandomCode();

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

            String tokenKey = "mfa:token:" + email;
            redisTemplate.opsForValue().set(tokenKey, newToken, TOKEN_EXPIRY, TimeUnit.SECONDS);

            redisTemplate.opsForValue().increment(resendKey);
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
        User user = userService.findByEmail(email);

        String lockKey = "mfa:locked:" + email;
        Boolean isLocked = redisTemplate.hasKey(lockKey);
        if (Boolean.TRUE.equals(isLocked)) {
            throw new AppException("ACCOUNT_LOCKED", "Account is temporarily locked due to too many failed attempts");
        }

        try {
            String backupCodesJson = user.getMfaRecoveryCodes();
            if (backupCodesJson == null || backupCodesJson.equals("[]")) {
                throw new AppException("NO_BACKUP_CODES", "No backup codes available");
            }

            List<String> backupCodes = objectMapper.readValue(backupCodesJson, new TypeReference<List<String>>() {});

            if (backupCodes.contains(token)) {
                backupCodes.remove(token);

                user.setMfaRecoveryCodes(objectMapper.writeValueAsString(backupCodes));
                userService.updateUser(user);

                String attemptsKey = "mfa:attempts:" + email;
                redisTemplate.delete(attemptsKey);

                return true;
            } else {
                String attemptsKey = "mfa:attempts:" + email;
                Long attempts = redisTemplate.opsForValue().increment(attemptsKey);

                if (attempts != null && attempts == 1) {
                    redisTemplate.expire(attemptsKey, 1, TimeUnit.HOURS);
                }

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
        return "123456".equals(token); // Replace with real validation logic
    }
}