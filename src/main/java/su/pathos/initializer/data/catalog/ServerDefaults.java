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
    Difficulty difficulty,
    GameMode gameMode,
    String motd,
    boolean whitelist,
    String whitelistPlayers,
    BackupDestination backup,
    BackupInterval backupInterval,
    int retention,
    boolean eula
) {
    public static ServerDefaults standard() {
        return new ServerDefaults(
            "my-world", ServerEngine.PAPER, MinecraftVersion.V26_1_2,
            MemoryPreset.DEFAULT_GIB, 25565, 20, 10, Difficulty.NORMAL, GameMode.SURVIVAL,
            "A Minecraft server", false, "", BackupDestination.NONE, BackupInterval.DAILY, 7, false
        );
    }
}
