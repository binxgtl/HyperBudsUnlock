# Troubleshooting

## Panel is still absent

1. Confirm earbuds connected, model ID `01013A03`.
2. LSPosed module enabled, scope **only** `com.xiaomi.bluetooth`; restart device after enabling.
3. Open HyperBudsUnlock → **Apply without reboot**, approve the root request and reopen Bluetooth settings. This is available only after you enable the module and scope in LSPosed. Reboot if your LSPosed variant still requires it.
4. Ensure earlier debug override is disabled:

   ```sh
   su -c 'getprop log.tag.MiuiFCServiceTest'
   su -c 'setprop log.tag.MiuiFCServiceTest INFO'
   ```

5. Look for `[HyperBudsUnlock] Hooks installed` in LSPosed logs.
6. Read running list:

   ```sh
   su -c 'dumpsys activity service com.xiaomi.bluetooth/com.android.bluetooth.ble.app.headset.BluetoothHeadsetService' |
     grep -F mSupportDeviceId | grep -o 01013A03
   ```

7. If ID appears but UI does not, another gate exists. Avoid guessing or patching system apps.
8. If hook does not load, check package name, class layout, scope and LSPosed version. Field `b` and method `l2` are obfuscated OEM internals; newer ROMs may differ.

## Bluetooth unstable

Disable module in LSPosed and restart Xiaomi Bluetooth Extension using Apply (or reboot); if fixed, file a redacted report. Do **not** clear Bluetooth storage, flash ROM components, modify SELinux policy or delete OEM cloud files just to test this module.

## App installation fails with incompatible update signature

The auto-published alpha is **debug-signed with an ephemeral CI key**. For a build from another run, uninstall this module APK first. No headset state is stored in our app.

## AAC video pause lag persists

This module only changes the native headset list gate. It does not modify A2DP buffering, codecs, audio effects or YouTube.

## Report format

Device/ROM build; Android version; `com.xiaomi.bluetooth` path/version; module version; LSPosed variant; whether debug property is OFF; whether the live whitelist includes `01013A03`; sanitized logs. Remove MAC addresses, personal identifiers and secrets.

## Apply without reboot fails

The button is intentionally manual: root cannot safely be assumed at APK installation time and LSPosed scope must be enabled first. If no process is running, open Bluetooth settings and retry. If root is denied, authorize only from your trusted root manager. It sends SIGTERM only to a PID whose `/proc/<pid>/cmdline` matches `com.xiaomi.bluetooth`, never `com.android.bluetooth`, and does not use `am force-stop`. Native controls can briefly disconnect while Android recreates the service. A full reboot remains the recovery path.
