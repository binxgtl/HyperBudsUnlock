package io.github.binxgtl.hyperbudsunlock;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/** Legacy LSPosed entrypoint. No hooks outside the Xiaomi Bluetooth package. */
public final class ModuleEntry implements IXposedHookLoadPackage {
    private static final String PKG = "com.xiaomi.bluetooth";
    private static final String SERVICE =
            "com.android.bluetooth.ble.app.headset.BluetoothHeadsetService";
    private static final String TAG = "[HyperBudsUnlock] ";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!PKG.equals(lpparam.packageName)) return;
        try {
            Class<?> service = XposedHelpers.findClass(SERVICE, lpparam.classLoader);
            XC_MethodHook after = new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    try {
                        // Actual obfuscated field name in analyzed Android 15 ROM: b.
                        Object ids = XposedHelpers.getObjectField(param.thisObject, "b");
                        if (WhitelistPatcher.addIfCompatible(ids)) {
                            XposedBridge.log(TAG + "Inserted 01013A03 into runtime whitelist");
                        }
                    } catch (Throwable ignored) {
                        // Fail closed: an OEM update must not crash Bluetooth.
                        XposedBridge.log(TAG + "Hook skipped (field/layout incompatible)");
                    }
                }
            };
            XposedHelpers.findAndHookMethod(service, "onCreate", after);
            XposedHelpers.findAndHookMethod(service, "l2", after);
            XposedBridge.log(TAG + "Hooks installed in " + PKG);
        } catch (Throwable ignored) {
            XposedBridge.log(TAG + "Could not install hooks; unsupported ROM or class layout");
        }
    }
}
