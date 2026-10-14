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
			case TV -> 0xFFF0B45A;
			case YOUTUBE -> 0xFFE5484D;
			case RADIO -> 0xFFE8826A;
			case PODCAST -> 0xFFD99A7A;
			case AUDIOBOOK -> 0xFFC7A27C;
			case STREMIO -> 0xFFC7A27C;
			case WEB -> 0xFFD9C29A;
			case GENERIC -> 0xFF9FB7A8;
		};
	}

	static int itemColor(String name, AddonInfo info) {
		if (info != null) return color(role(info));
		if (name == null) return 0xFFF0B45A;
		if (DashboardItems.MIRROR.equals(name)) return 0xFF9FB7A8;
		if (DashboardItems.DEM_TV.equals(name)) return 0xFFF0B45A;
		if (DashboardItems.FOLDERS.equals(name)) return 0xFFE3C26B;
		if (DashboardItems.SETTINGS.equals(name)) return 0xFFC9C2B6;
		if (DashboardItems.FAVORITES.equals(name)) return 0xFFE8826A;
		if (DashboardItems.RECENT.equals(name)) return 0xFFC9C2B6;
		if (DashboardItems.PLAYLISTS.equals(name)) return 0xFFC7A27C;
		if ("chatgpt".equalsIgnoreCase(name)) return 0xFF9FB7A8;
		return 0xFFF0B45A;
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
