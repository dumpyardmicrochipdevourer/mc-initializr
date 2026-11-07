package su.pathos.initializer.data.catalog.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import su.pathos.initializer.data.catalog.CatalogOption;

public enum MinecraftVersion implements CatalogOption {
    V26_1_2("26.1.2", 25),
    V1_21_11("1.21.11", 21),
    V1_21_8("1.21.8", 21);

    private final String id;
    private final int javaVersion;

    MinecraftVersion(String id, int javaVersion) {
        this.id = id;
        this.javaVersion = javaVersion;
    }

    @Override
    public String id() { return id; }
    public int javaVersion() { return javaVersion; }

    @JsonCreator
    public static MinecraftVersion fromId(String id) {
        return CatalogOption.fromId(MinecraftVersion.class, id);
    }
}
