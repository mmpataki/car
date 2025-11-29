package car.app.service.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class UnAuthorizedException extends Exception {
    public UnAuthorizedException(String s) {
        super(s);
    }
}
