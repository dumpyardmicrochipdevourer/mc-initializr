package su.pathos.initializer.web.exception.dto;

import java.util.Map;

public interface ApiErrorResponse {
    String message();
    Map<String, String> errors();
}
