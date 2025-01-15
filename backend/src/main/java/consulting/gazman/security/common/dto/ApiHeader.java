package consulting.gazman.security.common.dto;


import java.util.UUID;

import lombok.Data;


import java.time.Instant;


import lombok.Builder;


@Data
@Builder
public class ApiHeader {
    private UUID traceId;       // Single ID for tracing/logging
    private Instant timestamp;  // Timestamp of the request/response
}


