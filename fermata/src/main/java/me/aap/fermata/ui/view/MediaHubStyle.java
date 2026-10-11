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
		if (destination == R.id.recent_fragment) return 0xFF76DFC5;
		if (destination == R.id.playlists_fragment) return 0xFF9A83FF;
		if (destination == R.id.dashboard_mirror_action) return 0xFFC4B2E9;
		AddonInfo info = AddonManager.get().getAddonInfo(destination);
		if (info != null) {
			if (info.hasCapability(AddonCapability.TV)) return 0xFF76DFC5;
			if (info.hasCapability(AddonCapability.YOUTUBE)) return 0xFFEDAB91;
			if (info.hasCapability(AddonCapability.RADIO)) return 0xFFEDAB91;
			if (info.hasCapability(AddonCapability.PODCAST)) return 0xFFC4B2E9;
			if (info.hasCapability(AddonCapability.AUDIOBOOK)) return 0xFF9A83FF;
			if (info.hasCapability(AddonCapability.WEB)) return 0xFF83C5CE;
			if (info.hasCapability(AddonCapability.STREMIO)) return 0xFFAA83FF;
		}
		return 0xFF76DFC5;
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
		mask.setCornerRadius(dp(context, featured ? 26 : 20));
		return new RippleDrawable(ColorStateList.valueOf(ColorUtils.setAlphaComponent(accent, 55)), states, mask);
	}

	public static Drawable launcher(Context context, int accent) {
        return card(context, accent, true);
    }

    public static Drawable badge(Context context, int accent) {
		GradientDrawable shape = new GradientDrawable();
		shape.setColor(ColorUtils.setAlphaComponent(accent, 28));
		shape.setCornerRadius(dp(context, 28));
		return shape;
	}

	private static Drawable surface(Context context, int accent, boolean featured, boolean focused) {
		int base = color(context, com.google.android.material.R.attr.colorSecondary, 0xFF08131E);
		GradientDrawable shape = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
				new int[]{ColorUtils.blendARGB(base, 0xFFFFFFFF, focused ? .22F : .14F),
                        ColorUtils.setAlphaComponent(ColorUtils.blendARGB(base, accent, .13F), 220),
                        ColorUtils.setAlphaComponent(base, 195)});
        shape.setCornerRadius(dp(context, featured ? 26 : 20));
        shape.setStroke(Math.max(1, Math.round(dp(context, focused ? 2 : 1))),
                focused ? accent : 0x48FFFFFF);
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
