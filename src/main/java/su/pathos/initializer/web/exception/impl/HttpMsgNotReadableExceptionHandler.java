package su.pathos.initializer.web.exception.impl;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import su.pathos.initializer.web.exception.ApiExceptionHandler;
import su.pathos.initializer.web.exception.dto.impl.DefaultApiErrorResponse;

import java.util.Map;

@Component
public class HttpMsgNotReadableExceptionHandler implements ApiExceptionHandler<HttpMessageNotReadableException, DefaultApiErrorResponse> {
    @Override
    public ResponseEntity<DefaultApiErrorResponse> handle(HttpMessageNotReadableException exception) {
        Map<String, String> errors = Map.of();
        for (Throwable cause = exception.getCause(); cause != null; cause = cause.getCause()) {
            if (cause instanceof JsonMappingException mapping && !mapping.getPath().isEmpty()) {
                String field = mapping.getPath().getFirst().getFieldName();
                if (field != null) {
                    errors = Map.of(field, "Invalid value.");
                    break;
                }
            }
        }
        return ResponseEntity.badRequest().body(
                new DefaultApiErrorResponse("Send a valid server configuration as JSON.", errors)
        );
    }
}
