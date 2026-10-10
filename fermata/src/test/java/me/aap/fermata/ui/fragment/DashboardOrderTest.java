package me.aap.fermata.ui.fragment;

import static org.junit.Assert.assertArrayEquals;

import java.util.List;

import org.junit.Test;

public class DashboardOrderTest {

	@Test
	public void featuredMediaLeadWithoutLosingOtherFeaturesOrTheirOrder() {
		assertArrayEquals(new String[]{"tv", "youtube", "screen_mirroring",
				"recent", "custom", "folders",
				"favorites", "playlists"},
				DashboardOrder.feature(new String[]{"recent",
						"custom", "youtube", "folders", "tv"},
						List.of("tv", "youtube", "screen_mirroring",
								"favorites", "playlists"),
						List.of("tv", "youtube", "screen_mirroring")));
	}

	@Test
	public void featuredOrderIsStableAndDeduplicated() {
		List<String> defaults = List.of("tv", "youtube", "screen_mirroring", "radio");
		List<String> featured = List.of("tv", "youtube", "screen_mirroring");
		String[] first = DashboardOrder.feature(null, defaults, featured);
		assertArrayEquals(new String[]{"tv", "youtube", "screen_mirroring", "radio"}, first);
		assertArrayEquals(first, DashboardOrder.feature(first, defaults, featured));
	}

    @Test
    public void mergePreservesUserEditsAfterMigration() {
        assertArrayEquals(new String[]{"radio", "screen_mirroring", "youtube", "tv", "folders"},
                DashboardOrder.merge(new String[]{"radio", "screen_mirroring", "youtube", "tv"},
                        List.of("tv", "youtube", "screen_mirroring", "radio", "folders")));
    }
}
