# Technical analysis

## Evidence

Phone: Redmi Note 12 Turbo (`marble`), HyperOS `3.0.5.0.VMRCNXM` Android 15 China. Headset: REDMI Buds 8 Lite, model identifier `01013A03`.

The user provided their own Bluetooth Extension APK for analysis; **that binary is NOT included in this repo**. The class `com.android.bluetooth.ble.app.headset.BluetoothHeadsetService` performs a membership check against a mutable model list. The observed check failed with:

```text
[checkSupport] info = 01013A03 ...
[checkSupport] headset mSupportDeviceId not support
```

The live `dumpsys activity service` output contained reference models `01011001`, `01013A00` and many others, but did not contain `01013A03`. System cloud cached configuration files did contain the model ID, and hundreds of downloaded WebP graphics existed under `fc_resources/01013A03`; these observations do not imply that the live service's allowlist is populated.

With `setprop log.tag.MiuiFCServiceTest V`, the original native UI appeared and controls actually switched ANC/Transparency. Restoring `INFO` did not immediately hide the panel, likely due to cache or a surviving service; a full clean reboot test with the LSPosed module is still pending.

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
| New LSPosed module compiles and unit tests pass | CI to determine |
| Module injects ID in live service | **Not yet device verified** |
| Native controls work after clean reboot, debug flag OFF, module ON | **Not yet device verified** |
| Low latency / gestures / firmware update | Not established |

## For maintainers

Treat changes to the obfuscated field or method names as breaking compatibility. Do not assume a loaded image cache guarantees functionality. Do not copy copyrighted OEM APKs, smali or large decompiled source files into public issues. Provide minimal anonymized logs and firmware/build information instead.
