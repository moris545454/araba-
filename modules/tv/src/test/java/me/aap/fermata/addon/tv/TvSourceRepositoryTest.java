package me.aap.fermata.addon.tv;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import me.aap.utils.pref.BasicPreferenceStore;
import me.aap.utils.pref.PreferenceStore;

public class TvSourceRepositoryTest {
	@Test
	public void defaultPlaylistIsAddedOnceAndStaysDeleted() {
		BasicPreferenceStore store = new BasicPreferenceStore();
		TvSourceRepository repo = new TvSourceRepository(store);
		repo.seedDefaultPlaylist(id -> null, () -> "turkish-list");
		assertArrayEquals(new int[]{1}, repo.getSourceIds());
		assertEquals("turkish-list", repo.getM3uId(1));
		repo.removeSource(1);
		new TvSourceRepository(store).seedDefaultPlaylist(id -> null, () -> {
			throw new AssertionError("Deleted source must not be recreated");
		});
		assertArrayEquals(new int[0], repo.getSourceIds());
	}

	@Test
	public void defaultPlaylistDoesNotDuplicateExistingOrReplaceOtherSources() {
		BasicPreferenceStore store = new BasicPreferenceStore();
		TvSourceRepository repo = new TvSourceRepository(store);
		try (PreferenceStore.Edit edit = store.editPreferenceStore()) {
			repo.saveM3uSource(edit, 1, "existing");
		}
		repo.setSourceIds(new int[]{1});
		repo.seedDefaultPlaylist(id -> TvSourceRepository.DEFAULT_PLAYLIST_URL, () -> {
			throw new AssertionError("Existing default source must not be duplicated");
		});
		assertArrayEquals(new int[]{1}, repo.getSourceIds());
	}

	@Test
	public void seedPreservesExistingSourcesAndAllocatesANewId() {
		BasicPreferenceStore store = new BasicPreferenceStore();
		TvSourceRepository repo = new TvSourceRepository(store);
		try (PreferenceStore.Edit edit = store.editPreferenceStore()) {
			repo.saveM3uSource(edit, 7, "other-list");
		}
		repo.setSourceIds(new int[]{7});
		repo.seedDefaultPlaylist(id -> "https://example.com/list.m3u", () -> "turkish-list");
		assertArrayEquals(new int[]{7, 8}, repo.getSourceIds());
		assertEquals("other-list", repo.getM3uId(7));
		assertEquals("turkish-list", repo.getM3uId(8));
	}

	@Test
	public void failedRegistrationCanRetryWithoutMarkingThePlaylistSeeded() {
		BasicPreferenceStore store = new BasicPreferenceStore();
		TvSourceRepository repo = new TvSourceRepository(store);
		try {
			repo.seedDefaultPlaylist(id -> null, () -> { throw new IllegalStateException("disk unavailable"); });
			org.junit.Assert.fail("Registration must report the failure");
		} catch (IllegalStateException expected) {
			assertArrayEquals(new int[0], repo.getSourceIds());
		}
		repo.seedDefaultPlaylist(id -> null, () -> "turkish-list");
		assertArrayEquals(new int[]{1}, repo.getSourceIds());
	}

	@Test
	public void radioListIsSeededOnceAfterTheTvListAndStaysDeleted() {
		BasicPreferenceStore store = new BasicPreferenceStore();
		TvSourceRepository repo = new TvSourceRepository(store);
		repo.seedDefaultPlaylist(id -> null, () -> "turkish-list");
		repo.seedDefaultRadioPlaylist(id -> null, () -> "turkish-radio");
		assertArrayEquals(new int[]{1, 2}, repo.getSourceIds());
		assertEquals("turkish-radio", repo.getM3uId(2));

		repo.removeSource(2);
		new TvSourceRepository(store).seedDefaultRadioPlaylist(id -> null, () -> {
			throw new AssertionError("Deleted radio list must not be recreated");
		});
		assertArrayEquals(new int[]{1}, repo.getSourceIds());
	}

