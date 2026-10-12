package me.aap.fermata.addon.tv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TvPlaylistUrlsTest {
	@Test
	public void acceptsHttpAndHttpsLinks() {
		assertEquals("https://example.com/list.m3u",
				TvPlaylistUrls.normalize("  https://example.com/list.m3u  "));
		assertEquals("http://example.com:8080/get.php?type=m3u",
				TvPlaylistUrls.normalize("http://example.com:8080/get.php?type=m3u"));
	}

	@Test
	public void completesBareHostWithHttp() {
		assertEquals("http://example.com/list.m3u",
				TvPlaylistUrls.normalize("example.com/list.m3u"));
	}

	@Test
	public void rejectsUnusableInput() {
		assertNull(TvPlaylistUrls.normalize(null));
		assertNull(TvPlaylistUrls.normalize("   "));
		assertNull(TvPlaylistUrls.normalize("ftp://example.com/list.m3u"));
		assertNull(TvPlaylistUrls.normalize("file:///sdcard/list.m3u"));
		assertNull(TvPlaylistUrls.normalize("https://exa mple.com/list.m3u"));
		assertNull(TvPlaylistUrls.normalize("https:///list.m3u"));
	}

	@Test
	public void suggestsFileNameOrHost() {
		assertEquals("example.com - sports",
				TvPlaylistUrls.suggestName("https://example.com/lists/sports.m3u8"));
		assertEquals("onureroz.com",
				TvPlaylistUrls.suggestName("https://onureroz.com/indirmeler/turk/index.m3u"));
		assertEquals("example.com", TvPlaylistUrls.suggestName("http://example.com/"));
		assertEquals("panel.example.com",
				TvPlaylistUrls.suggestName("http://panel.example.com/get.php?username=u"));
		assertTrue(TvPlaylistUrls.suggestName("https://" + "a".repeat(100) + ".com/").length() <= 48);
	}

	@Test
	public void comparesLocationsIgnoringHostCaseAndTrailingSlash() {
		assertTrue(TvPlaylistUrls.sameLocation(TvSourceRepository.DEFAULT_PLAYLIST_URL,
				"HTTPS://OnurEroz.com/indirmeler/turk/index.m3u"));
		assertTrue(TvPlaylistUrls.sameLocation("http://example.com/x/", "http://example.com/x"));
		assertFalse(TvPlaylistUrls.sameLocation("http://example.com/A.m3u", "http://example.com/a.m3u"));
		assertFalse(TvPlaylistUrls.sameLocation("http://example.com/a.m3u", null));
	}
}
