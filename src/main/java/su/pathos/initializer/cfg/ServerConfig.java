package su.pathos.initializer.cfg;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import jakarta.validation.groups.Default;
import su.pathos.initializer.data.catalog.ServerDefaults;
import su.pathos.initializer.data.catalog.impl.*;

public class ServerConfig {
    private static final ServerDefaults DEFAULTS = ServerDefaults.standard();
    public interface Generation extends Default {}

    @NotNull @Pattern(regexp = "[a-z][a-z0-9-]{0,39}", message = "Use 1–40 lowercase letters, numbers or hyphens; start with a letter")
    public String name = DEFAULTS.name();
    @NotNull public ServerEngine engine = DEFAULTS.engine();
    @NotNull public MinecraftVersion version = DEFAULTS.version();
    // just min/max for double values
    @DecimalMin("1") @DecimalMax("32") public double memory = DEFAULTS.memory();
    @Min(1024) @Max(65535) public int port = DEFAULTS.port();
    @Min(1) @Max(200) public int players = DEFAULTS.players();
    @Min(2) @Max(24) public int viewDistance = DEFAULTS.viewDistance();
    @NotNull public Difficulty difficulty = DEFAULTS.difficulty();
    @NotNull public GameMode gameMode = DEFAULTS.gameMode();
    @NotNull @Size(max=120) @Pattern(regexp = "[^\\p{Cntrl}]*", message = "Use a single line without control characters")
    public String motd = DEFAULTS.motd();
    public boolean whitelist = DEFAULTS.whitelist();
    @NotNull @Pattern(regexp = "(?:[A-Za-z0-9_]{3,16})(?:,[A-Za-z0-9_]{3,16})*|", message = "Use comma-separated Minecraft usernames")
    @Size(max=1000) public String whitelistPlayers = DEFAULTS.whitelistPlayers();
    @NotNull public BackupDestination backup = DEFAULTS.backup();
    @NotNull public BackupInterval backupInterval = DEFAULTS.backupInterval();
    @Min(1) @Max(90) public int retention = DEFAULTS.retention();
    @AssertTrue(groups = Generation.class, message = "Accept the Minecraft EULA to generate a server") public boolean eula = DEFAULTS.eula();

    @AssertTrue(message = "Add at least one username when enabling the whitelist")
    @JsonIgnore
    public boolean isWhitelistConfigured() { return !whitelist || (whitelistPlayers != null && !whitelistPlayers.isBlank()); }

    @AssertTrue(message = "The selected engine does not support this Minecraft version")
    @JsonIgnore
    public boolean isVersionSupported() { return engine == null || version == null || version.supports(engine); }

    public int javaVersion() { return version.javaVersion(engine); }
}
