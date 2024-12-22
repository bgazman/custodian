package consulting.gazman.security.exception;


public class InputValidationException extends RuntimeException {
    public InputValidationException(String message) {
        super(message);
    }
}