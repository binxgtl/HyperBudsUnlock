package io.github.binxgtl.hyperbudsunlock;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.IOException;

/**
 * Explicit, user-approved application of LSPosed changes, without rebooting
 * the entire phone. No root action is executed by installation or in background.
 */
public final class ApplyActivity extends Activity {
    private Button applyButton;
    private TextView statusView;

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView text(String content, int sp, boolean heading) {
        TextView view = new TextView(this);
        view.setText(content);
        view.setTextSize(sp);
        if (heading) view.setTypeface(null, Typeface.BOLD);
        view.setPadding(0, 0, 0, dp(16));
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("HyperBudsUnlock");

        ScrollView scroll = new ScrollView(this);
        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(22), dp(38), dp(22), dp(24));
        scroll.addView(body);

        body.addView(text("HyperBudsUnlock", 26, true));
        body.addView(text("REDMI Buds 8 Lite · 01013A03", 16, true));
        body.addView(text(
                "1. Enable this module in LSPosed.\n"
                + "2. Scope only com.xiaomi.bluetooth.\n"
                + "3. Tap Apply below, approve the root prompt.\n"
                + "4. Reopen Bluetooth settings and reconnect if needed.",
                15, false));
        body.addView(text(
                "This sends SIGTERM only to the verified Xiaomi Bluetooth "
                + "Extension process. Audio or Bluetooth controls can briefly "
                + "disconnect. It does not force-stop the package, reset pairings, "
                + "or restart the whole phone.",
                14, false));

        applyButton = new Button(this);
        applyButton.setText("Apply without reboot");
        applyButton.setAllCaps(false);
        applyButton.setOnClickListener(v -> confirmApply());
        body.addView(applyButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        statusView = text("Status: ready. Root is used only after confirmation.", 14, false);
        body.addView(statusView);
        setContentView(scroll);
    }

    private void confirmApply() {
        new AlertDialog.Builder(this)
                .setTitle("Restart Xiaomi Bluetooth Extension?")
                .setMessage("This temporarily terminates only the Xiaomi Bluetooth "
                        + "Extension process so LSPosed can inject the enabled module "
                        + "when Android starts it again. Enable LSPosed scope first. "
                        + "The process may need you to reopen Bluetooth settings to relaunch.")
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton("Restart extension", (dialog, which) -> restartWithRoot())
                .show();
    }

    private void restartWithRoot() {
        applyButton.setEnabled(false);
        statusView.setText("Requesting root and restarting the extension...");
        new Thread(() -> {
            final String result;
            try {
                Process process = new ProcessBuilder("su", "-c", RestartCommand.shell())
                        .redirectErrorStream(true)
                        .start();
                int exit = process.waitFor();
                if (exit == 0) {
                    result = "Restart signal sent. Open Bluetooth settings again to "
                            + "reactivate the service; then verify the native panel and ANC.";
                } else if (exit == 10 || exit == 11) {
                    result = "Extension process was not found or did not match the expected name. "
                            + "Open Bluetooth settings, then try again. (exit " + exit + ")";
                } else {
                    result = "Root command failed (exit " + exit + "). Check your root manager "
                            + "and grant permission only if you trust this module.";
                }
            } catch (IOException error) {
                result = "Could not start su: " + error.getClass().getSimpleName();
            } catch (InterruptedException error) {
                Thread.currentThread().interrupt();
                result = "Restart interrupted.";
            }
            runOnUiThread(() -> {
                applyButton.setEnabled(true);
                statusView.setText(result);
            });
        }, "HyperBudsUnlock-apply").start();
    }
}
