package consulting.gazman.common.advice;

import consulting.gazman.common.dto.ApiError;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.common.utils.StatusMapper;
import consulting.gazman.security.exception.UnauthorizedException;
import consulting.gazman.security.exception.UserAlreadyExistsException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(UnauthorizedException.class)
//    public ResponseEntity<ApiResponse<Object>> handleUnauthorized(UnauthorizedException ex) {
//        return ResponseEntity
//                .status(StatusMapper.toHttpStatus("unauthorized"))
//                .headers(buildHeaders())
//                .body(ApiResponse.error(
//                        "unauthorized",
//                        "Authentication failed",
//                        ApiError.of("UNAUTHORIZED", ex.getMessage())
//                ));
//    }
//
//    @ExceptionHandler(BadRequestException.class)
//    public ResponseEntity<ApiResponse<Object>> handleBadRequest(BadRequestException ex) {
//        return ResponseEntity
//                .status(StatusMapper.toHttpStatus("error"))
//                .headers(buildHeaders())
//                .body(ApiResponse.error(
//                        "BAD_REQUEST",
//                        "Invalid request",
//                        ApiError.of("BAD_REQUEST", ex.getMessage())
//                ));
//    }
//
//    @ExceptionHandler(UserAlreadyExistsException.class)
//    public ResponseEntity<ApiResponse<Object>> handleUserExists(UserAlreadyExistsException ex) {
//        return ResponseEntity
//                .status(StatusMapper.toHttpStatus("conflict"))
//                .headers(buildHeaders())
//                .body(ApiResponse.error(
//                        "USER_EXISTS",
//                        "User already exists",
//                        ApiError.of("USER_EXISTS", ex.getMessage())
//                ));
//    }
//
//    @ExceptionHandler({JwtException.class, ExpiredJwtException.class})
//    public ResponseEntity<ApiResponse<Object>> handleJwt(Exception ex) {
//        String code = ex instanceof ExpiredJwtException ? "TOKEN_EXPIRED" : "INVALID_TOKEN";
//        String message = ex instanceof ExpiredJwtException ? "Token has expired" : "Invalid token";
//
//        return ResponseEntity
//                .status(StatusMapper.toHttpStatus("unauthorized"))
//                .headers(buildHeaders())
//                .body(ApiResponse.error(
//                        code,
//                        message,
//                        ApiError.of(code, ex.getMessage())
//                ));
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse<Object>> handleGeneral(Exception ex) {
//        return ResponseEntity
//                .status(StatusMapper.toHttpStatus("server_error"))
//                .headers(buildHeaders())
//                .body(ApiResponse.error(
//                        "SERVER_ERROR",
//                        "Internal server error",
//                        ApiError.of("SERVER_ERROR", ex.getMessage())
//                ));
//    }
//
//    // Helper to build headers
//    private HttpHeaders buildHeaders() {
//        HttpHeaders headers = new HttpHeaders();
//
//        // Get or generate traceId
//        String traceId = MDC.get("traceId");
//        if (traceId == null) {
//            traceId = UUID.randomUUID().toString();
//        }
//
//        // Add headers
//        headers.add("X-Trace-Id", traceId);
//        headers.add("X-Response-Id", UUID.randomUUID().toString());
//        headers.add("X-Timestamp", Instant.now().toString());
//
//        return headers;
//    }
}
