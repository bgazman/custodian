package consulting.gazman.common.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
@NoArgsConstructor
@Data
public class ApiResponse<T> {
//    private String status;  // "success" or "error"
//    private String message;
//    private T data;
//    private ApiError error;
//
//    // Factory method for success response
//    public static <T> ApiResponse<T> success(T data, String message) {
//        ApiResponse<T> response = new ApiResponse<>();
//        response.status = "success";
//        response.message = message;
//        response.data = data;
//        return response;
//    }
//
//    // Factory method for error response with ApiError
//    public static <T> ApiResponse<T> error(String code, String message, ApiError apiError) {
//        ApiResponse<T> response = new ApiResponse<>();
//        response.status = code;
//        response.message = message;
//        response.error = apiError;
//        return response;
//    }
//
//    // Factory method for error response with just code and message
//    public static <T> ApiResponse<T> error(String code, String message) {
//        return error(code, message, ApiError.of(code, message));
//    }

    // Getters and setters omitted for brevity
}



