package su.pathos.initializer.web.dto;

import su.pathos.initializer.data.catalog.impl.ServerEngine;

import java.util.Map;
import java.util.List;

public record VersionResponse(String id, Map<ServerEngine, Integer> javaVersions, boolean simulationDistanceSupported, boolean hardcoreSupported, boolean spawnProtectionSupported, List<String> worldTypes) {}
