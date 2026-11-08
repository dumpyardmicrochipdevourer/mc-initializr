package su.pathos.initializer.web.dto;

import su.pathos.initializer.data.catalog.impl.ServerEngine;

import java.util.Map;

public record VersionResponse(String id, Map<ServerEngine, Integer> javaVersions) {}
