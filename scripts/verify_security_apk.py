#!/usr/bin/env python3
"""Fail publication if legacy privilege/control surfaces re-enter the actual APK."""
import argparse
import hashlib
import json
import re
import subprocess
import zipfile
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('apk', type=Path)
parser.add_argument('--aapt', required=True)
parser.add_argument('--output', type=Path)
args = parser.parse_args()
def dump(*flags):
    return subprocess.check_output([args.aapt, 'dump', *flags, str(args.apk)], text=True)
permissions = dump('permissions')
badging = dump('badging')
manifest = subprocess.check_output([args.aapt, 'dump', 'xmltree', str(args.apk), 'AndroidManifest.xml'], text=True)
forbidden = ['SYSTEM_ALERT_WINDOW', 'MANAGE_EXTERNAL_STORAGE', 'WRITE_SETTINGS',
             'DISABLE_KEYGUARD', 'USE_FULL_SCREEN_INTENT', 'READ_SMS', 'RECEIVE_SMS',
             'READ_CONTACTS', 'READ_CALL_LOG', 'READ_PHONE_STATE', 'QUERY_ALL_PACKAGES',
             'CAMERA', 'ACCESS_FINE_LOCATION', 'ACCESS_COARSE_LOCATION',
             'WRITE_EXTERNAL_STORAGE']
# Allowed on purpose (runtime permissions the user grants): RECORD_AUDIO for voice commands,
# READ_MEDIA_VIDEO/AUDIO and READ_EXTERNAL_STORAGE (max SDK 32) for the Media card.
for name in forbidden:
    assert "name='android.permission." + name + "'" not in permissions, name
assert 'application-debuggable' not in badging, 'APK is debuggable'
assert 'BIND_ACCESSIBILITY_SERVICE' not in manifest
assert 'xposedmodule' not in manifest
assert 'me.app.fermatax.auto.ProjectionActivity' in manifest
assert 'me.app.fermatax.auto.ProjectionService' in manifest
# Mola and Mola 2 use me.app.fermataX.auto.debug; the separate Mola 3 app adds ".mola3".
assert re.search(r"package: name='me\.app\.fermataX\.auto(\.mola3)?\.debug'", badging)
removed = ['AccessibilityEventDispatcherService', 'XposedEventDispatcherService', 'Xposed', 'Su',
           'EventDispatcher', 'MirrorDisplay', 'MirrorActivity', 'MirrorService', 'MirrorServiceFS',
           'LauncherActivity']
with zipfile.ZipFile(args.apk) as apk:
    assert 'assets/xposed_init' not in apk.namelist()
    assert not any('/xml/accessibility.' in name or '/xml/filepaths.' in name for name in apk.namelist())
    for name in apk.namelist():
        if re.fullmatch(r'classes\d*\.dex', name):
            dex = apk.read(name)
            assert b'Lme/aap/utils/security/SecurityUtils$InsecureTrustManager;' not in dex
            assert b'Lde/robv/android/xposed/' not in dex
            for addon in ['audiobook', 'chat', 'cast', 'gdrive']:
                assert ('Lme/aap/fermata/addon/' + addon + '/').encode() not in dex, addon
            assert b'Lme/aap/fermata/addon/web/stremio/' not in dex, 'stremio'
            for cls in removed:
                assert ('Lme/app/fermatax/auto/' + cls + ';').encode() not in dex, cls
    assert 'res/drawable-nodpi-v4/dashboard_reference.jpg' in apk.namelist(), 'Dashboard artwork missing'
with args.apk.open('rb') as stream:
    checksum = hashlib.file_digest(stream, 'sha256').hexdigest()
report = {'apk': args.apk.name, 'sha256': checksum,
          'versionName': re.search(r"versionName='([^']+)'", badging).group(1),
          'versionCode': int(re.search(r"versionCode='(\d+)'", badging).group(1)),
          'forbiddenPermissionsAbsent': forbidden, 'debuggable': False,
          'legacyControlCodePackaged': False, 'nativeScreenCapturePresent': True,
          'scope': 'Static APK checks; not a malware certification or bank/Play Protect approval.'}
if args.output: args.output.write_text(json.dumps(report, indent=2) + '\n')
print(json.dumps(report, indent=2))
