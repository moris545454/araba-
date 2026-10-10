package me.aap.fermata.update;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import me.aap.fermata.BuildConfig;
import me.aap.fermata.R;

public final class GithubUpdater {
    private static final int JOB = 90427;
    private static final int NOTICE = 90428;
    private static final String CHANNEL = "github_updates";
    private GithubUpdater() {}

    public static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences("github_updates", Context.MODE_PRIVATE);
    }

    public static void requestNoticePermission(android.app.Activity activity) {
        if (activity instanceof me.aap.utils.ui.activity.ActivityBase base && base.hasPendingPermissionRequest()) return;
        if (Build.VERSION.SDK_INT >= 33 && prefs(activity).getBoolean("enabled", true) &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED && !prefs(activity).getBoolean("permission_asked", false)) {
            prefs(activity).edit().putBoolean("permission_asked", true).apply();
            activity.requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 90429);
        }
    }

    public static void schedule(Context context) {
        // The app supports API 28+; older test/host runtimes have no compatible job API.
        if (Build.VERSION.SDK_INT < 28) return;
        JobScheduler scheduler = context.getSystemService(JobScheduler.class);
        if (scheduler == null) return;
        if (!prefs(context).getBoolean("enabled", true)) { scheduler.cancel(JOB); return; }
        if (scheduler.getPendingJob(JOB) != null) return;
        scheduler.schedule(new JobInfo.Builder(JOB, new ComponentName(context, UpdateCheckJob.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setPersisted(true)
                .setPeriodic(6 * 60 * 60 * 1000L).build());
    }

    /** Called off the UI thread; errors do not affect playback or the last successful check. */
    public static UpdateManifest check(Context context) throws Exception {
        JSONObject release = json("https://api.github.com/repos/" + UpdateManifest.REPOSITORY + "/releases/latest");
        JSONObject manifest = json(UpdateManifest.assetUrl(release, UpdateManifest.MANIFEST_ASSET));
        UpdateManifest update = UpdateManifest.parse(release, manifest, context.getPackageName(), Build.VERSION.SDK_INT);
        prefs(context).edit().putLong("last_check", System.currentTimeMillis()).apply();
        return update.versionCode() > BuildConfig.VERSION_CODE ? update : null;
    }

    public static synchronized void checkAndNotify(Context context) {
        if (!prefs(context).getBoolean("enabled", true)) return;
        if (System.currentTimeMillis() - prefs(context).getLong("last_check", 0) < 60 * 60 * 1000L) return;
        try {
            UpdateManifest update = check(context);
            if (update != null) notifyUpdate(context, update);
        } catch (Exception ignored) { /* Offline or incomplete releases are retried on the next check. */ }
    }

    public static void notifyUpdate(Context context, UpdateManifest update) {
        if (!prefs(context).getBoolean("enabled", true)) return;
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager == null || !manager.areNotificationsEnabled() ||
                (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context,
                        Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)) return;
        if (prefs(context).getInt("notified_version", 0) >= update.versionCode()) return;
        manager.createNotificationChannel(new NotificationChannel(CHANNEL,
                context.getString(R.string.github_updates), NotificationManager.IMPORTANCE_DEFAULT));
        PendingIntent open = PendingIntent.getActivity(context, 0, new Intent(context, UpdateActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        try {
            manager.notify(NOTICE, new NotificationCompat.Builder(context, CHANNEL)
                    .setSmallIcon(R.drawable.notification).setContentTitle(context.getString(R.string.github_update_available))
                    .setContentText(context.getString(R.string.github_update_version, update.versionName()))
                    .setContentIntent(open).setAutoCancel(true).build());
            prefs(context).edit().putInt("notified_version", update.versionCode()).apply();
        } catch (SecurityException ignored) { /* Permission may change while constructing the notice. */ }
    }

    public static HttpURLConnection connect(String address) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(address).openConnection();
        connection.setConnectTimeout(15_000);
        connection.setReadTimeout(30_000);
        connection.setRequestProperty("User-Agent", "FermataX/" + BuildConfig.VERSION_NAME);
        connection.setRequestProperty("Accept", "application/vnd.github+json");
        if (connection.getResponseCode() != 200) {
            connection.disconnect();
            throw new IOException("Release request failed");
        }
        if (!"https".equals(connection.getURL().getProtocol())) {
            connection.disconnect();
            throw new IOException("HTTPS required");
        }
        return connection;
    }

    private static JSONObject json(String address) throws Exception {
        HttpURLConnection connection = connect(address);
        try (var stream = connection.getInputStream(); var bytes = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = stream.read(buffer)) != -1) {
                if (bytes.size() + count > 256 * 1024) throw new IOException("Release metadata too large");
                bytes.write(buffer, 0, count);
            }
            return new JSONObject(new String(bytes.toByteArray(), StandardCharsets.UTF_8));
        } finally { connection.disconnect(); }
    }
}
