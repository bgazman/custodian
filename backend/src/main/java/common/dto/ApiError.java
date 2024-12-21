package common.dto;

import lombok.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
public class ApiError {
    private String code;
    private String message;
    private Map<String, Object> details;

    public static ApiError of(String code, String message) {
        return ApiError.builder()
                .code(code)
                .message(message)
                .details(new HashMap<>())
                .build();
    }

    public static ApiError of(String code, String message, Map<String, Object> details) {
        return ApiError.builder()
                .code(code)
                .message(message)
                .details(details)
                .build();
    }
}