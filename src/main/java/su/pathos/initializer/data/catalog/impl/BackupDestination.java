package su.pathos.initializer.data.catalog.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import su.pathos.initializer.data.catalog.CatalogOption;

import java.util.Locale;

public enum BackupDestination implements CatalogOption {
    NONE, LOCAL, S3;

    @Override
    public String id() { return name().toLowerCase(Locale.ROOT); }

    @JsonCreator
    public static BackupDestination fromId(String id) {
        return CatalogOption.fromId(BackupDestination.class, id);
    }
}
