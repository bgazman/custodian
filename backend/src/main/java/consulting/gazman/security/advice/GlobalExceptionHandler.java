package consulting.gazman.security.advice;

import consulting.gazman.common.dto.ApiError;
import consulting.gazman.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        String message = "The requested mshod is not allowed for this endpoint";
        ApiResponse apiResponse = ApiResponse.error("method_not_allowed", message,
                ApiError.of("METHOD_NOT_ALLOWED", ex.getMessage()));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(apiResponse);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleInternalServerError(Exception ex) {
        // Log the full error details for server-side tracking
        log.error("Unexpected error occurred", ex);

        // Create a generic error response
        ApiResponse apiResponse = ApiResponse.error(
                "internal_server_error",
                "An unexpected error occurred",
                ApiError.of("INTERNAL_ERROR", "Please contact support if the problem persists")
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(apiResponse);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse> handleNotFound(NoHandlerFoundException ex) {
        ApiResponse apiResponse = ApiResponse.error(
                "not_found",
                "The requested resource could not be found",
                ApiError.of("RESOURCE_NOT_FOUND", ex.getMessage())
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(apiResponse);
    }
}
