# @@NAME@@ mc-initializr

A standalone Minecraft Java server.
Backup destination: **@@BACKUP@@**.

## Install on your VPS

Use a fresh Ubuntu 24.04 or Debian 12/13 VPS (amd64 or arm64),
at least @@MEMORY@@ GiB RAM and enough disk space for worlds and backups.
Upload and extract this entire directory onto the VPS, then run inside it:

```sh
sudo bash setup.sh
```

The script installs Python, Ansible (in a local virtualenv), Docker (+ compose).
Ansible copies the configuration to `/opt/mc/@@NAME@@`, starts containers,
and waits up to 15 minutes for Minecraft to become healthy. Keep the installer
folder outside `/opt/mc/@@NAME@@`. Internet connection is required to download packages,
images, Minecraft and the selected engine. Binaries are not bundled.

Connect with a matching Minecraft Java client to `YOUR_VPS_IP:@@PORT@@`.
Allow **TCP @@PORT@@** in your provider's firewall. Docker-published ports can
bypass UFW; use your provider firewall or the DOCKER-USER chain. RCON has no host
port. Online mode stays enabled!!. This installer does not alter SSH or firewall rules.

## Manage

```sh
cd /opt/mc/@@NAME@@
sudo bash manage.sh status
sudo bash manage.sh logs
sudo bash manage.sh console list
sudo bash manage.sh backup
sudo bash manage.sh stop
sudo bash manage.sh start
```

Worlds, plugins and mods persist in `data/`. Put compatible JARs in
`data/plugins/` (Paper-core) or `data/mods/` (Fabric-core), then restart (or reload in case of plugins).
No plugins/mods are preinstalled. The image downloads an engine build for the selected version;
image tags and engine builds are mutable. Pin image digests/builds yourself if repeatable installations are required.

## Backups and recovery

When enabled, the mc-backup sidecar flushes and pauses saves via RCON, copies
world data, then resumes saves. The interval and retention are in `compose.yaml`.

(Backups include plugins and mods; logs/cache are excluded!!)

- `local`: compressed archives in `/opt/mc/@@NAME@@/backups/`.
  This is the same disk as the world: copy them off the VPS as well.
- `s3`: encrypted Restic snapshots in your own S3-compatible bucket.
  The installer asks for an existing bucket/repository URL, access key, secret,
  region and encryption password on the VPS. The sidecar initializes a new
  repository automatically. Keep the password outside the VPS: losing it makes
  backups unrecoverable. Secrets are stored in root-only `.env`, not in the ZIP.
- `none`: no scheduled backup. `manage.sh backup` reports that it is disabled.

Local restore: stop all services with `sudo docker compose stop`, keep a copy
of `data/`, inspect the archive with `tar -tzf backups/ARCHIVE.tgz`, and extract a
trusted archive into an empty `data/` directory. Restart with `manage.sh start`.
S3 restore: stop all services, use Restic with the `.env` repository credentials
and `restic snapshots`, then restore the chosen snapshot to a temporary folder
with `restic restore SNAPSHOT --target /tmp/world-restore`. Inspect its `/data`
contents and copy them into an empty server `data/` directory before starting.
Test recovery before relying on any backup. Retention removes old backups.

## Reconfigure

Generate a new bundle with the **same name**, upload it to a separate installer
folder and rerun `setup.sh`. The existing world and credentials are preserved.
Changing the name creates a separate server; choose a free port. Take a backup
before changing versions: downgrading a world may corrupt it. Remove stale S3
keys manually from `.env` if you turn off offsite backups. The next deploy removes old backup sidecars automatically when backups are
disabled. Existing backup files remain on disk.

For an Ansible dry-run with Ansible already installed:
`sudo bash setup.sh --check`. It does not bootstrap dependencies or start
containers, and cannot replace a real deployment test.

You accepted https://aka.ms/MinecraftEULA when generating this bundle.
Minecraft and engine binaries keep their own licenses.
