package me.aap.fermata.addon.tv;

import static me.aap.utils.collection.CollectionUtils.contains;

import me.aap.fermata.addon.tv.xtream.XtreamAccount;
import me.aap.fermata.addon.tv.stalker.StalkerAccount;
import me.aap.utils.function.IntSupplier;
import me.aap.utils.function.Supplier;
import me.aap.utils.log.Log;
import me.aap.utils.pref.PreferenceStore;
import me.aap.utils.pref.PreferenceStore.Pref;

final class TvSourceRepository {
	private static final Pref<IntSupplier> SOURCE_COUNTER =
			Pref.i("SOURCE_COUNTER", 0).withInheritance(false);
	private static final Pref<Supplier<int[]>> SOURCE_IDS =
			Pref.ia("SOURCE_IDS", () -> new int[0]).withInheritance(false);
	private static final String SOURCE_TYPE_PREFIX = "SOURCE_TYPE#";
	private static final String M3U_ID_PREFIX = "M3UID#";
	static final String DEFAULT_PLAYLIST_URL = "https://onureroz.com/indirmeler/turk/index.m3u";
	private static final Pref<me.aap.utils.function.BooleanSupplier> DEFAULT_PLAYLIST_SEEDED =
			Pref.b("TURKISH_PLAYLIST_SEEDED_V1", false).withInheritance(false);
	/** Turkish stations from the public Radio Browser directory, most voted first. */
	static final String DEFAULT_RADIO_URL = "https://all.api.radio-browser.info/m3u/stations/search" +
			"?countrycode=TR&order=votes&reverse=true&hidebroken=true&limit=150";
	private static final Pref<me.aap.utils.function.BooleanSupplier> DEFAULT_RADIO_SEEDED =
			Pref.b("TURKISH_RADIO_SEEDED_V1", false).withInheritance(false);
	private final PreferenceStore store;

	TvSourceRepository(PreferenceStore store) {
		this.store = store;
	}

	/** One-time registration; removal is respected even after the next application update. */
	synchronized void seedDefaultPlaylist(me.aap.utils.function.Function<String, String> urlForId,
			me.aap.utils.function.Supplier<String> createM3uId) {
		seedPlaylist(DEFAULT_PLAYLIST_SEEDED, DEFAULT_PLAYLIST_URL, urlForId, createM3uId);
	}

	/** The bundled radio list is registered once, after the TV list, with the same rules. */
	synchronized void seedDefaultRadioPlaylist(me.aap.utils.function.Function<String, String> urlForId,
			me.aap.utils.function.Supplier<String> createM3uId) {
		seedPlaylist(DEFAULT_RADIO_SEEDED, DEFAULT_RADIO_URL, urlForId, createM3uId);
	}

	private void seedPlaylist(Pref<me.aap.utils.function.BooleanSupplier> seeded, String url,
			me.aap.utils.function.Function<String, String> urlForId,
			me.aap.utils.function.Supplier<String> createM3uId) {
		if (store.getBooleanPref(seeded)) return;
		int[] ids = getSourceIds();
		for (int id : ids) {
			String m3uId = getM3uId(id);
			try {
				if (m3uId != null && url.equals(urlForId.apply(m3uId))) {
					store.applyBooleanPref(seeded, true);
					return;
				}
			} catch (RuntimeException failure) {
				Log.e(failure, "Skipping invalid source while checking a default playlist");
			}
		}
		String m3uId = createM3uId.get();
		int id = nextSourceId();
		int[] updated = java.util.Arrays.copyOf(ids, ids.length + 1);
		updated[ids.length] = id;
		try (PreferenceStore.Edit edit = store.editPreferenceStore()) {
			saveM3uSource(edit, id, m3uId);
			edit.setIntArrayPref(SOURCE_IDS, updated);
			edit.setBooleanPref(seeded, true);
		}
	}

	/** True when an M3U source already points at the given playlist location. */
	boolean containsM3uUrl(me.aap.utils.function.Function<String, String> urlForId, String url) {
		for (int id : getSourceIds()) {
			if (!TvSourceItem.TYPE_M3U.equals(getSourceType(id))) continue;
			String m3uId = getM3uId(id);
			if (m3uId == null) continue;
			try {
				if (TvPlaylistUrls.sameLocation(url, urlForId.apply(m3uId))) return true;
			} catch (RuntimeException failure) {
				Log.e(failure, "Skipping invalid source while checking playlist location");
			}
		}
		return false;
	}

	int[] getSourceIds() {
		return store.getIntArrayPref(SOURCE_IDS);
	}

	boolean hasSource(int sourceId) {
		return contains(getSourceIds(), sourceId);
	}

	void setSourceIds(int[] sourceIds) {
		store.applyIntArrayPref(SOURCE_IDS, sourceIds);
	}

	int nextSourceId() {
		return store.getIntPref(SOURCE_COUNTER) + 1;
	}

