package de.robv.android.xposed;

/** Build-time-only declaration, excluded by compileOnly. */
public abstract class XC_MethodHook {
    protected XC_MethodHook() {}
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {}
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {}
    public static class MethodHookParam { public Object thisObject; }
    public class Unhook {}
}
