package me.aap.fermata.ui.fragment;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DashboardWebShortcutsTest {
	@Test
	public void normalizesWebsiteLinks() {
		assertEquals("https://example.com", DashboardWebShortcuts.normalize(" example.com "));
		assertEquals("http://example.com/tv", DashboardWebShortcuts.normalize("http://example.com/tv"));
		assertNull(DashboardWebShortcuts.normalize(""));
		assertNull(DashboardWebShortcuts.normalize("javascript:alert(1)"));
		assertNull(DashboardWebShortcuts.normalize("file:///sdcard/a.html"));
		assertNull(DashboardWebShortcuts.normalize("https://exa mple.com"));
	}

	@Test
	public void suggestsHostWithoutWww() {
		assertEquals("example.com", DashboardWebShortcuts.suggestTitle("https://www.example.com/a"));
		assertEquals("windasist.com", DashboardWebShortcuts.suggestTitle("https://windasist.com"));
	}

	@Test
	public void roundTripsAndCleansStoredShortcuts() {
		List<DashboardWebShortcuts.Shortcut> list = new ArrayList<>();
		list.add(new DashboardWebShortcuts.Shortcut("Haber", "https://example.com/news"));
		list.add(new DashboardWebShortcuts.Shortcut("  ", "https://www.example.org"));
		String[] stored = DashboardWebShortcuts.encode(list);
		assertArrayEquals(new String[]{"Haber\nhttps://example.com/news",
				"example.org\nhttps://www.example.org"}, stored);
		List<DashboardWebShortcuts.Shortcut> decoded = DashboardWebShortcuts.decode(stored);
		assertEquals(2, decoded.size());
		assertEquals("Haber", decoded.get(0).title());
		assertEquals("example.org", decoded.get(1).title());
	}

	@Test
	public void ignoresBrokenEntriesAndCapsTheList() {
		assertTrue(DashboardWebShortcuts.decode(null).isEmpty());
		assertTrue(DashboardWebShortcuts.decode(new String[]{null, "no-separator",
				"Bad\nftp://example.com"}).isEmpty());
		String[] many = new String[10];
		for (int i = 0; i < many.length; i++) many[i] = "S" + i + "\nhttps://s" + i + ".example.com";
		assertEquals(DashboardWebShortcuts.MAX_SHORTCUTS, DashboardWebShortcuts.decode(many).size());
	}

	@Test
	public void mapsItemNamesToIndexes() {
		assertEquals(2, DashboardWebShortcuts.indexOf(DashboardWebShortcuts.itemName(2)));
		assertEquals(-1, DashboardWebShortcuts.indexOf(DashboardWebShortcuts.NAME_PREFIX + "add"));
		assertEquals(-1, DashboardWebShortcuts.indexOf("windasist"));
		assertEquals(-1, DashboardWebShortcuts.indexOf(DashboardWebShortcuts.itemName(99)));
	}
}
