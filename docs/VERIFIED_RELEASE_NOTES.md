# HyperBudsUnlock v0.1.1 — DEVICE-TESTED

**Tested on real hardware: 2026-11-01.**

This release promotes **exactly the same APK bytes** from `v0.1.1-alpha.2`; it is not rebuilt. The device tester reports that the native headset panel, ANC and all other headset features exercised work after applying the LSPosed module **without rebooting the entire phone**.

## Verified configuration

| Item | Verified |
|---|---|
| Phone | Redmi Note 12 Turbo (`marble`) |
| ROM | China HyperOS `3.0.5.0.VMRCNXM` |
| Android | 15 |
| Earbuds | REDMI Buds 8 Lite, model `01013A03` |
| LSPosed scope | `com.xiaomi.bluetooth` |
| Debug override | `log.tag.MiuiFCServiceTest=INFO` |
| Runtime whitelist after Apply | `01013A03` **present** |
| Native Bluetooth headset interface | **Working** |
| ANC and tested headset controls | **Working**, per device tester |
| `Apply without reboot` | **Working**, per device tester |

The tester also performed a negative control: disabling the LSPosed hook and restarting the extension removed the model from the queried whitelist, while re-enabling/applying restored the ID and working controls.

## What the module does

- Inserts only `01013A03` into the live `mSupportDeviceId` list of Xiaomi Bluetooth Extension.
- Provides a manual **Apply without reboot** button which, with explicit root approval, restarts only the verified `com.xiaomi.bluetooth` process.
- Does **not** flip the vendor debug override, patch system APKs, flash firmware, clear Bluetooth data or alter unrelated device IDs.

## Binary integrity and signing

The release pipeline downloads the exact APK from `v0.1.1-alpha.2`, verifies its SHA-256 against the existing published checksum, and republishes the unchanged binary as `HyperBudsUnlock-v0.1.1-device-tested.apk`. Its **internal Android `versionName` remains `0.1.1-alpha.2`**. It is **debug-signed by GitHub Actions**; it is not production-signed. This ensures the shipped bytes are precisely those validated by the device tester, without re-signing or recompilation.

Existing users who already installed the exact alpha.2 APK **do not need to reinstall**. Other independently debug-signed builds may require uninstalling first due to certificate mismatch.

## Scope of verification

**Single device and ROM only.** Other phones, OS versions, long-term system stability, firmware updates, microphone quality, and unexercised features are **not** independently verified. Use on other devices at your own risk. This is an unofficial community module.

See [README](../README.md), [technical analysis](TECHNICAL.md), and [security](SECURITY.md).
