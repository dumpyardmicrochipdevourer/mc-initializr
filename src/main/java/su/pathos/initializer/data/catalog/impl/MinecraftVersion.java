package su.pathos.initializer.data.catalog.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import su.pathos.initializer.data.catalog.CatalogOption;

import java.util.EnumMap;
import java.util.Map;

// piston-meta.mojang.com/mc/game/version_manifest_v2.json
public enum MinecraftVersion implements CatalogOption {
    V26_3("26.3", 25, 25, true),
    V26_2("26.2", 25, 25, true),
    V26_1_2("26.1.2", 25, 25, true),
    V26_1_1("26.1.1", 25, 25, true),
    V26_1("26.1", 25, 0, true),
    V1_21_11("1.21.11", 21, 21, true),
    V1_21_10("1.21.10", 21, 21, true),
    V1_21_9("1.21.9", 21, 21, true),
    V1_21_8("1.21.8", 21, 21, true),
    V1_21_7("1.21.7", 21, 21, true),
    V1_21_6("1.21.6", 21, 21, true),
    V1_21_5("1.21.5", 21, 21, true),
    V1_21_4("1.21.4", 21, 21, true),
    V1_21_3("1.21.3", 21, 21, true),
    V1_21_2("1.21.2", 21, 0, true),
    V1_21_1("1.21.1", 21, 21, true),
    V1_21("1.21", 21, 21, true),
    V1_20_6("1.20.6", 21, 21, true),
    V1_20_5("1.20.5", 21, 21, true),
    V1_20_4("1.20.4", 17, 21, true),
    V1_20_3("1.20.3", 17, 0, true),
    V1_20_2("1.20.2", 17, 21, true),
    V1_20_1("1.20.1", 17, 21, true),
    V1_20("1.20", 17, 21, true),
    V1_19_4("1.19.4", 17, 17, true),
    V1_19_3("1.19.3", 17, 17, true),
    V1_19_2("1.19.2", 17, 17, true),
    V1_19_1("1.19.1", 17, 17, true),
    V1_19("1.19", 17, 17, true),
    V1_18_2("1.18.2", 17, 17, true),
    V1_18_1("1.18.1", 17, 17, true),
    V1_18("1.18", 17, 17, true),
    V1_17_1("1.17.1", 16, 17, true),
    V1_17("1.17", 16, 17, true),
    V1_16_5("1.16.5", 8, 16, true),
    V1_16_4("1.16.4", 8, 11, true),
    V1_16_3("1.16.3", 8, 11, true),
    V1_16_2("1.16.2", 8, 11, true),
    V1_16_1("1.16.1", 8, 11, true),
    V1_16("1.16", 8, 0, true),
    V1_15_2("1.15.2", 8, 11, true),
    V1_15_1("1.15.1", 8, 11, true),
    V1_15("1.15", 8, 11, true),
    V1_14_4("1.14.4", 8, 11, true),
    V1_14_3("1.14.3", 8, 11, true),
    V1_14_2("1.14.2", 8, 11, true),
    V1_14_1("1.14.1", 8, 11, true),
    V1_14("1.14", 8, 11, true),
    V1_13_2("1.13.2", 8, 11, false),
    V1_13_1("1.13.1", 8, 11, false),
    V1_13("1.13", 8, 11, false),
    V1_12_2("1.12.2", 8, 11, false),
    V1_12_1("1.12.1", 8, 11, false),
    V1_12("1.12", 8, 11, false),
    V1_11_2("1.11.2", 8, 8, false),
    V1_11_1("1.11.1", 8, 0, false),
    V1_11("1.11", 8, 0, false),
    V1_10_2("1.10.2", 8, 8, false),
    V1_10_1("1.10.1", 8, 0, false),
    V1_10("1.10", 8, 0, false),
    V1_9_4("1.9.4", 8, 8, false),
    V1_9_3("1.9.3", 8, 0, false),
    V1_9_2("1.9.2", 8, 0, false),
    V1_9_1("1.9.1", 8, 0, false),
    V1_9("1.9", 8, 0, false),
    V1_8_9("1.8.9", 8, 0, false),
    V1_8_8("1.8.8", 8, 8, false),
    V1_8_7("1.8.7", 8, 0, false),
    V1_8_6("1.8.6", 8, 0, false),
    V1_8_5("1.8.5", 8, 0, false),
    V1_8_4("1.8.4", 8, 0, false),
    V1_8_3("1.8.3", 8, 0, false),
    V1_8_2("1.8.2", 8, 0, false),
    V1_8_1("1.8.1", 8, 0, false),
    V1_8("1.8", 8, 0, false),
    V1_7_10("1.7.10", 8, 8, false),
    V1_7_9("1.7.9", 8, 0, false),
    V1_7_8("1.7.8", 8, 0, false),
    V1_7_7("1.7.7", 8, 0, false),
    V1_7_6("1.7.6", 8, 0, false),
    V1_7_5("1.7.5", 8, 0, false),
    V1_7_4("1.7.4", 8, 0, false),
    V1_7_3("1.7.3", 8, 0, false),
    V1_7_2("1.7.2", 8, 0, false),
    V1_6_4("1.6.4", 8, 0, false),
    V1_6_2("1.6.2", 8, 0, false),
    V1_6_1("1.6.1", 8, 0, false),
    V1_5_2("1.5.2", 8, 0, false),
    V1_5_1("1.5.1", 8, 0, false),
    V1_4_7("1.4.7", 8, 0, false),
    V1_4_6("1.4.6", 8, 0, false),
    V1_4_5("1.4.5", 8, 0, false),
    V1_4_4("1.4.4", 8, 0, false),
    V1_4_2("1.4.2", 8, 0, false),
    V1_3_2("1.3.2", 8, 0, false),
    V1_3_1("1.3.1", 8, 0, false),
    V1_2_5("1.2.5", 8, 0, false);

    private final String id;
    private final Map<ServerEngine, Integer> javaVersions;

    MinecraftVersion(String id, int vanillaJava, int paperJava, boolean fabric) {
        this.id = id;
        var runtimes = new EnumMap<ServerEngine, Integer>(ServerEngine.class);
        runtimes.put(ServerEngine.VANILLA, vanillaJava);
        if (paperJava > 0) runtimes.put(ServerEngine.PAPER, paperJava);
        if (fabric) runtimes.put(ServerEngine.FABRIC, vanillaJava);
        javaVersions = Map.copyOf(runtimes);
    }

    @Override
    public String id() { return id; }
    public Map<ServerEngine, Integer> javaVersions() { return javaVersions; }
    public boolean supports(ServerEngine engine) { return javaVersions.containsKey(engine); }

    public int javaVersion(ServerEngine engine) {
        var java = javaVersions.get(engine);
        if (java == null) throw new IllegalArgumentException(engine + " does not support Minecraft " + id);
        return java;
    }

    @JsonCreator
    public static MinecraftVersion fromId(String id) {
        return CatalogOption.fromId(MinecraftVersion.class, id);
    }
}
