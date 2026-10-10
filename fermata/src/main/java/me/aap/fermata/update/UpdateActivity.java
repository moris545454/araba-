package me.aap.fermata.update;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import me.aap.fermata.BuildConfig;
import me.aap.fermata.R;

/** Phone-only update screen. Installation always goes through Android's confirmation UI. */
public final class UpdateActivity extends AppCompatActivity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private TextView status;
    private MaterialButton install;
    private MaterialButton check;
    private UpdateManifest available;
    private File verifiedApk;
    private boolean waitingPermission;

    @Override protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(me.aap.fermata.ui.activity.MainActivityDelegate.attachBaseContext(base));
    }

    @Override public void onCreate(Bundle saved) {
        me.aap.fermata.ui.activity.MainActivityDelegate.setTheme(this, false);
        super.onCreate(saved);
        setTitle(R.string.github_updates);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (24 * getResources().getDisplayMetrics().density);
        content.setPadding(padding, padding, padding, padding);
        content.setBackgroundResource(R.drawable.app_background);
        TextView title = new TextView(this);
        title.setText(R.string.github_updates);
        title.setTextSize(26);
        content.addView(title);
        TextView current = new TextView(this);
        current.setText(getString(R.string.github_update_version, BuildConfig.VERSION_NAME));
        content.addView(current);
        TextView help = new TextView(this);
        help.setText(R.string.github_updates_help);
        content.addView(help);
        SwitchMaterial automatic = new SwitchMaterial(this);
        automatic.setText(R.string.github_updates_auto);
        automatic.setChecked(GithubUpdater.prefs(this).getBoolean("enabled", true));
        automatic.setOnCheckedChangeListener((button, enabled) -> {
            GithubUpdater.prefs(this).edit().putBoolean("enabled", enabled).apply();
            GithubUpdater.schedule(this);
            if (enabled) requestNoticePermission();
        });
        content.addView(automatic);
        MaterialButton notifications = new MaterialButton(this);
        notifications.setText(R.string.github_updates_notifications);
        notifications.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName())));
        content.addView(notifications);
        status = new TextView(this);
        status.setPadding(0, padding, 0, padding);
        content.addView(status);
        check = new MaterialButton(this);
        check.setText(R.string.github_updates_check);
        check.setOnClickListener(v -> check());
        content.addView(check);
        install = new MaterialButton(this);
        install.setText(R.string.github_updates_install);
        install.setEnabled(false);
        install.setOnClickListener(v -> { if (verifiedApk != null) installVerified(); else download(); });
        content.addView(install);
        MaterialButton close = new MaterialButton(this);
        close.setText(R.string.github_updates_close);
        close.setOnClickListener(v -> finish());
        content.addView(close);
        android.widget.ScrollView scroll = new android.widget.ScrollView(this);
        scroll.addView(content);
        setContentView(scroll);
        if (automatic.isChecked()) requestNoticePermission();
        check();
    }

    private void requestNoticePermission() {
        GithubUpdater.requestNoticePermission(this);
    }

    private void check() {
        available = null;
        verifiedApk = null;
        install.setEnabled(false);
        check.setEnabled(false);
        status.setText(R.string.github_updates_checking);
        executor.submit(() -> {
            try {
                UpdateManifest update = GithubUpdater.check(getApplicationContext());
                ui(() -> {
                    available = update;
                    status.setText(update == null ? getString(R.string.github_updates_latest) :
                            getString(R.string.github_update_version, update.versionName()));
                    install.setEnabled(update != null);
                    check.setEnabled(true);
                });
            } catch (Exception error) { failed(); }
        });
    }

    private void download() {
        UpdateManifest update = available;
        if (update == null) return;
        check.setEnabled(false);
        install.setEnabled(false);
        status.setText(R.string.github_updates_downloading);
        executor.submit(() -> {
            File directory = new File(getCacheDir(), "updates");
            File apk = null;
            try {
                if (!directory.isDirectory() && !directory.mkdirs()) throw new IOException("Cache unavailable");
                apk = File.createTempFile("verified-", ".apk", directory);
                var connection = GithubUpdater.connect(update.apkUrl());
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                long total = 0;
                try (var input = connection.getInputStream(); var output = new FileOutputStream(apk)) {
                    byte[] buffer = new byte[64 * 1024];
                    int count;
                    while ((count = input.read(buffer)) != -1) {
                        if (Thread.currentThread().isInterrupted()) throw new IOException("Cancelled");
                        total += count;
                        if (total > update.size()) throw new IOException("Unexpected APK size");
                        output.write(buffer, 0, count);
                        digest.update(buffer, 0, count);
                    }
                } finally { connection.disconnect(); }
                StringBuilder hash = new StringBuilder();
                for (byte b : digest.digest()) hash.append(String.format(java.util.Locale.ROOT, "%02x", b & 255));
                if (total != update.size() || !hash.toString().equalsIgnoreCase(update.sha256()))
                    throw new IOException("APK checksum mismatch");
                PackageManager manager = getPackageManager();
                PackageInfo archive = manager.getPackageArchiveInfo(apk.getAbsolutePath(), PackageManager.GET_SIGNING_CERTIFICATES);
                PackageInfo installed = manager.getPackageInfo(getPackageName(), PackageManager.GET_SIGNING_CERTIFICATES);
                if (archive == null || !getPackageName().equals(archive.packageName) ||
                        archive.getLongVersionCode() != update.versionCode() || archive.signingInfo == null ||
                        installed.signingInfo == null || !Arrays.equals(archive.signingInfo.getApkContentsSigners(),
                        installed.signingInfo.getApkContentsSigners())) throw new IOException("APK identity mismatch");
                File downloaded = apk;
                ui(() -> { verifiedApk = downloaded; installVerified(); });
            } catch (Exception error) { if (apk != null) apk.delete(); failed(); }
        });
    }

    private void installVerified() {
        if (verifiedApk == null || !verifiedApk.isFile()) return;
        try {
            if (!getPackageManager().canRequestPackageInstalls()) {
                waitingPermission = true;
                status.setText(R.string.github_updates_permission);
                startActivity(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                        Uri.parse("package:" + getPackageName())));
                return;
            }
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".UpdateFileProvider", verifiedApk);
            startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(uri, "application/vnd.android.package-archive")
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION));
            status.setText(R.string.github_updates_confirm);
            check.setEnabled(true);
            install.setEnabled(true);
        } catch (Exception error) { failed(); }
    }

    @Override protected void onResume() {
        super.onResume();
        if (waitingPermission) {
            waitingPermission = false;
            if (getPackageManager().canRequestPackageInstalls()) installVerified();
            else { status.setText(R.string.github_updates_permission); check.setEnabled(true); install.setEnabled(true); }
        }
    }
    private void failed() {
        ui(() -> { status.setText(R.string.github_updates_error); check.setEnabled(true); install.setEnabled(available != null); });
    }
    private void ui(Runnable action) {
        runOnUiThread(() -> { if (!isFinishing() && !isDestroyed()) action.run(); });
    }
    @Override protected void onDestroy() { executor.shutdownNow(); super.onDestroy(); }
}
