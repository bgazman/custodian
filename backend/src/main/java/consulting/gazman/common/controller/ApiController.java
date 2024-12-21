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
        log.info("Request received: traceId={}, method={}, endpoint={}", traceId, method, endpoint);
    }

    // Handle ApiResponse and map it to ResponseEntity
    protected <T> ResponseEntity<?> handleApiResponse(ApiResponse<T> apiResponse) {
        // Map status using StatusMapper
        HttpStatus status = StatusMapper.toHttpStatus(apiResponse.getStatus());

        // Retrieve headers
        String traceId = MDC.get("traceId");
        UUID responseId = UUID.randomUUID();
        Instant timestamp = Instant.now();

        // Success case: Return the data
        if ("success".equalsIgnoreCase(apiResponse.getStatus())) {
            return ResponseEntity.status(status)
                    .header("X-Trace-Id", traceId)
                    .header("X-Response-Id", responseId.toString())
                    .header("X-Timestamp", timestamp.toString())
                    .body(apiResponse.getData()); // Return the resource
        }

        // Error case: Return the ApiError
        return ResponseEntity.status(status)
                .header("X-Trace-Id", traceId)
                .header("X-Response-Id", responseId.toString())
                .header("X-Timestamp", timestamp.toString())
                .body(apiResponse.getError()); // Return the error
    }

}