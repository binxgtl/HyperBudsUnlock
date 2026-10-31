package io.github.binxgtl.hyperbudsunlock;

import java.util.List;

/** Narrow, idempotent patch of a verified Xiaomi headset ID allowlist. */
public final class WhitelistPatcher {
    static final String TARGET = "01013A03";
    static final String SENTINEL_A = "01011001";
    static final String SENTINEL_B = "01013A00";

    private WhitelistPatcher() {}

    /** Returns true iff the model was inserted; incompatible lists are unchanged. */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static boolean addIfCompatible(Object value) {
        if (!(value instanceof List)) return false;
        List list = (List) value;
        synchronized (list) {
            if (!list.contains(SENTINEL_A) || !list.contains(SENTINEL_B)) return false;
            if (list.contains(TARGET)) return false;
            return list.add(TARGET);
        }
    }
}
