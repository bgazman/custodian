package consulting.gazman.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class ApiResponse<T> {
    private String status;
    private int statusCode;
    private String message;
    private T data;
    private Object errorDetails;

    // Constructors, getters, and setters
}