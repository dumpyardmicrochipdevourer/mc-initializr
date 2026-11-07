package su.pathos.initializer.data.catalog;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public interface CatalogOption {
    @JsonValue
    String id();

    static <E extends Enum<E> & CatalogOption> E fromId(Class<E> type, String id) {
        return Arrays.stream(type.getEnumConstants())
            .filter(option -> option.id().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unsupported " + type.getSimpleName() + ": " + id));
    }
}
