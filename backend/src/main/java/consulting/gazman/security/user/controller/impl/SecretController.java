package consulting.gazman.security.user.controller.impl;

import consulting.gazman.security.user.controller.ISecretController;
import consulting.gazman.security.common.controller.ApiController;
import consulting.gazman.security.common.exception.AppException;
import consulting.gazman.security.idp.oauth.entity.Secret;
import consulting.gazman.security.idp.oauth.service.SecretService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class SecretController extends ApiController implements ISecretController {

    private final SecretService secretService;

    public SecretController(SecretService secretService) {
        this.secretService = secretService;
    }

    @Override
    public ResponseEntity<?> getAllSecrets() {
        try {
            List<Secret> secrets = secretService.findAll();
            return wrapSuccessResponse(secrets, "Secrets retrieved successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> getSecretById(Long id) {
        try {
            Optional<Secret> secret = secretService.findById(id);
            if (secret.isEmpty()) {
                return wrapErrorResponse("NOT_FOUND", "Secret not found", HttpStatus.NOT_FOUND);
            }
            return wrapSuccessResponse(secret.get(), "Secret retrieved successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> createSecret(Secret secret) {
        try {
            Secret savedSecret = secretService.save(secret);
            return wrapSuccessResponse(savedSecret, "Secret created successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> updateSecret(Long id, Secret secret) {
        try {
            Optional<Secret> existingSecret = secretService.findById(id);
            if (existingSecret.isEmpty()) {
                return wrapErrorResponse("NOT_FOUND", "Secret not found", HttpStatus.NOT_FOUND);
            }
            secret.setId(id);
            Secret updatedSecret = secretService.save(secret);
            return wrapSuccessResponse(updatedSecret, "Secret updated successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> deleteSecret(Long id) {
        try {
            secretService.deleteById(id);
            return wrapSuccessResponse(null, "Secret deleted successfully");
        } catch (AppException e) {
            return wrapErrorResponse(e.getErrorCode(), e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return wrapErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
