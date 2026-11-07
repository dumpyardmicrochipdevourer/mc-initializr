package su.pathos.initializer.web.exception;

import org.springframework.http.ResponseEntity;
import su.pathos.initializer.web.exception.dto.ApiErrorResponse;

public interface ApiExceptionHandler<E extends Exception, R extends ApiErrorResponse> {
    ResponseEntity<R> handle(E exception);
}
