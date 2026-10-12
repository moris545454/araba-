package me.aap.fermata.addon.tv;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

/** Pure URL rules for the quick "add playlist by link" flow. */
final class TvPlaylistUrls {
	private static final int MAX_NAME_LENGTH = 48;

	private TvPlaylistUrls() {
	}

	/**
	 * Returns a trimmed HTTP(S) playlist URL, or {@code null} when the input cannot be used.
	 * A bare host such as {@code example.com/list.m3u} is completed with {@code http://}.
	 */
	static String normalize(String input) {
		if (input == null) return null;
		String url = input.trim();
		if (url.isEmpty() || containsWhitespace(url)) return null;
		if (!url.contains("://")) url = "http://" + url;

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

	/** A readable default name: the playlist file name without extension, otherwise the host. */
	static String suggestName(String url) {
		try {
			URI uri = new URI(url);
			String path = uri.getPath();
			if (path != null) {
				int slash = path.lastIndexOf('/');
				String file = (slash >= 0) ? path.substring(slash + 1) : path;
				int dot = file.lastIndexOf('.');
				if (dot > 0) file = file.substring(0, dot);
				if (!file.isEmpty() && !"index".equalsIgnoreCase(file) &&
						!"get".equalsIgnoreCase(file) && !"playlist".equalsIgnoreCase(file)) {
					return limit(uri.getHost() + " - " + file);
				}
			}
			String host = uri.getHost();
			if ((host != null) && !host.isEmpty()) return limit(host);
		} catch (URISyntaxException ignore) {
		}
		return limit(url);
	}

	/** Compares two playlist locations ignoring scheme/host case and a trailing slash. */
	static boolean sameLocation(String a, String b) {
		if ((a == null) || (b == null)) return false;
		return canonical(a).equals(canonical(b));
	}

	private static String canonical(String url) {
		String u = url.trim();
		while (u.endsWith("/")) u = u.substring(0, u.length() - 1);
		try {
			URI uri = new URI(u);
			String scheme = uri.getScheme();
			String host = uri.getHost();
			if ((scheme == null) || (host == null)) return u;
			int prefix = scheme.length() + 3 + ((uri.getRawUserInfo() == null) ? 0 :
					uri.getRawUserInfo().length() + 1) + host.length();
			if (prefix > u.length()) return u;
			return scheme.toLowerCase(Locale.ROOT) + "://" +
					((uri.getRawUserInfo() == null) ? "" : uri.getRawUserInfo() + "@") +
					host.toLowerCase(Locale.ROOT) + u.substring(prefix);
		} catch (URISyntaxException invalid) {
			return u;
		}
	}

	private static boolean containsWhitespace(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (Character.isWhitespace(s.charAt(i))) return true;
		}
		return false;
	}

	private static String limit(String name) {
		return (name.length() <= MAX_NAME_LENGTH) ? name : name.substring(0, MAX_NAME_LENGTH);
	}
}
