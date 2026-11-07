package su.pathos.initializer.service.catalog;

import org.springframework.stereotype.Service;
import su.pathos.initializer.data.catalog.CatalogOption;
import su.pathos.initializer.data.catalog.ServerDefaults;
import su.pathos.initializer.data.catalog.impl.*;
import su.pathos.initializer.web.dto.CatalogResponse;
import su.pathos.initializer.web.dto.EngineResponse;
import su.pathos.initializer.web.dto.VersionResponse;

import java.util.Arrays;
import java.util.List;

@Service
public class ServerCatalogService {
    public CatalogResponse catalog() {
        return new CatalogResponse(
            Arrays.stream(ServerEngine.values())
                .map(engine -> new EngineResponse(engine.id(), engine.displayName(), engine.description())).toList(),
            ids(MinecraftVersion.values()),
            ids(BackupInterval.values()),
            Arrays.stream(MinecraftVersion.values())
                .map(version -> new VersionResponse(version.id(), version.javaVersion())).toList(),
            ids(BackupDestination.values()),
            ids(Difficulty.values()),
            ids(GameMode.values()),
            Arrays.stream(MemoryPreset.values()).map(MemoryPreset::gib).toList(),
            ServerDefaults.standard()
        );
    }

    private List<String> ids(CatalogOption[] options) {
        return Arrays.stream(options).map(CatalogOption::id).toList();
    }
}
