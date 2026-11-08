package su.pathos.initializer.data.catalog.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import su.pathos.initializer.data.catalog.CatalogOption;

import static su.pathos.initializer.data.catalog.impl.MinecraftVersion.*;

public enum WorldType implements CatalogOption {
    NORMAL("normal", "default", V1_2_5),
    FLAT("flat", "flat", V1_2_5),
    LARGE_BIOMES("large_biomes", "largeBiomes", V1_3_1),
    AMPLIFIED("amplified", "amplified", V1_7_2);

    private final String id;
    private final String legacyId;
    private final MinecraftVersion since;

    WorldType(String id, String legacyId, MinecraftVersion since) {
        this.id = id;
        this.legacyId = legacyId;
        this.since = since;
    }

    @Override
    public String id() { return id; }
    public boolean supports(MinecraftVersion version) { return version.isAtLeast(since); }
    public String propertyValue(MinecraftVersion version) { return version.isAtLeast(V1_19) ? "minecraft:" + id : legacyId; }

    @JsonCreator
    public static WorldType fromId(String id) { return CatalogOption.fromId(WorldType.class, id); }
}
