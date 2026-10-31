package de.robv.android.xposed;
public final class XposedHelpers {
    private XposedHelpers() {}
    public static Class<?> findClass(String name, ClassLoader loader) {
        throw new UnsupportedOperationException("stub");
    }
    public static Object getObjectField(Object instance, String field) {
        throw new UnsupportedOperationException("stub");
    }
    public static XC_MethodHook.Unhook findAndHookMethod(Class<?> clazz, String name,
                                                          Object... paramsAndCallback) {
        throw new UnsupportedOperationException("stub");
    }
}
