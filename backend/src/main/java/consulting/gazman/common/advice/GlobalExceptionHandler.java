package consulting.gazman.common.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import consulting.gazman.common.dto.ApiError;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.common.utils.StatusMapper;
import consulting.gazman.security.exception.UnauthorizedException;
import consulting.gazman.security.exception.UserAlreadyExistsException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) throws IOException, JsonProcessingException {
        ApiResponse<Object> errorResponse = ApiResponse.error(
                "error",
                "An unexpected error occurred",
                ApiError.of("INTERNAL_SERVER_ERROR", ex.getMessage())
        );
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonResponse);
    }
}
