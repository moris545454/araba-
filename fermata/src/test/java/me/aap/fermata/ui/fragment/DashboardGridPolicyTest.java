package me.aap.fermata.ui.fragment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DashboardGridPolicyTest {
	@Test
	public void wideRowsFitThreeFeaturedCardsOrFiveShortcuts() {
		int spans = DashboardGridPolicy.gridSpans(3);
		assertEquals(spans, 3 * DashboardGridPolicy.itemSpan(true, false, spans));
		assertEquals(spans, 5 * DashboardGridPolicy.itemSpan(false, false, spans));
	}

	@Test
	public void narrowAndEditLayoutsUseWholeColumns() {
		for (int columns : new int[]{1, 2}) {
			int spans = DashboardGridPolicy.gridSpans(columns);
			assertEquals(columns, spans);
			assertEquals(1, DashboardGridPolicy.itemSpan(true, false, spans));
			assertEquals(1, DashboardGridPolicy.itemSpan(false, false, spans));
		}
	}

	@Test
	public void shelfAndPlaybackPanelsAlwaysSpanTheFullRow() {
		for (int columns = 1; columns <= 3; columns++) {
			int spans = DashboardGridPolicy.gridSpans(columns);
			assertEquals(spans, DashboardGridPolicy.itemSpan(false, true, spans));
		}
	}

	@Test
	public void everyCardFitsEvenWhenTheUserMixesLargeAndSmallCards() {
		for (int columns = 1; columns <= 3; columns++) {
			int spans = DashboardGridPolicy.gridSpans(columns);
			for (boolean featured : new boolean[]{true, false}) {
				int width = DashboardGridPolicy.itemSpan(featured, false, spans);
				assertTrue(width > 0 && width <= spans);
			}
		}
	}
}
