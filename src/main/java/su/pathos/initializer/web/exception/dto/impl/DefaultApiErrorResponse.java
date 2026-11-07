package su.pathos.initializer.web.exception.dto.impl;

import su.pathos.initializer.web.exception.dto.ApiErrorResponse;

import java.util.Map;

public record DefaultApiErrorResponse(String message, Map<String, String> errors) implements ApiErrorResponse {}
