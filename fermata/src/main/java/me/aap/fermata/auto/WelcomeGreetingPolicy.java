package me.aap.fermata.auto;

/** Pure rules for the optional spoken welcome when the phone connects to Android Auto. */
public final class WelcomeGreetingPolicy {
	public enum PartOfDay { MORNING, DAY, EVENING, NIGHT }

	private static final int MAX_NAME_LENGTH = 24;

	private WelcomeGreetingPolicy() {
	}

	/** 05-11 morning, 12-17 day, 18-22 evening, otherwise night. */
	public static PartOfDay partOfDay(int hour) {
		if ((hour >= 5) && (hour < 12)) return PartOfDay.MORNING;
		if ((hour >= 12) && (hour < 18)) return PartOfDay.DAY;
		if ((hour >= 18) && (hour < 23)) return PartOfDay.EVENING;
		return PartOfDay.NIGHT;
	}

	/** Greets when enabled; with once-a-day only when the last greeting was on an earlier day. */
	public static boolean shouldGreet(boolean enabled, boolean oncePerDay, long lastGreetingDay,
			long today) {
		if (!enabled) return false;
		return !oncePerDay || (lastGreetingDay != today);
	}

	/** A spoken-safe name: trimmed, single line, without markup characters, length-limited. */
	public static String cleanName(String name) {
		if (name == null) return "";
		StringBuilder b = new StringBuilder(name.length());
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (Character.isLetterOrDigit(c) || (c == ' ') || (c == '-') || (c == '\'') ||
					(c == '’') || (c == '.')) b.append(c);
			else if (Character.isWhitespace(c)) b.append(' ');
		}
		String s = b.toString().trim().replaceAll(" +", " ");
		return (s.length() <= MAX_NAME_LENGTH) ? s : s.substring(0, MAX_NAME_LENGTH).trim();
	}
}
