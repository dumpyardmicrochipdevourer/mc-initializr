# mc initializr

Minecraft server configuration generator inspired by spring initializr (https://start.spring.io/).
Choose settings, preview the files and download a ZIP for installation on your VPS.

java 21+

## Generated server

Paper, Vanilla or Fabric with configurable memory, port, player limit, whitelist
and optional local or S3 backups. The ZIP includes Compose, Ansible, installation
and management scripts, and a README with recovery instructions.

Extract it on Ubuntu 24.04 or Debian 12/13 and run `sudo bash setup.sh`.
Server data is stored in `/opt/mc/<name>`. S3 credentials are entered on the VPS.

[MIT](LICENSE).
