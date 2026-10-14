package me.aap.fermata.ui.activity;

import android.view.View;

import me.aap.fermata.R;
import me.aap.fermata.ui.view.ControlPanelView;

/** Car screen chrome layout, kept apart from {@link MainActivityDelegate}. */
final class CarChromeLayout {
	private CarChromeLayout() {
	}

	/**
	 * Car screen: Home floats at the bottom left with the microphone just above it, the tool bar
	 * floats along the top, the content fills the screen and the player floats at the bottom.
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

		View voice = ensureVoiceButton(root);
		int rail = Math.round(RAIL_BUTTON_DP * root.getResources().getDisplayMetrics().density);
		set.constrainWidth(R.id.persistent_home_button, rail);
		set.constrainHeight(R.id.persistent_home_button, rail);
		set.clear(R.id.persistent_home_button, end);
		set.clear(R.id.persistent_home_button, top);
		set.connect(R.id.persistent_home_button, start, parent, start, margin);
		set.connect(R.id.persistent_home_button, bottom, parent, bottom, margin);
		set.constrainWidth(voice.getId(), rail);
		set.constrainHeight(voice.getId(), rail);
		set.connect(voice.getId(), start, parent, start, margin);
		set.connect(voice.getId(), bottom, R.id.persistent_home_button, top, margin);
		set.setTranslationZ(voice.getId(), 200 * root.getResources().getDisplayMetrics().density);

		set.clear(R.id.tool_bar, bottom);
		set.clear(R.id.tool_bar, start);
		set.connect(R.id.tool_bar, top, parent, top, margin);
		set.connect(R.id.tool_bar, start, parent, start, margin);
		set.connect(R.id.tool_bar, end, parent, end, margin + a.getCarHostEndInset());
		// Declared before the body: without lifting it, page content (e.g. a web page) covers it.
		set.setTranslationZ(R.id.tool_bar, 4 * root.getResources().getDisplayMetrics().density);

		// The buttons float over the content: the list fills the whole screen.
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
		if (home instanceof android.widget.ImageView icon) {
			styleRailButton(home);
			icon.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
			int padding = Math.round(RAIL_ICON_PADDING_DP * root.getResources().getDisplayMetrics().density);
			home.setPadding(padding, padding, padding, padding);
		}
		styleRailButton(voice);
		syncVoiceButton(a);
		if (controlPanel != null) controlPanel.useFloatingPill();
	}

	private static final int RAIL_BUTTON_DP = 64;
	private static final int RAIL_ICON_PADDING_DP = 16;

	/** The car microphone shares Home's look and starts Mola's own voice control. */
	private static View ensureVoiceButton(androidx.constraintlayout.widget.ConstraintLayout root) {
		View existing = root.findViewById(R.id.car_voice_button);
		if (existing != null) return existing;
		android.content.Context ctx = root.getContext();
		androidx.appcompat.widget.AppCompatImageButton voice =
				new androidx.appcompat.widget.AppCompatImageButton(ctx);
		voice.setId(R.id.car_voice_button);
		voice.setImageResource(R.drawable.voice_microphone);
		voice.setContentDescription(ctx.getString(R.string.action_activate_voice_ctrl));
		voice.setImageTintList(android.content.res.ColorStateList.valueOf(
				me.aap.fermata.ui.view.MediaHubStyle.color(ctx, android.R.attr.textColorPrimary,
						0xFFF3EFE7)));
		voice.setBackground(me.aap.fermata.ui.view.MediaHubStyle.card(ctx, 0xFF76DFC5, false));
		int padding = Math.round(RAIL_ICON_PADDING_DP * ctx.getResources().getDisplayMetrics().density);
		voice.setPadding(padding, padding, padding, padding);
		voice.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
		voice.setFocusable(true);
		voice.setOnClickListener(v -> MainActivityDelegate.get(v.getContext()).startGlobalVoiceControl());
		root.addView(voice, new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(0, 0));
		return voice;
	}

	/**
	 * The rail buttons float over the content, so they are fully opaque with a soft shadow:
	 * text scrolling underneath never shows through them.
	 */
	private static void styleRailButton(View button) {
		android.content.Context ctx = button.getContext();
		float density = ctx.getResources().getDisplayMetrics().density;
		button.setAlpha(1F);
		android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable(
				android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM,
				new int[]{0xFF2C3237, 0xFF1C2024});
		bg.setCornerRadius(18 * density);
		bg.setStroke(Math.round(density), 0x5576DFC5);
		button.setBackground(new android.graphics.drawable.RippleDrawable(
				android.content.res.ColorStateList.valueOf(0x4476DFC5), bg, null));
		button.setElevation(8 * density);
		button.setOutlineProvider(android.view.ViewOutlineProvider.BACKGROUND);
	}

	/**
	 * The tool bar floats along the top. The dashboard has its own header row under it, every
	 * other page (lists, settings, the web page) starts below the tool bar so its buttons never
	 * cover a row or the page.
	 */
	static void installContentInset(MainActivityDelegate d) {
		View toolBar = d.getToolBar();
		View frame = d.getAppActivity().findViewById(R.id.frame_layout);
		if ((toolBar == null) || (frame == null) || (frame.getTag(R.id.frame_layout) != null)) return;
		Runnable update = () -> {
			int id = d.getActiveFragmentId();
			boolean inset = (toolBar.getVisibility() == View.VISIBLE) && (id != 0) &&
					(id != R.id.dashboard_fragment);
			int top = inset ? toolBar.getBottom() : 0;
			if (frame.getPaddingTop() != top) {
				frame.setPadding(frame.getPaddingLeft(), top, frame.getPaddingRight(),
						frame.getPaddingBottom());
			}
		};
		toolBar.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) -> update.run());
		me.aap.utils.ui.activity.ActivityListener listener = (a, e) -> frame.post(update);
		d.addBroadcastListener(listener, me.aap.utils.ui.activity.ActivityListener.FRAGMENT_CHANGED);
		frame.setTag(R.id.frame_layout, listener); // keeps the listener reachable with the view
		frame.post(update);
	}

	/** The microphone follows Home: both hide together with the shell chrome. */
	static void syncVoiceButton(FermataActivity a) {
		View voice = a.findViewById(R.id.car_voice_button);
		View home = a.findViewById(R.id.persistent_home_button);
		if ((voice != null) && (home != null)) voice.setVisibility(home.getVisibility());
	}
}
