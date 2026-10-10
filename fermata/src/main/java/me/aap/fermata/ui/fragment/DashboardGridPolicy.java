package me.aap.fermata.ui.fragment;

/** Three featured cards and five compact shortcuts share a row on wide screens. */
final class DashboardGridPolicy {
	private DashboardGridPolicy() {
	}

	static int gridSpans(int columns) {
		return columns >= 3 ? 15 : Math.max(1, columns);
	}

	static int itemSpan(boolean featured, boolean wide, int spans) {
		if (wide) return spans;
		if (spans == 15) return featured ? 5 : 3;
		return 1;
	}
}
