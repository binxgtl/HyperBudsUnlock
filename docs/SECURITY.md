# Security and privacy

- Local, narrow hook inside `com.xiaomi.bluetooth` only.
- Adds exactly one allowlist item (`01013A03`) after compatibility checks.
- Does not use Internet permission, telemetry, shell commands, OTA, external files, databases, system settings, firmware updates or persistent debug flags.
- Framework APIs are compile-only and must not be packed into the module APK.
- Xiaomi may change private fields/methods. Hook failure should leave Bluetooth behavior unchanged, but testing in a privileged OEM process inherently has risk.
- Uninstall/revert: disable in LSPosed and reboot.

## Responsible public reporting

Never upload proprietary BluetoothExtension APKs, private Qigsaw payloads, credentials, Bluetooth MACs, device serials, account identifiers or unredacted `logcat`. Use short anonymized excerpts and version info.

## Signing

The first alpha release is GitHub-Actions **debug-signed** and uses an ephemeral key. This is not a production signing solution. Configure a stable private signing key as a GitHub secret for future releases; never commit a keystore/password.
