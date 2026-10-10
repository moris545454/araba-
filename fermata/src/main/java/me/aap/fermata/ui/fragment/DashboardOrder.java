package me.aap.fermata.ui.fragment;

import java.util.Collection;
import java.util.LinkedHashSet;

/** Pure ordering rules shared by the one-time dashboard layout migration. */
final class DashboardOrder {
	private DashboardOrder() {
	}

	static String[] merge(String[] persisted, Collection<String> defaults) {
		LinkedHashSet<String> merged = new LinkedHashSet<>();
		if (persisted != null) {
			for (String name : persisted) merged.add(name);
		}
		merged.addAll(defaults);
		return merged.toArray(new String[0]);
	}

	static String[] feature(String[] persisted, Collection<String> defaults,
							Collection<String> featured) {
		LinkedHashSet<String> ordered = new LinkedHashSet<>(featured);
		for (String name : merge(persisted, defaults)) ordered.add(name);
		return ordered.toArray(new String[0]);
	}
}
