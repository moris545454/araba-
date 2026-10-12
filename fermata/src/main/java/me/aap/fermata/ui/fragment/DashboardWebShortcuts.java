package me.aap.fermata.ui.fragment;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Pure rules for the user's own Dashboard web shortcuts; storage stays in DashboardItems. */
final class DashboardWebShortcuts {
	static final int MAX_SHORTCUTS = 6;
	static final String NAME_PREFIX = "web_shortcut:";
	private static final int MAX_TITLE_LENGTH = 32;
	private static final char SEPARATOR = '\n';

	private DashboardWebShortcuts() {
	}

	record Shortcut(String title, String url) {
	}

	static List<Shortcut> decode(String[] values) {
		List<Shortcut> shortcuts = new ArrayList<>();
		if (values == null) return shortcuts;
		for (String value : values) {
			if (value == null) continue;
			int sep = value.indexOf(SEPARATOR);
			if (sep <= 0) continue;
			String url = normalize(value.substring(sep + 1));
			if (url == null) continue;
			shortcuts.add(new Shortcut(cleanTitle(value.substring(0, sep), url), url));
			if (shortcuts.size() == MAX_SHORTCUTS) break;
		}
		return shortcuts;
	}

	static String[] encode(List<Shortcut> shortcuts) {
		String[] values = new String[Math.min(shortcuts.size(), MAX_SHORTCUTS)];
		for (int i = 0; i < values.length; i++) {
			Shortcut s = shortcuts.get(i);
			values[i] = cleanTitle(s.title(), s.url()) + SEPARATOR + s.url();
		}
		return values;
	}

	/** Returns a trimmed HTTP(S) URL; a bare host is completed with https://. */
	static String normalize(String input) {
		if (input == null) return null;
		String url = input.trim();
		if (url.isEmpty()) return null;
		for (int i = 0; i < url.length(); i++) {
			if (Character.isWhitespace(url.charAt(i))) return null;
		}
		if (!url.contains("://")) url = "https://" + url;
		try {
			URI uri = new URI(url);
			String scheme = uri.getScheme();
			if (scheme == null) return null;
			scheme = scheme.toLowerCase(Locale.ROOT);
			if (!"http".equals(scheme) && !"https".equals(scheme)) return null;
			String host = uri.getHost();
			if ((host == null) || host.isEmpty()) return null;
			return url;
		} catch (URISyntaxException invalid) {
			return null;
		}
	}

	/** The host without a leading "www.", used when the user leaves the name empty. */
	static String suggestTitle(String url) {
		try {
			String host = new URI(url).getHost();
			if ((host != null) && !host.isEmpty()) {
				if (host.regionMatches(true, 0, "www.", 0, 4) && (host.length() > 4))
					host = host.substring(4);
				return limit(host);
			}
		} catch (URISyntaxException ignore) {
		}
		return limit(url);
	}

	static String subtitle(String url) {
		return suggestTitle(url);
	}

	static String itemName(int index) {
		return NAME_PREFIX + index;
	}

	/** Index of a shortcut item name, or -1 when the name is not a web shortcut. */
	static int indexOf(String name) {
		if ((name == null) || !name.startsWith(NAME_PREFIX)) return -1;
		try {
			int index = Integer.parseInt(name.substring(NAME_PREFIX.length()));
			return ((index >= 0) && (index < MAX_SHORTCUTS)) ? index : -1;
		} catch (NumberFormatException invalid) {
			return -1;
		}
	}

	private static String cleanTitle(String title, String url) {
		String t = (title == null) ? "" : title.replace(SEPARATOR, ' ').trim();
		return t.isEmpty() ? suggestTitle(url) : limit(t);
	}

	private static String limit(String s) {
		return (s.length() <= MAX_TITLE_LENGTH) ? s : s.substring(0, MAX_TITLE_LENGTH);
	}
}
