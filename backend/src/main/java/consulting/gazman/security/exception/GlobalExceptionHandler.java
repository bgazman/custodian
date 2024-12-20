package consulting.gazman.security.exception;

import consulting.gazman.security.dto.ApiResponse;
import consulting.gazman.security.utils.ResponseMapper;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import consulting.gazman.security.dto.ErrorResponse;
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ApiResponse<Object> handleUnauthorized(UnauthorizedException ex) {
        return ResponseMapper.unauthorized(ex.getMessage());

    }

    @ExceptionHandler(BadRequestException.class)
    public ApiResponse<Object> handleBadRequest(BadRequestException ex) {
        return ResponseMapper.badRequest(ex.getMessage());

    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Object> handleGeneral(Exception ex) {
        return ResponseMapper.internalServerError(ex.getMessage(),ex);

    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ApiResponse<Object> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return ResponseMapper.badRequest(ex.getMessage());

    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ApiResponse<Object> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
        return ResponseMapper.badRequest(ex.getMessage());
    }
}

