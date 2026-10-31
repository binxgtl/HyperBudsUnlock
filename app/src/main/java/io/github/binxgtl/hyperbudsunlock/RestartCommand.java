package io.github.binxgtl.hyperbudsunlock;

/**
 * Fixed, non-user-controlled root shell command for a scoped process restart.
 *
 * Avoids "am force-stop" because force-stop could leave the OEM app in the
 * stopped package state, preventing normal service auto-start.
 */
public final class RestartCommand {
    private RestartCommand() {}

    public static String shell() {
        return "PIDS=$(/system/bin/pidof com.xiaomi.bluetooth 2>/dev/null); "
                + "[ -n \"$PIDS\" ] || exit 10; "
                + "RESTARTED=0; "
                + "for PID in $PIDS; do "
                + "case \"$PID\" in ''|*[!0-9]*) continue ;; esac; "
                + "[ -r \"/proc/$PID/cmdline\" ] || continue; "
                + "NAME=$(/system/bin/tr '\\000' '\\n' < \"/proc/$PID/cmdline\" | /system/bin/head -n 1); "
                + "[ \"$NAME\" = \"com.xiaomi.bluetooth\" ] || continue; "
                + "/system/bin/kill -TERM \"$PID\" && RESTARTED=1; "
                + "done; "
                + "[ \"$RESTARTED\" = 1 ] || exit 11";
    }
}