	@Test
	public void radioSeedingKeepsUpdatedInstallSourcesAndDoesNotDuplicate() {
		BasicPreferenceStore store = new BasicPreferenceStore();
		TvSourceRepository repo = new TvSourceRepository(store);
		try (PreferenceStore.Edit edit = store.editPreferenceStore()) {
			repo.saveM3uSource(edit, 3, "user-list");
			repo.saveM3uSource(edit, 4, "radio");
		}
		repo.setSourceIds(new int[]{3, 4});
		repo.seedDefaultRadioPlaylist(
				id -> "radio".equals(id) ? TvSourceRepository.DEFAULT_RADIO_URL : "https://example.com/a.m3u",
				() -> { throw new AssertionError("Existing radio list must not be duplicated"); });
		assertArrayEquals(new int[]{3, 4}, repo.getSourceIds());
		assertEquals("user-list", repo.getM3uId(3));
	}

	@Test
	public void findsExistingM3uLocationOnly() {
		BasicPreferenceStore store = new BasicPreferenceStore();
		TvSourceRepository repo = new TvSourceRepository(store);
		try (PreferenceStore.Edit edit = store.editPreferenceStore()) {
			repo.saveM3uSource(edit, 1, "default");
			repo.saveM3uSource(edit, 2, "other");
		}
		repo.setSourceIds(new int[]{1, 2});
		java.util.Map<String, String> urls = java.util.Map.of(
				"default", TvSourceRepository.DEFAULT_PLAYLIST_URL,
				"other", "https://example.com/other.m3u");
		assertEquals(true, repo.containsM3uUrl(urls::get, "https://example.com/other.m3u/"));
		assertEquals(true, repo.containsM3uUrl(urls::get, TvSourceRepository.DEFAULT_PLAYLIST_URL));
		assertFalse(repo.containsM3uUrl(urls::get, "https://example.com/new.m3u"));

		repo.removeSource(1);
		assertFalse(repo.containsM3uUrl(urls::get, TvSourceRepository.DEFAULT_PLAYLIST_URL));
		assertArrayEquals(new int[]{2}, repo.getSourceIds());
	}

	@Test
	public void defaultsMissingSourceTypeToM3u() {
		BasicPreferenceStore store = new BasicPreferenceStore();
		assertEquals(TvSourceItem.TYPE_M3U, TvSourceRepository.getSourceType(store, 7));
	}

	@Test
	public void savesLegacyM3uSourcePrefs() {
		BasicPreferenceStore store = new BasicPreferenceStore();
		TvSourceRepository repo = new TvSourceRepository(store);

		assertEquals(1, repo.nextSourceId());
		try (PreferenceStore.Edit edit = store.editPreferenceStore()) {
			repo.saveM3uSource(edit, 1, "m3u-resource-id");
		}
		repo.setSourceIds(new int[]{1});

		assertArrayEquals(new int[]{1}, repo.getSourceIds());
		assertEquals(2, repo.nextSourceId());
		assertEquals(TvSourceItem.TYPE_M3U, repo.getSourceType(1));
		assertEquals("m3u-resource-id", repo.getM3uId(1));
	}

	@Test
	public void readsXtreamSourceTypeMarker() {
		BasicPreferenceStore store = new BasicPreferenceStore();
		TvSourceRepository repo = new TvSourceRepository(store);

		try (PreferenceStore.Edit edit = store.editPreferenceStore()) {
			edit.setStringPref(TvSourceRepository.sourceTypePref(2), TvSourceItem.TYPE_XTREAM);
		}

		assertEquals(TvSourceItem.TYPE_XTREAM, repo.getSourceType(2));
	}

	@Test
	public void readsStalkerSourceTypeMarker() {
		BasicPreferenceStore store = new BasicPreferenceStore();
		TvSourceRepository repo = new TvSourceRepository(store);
		try (PreferenceStore.Edit edit = store.editPreferenceStore()) {
			edit.setStringPref(TvSourceRepository.sourceTypePref(3), TvSourceItem.TYPE_STALKER);
		}
		assertEquals(TvSourceItem.TYPE_STALKER, repo.getSourceType(3));
	}

	@Test
	public void removesSourcePrefs() {
		BasicPreferenceStore store = new BasicPreferenceStore();
		TvSourceRepository repo = new TvSourceRepository(store);

		try (PreferenceStore.Edit edit = store.editPreferenceStore()) {
			repo.saveM3uSource(edit, 1, "m3u-resource-id");
		}
		repo.setSourceIds(new int[]{1});

		repo.removeSource(1);

		assertArrayEquals(new int[0], repo.getSourceIds());
		assertFalse(repo.hasSource(1));
		assertEquals(TvSourceItem.TYPE_M3U, repo.getSourceType(1));
		assertEquals(null, repo.getM3uId(1));
	}
}
