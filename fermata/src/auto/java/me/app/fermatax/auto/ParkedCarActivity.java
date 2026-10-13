package me.app.fermatax.auto;

import me.aap.fermata.ui.activity.MainActivity;

/**
 * Android 15+ parked-app entry: Android Auto starts this activity directly on the head unit
 * display, so it uses the automotive presentation instead of the phone layout.
 */
public final class ParkedCarActivity extends MainActivity {

	@Override
	public boolean isCarActivity() {
		return true;
	}

	@Override
	public boolean isParkedCarActivity() {
		return true;
	}
}
