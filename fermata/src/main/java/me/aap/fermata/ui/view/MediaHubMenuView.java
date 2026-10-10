package me.aap.fermata.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import me.aap.fermata.R;
import me.aap.utils.ui.menu.OverlayMenuView;

/** Visual wrapper for every app menu; keeps the existing selection, Back and focus behavior. */
public final class MediaHubMenuView extends OverlayMenuView {
	public MediaHubMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setBackgroundResource(R.drawable.media_hub_menu_bg);
		int padding = Math.round(8 * getResources().getDisplayMetrics().density);
		setPadding(padding, padding, padding, padding);
		setClipToOutline(true);
		setElevation(16 * getResources().getDisplayMetrics().density);
	}
}
