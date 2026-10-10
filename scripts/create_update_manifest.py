#!/usr/bin/env python3
"""Create updater metadata for the single universal Auto debug APK."""
import hashlib
import json
import re
import sys
from pathlib import Path

if len(sys.argv) not in (3, 4):
    raise SystemExit('Usage: create_update_manifest.py APK OUTPUT_JSON [repository-lfs]')
versions = Path('gradle/libs.versions.toml').read_text()
apk = Path(sys.argv[1])
version = re.search(r'appVersionName = "([^"]+)"', versions).group(1)
code = int(re.search(r'appVersionCode = "(\d+)"', versions).group(1))
with apk.open('rb') as stream:
    checksum = hashlib.file_digest(stream, 'sha256').hexdigest()
metadata = {
    'distribution': 'repository-lfs' if len(sys.argv) == 4 and sys.argv[3] == 'repository-lfs' else 'release',
    'versionCode': code,
    'versionName': version,
    'packageId': 'me.app.fermataX.auto.debug',
    'minSdkVersion': int(re.search(r'sdkMin = "(\d+)"', versions).group(1)),
    'apkAsset': 'FermataX-yeni-arayuz-debug.apk',
    'sha256': checksum,
    'size': apk.stat().st_size,
}
Path(sys.argv[2]).write_text(json.dumps(metadata, ensure_ascii=False, indent=2) + '\n')
print(f'Created update metadata for {version} ({code})')
