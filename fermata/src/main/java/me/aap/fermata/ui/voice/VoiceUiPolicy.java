package me.aap.fermata.ui.voice;

import me.aap.fermata.ui.activity.MainActivityDelegate;
import me.aap.fermata.ui.policy.RuntimeHostMode;

/** Keeps a single global voice entry in the shared navigation rail. */
public final class VoiceUiPolicy {
	private VoiceUiPolicy() {
	}

	public static boolean showToolbarButton(MainActivityDelegate activity) {
		return showToolbarButton(activity.getRuntimeHostMode(),
				activity.getPrefs().getVoiceControlEnabledPref(), railShown(activity));
	}

	public static boolean showNavBarButton(MainActivityDelegate activity) {
		return showNavBarButton(activity.getRuntimeHostMode(),
				activity.getPrefs().getVoiceControlEnabledPref(), railShown(activity));
	}

	private static boolean railShown(MainActivityDelegate activity) {
		return activity.getPrefs().getBooleanPref(
				me.aap.fermata.ui.activity.MainActivityPrefs.SHOW_SHORTCUT_RAIL);
	}

	/** The microphone sits with the other floating buttons unless the shortcut rail is shown. */
	static boolean showToolbarButton(RuntimeHostMode hostMode, boolean voiceEnabled,
			boolean railShown) {
		return voiceEnabled && !railShown;
	}

	static boolean showNavBarButton(RuntimeHostMode hostMode, boolean voiceEnabled,
			boolean railShown) {
		return voiceEnabled && railShown;
	}
}
