# HyperBudsUnlock

[![Android CI](https://github.com/binxgtl/HyperBudsUnlock/actions/workflows/android.yml/badge.svg)](https://github.com/binxgtl/HyperBudsUnlock/actions/workflows/android.yml)

**Experimental LSPosed module for REDMI Buds 8 Lite (`01013A03`) native headset controls on Xiaomi HyperOS.**

> **Pre-release / untested APK**: The debug-property proof of concept displayed native controls and actually switched ANC/Transparency. The new LSPosed APK has NOT yet been tested on hardware. A passing CI build is not evidence of runtime support.

## Observed bug

On Redmi Note 12 Turbo (`marble`), HyperOS 3.0.5.0.VMRCNXM, Android 15, ROM China:

- Bluetooth identifies REDMI Buds 8 Lite and loads custom imagery.
- Native settings were missing despite the device being paired.
- `BluetoothHeadsetService` logged `headset mSupportDeviceId not support` for model `01013A03`.
- `dumpsys` confirmed model `01013A03` absent from the active runtime whitelist.
- Setting `log.tag.MiuiFCServiceTest=V` made native settings open; ANC and Transparency worked in that diagnostic test.

**This module does NOT enable global debug.** It adds only the missing ID to the expected runtime list, inside `com.xiaomi.bluetooth`.

## Install and verify

1. Install LSPosed on a supported rooted Android device.
2. Download the APK from [Releases](https://github.com/binxgtl/HyperBudsUnlock/releases) or a CI artifact. **This is an installable LSPosed APK, not a Magisk ZIP.**
3. Install the APK, enable **HyperBudsUnlock** in LSPosed, and select **only Xiaomi Bluetooth Extension (`com.xiaomi.bluetooth`)** as scope.
4. Revert the old diagnostic flag and reboot:

   ```sh
   su -c 'setprop log.tag.MiuiFCServiceTest INFO'
   ```

5. Pair/connect REDMI Buds 8 Lite and open its Bluetooth settings. Test ANC and Transparency cautiously; firmware updates are not part of this test.
6. Check LSPosed logs for `[HyperBudsUnlock] Hooks installed` and verify the model is in the running service whitelist:

   ```sh
   su -c 'dumpsys activity service com.xiaomi.bluetooth/com.android.bluetooth.ble.app.headset.BluetoothHeadsetService' |
     grep -F mSupportDeviceId | grep -o 01013A03
   ```

**No launcher icon or module settings screen** is intentionally provided. If the runtime list is inaccessible, a missing grep result alone is inconclusive.

## How it works

- Legacy Xposed API entrypoint, scoped to `com.xiaomi.bluetooth`.
- After-hooks `BluetoothHeadsetService.onCreate()` and `l2()`.
- Reflectively reads the obfuscated list field `b` from the **analyzed** ROM.
- Checks that the object is a List containing known reference IDs `01011001` and `01013A00`.
- Adds only `01013A03` if absent; never replaces a list or bypasses checks for unrelated models.
- Fails closed on unknown firmware, without persistent property or cloud-file changes.

**Compatibility:** The initial target is Redmi Note 12 Turbo / HyperOS 3.0.5.0.VMRCNXM / Android 15. Obfuscation may vary on other builds. Other Redmi/Xiaomi models are not yet validated.

## Rollback

Disable the module in LSPosed, reboot, optionally uninstall its APK. No ROM partition, Xiaomi APK, headset firmware, Bluetooth pairing database, or cloud configuration is modified by this module.

## Build

Requirements: JDK 17, Gradle **8.9**, Android SDK Platform **35**.

   ```sh
   gradle --no-daemon :app:testDebugUnitTest :app:assembleDebug
   ```

APK: `app/build/outputs/apk/debug/app-debug.apk`. Local developer builds need Android SDK setup (e.g., `local.properties` or `ANDROID_HOME`).

The project uses an isolated, **compile-only** Java subproject `xposed-stubs` with the tiny declarations used by this module. It has no runtime implementation and is excluded from the packaged APK; LSPosed supplies the actual framework. This avoids unreliable legacy Xposed Maven endpoints.

## CI / release

[GitHub Actions](.github/workflows/android.yml) runs tests + builds on pushes/PRs and uploads debug APK artifacts. On the **first successful push to main**, it creates a **v0.1.0-alpha.1 pre-release** with the built APK and SHA-256.

**IMPORTANT: Alpha uses ephemeral CI debug signing.** APKs from different CI runs may have different signing keys. Uninstall previous preview builds before installing one from a different run. Production releases will require a securely stored persistent signing key.

## Documentation

- [Technical analysis and limitations](docs/TECHNICAL.md)
- [Troubleshooting](docs/TROUBLESHOOTING.md)
- [Security / privacy](docs/SECURITY.md)
- [First alpha release notes](docs/RELEASE_NOTES.md)

**Privacy:** redact Bluetooth MACs, accounts, identifiers and private logs before filing issues. Do not redistribute proprietary Xiaomi APKs.

## License

MIT; see [LICENSE](LICENSE). Unofficial community project, **not affiliated with Xiaomi/Redmi, LSPosed or OpenAI**.
