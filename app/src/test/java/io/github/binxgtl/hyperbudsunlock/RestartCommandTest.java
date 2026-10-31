package io.github.binxgtl.hyperbudsunlock;

import org.junit.Test;
import static org.junit.Assert.*;

public class RestartCommandTest {
    @Test public void targetsOnlyExtensionProcess() {
        String s = RestartCommand.shell();
        assertTrue(s.contains("/system/bin/pidof com.xiaomi.bluetooth"));
        assertTrue(s.contains("\"$NAME\" = \"com.xiaomi.bluetooth\""));
        assertTrue(s.contains("/system/bin/kill -TERM \"$PID\""));
        assertFalse(s.contains("am force-stop"));
        assertFalse(s.contains("com.android.bluetooth"));
    }

    @Test public void neverTogglesDebugOrResetsData() {
        String s = RestartCommand.shell();
        assertFalse(s.contains("setprop"));
        assertFalse(s.contains("pm clear"));
        assertFalse(s.contains("rm -"));
        assertFalse(s.contains("reboot"));
    }
}
