package su.pathos.initializer.data.catalog.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import su.pathos.initializer.data.catalog.CatalogOption;

public enum ServerEngine implements CatalogOption {
    PAPER("Paper", "Plugins & performance"),
    VANILLA("Vanilla", "The original experience"),
    FABRIC("Fabric", "A foundation for mods");

    private final String displayName;
    private final String description;

    ServerEngine(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    @Override
    public String id() { return name(); }
    public String displayName() { return displayName; }
    public String description() { return description; }

    @JsonCreator
    public static ServerEngine fromId(String id) {
        return CatalogOption.fromId(ServerEngine.class, id);
    }
}
