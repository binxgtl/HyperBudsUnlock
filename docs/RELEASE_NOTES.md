# v0.1.1-alpha.2 — apply LSPosed without a full reboot

**Confirmed in v0.1.0-alpha.1:** After a process restart with LSPosed enabled, Redmi Buds 8 Lite identifier `01013A03` appeared in the live Xiaomi Bluetooth Extension whitelist, and ANC operated through the native controls.

New in this pre-release:
- Adds a simple launcher screen and **Apply without reboot** button.
- After explicit confirmation and a root grant, terminates **only the verified `com.xiaomi.bluetooth` process** (SIGTERM), permitting LSPosed to apply the hook on process restart. Reopen Bluetooth settings to trigger a restart if needed.
- Does **not** automatically restart on APK install (LSPosed scope/root approval must come first), kill Android's core Bluetooth process, force-stop the package, or persist debugging properties.
- Adds tests for the fixed restart command and documents rollback and failure cases.
- Original whitelist patch remains unchanged.

**Still experimental:** The new one-tap apply UI has not yet been verified on hardware. A full reboot remains a fallback. Changing CI builds may require uninstalling the previous APK because the alpha is signed with an ephemeral GitHub Actions debug key. Avoid experimental firmware updates.
