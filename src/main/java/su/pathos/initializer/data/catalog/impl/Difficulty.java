package su.pathos.initializer.data.catalog.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import su.pathos.initializer.data.catalog.CatalogOption;

import java.util.Locale;

public enum Difficulty implements CatalogOption {
    PEACEFUL, EASY, NORMAL, HARD;

    @Override
    public String id() { return name().toLowerCase(Locale.ROOT); }

    @JsonCreator
    public static Difficulty fromId(String id) {
        return CatalogOption.fromId(Difficulty.class, id);
    }
}
