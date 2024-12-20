package consulting.gazman.security.utils;

import consulting.gazman.security.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
public class ResponseMapper {

    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus("success");
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message, Object errorDetails) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus("error");
        response.setStatusCode(status.value());
        response.setMessage(message);
        response.setErrorDetails(errorDetails);
        return response;
    }

    public static <T> ApiResponse<T> badRequest(String message) {
        return error(HttpStatus.BAD_REQUEST, message, null);
    }

    public static <T> ApiResponse<T> unauthorized(String message) {
        return error(HttpStatus.UNAUTHORIZED, message, null);
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return error(HttpStatus.NOT_FOUND, message, null);
    }

    public static <T> ApiResponse<T> internalServerError(String message, Object errorDetails) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, message, errorDetails);
    }
}
