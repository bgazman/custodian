package consulting.gazman.common.controller;

import consulting.gazman.common.dto.ApiError;
import consulting.gazman.common.dto.ApiResponse;
import consulting.gazman.common.utils.StatusMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
public abstract class ApiController {

    // Log request details with Sleuth traceId
    protected void logRequest(String method, String endpoint) {
        String traceId = MDC.get("traceId"); // Retrieve Sleuth's traceId
        if (traceId == null) {
            traceId = "UNKNOWN"; // Fallback if traceId is not set
        }
        log.info("Request received: traceId={}, method={}, endpoint={}", traceId, method, endpoint);
    }

    // Wrap a successful result into an ApiResponse and map to ResponseEntity
    protected <T> ResponseEntity<ApiResponse<T>> wrapSuccessResponse(T data, String message) {
        // Retrieve metadata for headers
        String traceId = MDC.get("traceId");
        UUID responseId = UUID.randomUUID();
        Instant timestamp = Instant.now();

        // Create ApiResponse for success
        ApiResponse<T> apiResponse = ApiResponse.success(data, message);

        // Return ResponseEntity with metadata headers
        return ResponseEntity.ok()
                .header("X-Trace-Id", traceId)
                .header("X-Response-Id", responseId.toString())
                .header("X-Timestamp", timestamp.toString())
                .body(apiResponse);
    }

    // Wrap an error into an ApiResponse and map to ResponseEntity
    protected ResponseEntity<ApiResponse<Void>> wrapErrorResponse(String errorCode, String message, HttpStatus status) {
        // Retrieve metadata for headers
        String traceId = MDC.get("traceId");
        UUID responseId = UUID.randomUUID();
        Instant timestamp = Instant.now();

        // Create ApiResponse for error
        ApiResponse<Void> apiResponse = ApiResponse.error(errorCode, message);

        // Return ResponseEntity with metadata headers
        return ResponseEntity.status(status)
                .header("X-Trace-Id", traceId)
                .header("X-Response-Id", responseId.toString())
                .header("X-Timestamp", timestamp.toString())
                .body(apiResponse);
    }
}