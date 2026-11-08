package su.pathos.initializer.web.dto;

import su.pathos.initializer.data.catalog.ServerDefaults;

import java.util.List;

public record CatalogResponse(
    List<EngineResponse> engines,
    List<String> versions,
    List<String> backupIntervals,
    List<VersionResponse> versionDetails,
    List<String> backupDestinations,
    List<String> difficulties,
    List<String> gameModes,
    List<Double> memoryOptions,
    ServerDefaults defaults
) {}
