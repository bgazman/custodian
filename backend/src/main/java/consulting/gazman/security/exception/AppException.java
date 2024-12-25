package consulting.gazman.security.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class AppException extends RuntimeException {

    private final String errorCode;

    public AppException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AppException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    // Factory methods for different exceptions
    public static AppException invalidRefreshToken(String message) {
        return new AppException("INVALID_REFRESH_TOKEN", message);
    }

    public static AppException invalidToken(String message) {
        return new AppException("INVALID_TOKEN", message);
    }

    public static AppException jwtAuthenticationFailed(String message) {
        return new AppException("JWT_AUTHENTICATION_FAILED", message);
    }

    public static AppException jwtProcessingFailed(String message) {
        return new AppException("JWT_PROCESSING_FAILED", message);
    }

    public static AppException resourceNotFound(String message) {
        return new AppException("RESOURCE_NOT_FOUND", message);
    }

    public static AppException tokenExpired(String message) {
        return new AppException("TOKEN_EXPIRED", message);
    }

    public static AppException unauthorized(String message) {
        return new AppException("UNAUTHORIZED", message);
    }

    public static AppException userAlreadyExists(String message) {
        return new AppException("USER_ALREADY_EXISTS", message);
    }

    public static AppException userNotFound(String message) {
        return new AppException("USER_NOT_FOUND", message);
    }

    public static AppException invalidCredentials(String message) {
        return new AppException("USER_NOT_FOUND", message);
    }
    public static AppException accountLocked(String message) {
        return new AppException("USER_NOT_FOUND", message);
    }
}
