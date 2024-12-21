package consulting.gazman.common.dto;





import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private String code;                // Error code for identifying the type of error
    private String message;             // Human-readable error message
    private Map<String, Object> details; // Additional context (optional, e.g., field-specific errors)

    // Factory method for simple errors with just a code and message
    public static ApiError of(String code, String message) {
        return ApiError.builder()
                .code(code)
                .message(message)
                .details(new HashMap<>()) // Empty details map
                .build();
    }


    // Factory method for errors with detailed context
    public static ApiError of(String code, String message, Map<String, Object> details) {
        return ApiError.builder()
                .code(code)
                .message(message)
                .details(details)
                .build();
    }
}
