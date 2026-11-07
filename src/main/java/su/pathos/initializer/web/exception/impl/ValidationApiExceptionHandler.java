package su.pathos.initializer.web.exception.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import su.pathos.initializer.web.exception.ApiExceptionHandler;
import su.pathos.initializer.web.exception.dto.impl.DefaultApiErrorResponse;

import java.util.HashMap;

@Component
public class ValidationApiExceptionHandler implements ApiExceptionHandler<MethodArgumentNotValidException, DefaultApiErrorResponse> {
    @Override
    public ResponseEntity<DefaultApiErrorResponse> handle(MethodArgumentNotValidException exception) {
        var errors = new HashMap<String, String>();
        exception.getBindingResult().getFieldErrors()
            .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(new DefaultApiErrorResponse("Check your server settings.", errors));
    }
}
