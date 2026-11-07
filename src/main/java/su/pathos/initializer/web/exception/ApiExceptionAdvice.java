package su.pathos.initializer.web.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import su.pathos.initializer.web.exception.dto.impl.DefaultApiErrorResponse;

@RestControllerAdvice
public class ApiExceptionAdvice {
    private final ApiExceptionHandler<MethodArgumentNotValidException, DefaultApiErrorResponse> validation;
    private final ApiExceptionHandler<HttpMessageNotReadableException, DefaultApiErrorResponse> malformed;

    public ApiExceptionAdvice(
            ApiExceptionHandler<MethodArgumentNotValidException, DefaultApiErrorResponse> validation,
            ApiExceptionHandler<HttpMessageNotReadableException, DefaultApiErrorResponse> malformed) {
        this.validation = validation;
        this.malformed = malformed;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DefaultApiErrorResponse> validation(MethodArgumentNotValidException exception) {
        return validation.handle(exception);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DefaultApiErrorResponse> malformed(HttpMessageNotReadableException exception) {
        return malformed.handle(exception);
    }
}
