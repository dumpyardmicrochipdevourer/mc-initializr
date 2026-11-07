package su.pathos.initializer.data.catalog.impl;

public enum MemoryPreset {
    GIB_2(2), GIB_4(4), GIB_6(6), GIB_8(8), GIB_12(12), GIB_16(16), GIB_32(32);

    private final int gib;

    MemoryPreset(int gib) { this.gib = gib; }

    public int gib() { return gib; }
}
