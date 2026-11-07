#!/usr/bin/env bash
set -euo pipefail
cd -- "$(dirname -- "${BASH_SOURCE[0]}")"
if [[ ${EUID} -ne 0 ]]; then
  echo 'Run this on your VPS with sudo bash setup.sh' >&2; exit 1
fi
[[ $(uname -s) == Linux ]] || { echo 'This installer requires Linux.' >&2; exit 1; }
source /etc/os-release
case "$ID:$VERSION_ID" in
  ubuntu:24.04|debian:12|debian:13) ;;
  *) echo 'Supported: Ubuntu 24.04 or Debian 12/13.' >&2; exit 1 ;;
esac
case "${1:-}" in
  --check)
    command -v ansible-playbook >/dev/null || { echo 'Install Ansible first to run --check (no bootstrap is performed).' >&2; exit 1; }
    ANSIBLE_CONFIG="$PWD/ansible/ansible.cfg" ansible-playbook -i localhost, -c local ansible/site.yml --check --diff
    exit
    ;;
  '') ;;
  *) echo 'Usage: sudo bash setup.sh [--check]' >&2; exit 1 ;;
esac
export DEBIAN_FRONTEND=noninteractive
apt-get update
apt-get install -y python3 python3-venv ca-certificates
python3 configure.py
python3 -m venv .venv
.venv/bin/pip install --disable-pip-version-check -r requirements.txt
ANSIBLE_CONFIG="$PWD/ansible/ansible.cfg" .venv/bin/ansible-playbook -i localhost, -c local ansible/site.yml
