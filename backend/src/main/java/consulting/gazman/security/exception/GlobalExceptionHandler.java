package consulting.gazman.security.exception;

import common.dto.ApiError;
import common.dto.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(
                        HttpStatus.UNAUTHORIZED,
                        "Authentication failed",
                        ApiError.of("UNAUTHORIZED", ex.getMessage())
                ));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        HttpStatus.BAD_REQUEST,
                        "Invalid request",
                        ApiError.of("BAD_REQUEST", ex.getMessage())
                ));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserExists(UserAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(
                        HttpStatus.CONFLICT,
                        "User already exists",
                        ApiError.of("USER_EXISTS", ex.getMessage())
                ));
    }

    @ExceptionHandler({JwtException.class, ExpiredJwtException.class})
    public ResponseEntity<ApiResponse<Object>> handleJwt(Exception ex) {
        String code = ex instanceof ExpiredJwtException ? "TOKEN_EXPIRED" : "INVALID_TOKEN";
        String message = ex instanceof ExpiredJwtException ? "Token has expired" : "Invalid token";

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(
                        HttpStatus.UNAUTHORIZED,
                        message,
                        ApiError.of(code, ex.getMessage())
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneral(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Internal server error",
                        ApiError.of("SERVER_ERROR", ex.getMessage())
                ));
    }
}