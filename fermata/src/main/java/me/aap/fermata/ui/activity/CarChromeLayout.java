package me.aap.fermata.ui.activity;

import android.view.View;

import me.aap.fermata.R;
import me.aap.fermata.ui.view.ControlPanelView;
import me.aap.fermata.ui.voice.VoiceUiPolicy;

/** Car screen chrome layout, kept apart from {@link MainActivityDelegate}. */
final class CarChromeLayout {
	/** Home and the microphone are larger than the other floating buttons: easy to hit. */
	static final int HOME_SIZE_DP = 64;

	private CarChromeLayout() {
	}

	/**
	 * Car screen: no tool bar at all. Back and the tool buttons float at the top over the content,
	 * a large Home button sits at the bottom left with the microphone right above it, and the
	 * player floats at the bottom next to them.
	 */
	static void apply(FermataActivity a, ControlPanelView controlPanel) {
		androidx.constraintlayout.widget.ConstraintLayout root = a.findViewById(R.id.main_activity);
		if (root == null) return;
		int margin = Math.round(8 * root.getResources().getDisplayMetrics().density);
		androidx.constraintlayout.widget.ConstraintSet set =
				new androidx.constraintlayout.widget.ConstraintSet();
		set.clone(root);
		int parent = androidx.constraintlayout.widget.ConstraintSet.PARENT_ID;
		int top = androidx.constraintlayout.widget.ConstraintSet.TOP;
		int bottom = androidx.constraintlayout.widget.ConstraintSet.BOTTOM;
		int start = androidx.constraintlayout.widget.ConstraintSet.START;
		int end = androidx.constraintlayout.widget.ConstraintSet.END;

		int bigButton = margin * HOME_SIZE_DP / 8;
		set.clear(R.id.persistent_home_button);
		set.constrainWidth(R.id.persistent_home_button, bigButton);
		set.constrainHeight(R.id.persistent_home_button, bigButton);
		set.connect(R.id.persistent_home_button, start, parent, start, margin);
		set.connect(R.id.persistent_home_button, bottom, parent, bottom, margin);

		set.clear(R.id.car_voice_button);
		set.constrainWidth(R.id.car_voice_button, bigButton);
		set.constrainHeight(R.id.car_voice_button, bigButton);
		set.connect(R.id.car_voice_button, start, parent, start, margin);
		set.connect(R.id.car_voice_button, bottom, R.id.persistent_home_button, top, margin);

		set.clear(R.id.tool_bar, bottom);
		set.clear(R.id.tool_bar, start);
		set.connect(R.id.tool_bar, top, parent, top, margin);
		set.connect(R.id.tool_bar, start, parent, start, margin);
		set.connect(R.id.tool_bar, end, parent, end, margin);

		// The buttons float over the content: the list starts at the very top of the screen.
		set.connect(R.id.body_layout, top, parent, top);
		set.connect(R.id.body_layout, bottom, parent, bottom);
		set.connect(R.id.control_panel, bottom, parent, bottom, margin);
		set.connect(R.id.control_panel, start, R.id.persistent_home_button, end, margin * 2);
		set.connect(R.id.control_panel, end, parent, end, margin * 2);
		set.constrainMaxWidth(R.id.control_panel, margin * 110);
		set.connect(R.id.control_panel_scrim, bottom, parent, bottom);
		set.connect(R.id.floating_button, bottom, parent, bottom, margin);

		set.clear(R.id.tool_menu, bottom);
		set.connect(R.id.tool_menu, top, R.id.tool_bar, bottom, margin / 2);
		set.connect(R.id.tool_menu, bottom, parent, bottom, margin);
		set.setVerticalBias(R.id.tool_menu, 0F);
		set.applyTo(root);
		View home = a.findViewById(R.id.persistent_home_button);
		if (home != null) home.setAlpha(0.92F);
		if (controlPanel != null) controlPanel.useFloatingPill();
	}

	/** The car microphone shows only on the car screen and only while voice control is on. */
	static void refreshVoiceButton(MainActivityDelegate a) {
		View voice = a.getAppActivity().findViewById(R.id.car_voice_button);
		if (voice == null) return;
		voice.setVisibility(VoiceUiPolicy.showCarButton(a) ? View.VISIBLE : View.GONE);
		voice.setAlpha(0.92F);
	}
}
