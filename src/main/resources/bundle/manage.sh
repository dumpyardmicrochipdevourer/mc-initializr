#!/usr/bin/env bash
set -euo pipefail
cd -- "$(dirname -- "${BASH_SOURCE[0]}")"
[[ ${EUID} -eq 0 ]] || { echo 'Run with sudo bash manage.sh <command>' >&2; exit 1; }
case "${1:-status}" in
  status) docker compose ps ;;
  logs) docker compose logs -f --tail=100 ;;
  start) docker compose up -d --remove-orphans --wait --wait-timeout 900 ;;
  stop) docker compose stop ;;
  restart) docker compose restart minecraft ;;
  console) shift; docker compose exec minecraft rcon-cli "$@" ;;
  backup)
    docker compose config --services | grep -qx backup || { echo 'Backups were not enabled in this bundle.' >&2; exit 1; }
    docker compose exec backup backup now
    ;;
  *) echo 'Commands: status, logs, start, stop, restart, console [command], backup' >&2; exit 1 ;;
esac
