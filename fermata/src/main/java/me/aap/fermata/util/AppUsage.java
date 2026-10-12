package me.aap.fermata.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;

import me.aap.fermata.auto.AutomotiveConnectionState;

/**
 * Whether Mola is being used right now: a phone screen is visible or the car is connected.
 * Background data refresh waits until this is true.
 */
public final class AppUsage {
	private static int startedActivities;

	private AppUsage() {
	}

	public static void install(Application app) {
		app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
			@Override public void onActivityStarted(@NonNull Activity a) { startedActivities++; }
			@Override public void onActivityStopped(@NonNull Activity a) {
				if (startedActivities > 0) startedActivities--;
			}
			@Override public void onActivityCreated(@NonNull Activity a, Bundle b) { }
			@Override public void onActivityResumed(@NonNull Activity a) { }
			@Override public void onActivityPaused(@NonNull Activity a) { }
			@Override public void onActivitySaveInstanceState(@NonNull Activity a, @NonNull Bundle b) { }
			@Override public void onActivityDestroyed(@NonNull Activity a) { }
		});
	}

	public static boolean isInUse() {
		if (startedActivities > 0) return true;
		try {
			return AutomotiveConnectionState.get().isProjectionConnected();
		} catch (Throwable ignore) {
			return false;
		}
	}
}
