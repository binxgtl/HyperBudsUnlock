# HyperBudsUnlock

[![Android CI](https://github.com/binxgtl/HyperBudsUnlock/actions/workflows/android.yml/badge.svg)](https://github.com/binxgtl/HyperBudsUnlock/actions/workflows/android.yml)

**Device-tested LSPosed module for REDMI Buds 8 Lite (`01013A03`) native headset controls on Xiaomi HyperOS.**

> **DEVICE-TESTED (2026-11-01)**: The user tested the exact `v0.1.1-alpha.2` APK on Redmi Note 12 Turbo (`marble`), China HyperOS `3.0.5.0.VMRCNXM` / Android 15. With `MiuiFCServiceTest=INFO`, the module successfully inserted `01013A03`; the native panel, ANC and the user's tested functions worked. **Apply without reboot** was also successful. **This is one confirmed device/ROM combination, not universal compatibility or long-term stability testing.**

**[Download device-tested v0.1.1](https://github.com/binxgtl/HyperBudsUnlock/releases/tag/v0.1.1)** — the release promotes the **byte-for-byte identical tested alpha.2 APK**, with SHA-256 verification; it remains **debug-signed** and its Android-internal `versionName` is `0.1.1-alpha.2` to preserve the exact verified build.

## Verified device test

| Item | Result |
|---|---|
| Device | Redmi Note 12 Turbo / `marble` |
| OS | HyperOS `3.0.5.0.VMRCNXM`, Android 15, China ROM |
| Earbuds | REDMI Buds 8 Lite, `01013A03` |
| Debug override | `MiuiFCServiceTest=INFO` |
| Module allowlist injection | **PASS** — `01013A03` appears in live `mSupportDeviceId` |
| Native headset panel | **PASS** |
| ANC and user-tested headset features | **PASS**, reported by device tester |
| Apply without reboot | **PASS** on this device |
| Firmware update / other ROMs / long-term stability | **Not verified** |

**Reproducible A/B test:** Without the active hook after a process restart, `01013A03` was absent. With LSPosed enabled and the extension restarted, the ID returned and the native controls worked. This is a real-world single-device confirmation, not a promise about future Xiaomi firmware.

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
4. Disable the previous diagnostic override:

   ```sh
   su -c 'setprop log.tag.MiuiFCServiceTest INFO'
   ```

5. Open **HyperBudsUnlock** from the app launcher, tap **Apply without reboot**, confirm, and grant root permission. This restarts only the Xiaomi Bluetooth Extension process (SIGTERM). It **does not** change LSPosed scope automatically. When installing for the first time, make sure LSPosed is already enabled and scoped **before** tapping Apply.
6. Reconnect earbuds if necessary and open **Settings → Bluetooth → REDMI Buds 8 Lite**. Native controls and ANC should appear. If LSPosed has not applied the scope, a full reboot remains the fallback.
7. Check LSPosed logs for `[HyperBudsUnlock] Hooks installed` and verify the model is in the running service whitelist:

   ```sh
   su -c 'dumpsys activity service com.xiaomi.bluetooth/com.android.bluetooth.ble.app.headset.BluetoothHeadsetService' |
     grep -F mSupportDeviceId | grep -o 01013A03
   ```

**Why no silent restart on APK installation?** Installing an APK does not grant root or automatically enable an LSPosed scope. Silently killing a privileged Bluetooth process during installation is unreliable and disruptive. The new one-tap Apply flow runs **only after explicit user confirmation** and verifies the exact process name before sending SIGTERM. The extension normally relaunches when Android needs its services; reopen Bluetooth settings if it does not. If the runtime list is inaccessible, a missing grep result alone is inconclusive.

## How it works

- Legacy Xposed API entrypoint, scoped to `com.xiaomi.bluetooth`.
- After-hooks `BluetoothHeadsetService.onCreate()` and `l2()`.
- Reflectively reads the obfuscated list field `b` from the **analyzed** ROM.
- Checks that the object is a List containing known reference IDs `01011001` and `01013A00`.
- Adds only `01013A03` if absent; never replaces a list or bypasses checks for unrelated models.
- Fails closed on unknown firmware, without persistent property or cloud-file changes.

**Compatibility:** The initial target is Redmi Note 12 Turbo / HyperOS 3.0.5.0.VMRCNXM / Android 15. Obfuscation may vary on other builds. Other Redmi/Xiaomi models are not yet validated.

## Rollback

Disable the module in LSPosed, then use **Apply without reboot** to restart the extension while disabled (or reboot), and optionally uninstall the APK. No ROM partition, Xiaomi APK, headset firmware, Bluetooth pairing database, or cloud configuration is modified by this module.

## Build

Requirements: JDK 17, Gradle **8.9**, Android SDK Platform **35**.

   ```sh
   gradle --no-daemon :app:testDebugUnitTest :app:assembleDebug
   ```

APK: `app/build/outputs/apk/debug/app-debug.apk`. Local developer builds need Android SDK setup (e.g., `local.properties` or `ANDROID_HOME`).

The new launcher utility uses a fixed root command only after a confirmation dialog; it never runs automatically during installation. No Android permissions are requested.

The project uses an isolated, **compile-only** Java subproject `xposed-stubs` with the tiny declarations used by this module. It has no runtime implementation and is excluded from the packaged APK; LSPosed supplies the actual framework. This avoids unreliable legacy Xposed Maven endpoints.

## CI / release

[GitHub Actions](.github/workflows/android.yml) runs tests + builds on pushes/PRs and uploads debug APK artifacts. The device-tested **v0.1.1** release promotes the **exact same artifact** as `v0.1.1-alpha.2` with a verified SHA-256, rather than recompiling untested bytes. The separate `publish-tested.yml` workflow performs that promotion. The Android build workflow still runs CI tests and uploads artifacts.

**IMPORTANT: v0.1.1 retains the original alpha APK's debug signing key and Android `versionName`.** It is an identical tested binary, not a freshly built production-signed artifact. Upgrading from the specific `v0.1.1-alpha.2` APK should preserve signing compatibility; other CI builds may use different debug keys and require uninstalling first. Stable production signing remains future work.

## Documentation

- [Technical analysis and limitations](docs/TECHNICAL.md)
- [Troubleshooting](docs/TROUBLESHOOTING.md)
- [Security / privacy](docs/SECURITY.md)
- [Verified v0.1.1 release notes](docs/VERIFIED_RELEASE_NOTES.md)
- [Previous alpha release notes](docs/RELEASE_NOTES.md)

**Privacy:** redact Bluetooth MACs, accounts, identifiers and private logs before filing issues. Do not redistribute proprietary Xiaomi APKs.

## License

MIT; see [LICENSE](LICENSE). Unofficial community project, **not affiliated with Xiaomi/Redmi, LSPosed or OpenAI**.
