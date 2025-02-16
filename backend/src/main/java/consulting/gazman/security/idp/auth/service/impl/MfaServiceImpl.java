package consulting.gazman.security.idp.auth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import consulting.gazman.security.client.user.entity.User;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.common.service.NotificationService;
import consulting.gazman.security.idp.auth.dto.*;
import consulting.gazman.security.idp.auth.service.MfaService;
import consulting.gazman.security.client.user.service.UserService;
import consulting.gazman.security.idp.model.OAuthSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    private NotificationService notificationService;

    public MfaServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public MfaInitiationResult initiateMfaChallenge(OAuthSession session, String preferredMethod) {
        User user = userService.findByEmail(session.getEmail());
        String method = preferredMethod != null ? preferredMethod : user.getMfaMethod();

        MfaCode mfaCode = generateRandomCode(method);
        String challengeId = UUID.randomUUID().toString();

        try {
            if ("SMS".equalsIgnoreCase(method)) {
                String phoneNumber = user.getPhoneNumber();
                if (phoneNumber == null || phoneNumber.isEmpty()) {
                    return MfaInitiationResult.builder()
                            .success(false)
                            .errorMessage("No phone number registered")
                            .build();
                }
                notificationService.sendSms(phoneNumber, "Your OTP is: " + mfaCode.getCode());
            } else if ("EMAIL".equalsIgnoreCase(method)) {
                notificationService.sendEmail(session.getEmail(), "Your OTP", "Your OTP is: " + mfaCode.getCode());
            }

            redisTemplate.opsForValue().set("mfa:token:" + session.getEmail(), mfaCode.getCode(), TOKEN_EXPIRY, TimeUnit.SECONDS);

            Map<String, Object> additionalData = new HashMap<>();
            additionalData.put("deliveryMedium", method.toLowerCase());

            return MfaInitiationResult.builder()
                    .success(true)
                    .challengeId(challengeId)
                    .method(method)
                    .expiresAt(mfaCode.getExpiresAt())
                    .additionalData(additionalData)
                    .build();
        } catch (Exception e) {
            log.error("Failed to initiate MFA challenge", e);
            return MfaInitiationResult.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    @Override
    public MfaSetupResult generateMfaSecret(OAuthSession session, String method) {
        try {
            if ("TOTP".equalsIgnoreCase(method)) {
                TotpSetupResult totpResult = generateTotpSecret(session);
                List<String> backupCodes = generateBackupCodes();

                Map<String, Object> configuration = new HashMap<>();
                configuration.put("issuer", "YourApp");
                configuration.put("digits", 6);
                configuration.put("period", 30);
                configuration.put("algorithm", "SHA1");

                return MfaSetupResult.builder()
                        .success(true)
                        .method(method)
                        .secret(totpResult.getSecret())
                        .configuration(configuration)
                        .backupCodes(backupCodes)
                        .build();
            }

            return MfaSetupResult.builder()
                    .success(true)
                    .method(method)
                    .configuration(new HashMap<>())
                    .build();
        } catch (Exception e) {
            return MfaSetupResult.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    @Override
    public MfaValidationResult validateMfaToken(OAuthSession session, MfaRequest mfaRequest) {
        if (session == null || mfaRequest == null || mfaRequest.getToken() == null) {
            return MfaValidationResult.builder()
                    .valid(false)
                    .errorCode("INVALID_PARAMETERS")
                    .errorMessage("Missing required parameters")
                    .remainingAttempts(0)
                    .requiresReinitialization(true)
                    .build();
        }

        User user = userService.findByEmail(session.getEmail());
        String method = user.getMfaMethod();

        if ("TOTP".equalsIgnoreCase(method)) {
            boolean isValid = validateTotp(user.getMfaSecret(), String.valueOf(mfaRequest.getToken()));
            return MfaValidationResult.builder()
                    .valid(isValid)
                    .errorCode(isValid ? null : "INVALID_TOKEN")
                    .errorMessage(isValid ? null : "Invalid TOTP token")
                    .remainingAttempts(isValid ? 0 : 2)
                    .requiresReinitialization(!isValid)
                    .build();
        }

        String tokenKey = "mfa:token:" + session.getEmail();
        String storedToken = redisTemplate.opsForValue().get(tokenKey);

        if (storedToken == null) {
            return MfaValidationResult.builder()
                    .valid(false)
                    .errorCode("TOKEN_EXPIRED")
                    .errorMessage("Token has expired")
                    .requiresReinitialization(true)
                    .build();
        }

        boolean matches = storedToken.equals(mfaRequest.getToken());
        if (matches) {
            redisTemplate.delete(tokenKey);
            redisTemplate.delete("mfa:resend:" + session.getEmail());
        }

        return MfaValidationResult.builder()
                .valid(matches)
                .errorCode(matches ? null : "INVALID_TOKEN")
                .errorMessage(matches ? null : "Invalid token")
                .remainingAttempts(matches ? 0 : 2)
                .requiresReinitialization(!matches)
                .build();
    }

    @Override
    public TotpSetupResult generateTotpSecret(OAuthSession session) {
        String secret = generateSecureSecret();
        String qrCodeUri = String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                "YourApp",
                session.getEmail(),
                secret,
                "YourApp");

        List<String> backupCodes = generateBackupCodes();
        Map<String, Object> configuration = new HashMap<>();
        configuration.put("algorithm", "SHA1");
        configuration.put("digits", 6);
        configuration.put("period", 30);

        return TotpSetupResult.builder()
                .secret(secret)
                .qrCodeUri(qrCodeUri)
                .backupCodes(backupCodes)
                .configuration(configuration)
                .build();
    }

    @Override
    public MfaCode generateRandomCode(String method) {
        String code = String.format("%06d", new SecureRandom().nextInt(999999));
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusSeconds(TOKEN_EXPIRY);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("type", "numeric");
        metadata.put("length", 6);

        return MfaCode.builder()
                .code(code)
                .generatedAt(now)
                .expiresAt(expiresAt)
                .method(method)
                .metadata(metadata)
                .build();
    }

    @Override
    public MfaResendResult resendMfaCode(OAuthSession session, MfaRequest request) {
        User user = userService.findByEmail(session.getEmail());
        String method = user.getMfaMethod();
        String resendKey = "mfa:resend:" + session.getEmail();
        String attemptsStr = redisTemplate.opsForValue().get(resendKey);
        int attempts = attemptsStr != null ? Integer.parseInt(attemptsStr) : 0;

        if (attempts >= 3) {
            LocalDateTime nextAllowed = LocalDateTime.now().plusMinutes(15);
            return MfaResendResult.builder()
                    .success(false)
                    .nextAllowedAttempt(nextAllowed)
                    .remainingResendAttempts(0)
                    .errorMessage("Maximum resend attempts reached")
                    .build();
        }

        try {
            MfaCode mfaCode = generateRandomCode(method);

            if ("SMS".equalsIgnoreCase(method)) {
                String phoneNumber = user.getPhoneNumber();
                if (phoneNumber == null || phoneNumber.isEmpty()) {
                    return MfaResendResult.builder()
                            .success(false)
                            .errorMessage("No phone number registered")
                            .remainingResendAttempts(3 - attempts)
                            .build();
                }
                notificationService.sendSms(phoneNumber, "Your verification code is: " + mfaCode.getCode());
            } else if ("EMAIL".equalsIgnoreCase(method)) {
                notificationService.sendEmail(session.getEmail(), "Verification Code", "Your verification code is: " + mfaCode.getCode());
            }

            String tokenKey = "mfa:token:" + session.getEmail();
            redisTemplate.opsForValue().set(tokenKey, mfaCode.getCode(), TOKEN_EXPIRY, TimeUnit.SECONDS);

            redisTemplate.opsForValue().increment(resendKey);
            if (attempts == 0) {
                redisTemplate.expire(resendKey, TOKEN_EXPIRY, TimeUnit.SECONDS);
            }

            return MfaResendResult.builder()
                    .success(true)
                    .remainingResendAttempts(2 - attempts)
                    .nextAllowedAttempt(LocalDateTime.now().plusSeconds(30))
                    .build();

        } catch (Exception e) {
            return MfaResendResult.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .remainingResendAttempts(3 - attempts)
                    .build();
        }
    }

    @Override
    public BackupCodeValidationResult validateBackupCode(OAuthSession session, MfaRequest request) {
        User user = userService.findByEmail(session.getEmail());

        String lockKey = "mfa:locked:" + session.getEmail();
        Boolean isLocked = redisTemplate.hasKey(lockKey);
        if (Boolean.TRUE.equals(isLocked)) {
            return BackupCodeValidationResult.builder()
                    .valid(false)
                    .errorMessage("Account is temporarily locked")
                    .validatedAt(LocalDateTime.now())
                    .build();
        }

        try {
            String backupCodesJson = user.getMfaRecoveryCodes();
            if (backupCodesJson == null || backupCodesJson.equals("[]")) {
                return BackupCodeValidationResult.builder()
                        .valid(false)
                        .errorMessage("No backup codes available")
                        .validatedAt(LocalDateTime.now())
                        .build();
            }

            List<String> backupCodes = objectMapper.readValue(backupCodesJson, new TypeReference<List<String>>() {});

            if (backupCodes.contains(request.getToken())) {
                backupCodes.remove(request.getToken());

                user.setMfaRecoveryCodes(objectMapper.writeValueAsString(backupCodes));
                userService.updateUser(user);

                String attemptsKey = "mfa:attempts:" + session.getEmail();
                redisTemplate.delete(attemptsKey);

                return BackupCodeValidationResult.builder()
                        .valid(true)
                        .remainingCodes(backupCodes.size())
                        .validatedAt(LocalDateTime.now())
                        .build();
            } else {
                String attemptsKey = "mfa:attempts:" + session.getEmail();
                Long attempts = redisTemplate.opsForValue().increment(attemptsKey);

                if (attempts != null && attempts == 1) {
                    redisTemplate.expire(attemptsKey, 1, TimeUnit.HOURS);
                }

                if (attempts != null && attempts >= 5) {
                    redisTemplate.opsForValue().set(lockKey, "true", 1, TimeUnit.HOURS);
                    return BackupCodeValidationResult.builder()
                            .valid(false)
                            .errorMessage("Account has been locked due to too many failed attempts")
                            .validatedAt(LocalDateTime.now())
                            .build();
                }

                return BackupCodeValidationResult.builder()
                        .valid(false)
                        .errorMessage("Invalid backup code")
                        .validatedAt(LocalDateTime.now())
                        .remainingCodes(backupCodes.size())
                        .build();
            }
        } catch (JsonProcessingException e) {
            return BackupCodeValidationResult.builder()
                    .valid(false)
                    .errorMessage("Error processing backup codes")
                    .validatedAt(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            return BackupCodeValidationResult.builder()
                    .valid(false)
                    .errorMessage(e.getMessage())
                    .validatedAt(LocalDateTime.now())
                    .build();
        }
    }

    private boolean validateTotp(String secret, String token) {
        // TODO: Implement proper TOTP validation
        return "123456".equals(token);
    }

    private String generateSecureSecret() {
        // TODO: Implement proper secure secret generation
        return "base32-encoded-secret";
    }

    private List<String> generateBackupCodes() {
        // TODO: Implement proper backup codes generation
        return List.of("backup1", "backup2", "backup3");
    }
}