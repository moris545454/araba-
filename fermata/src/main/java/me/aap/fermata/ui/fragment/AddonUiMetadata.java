package me.aap.fermata.ui.fragment;

import android.graphics.drawable.GradientDrawable;

import me.aap.fermata.addon.AddonCapability;
import me.aap.fermata.addon.AddonInfo;

final class AddonUiMetadata {
	private AddonUiMetadata() {
	}

	static boolean isDashboardItem(AddonInfo info) {
		return info.hasFragment && info.hasCapability(AddonCapability.DASHBOARD);
	}

	static boolean isNavigationItem(AddonInfo info) {
		return info.hasFragment && info.hasCapability(AddonCapability.NAVIGATION);
	}

	static Role role(AddonInfo info) {
		if (info.hasCapability(AddonCapability.TV)) return Role.TV;
		if (info.hasCapability(AddonCapability.YOUTUBE)) return Role.YOUTUBE;
		if (info.hasCapability(AddonCapability.RADIO)) return Role.RADIO;
		if (info.hasCapability(AddonCapability.PODCAST)) return Role.PODCAST;
		if (info.hasCapability(AddonCapability.AUDIOBOOK)) return Role.AUDIOBOOK;
		if (info.hasCapability(AddonCapability.STREMIO)) return Role.STREMIO;
		if (info.hasCapability(AddonCapability.WEB)) return Role.WEB;
		return Role.GENERIC;
	}

	static int color(Role role) {
		return switch (role) {
			case TV -> 0xFFF2A65A;
			case YOUTUBE -> 0xFFFF344A;
			case RADIO -> 0xFFFF4B78;
			case PODCAST -> 0xFFBC7AFF;
			case AUDIOBOOK -> 0xFF8B5CF6;
			case STREMIO -> 0xFFA855F7;
			case WEB -> 0xFFE8C07D;
			case GENERIC -> 0xFF10A37F;
		};
	}

	static int itemColor(String name, AddonInfo info) {
		if (info != null) return color(role(info));
		if (name == null) return 0xFFF2A65A;
		if (DashboardItems.MIRROR.equals(name)) return 0xFFC084FC;
		if (DashboardItems.DEM_TV.equals(name)) return 0xFFFFB38A;
		if (DashboardItems.FOLDERS.equals(name)) return 0xFFFFC14D;
		if (DashboardItems.FAVORITES.equals(name)) return 0xFFFF4165;
		if (DashboardItems.RECENT.equals(name)) return 0xFFBFB3A5;
		if (DashboardItems.PLAYLISTS.equals(name)) return 0xFFD9A3C7;
		if ("chatgpt".equalsIgnoreCase(name)) return 0xFF10A37F;
		return 0xFFF2A65A;
	}

	static GradientDrawable createBadgeDrawable(int color, float radius) {
		GradientDrawable badge = new GradientDrawable();
		badge.setShape(GradientDrawable.RECTANGLE);
		badge.setCornerRadius(radius);
		badge.setColor((color & 0x00FFFFFF) | 0x24000000);
		return badge;
	}

	static int priority(AddonInfo info) {
		return switch (role(info)) {
			case TV -> 0;
			case YOUTUBE -> 1;
			case RADIO -> 2;
			case PODCAST -> 3;
			case AUDIOBOOK -> 4;
			case WEB -> 5;
			case STREMIO -> 6;
			case GENERIC -> 7;
		};
	}

	enum Role {
		TV,
		YOUTUBE,
		RADIO,
		PODCAST,
		AUDIOBOOK,
		STREMIO,
		WEB,
		GENERIC
	}
}
