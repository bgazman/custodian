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


}
