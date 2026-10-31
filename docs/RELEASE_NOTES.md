# v0.1.0-alpha.1 — experimental preview

Initial public LSPosed proof of concept for REDMI Buds 8 Lite, model `01013A03`.

- Only `com.xiaomi.bluetooth` in scope.
- Idempotently inserts the ID into a validated runtime list after `onCreate()` and `l2()`.
- No vendor debug mode, system APK patch, network, analytics or global changes.
- Android unit tests and a GitHub Actions debug APK build.

**No on-device validation of the new LSPosed module yet.** The earlier vendor debug-property experiment showed functional native UI and ANC/Transparency on one ROM; it was a different mechanism.

Preview APK is **CI debug-signed with an ephemeral key**. Uninstall an older build before installing one from another CI run. Do not update headset firmware during experimental tests.
