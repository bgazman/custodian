package common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.UUID;


@Getter
@Builder
public class ApiResponse<T> {
    private UUID id;
    private Instant timestamp;
    private String status;
    private int statusCode;
    private String message;
    private T data;
    private ApiError error;

    public static <T> ApiResponse<T> success(HttpStatus status,T data, String message) {
        return ApiResponse.<T>builder()
                .id(UUID.randomUUID())
                .timestamp(Instant.now())
                .status("success")
                .statusCode(status.value())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message, ApiError error) {
        return ApiResponse.<T>builder()
                .id(UUID.randomUUID())
                .timestamp(Instant.now())
                .status("error")
                .statusCode(status.value())
                .message(message)
                .error(error)
                .build();
    }
}