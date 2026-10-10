package me.aap.fermata.update;

import android.app.job.JobParameters;
import android.app.job.JobService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** Android schedules these checks subject to network, battery, and background restrictions. */
public final class UpdateCheckJob extends JobService {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Future<?> pending;
    @Override public boolean onStartJob(JobParameters params) {
        pending = executor.submit(() -> {
            GithubUpdater.checkAndNotify(getApplicationContext());
            if (!Thread.currentThread().isInterrupted()) jobFinished(params, false);
        });
        return true;
    }
    @Override public boolean onStopJob(JobParameters params) {
        if (pending != null) pending.cancel(true);
        return true;
    }
    @Override public void onDestroy() {
        executor.shutdownNow();
        super.onDestroy();
    }
}
