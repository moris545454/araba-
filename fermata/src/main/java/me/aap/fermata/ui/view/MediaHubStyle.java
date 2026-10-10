package me.aap.fermata.ui.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import me.aap.fermata.R;
import me.aap.fermata.addon.AddonCapability;
import me.aap.fermata.addon.AddonInfo;
import me.aap.fermata.addon.AddonManager;
import me.aap.fermata.ui.activity.MainActivityDelegate;

/** Shared visual tokens. Navigation and playback remain owned by the UI shell. */
public final class MediaHubStyle {
	private MediaHubStyle() {
	}

	public static int destinationAccent(int destination) {
		if (destination == R.id.folders_fragment) return 0xFFFFC14D;
		if (destination == R.id.favorites_fragment) return 0xFFFF4165;
		if (destination == R.id.recent_fragment) return 0xFF65BFFF;
		if (destination == R.id.playlists_fragment) return 0xFF9A83FF;
		if (destination == R.id.dashboard_mirror_action) return 0xFFC084FC;
		AddonInfo info = AddonManager.get().getAddonInfo(destination);
		if (info != null) {
			if (info.hasCapability(AddonCapability.TV)) return 0xFF55B9FF;
			if (info.hasCapability(AddonCapability.YOUTUBE)) return 0xFFFF344A;
			if (info.hasCapability(AddonCapability.RADIO)) return 0xFFFF4B78;
			if (info.hasCapability(AddonCapability.PODCAST)) return 0xFFBC7AFF;
			if (info.hasCapability(AddonCapability.AUDIOBOOK)) return 0xFF9A83FF;
			if (info.hasCapability(AddonCapability.WEB)) return 0xFF65C8FF;
			if (info.hasCapability(AddonCapability.STREMIO)) return 0xFFAA83FF;
		}
		return 0xFF65BFFF;
	}

	public static int routeAccent(MainActivityDelegate activity) {
		return destinationAccent(activity.getActiveNavItemId());
	}

	public static Drawable card(Context context, int accent, boolean featured) {
		StateListDrawable states = new StateListDrawable();
		states.addState(new int[]{android.R.attr.state_focused}, surface(context, accent, featured, true));
		states.addState(new int[]{android.R.attr.state_selected}, surface(context, accent, featured, true));
		states.addState(new int[]{android.R.attr.state_activated}, surface(context, accent, featured, true));
		states.addState(new int[0], surface(context, accent, featured, false));
		GradientDrawable mask = new GradientDrawable();
		mask.setColor(0xFFFFFFFF);
		mask.setCornerRadius(dp(context, featured ? 20 : 16));
		return new RippleDrawable(ColorStateList.valueOf(ColorUtils.setAlphaComponent(accent, 55)), states, mask);
	}

	public static Drawable badge(Context context, int accent) {
		GradientDrawable shape = new GradientDrawable();
		shape.setColor(ColorUtils.setAlphaComponent(accent, 28));
		shape.setCornerRadius(dp(context, 14));
		return shape;
	}

	private static Drawable surface(Context context, int accent, boolean featured, boolean focused) {
		int base = color(context, com.google.android.material.R.attr.colorSecondary, 0xFF08131E);
		GradientDrawable shape = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
				new int[]{ColorUtils.blendARGB(base, accent, focused ? .24F : featured ? .13F : .035F), base});
		shape.setCornerRadius(dp(context, featured ? 20 : 16));
		shape.setStroke(Math.max(1, Math.round(dp(context, focused ? 2 : 1))),
				ColorUtils.setAlphaComponent(accent, focused ? 255 : featured ? 135 : 48));
		return shape;
	}

	public static int color(Context context, int attribute, int fallback) {
		TypedValue value = new TypedValue();
		if (!context.getTheme().resolveAttribute(attribute, value, true)) return fallback;
		if (value.resourceId != 0) return ContextCompat.getColor(context, value.resourceId);
		return value.data;
	}

	private static float dp(Context context, int value) {
		return value * context.getResources().getDisplayMetrics().density;
	}
}
