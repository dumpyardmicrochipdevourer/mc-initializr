#!/usr/bin/env python3
"""Prepare local credentials. Never eval the config or source a dotenv file."""
import getpass
import json
import os
import re
import secrets
import shutil
from pathlib import Path


def prepare():
    config = json.loads(Path('server.json').read_text())
    name = config['name']
    if not re.fullmatch(r'[a-z][a-z0-9-]{0,39}', name):
        raise SystemExit('Invalid server name')
    target = Path('/opt/mc') / name
    for directory in (Path('/opt/mc'), target):
        if directory.is_symlink():
            raise SystemExit(f'Refusing symlink: {directory}')
    os.umask(0o077)
    env = Path('.env')
    if env.is_symlink():
        raise SystemExit('Refusing a symlink for .env')
    # Preserve the credentials of this world when a new ZIP is uploaded.
    if not env.exists() and (target / '.env').is_file():
        shutil.copyfile(target / '.env', env)
    current = env.read_text() if env.exists() else ''
    values = {}
    if not re.search(r'^RCON_PASSWORD=', current, re.M):
        values['RCON_PASSWORD'] = secrets.token_hex(32)
    if config['backup'] == 's3':
        questions = {
            'RESTIC_REPOSITORY': 'S3 repository (s3:https://endpoint/bucket/path)',
            'RESTIC_PASSWORD': 'Restic encryption password (keep a safe copy)',
            'AWS_ACCESS_KEY_ID': 'S3 access key',
            'AWS_SECRET_ACCESS_KEY': 'S3 secret key',
            'AWS_DEFAULT_REGION': 'S3 region (for example us-east-1)',
        }
        for key, prompt in questions.items():
            if re.search(r'^' + key + r'=', current, re.M):
                continue
            value = getpass.getpass(prompt + ': ')
            if not value or any(ord(char) < 32 for char in value):
                raise SystemExit(f'{key} must be a non-empty single line')
            if key == 'RESTIC_REPOSITORY' and not value.startswith('s3:https://'):
                raise SystemExit('Use s3:https://endpoint/bucket/path')
            values[key] = value
    with env.open('a') as stream:
        if current and not current.endswith('\n'):
            stream.write('\n')
        for key, value in values.items():
            encoded = json.dumps(value.replace("$", "$$"), ensure_ascii=False)
            stream.write(f"{key}={encoded}\n")
    env.chmod(0o600)


if __name__ == '__main__':
    prepare()
