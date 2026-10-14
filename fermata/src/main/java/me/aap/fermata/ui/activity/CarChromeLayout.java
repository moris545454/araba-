package me.aap.fermata.ui.activity;

import android.view.View;

import me.aap.fermata.R;
import me.aap.fermata.ui.view.ControlPanelView;

/** Car screen chrome layout, kept apart from {@link MainActivityDelegate}. */
final class CarChromeLayout {
	private CarChromeLayout() {
	}

	/**
	 * Car screen: no bottom bar. Home and the tool bar's Back sit together at the top left, the
	 * remaining tool buttons float on the same row and the player floats at the bottom.
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

		set.clear(R.id.persistent_home_button, end);
		set.clear(R.id.persistent_home_button, top);
		set.connect(R.id.persistent_home_button, start, parent, start, margin);
		set.connect(R.id.persistent_home_button, top, parent, top, margin);

		set.clear(R.id.tool_bar, bottom);
		set.clear(R.id.tool_bar, start);
		set.connect(R.id.tool_bar, top, parent, top, margin);
		set.connect(R.id.tool_bar, start, R.id.persistent_home_button, end, margin / 2);
		set.connect(R.id.tool_bar, end, parent, end, margin);

		set.connect(R.id.body_layout, top, R.id.tool_bar, bottom, margin / 2);
		set.connect(R.id.body_layout, bottom, parent, bottom);
		set.connect(R.id.control_panel, bottom, parent, bottom, margin);
		set.connect(R.id.control_panel, start, parent, start, margin * 2);
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
		if (home != null) home.setAlpha(0.88F);
		if (controlPanel != null) controlPanel.useFloatingPill();
	}
}
