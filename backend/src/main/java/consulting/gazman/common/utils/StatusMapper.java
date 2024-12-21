package consulting.gazman.common.utils;

import org.springframework.http.HttpStatus;

public class StatusMapper {

    // Map string status to HttpStatus
    public static HttpStatus toHttpStatus(String status) {
        switch (status.toLowerCase()) {
            case "success":
                return HttpStatus.OK; // Default for successful operations
            case "created":
                return HttpStatus.CREATED; // For resource creation
            case "error":
                return HttpStatus.BAD_REQUEST; // Default for client errors
            case "unauthorized":
                return HttpStatus.UNAUTHORIZED; // For auth errors
            case "forbidden":
                return HttpStatus.FORBIDDEN; // For permission issues
            case "not_found":
                return HttpStatus.NOT_FOUND; // For resource not found
            case "server_error":
                return HttpStatus.INTERNAL_SERVER_ERROR; // For server-side issues
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR; // Default fallback
        }
    }
}
