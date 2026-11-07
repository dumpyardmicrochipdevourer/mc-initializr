package su.pathos.initializer.service.bundle.files.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import su.pathos.initializer.cfg.ServerConfig;
import su.pathos.initializer.data.catalog.impl.BackupDestination;
import su.pathos.initializer.service.bundle.files.BundleFileGenerator;

import java.util.Map;

@Component
@Order(0)
public class ComposeFileGenerator implements BundleFileGenerator {
    @Override
    public Map<String, String> generate(ServerConfig config) {
        return Map.of("compose.yaml", compose(config));
    }

    private static String quote(String value) {
        return "'" + value.replace("'", "''").replace("$", "$$") + "'";
    }

    private String compose(ServerConfig config) {
        var text = """
                name: mc-%s
                services:
                  minecraft:
                    image: itzg/minecraft-server:java%d
                    restart: unless-stopped
                    stdin_open: true
                    tty: true
                    stop_grace_period: 2m
                    ports:
                      - '%d:25565'
                    environment:
                      EULA: 'TRUE'
                      TYPE: %s
                      VERSION: %s
                      MEMORY: '%dG'
                      MAX_PLAYERS: '%d'
                      VIEW_DISTANCE: '%d'
                      DIFFICULTY: %s
                      MODE: %s
                      MOTD: %s
                      ONLINE_MODE: 'true'
                      ENABLE_RCON: 'true'
                      RCON_PASSWORD: ${RCON_PASSWORD:?Run sudo bash setup.sh first}
                      ENABLE_WHITELIST: '%s'
                      ENFORCE_WHITELIST: '%s'
                      WHITELIST: %s
                      EXISTING_WHITELIST_FILE: 'SYNCHRONIZE'
                    volumes:
                      - ./data:/data
                    healthcheck:
                      test: ['CMD', 'mc-health']
                      interval: 30s
                      timeout: 10s
                      retries: 5
                      start_period: 5m
                    logging:
                      driver: json-file
                      options:
                        max-size: '10m'
                        max-file: '3'
                """.formatted(
                    config.name, config.javaVersion(), config.port, quote(config.engine.id()), quote(config.version.id()),
                    config.memory, config.players, config.viewDistance, quote(config.difficulty.id()), quote(config.gameMode.id()),
                    quote(config.motd), config.whitelist, config.whitelist, quote(config.whitelist ? config.whitelistPlayers : "")
                );
        if (config.backup == BackupDestination.NONE) return text;
        text += """
                  backup:
                    image: itzg/mc-backup:latest
                    restart: unless-stopped
                    depends_on:
                      minecraft:
                        condition: service_healthy
                    environment:
                      RCON_HOST: minecraft
                      RCON_PASSWORD: ${RCON_PASSWORD:?Run sudo bash setup.sh first}
                      BACKUP_INTERVAL: %s
                      BACKUP_NAME: %s
                      INITIAL_DELAY: 2m
                      EXCLUDES: 'cache,logs,*.tmp'
                """.formatted(quote(config.backupInterval.id()), quote(config.name));
        if (config.backup == BackupDestination.S3) text += """
                      BACKUP_METHOD: restic
                      RESTIC_HOST: %s
                      PRUNE_RESTIC_RETENTION: '--keep-within %dd'
                      RESTIC_REPOSITORY: ${RESTIC_REPOSITORY:?Set the S3 repository in .env}
                      RESTIC_PASSWORD: ${RESTIC_PASSWORD:?Set the repository password in .env}
                      AWS_ACCESS_KEY_ID: ${AWS_ACCESS_KEY_ID:?Set S3 credentials in .env}
                      AWS_SECRET_ACCESS_KEY: ${AWS_SECRET_ACCESS_KEY:?Set S3 credentials in .env}
                      AWS_DEFAULT_REGION: ${AWS_DEFAULT_REGION:-us-east-1}
                """.formatted(quote(config.name), config.retention);
        else text += """
                      BACKUP_METHOD: tar
                      PRUNE_BACKUPS_DAYS: '%d'
                """.formatted(config.retention);
        text += """
                    volumes:
                      - ./data:/data:ro
                      - ./backups:/backups
                    logging:
                      driver: json-file
                      options:
                        max-size: '10m'
                        max-file: '3'
                """;
        return text;
    }
}
