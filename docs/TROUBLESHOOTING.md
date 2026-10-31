# Troubleshooting

## Panel is still absent

1. Confirm earbuds connected, model ID `01013A03`.
2. LSPosed module enabled, scope **only** `com.xiaomi.bluetooth`; restart device after enabling.
3. Ensure earlier debug override is disabled:

   ```sh
   su -c 'getprop log.tag.MiuiFCServiceTest'
   su -c 'setprop log.tag.MiuiFCServiceTest INFO'
   ```

4. Look for `[HyperBudsUnlock] Hooks installed` in LSPosed logs.
5. Read running list:

   ```sh
   su -c 'dumpsys activity service com.xiaomi.bluetooth/com.android.bluetooth.ble.app.headset.BluetoothHeadsetService' |
     grep -F mSupportDeviceId | grep -o 01013A03
   ```

6. If ID appears but UI does not, another gate exists. Avoid guessing or patching system apps.
7. If hook does not load, check package name, class layout, scope and LSPosed version. Field `b` and method `l2` are obfuscated OEM internals; newer ROMs may differ.

## Bluetooth unstable

Disable module in LSPosed and reboot; if fixed, file a redacted report. Do **not** clear Bluetooth storage, flash ROM components, modify SELinux policy or delete OEM cloud files just to test this module.

## App installation fails with incompatible update signature

The auto-published alpha is **debug-signed with an ephemeral CI key**. For a build from another run, uninstall this module APK first. No headset state is stored in our app.

## AAC video pause lag persists

This module only changes the native headset list gate. It does not modify A2DP buffering, codecs, audio effects or YouTube.

## Report format

Device/ROM build; Android version; `com.xiaomi.bluetooth` path/version; module version; LSPosed variant; whether debug property is OFF; whether the live whitelist includes `01013A03`; sanitized logs. Remove MAC addresses, personal identifiers and secrets.
