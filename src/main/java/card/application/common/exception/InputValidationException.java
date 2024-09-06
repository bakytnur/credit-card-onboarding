package card.application.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InputValidationException extends RuntimeException {
    public InputValidationException(String message) {
        super(message);
    }
}
