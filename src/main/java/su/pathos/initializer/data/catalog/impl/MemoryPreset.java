package su.pathos.initializer.data.catalog.impl;

import java.util.List;
import java.util.stream.IntStream;

public final class MemoryPreset {
    public static final int MIN_GIB = 1;
    public static final int MAX_GIB = 32;
    public static final double DEFAULT_GIB = 4;

    private MemoryPreset() {}


    // to double (for ui)
    // ram is now double
    public static List<Double> values() {
        return IntStream.rangeClosed(MIN_GIB * 2, MAX_GIB * 2).mapToObj(value -> value / 2.0).toList();
    }
}
