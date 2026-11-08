package su.pathos.initializer.data.catalog;

import su.pathos.initializer.data.catalog.impl.*;

public record ServerDefaults(
    String name,
    ServerEngine engine,
    MinecraftVersion version,
    // ram is now double so we can get 1.5 ... etc
    double memory,
    int port,
    int players,
    int viewDistance,
    int simulationDistance,
    int spawnProtection,
    Difficulty difficulty,
    GameMode gameMode,
    String motd,
    String seed,
    WorldType worldType,
    boolean pvp,
    boolean hardcore,
    boolean whitelist,
    String whitelistPlayers,
    String operators,
    BackupDestination backup,
    BackupInterval backupInterval,
    int retention,
    boolean eula
) {
    public static ServerDefaults standard() {
        return new ServerDefaults(
            "my-world", ServerEngine.PAPER, MinecraftVersion.V26_1_2,
            MemoryPreset.DEFAULT_GIB, 25565, 20, 10, 10, 16, Difficulty.NORMAL, GameMode.SURVIVAL,
            "A Minecraft server", "", WorldType.NORMAL, true, false, false, "", "", BackupDestination.NONE, BackupInterval.DAILY, 7, false
        );
    }
}
