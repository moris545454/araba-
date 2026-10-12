package me.aap.fermata.update;

import static org.junit.Assert.*;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@org.robolectric.annotation.Config(sdk = 28)
@RunWith(RobolectricTestRunner.class)
public class GithubUpdaterTest {
    @Test public void neverSchedulesBackgroundChecksAndCancelsLegacyJob() {
        Context context = RuntimeEnvironment.getApplication();
        JobScheduler scheduler = context.getSystemService(JobScheduler.class);
        GithubUpdater.prefs(context).edit().clear().commit();
        // A periodic job left by an older version is removed.
        scheduler.schedule(new JobInfo.Builder(90427,
                new android.content.ComponentName(context, UpdateCheckJob.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(6 * 60 * 60 * 1000L).build());
        assertEquals(1, scheduler.getAllPendingJobs().size());
        GithubUpdater.schedule(context);
        assertTrue(scheduler.getAllPendingJobs().isEmpty());
        GithubUpdater.schedule(context);
        assertTrue(scheduler.getAllPendingJobs().isEmpty());
    }
    @Test public void notificationPermissionResultDoesNotCompleteMediaPermissionRequest() throws Exception {
        PermissionHost host = org.robolectric.Robolectric.buildActivity(PermissionHost.class).get();
        var pending = new me.aap.utils.async.Promise<int[]>();
        var field = me.aap.utils.ui.activity.ActivityBase.class.getDeclaredField("checkPermissions");
        field.setAccessible(true);
        field.set(host, pending);
        host.onRequestPermissionsResult(90429, new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                new int[]{android.content.pm.PackageManager.PERMISSION_GRANTED});
        assertFalse(pending.isDone());
        assertTrue(host.hasPendingPermissionRequest());
        host.onRequestPermissionsResult(1, new String[]{android.Manifest.permission.RECORD_AUDIO},
                new int[]{android.content.pm.PackageManager.PERMISSION_GRANTED});
        assertArrayEquals(new int[]{android.content.pm.PackageManager.PERMISSION_GRANTED}, pending.get());
        assertFalse(host.hasPendingPermissionRequest());
    }
    public static class PermissionHost extends me.aap.utils.ui.activity.ActivityBase {
        @Override protected me.aap.utils.async.FutureSupplier<? extends me.aap.utils.ui.activity.ActivityDelegate>
        createDelegate(me.aap.utils.ui.activity.AppActivity activity) {
            return me.aap.utils.async.Completed.completedNull();
        }
    }

}
