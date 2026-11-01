# Technical analysis

## Evidence

Phone: Redmi Note 12 Turbo (`marble`), HyperOS `3.0.5.0.VMRCNXM` Android 15 China. Headset: REDMI Buds 8 Lite, model identifier `01013A03`.

The user provided their own Bluetooth Extension APK for analysis; **that binary is NOT included in this repo**. The class `com.android.bluetooth.ble.app.headset.BluetoothHeadsetService` performs a membership check against a mutable model list. The observed check failed with:

```text
[checkSupport] info = 01013A03 ...
[checkSupport] headset mSupportDeviceId not support
```

The live `dumpsys activity service` output contained reference models `01011001`, `01013A00` and many others, but did not contain `01013A03`. System cloud cached configuration files did contain the model ID, and hundreds of downloaded WebP graphics existed under `fc_resources/01013A03`; these observations do not imply that the live service's allowlist is populated.

With `setprop log.tag.MiuiFCServiceTest V`, the original native UI appeared and controls actually switched ANC/Transparency. Restoring `INFO` did not immediately hide the panel, likely due to cache or a surviving service; the original debug property was no longer needed once the LSPosed module was enabled.

## Decompiled structure (build-specific)

- APK path: `/system_ext/app/BluetoothExtension/BluetoothExtension.apk`
- Target app: `com.xiaomi.bluetooth`.
- Service class: `com.android.bluetooth.ble.app.headset.BluetoothHeadsetService`.
- Obfuscated list field: `b` (JADX's collision-renamed identifier: `f11050b`). Runtime dump labels it `mSupportDeviceId`.
- Device list init: `onCreate()`.
- Dynamic addition from `MultiFeatureManager.getDeviceInfo()`: method `l2()`.
- Member check follows early validation of reported Bluetooth device ID and features.
- A preexisting vendor debug condition can bypass that membership test; this module never uses it.

## Hook strategy

After `onCreate()`, inspect field `b`; if it is a List with both known reference IDs, append `01013A03` once. After `l2()`, repeat the same idempotent operation. The latter permits handling model-list refreshes; it does not depend on cloud JSON schema.

The checks intentionally fail closed on other OS builds. Reflection exceptions are caught. No override of `checkSupport` return values, no global debugging, no direct writes to settings, DB, firmware or system partitions.

## Validation matrix

| Test | Result |
|---|---|
| System originally lacks model ID in live list | Confirmed |
| Vendor debug flag opens native UI | Confirmed |
| Native UI actually switches ANC and Transparency under debug | Confirmed |
| Initial LSPosed alpha v0.1.0-alpha.1 builds/tests | Confirmed by GitHub Actions |
| Initial module injects ID in live service | **Confirmed**: user saw `01013A03` at end of `dumpsys` list |
| Native controls work after process restart with module | **Confirmed** on device; user reports all tested native functions work |
| All native functions exercised in latest device test | **Reported working** by user; individual test traces were not captured |
| Firmware update / other ROMs / prolonged stability | Not established |

## For maintainers

Treat changes to the obfuscated field or method names as breaking compatibility. Do not assume a loaded image cache guarantees functionality. Do not copy copyrighted OEM APKs, smali or large decompiled source files into public issues. Provide minimal anonymized logs and firmware/build information instead.

## Apply without reboot (v0.1.1-alpha.2)

The extension is a separate Android process and LSPosed hooks only load on process start. Therefore installation/enabling does not necessarily affect an already-running process. The optional launcher Activity allows the user, **after configuring LSPosed scope**, to explicitly request a restart via `su`. Before signalling the PID, the command checks that `/proc/<pid>/cmdline` identifies `com.xiaomi.bluetooth`, and signals **only that extension** with SIGTERM. It avoids `am force-stop` (which can leave an app in a stopped state). This is **not an automatic trigger at package installation**; user permission is required and a full reboot remains a fallback. The user confirmed the new **Apply without reboot** flow works on Redmi Note 12 Turbo / HyperOS 3.0.5.0.VMRCNXM. This validates that process-restart behavior on this device, not other firmware configurations.

## End-to-end verified test (2026-11-01)

Device tester reported the following sequence with the **v0.1.1-alpha.2 artifact**:

1. Set `log.tag.MiuiFCServiceTest` to `INFO`.
2. Disable LSPosed hook and restart the Xiaomi Bluetooth Extension process; `01013A03` no longer appeared in the displayed whitelist check.
3. Enable the module, install and activate the updated APK, using the process restart mechanism without a full system reboot.
4. The subsequent Termux output showed `INFO` and `01013A03` in the live service whitelist.
5. Tester confirmed all the headset functions they exercised operated successfully.

The **no-ID** check used a `grep` pipeline, so a fully printed baseline list was not preserved in the second check; interpret this together with the previously verified baseline list and the successful post-hook dump. **Do not label firmware updates or unrelated devices as tested.**

The v0.1.1 public release intentionally promotes the exact alpha.2 APK to avoid modifying a known-working binary. The APK retains its original internal version and debug signing identity.
