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
		if (destination == R.id.folders_fragment) return 0xFFE3C26B;
		if (destination == R.id.favorites_fragment) return 0xFFE8826A;
		if (destination == R.id.recent_fragment) return 0xFFC9C2B6;
		if (destination == R.id.playlists_fragment) return 0xFFC7A27C;
		if (destination == R.id.dashboard_mirror_action) return 0xFF9FB7A8;
		AddonInfo info = AddonManager.get().getAddonInfo(destination);
		if (info != null) {
			if (info.hasCapability(AddonCapability.TV)) return 0xFFF0B45A;
			if (info.hasCapability(AddonCapability.YOUTUBE)) return 0xFFE5484D;
			if (info.hasCapability(AddonCapability.RADIO)) return 0xFFE8826A;
			if (info.hasCapability(AddonCapability.PODCAST)) return 0xFFD99A7A;
			if (info.hasCapability(AddonCapability.AUDIOBOOK)) return 0xFFC7A27C;
			if (info.hasCapability(AddonCapability.WEB)) return 0xFFD9C29A;
			if (info.hasCapability(AddonCapability.STREMIO)) return 0xFFC7A27C;
		}
		return 0xFFF0B45A;
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
        StateListDrawable states = new StateListDrawable();
        for (int[] state : new int[][]{new int[]{android.R.attr.state_focused},
                new int[]{android.R.attr.state_selected}, new int[0]}) {
            boolean focused = state.length != 0;
            GradientDrawable surface = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                    new int[]{ColorUtils.blendARGB(0xFF1C1F22, accent, .12F), 0xFF111315});
            surface.setCornerRadius(dp(context, 18));
            surface.setStroke(Math.max(1, Math.round(dp(context, focused ? 2 : 1))),
                    ColorUtils.setAlphaComponent(accent, focused ? 255 : 160));
            states.addState(state, surface);
        }
        GradientDrawable mask = new GradientDrawable();
        mask.setColor(0xFFFFFFFF);
        mask.setCornerRadius(dp(context, 18));
        return new RippleDrawable(ColorStateList.valueOf(ColorUtils.setAlphaComponent(accent, 55)),
                states, mask);
    }

    public static Drawable badge(Context context, int accent) {
		GradientDrawable shape = new GradientDrawable();
		shape.setColor(ColorUtils.setAlphaComponent(accent, 28));
		shape.setCornerRadius(dp(context, 28));
		return shape;
	}

	private static Drawable surface(Context context, int accent, boolean featured, boolean focused) {
		int base = color(context, com.google.android.material.R.attr.colorSecondary, 0xFF24282C);
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