	int getSourceCounter() {
		return store.getIntPref(SOURCE_COUNTER);
	}

	PreferenceStore getStore() {
		return store;
	}

	void saveM3uSource(PreferenceStore.Edit e, int sourceId, String m3uId) {
		e.setIntPref(SOURCE_COUNTER, sourceId);
		e.setStringPref(sourceTypePref(sourceId), TvSourceItem.TYPE_M3U);
		e.setStringPref(m3uIdPref(sourceId), m3uId);
	}

	void saveXtreamSource(PreferenceStore.Edit e, int sourceId, XtreamAccount account) {
		e.setIntPref(SOURCE_COUNTER, sourceId);
		e.setStringPref(sourceTypePref(sourceId), TvSourceItem.TYPE_XTREAM);
		XtreamAccount.save(e, sourceId, account);
	}

	void updateXtreamSource(PreferenceStore.Edit e, int sourceId, XtreamAccount account) {
		e.setStringPref(sourceTypePref(sourceId), TvSourceItem.TYPE_XTREAM);
		XtreamAccount.save(e, sourceId, account);
	}

	void saveStalkerSource(PreferenceStore.Edit e, int sourceId, StalkerAccount account) {
		e.setIntPref(SOURCE_COUNTER, sourceId);
		e.setStringPref(sourceTypePref(sourceId), TvSourceItem.TYPE_STALKER);
		StalkerAccount.save(e, sourceId, account);
	}

	void updateStalkerSource(PreferenceStore.Edit e, int sourceId, StalkerAccount account) {
		e.setStringPref(sourceTypePref(sourceId), TvSourceItem.TYPE_STALKER);
		StalkerAccount.save(e, sourceId, account);
	}

	void removeM3uSourcePrefs(PreferenceStore.Edit e, int sourceId) {
		e.removePref(sourceTypePref(sourceId));
		e.removePref(m3uIdPref(sourceId));
	}

	void removeXtreamSourcePrefs(PreferenceStore.Edit e, int sourceId) {
		e.removePref(sourceTypePref(sourceId));
		XtreamAccount.remove(e, sourceId);
	}

	void removeStalkerSourcePrefs(PreferenceStore.Edit e, int sourceId) {
		e.removePref(sourceTypePref(sourceId));
		StalkerAccount.remove(e, sourceId);
	}

	void removeSourcePrefs(PreferenceStore.Edit e, int sourceId) {
		removeM3uSourcePrefs(e, sourceId);
		XtreamAccount.remove(e, sourceId);
		StalkerAccount.remove(e, sourceId);
	}

	String getM3uId(int sourceId) {
		return store.getStringPref(m3uIdPref(sourceId));
	}

	String getSourceType(int sourceId) {
		return getSourceType(store, sourceId);
	}

	void restoreSources(int counter, java.util.List<TvAddon.SourceBackup> sources) {
		int[] ids = new int[sources.size()];
		try (PreferenceStore.Edit edit = store.editPreferenceStore()) {
			for (int i = 0; i < sources.size(); i++) {
				TvAddon.SourceBackup source = sources.get(i);
				ids[i] = source.id;
				if (source.account != null) saveXtreamSource(edit, source.id, source.account);
				else if (source.stalkerAccount != null) {
					saveStalkerSource(edit, source.id, source.stalkerAccount);
				} else saveM3uSource(edit, source.id, source.m3uId);
			}
			edit.setIntPref(SOURCE_COUNTER, counter);
			edit.setIntArrayPref(SOURCE_IDS, ids);
		}
	}

	void removeSource(int sourceId) {
		int[] ids = getSourceIds();
		if (ids.length == 0) return;

		int[] newIds = new int[ids.length - 1];
		boolean removed = false;

		for (int i = 0, j = 0; i < ids.length; i++) {
			if (ids[i] == sourceId) removed = true;
			else if (j < newIds.length) newIds[j++] = ids[i];
			else return;
		}

		if (removed) {
			Log.i("Removing source: ", sourceId);
			try (PreferenceStore.Edit e = store.editPreferenceStore()) {
				e.setIntArrayPref(SOURCE_IDS, newIds);
				removeSourcePrefs(e, sourceId);
			}
		}
	}

	static String getSourceType(PreferenceStore store, int sourceId) {
		String type = store.getStringPref(sourceTypePref(sourceId));
		if (TvSourceItem.TYPE_XTREAM.equals(type)) return TvSourceItem.TYPE_XTREAM;
		if (TvSourceItem.TYPE_STALKER.equals(type)) return TvSourceItem.TYPE_STALKER;
		return TvSourceItem.TYPE_M3U;
	}

	static Pref<Supplier<String>> sourceTypePref(int sourceId) {
		return Pref.s(SOURCE_TYPE_PREFIX + sourceId);
	}

	private static Pref<Supplier<String>> m3uIdPref(int sourceId) {
		return Pref.s(M3U_ID_PREFIX + sourceId);
	}
}
