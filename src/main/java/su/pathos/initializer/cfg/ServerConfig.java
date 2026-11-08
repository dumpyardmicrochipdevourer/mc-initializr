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
    @Min(3) @Max(32) public int simulationDistance = DEFAULTS.simulationDistance();
    @Min(0) @Max(1024) public int spawnProtection = DEFAULTS.spawnProtection();
    @NotNull public Difficulty difficulty = DEFAULTS.difficulty();
    @NotNull public GameMode gameMode = DEFAULTS.gameMode();
    @NotNull @Size(max=120) @Pattern(regexp = "[^\\p{Cntrl}]*", message = "Use a single line without control characters")
    public String motd = DEFAULTS.motd();
    @NotNull @Size(max=128) @Pattern(regexp = "[^\\p{Cntrl}]*", message = "Use a single line without control characters")
    public String seed = DEFAULTS.seed();
    @NotNull public WorldType worldType = DEFAULTS.worldType();
    public boolean pvp = DEFAULTS.pvp();
    public boolean hardcore = DEFAULTS.hardcore();
    public boolean whitelist = DEFAULTS.whitelist();
    @NotNull @Pattern(regexp = "(?:[A-Za-z0-9_]{3,16})(?:,[A-Za-z0-9_]{3,16})*|", message = "Use comma-separated Minecraft usernames")
    @Size(max=1000) public String whitelistPlayers = DEFAULTS.whitelistPlayers();
    @NotNull @Size(max=1000)
    @Pattern(regexp = "(?:[A-Za-z0-9_]{3,16})(?:,[A-Za-z0-9_]{3,16})*|", message = "Use comma-separated Minecraft usernames")
    public String operators = DEFAULTS.operators();
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

    @AssertTrue(message = "The selected world type is not available in this Minecraft version")
    @JsonIgnore
    public boolean isWorldTypeSupported() { return worldType == null || version == null || worldType.supports(version); }

    @AssertTrue(message = "Hardcore requires Minecraft 1.3.1 or newer")
    @JsonIgnore
    public boolean isHardcoreSupported() { return !hardcore || version == null || version.supportsHardcore(); }

    public int javaVersion() { return version.javaVersion(engine); }
}
