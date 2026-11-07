package su.pathos.initializer.data.catalog.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import su.pathos.initializer.data.catalog.CatalogOption;

public enum BackupInterval implements CatalogOption {
    HOURLY(1), EVERY_SIX_HOURS(6), EVERY_TWELVE_HOURS(12), DAILY(24);

    private final int hours;

    BackupInterval(int hours) { this.hours = hours; }

    @Override
    public String id() { return hours + "h"; }
    public int hours() { return hours; }

    @JsonCreator
    public static BackupInterval fromId(String id) {
        return CatalogOption.fromId(BackupInterval.class, id);
    }
}
