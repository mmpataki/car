package car.app.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestController
@RestControllerAdvice
public class ExceptionResolver extends ResponseEntityExceptionHandler {

    @Data
    @AllArgsConstructor
    class ErrorResponse {
        int code = 500;
        String message;
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
        log.error("Exception in API {}", request.getContextPath(), ex);
        return new ResponseEntity<>(new ErrorResponse(500, ex.getMessage()), new HttpHeaders(), 500);
    }
}