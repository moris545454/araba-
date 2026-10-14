package me.aap.fermata.addon.web;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WebStartUrlTest {
	@Test
	public void webCardStartsAtGoogleUnlessTheUserWasBrowsingElsewhere() {
		assertEquals(WebBrowserAddon.GOOGLE_URL, WebBrowserAddon.resolveStartUrl(null));
		assertEquals(WebBrowserAddon.GOOGLE_URL, WebBrowserAddon.resolveStartUrl(""));
		assertEquals(WebBrowserAddon.GOOGLE_URL, WebBrowserAddon.resolveStartUrl(
				"https://demtv.demiralayboncuk-092.workers.dev/"));
		assertEquals(WebBrowserAddon.GOOGLE_URL,
				WebBrowserAddon.resolveStartUrl("https://windasist.com/x"));
		assertEquals("https://example.com/page",
				WebBrowserAddon.resolveStartUrl("https://example.com/page"));
	}
}
