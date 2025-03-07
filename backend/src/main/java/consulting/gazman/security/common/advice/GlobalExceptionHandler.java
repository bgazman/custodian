package consulting.gazman.security.common.advice;

import com.fasterxml.jackson.databind.JsonMappingException;
import consulting.gazman.security.common.dto.ApiError;
import consulting.gazman.security.common.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<?> handleJsonMappingException(JsonMappingException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.builder()
                        .code("JSON_MAPPING_ERROR")
                        .message("An error occurred during serialization: " + ex.getMessage())
                        .build());
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiError.builder()
                        .code("METHOD_NOT_ALLOWED")
                        .message("The requested method is not allowed for this endpoint")
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleInternalServerError(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.builder()
                        .code("INTERNAL_ERROR")
                        .message("Please contact support if the problem persists")
                        .build());
    }
    @ExceptionHandler({AuthorizationDeniedException.class, AccessDeniedException.class})
    public ResponseEntity<ApiError> handleAccessDenied(Exception ex, WebRequest request) {
        ApiError error = ApiError.builder()
                .code("FORBIDDEN")
                .message("You don't have permission to access this resource")
                .details(Map.of(
                        "path", ((ServletWebRequest) request).getRequest().getRequestURI(),
                        "traceId", MDC.get("traceId"),
                        "timestamp", Instant.now().toString()
                ))
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .header("X-Trace-Id", MDC.get("traceId"))
                .header("X-Response-Id", UUID.randomUUID().toString())
                .header("X-Timestamp", Instant.now().toString())
                .body(error);
    }
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleNotFound(NoHandlerFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiError.builder()
                        .code("RESOURCE_NOT_FOUND")
                        .message("The requested resource could not be found")
                        .build());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParameter(MissingServletRequestParameterException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.builder()
                        .code("MISSING_PARAMETER")
                        .message(String.format("Required parameter '%s' is missing", ex.getParameterName()))
                        .build());
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleHibernateConstraintViolation(ConstraintViolationException ex) {
        String constraintName = ex.getConstraintName(); // Get the name of the violated constraint
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiError.builder()
                        .code("DATABASE_CONSTRAINT_VIOLATION")
                        .message("A database constraint was violated: " + constraintName)
                        .build());
    }



    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> handleAppException(AppException ex) {
        log.warn("Application error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.builder()
                        .code(ex.getErrorCode())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.builder()
                        .code("INVALID_ARGUMENT")
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> handleNullPointerException(NullPointerException ex) {
        log.error("Null pointer exception occurred", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.builder()
                        .code("NULL_POINTER_EXCEPTION")
                        .message("An unexpected error occurred")
                        .build());
    }


}
