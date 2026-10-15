package me.aap.fermata.ui.voice;

import androidx.annotation.Nullable;

/** Deterministic, UI-independent result of parsing one voice utterance. */
public final class VoiceIntent {
	public enum Kind { PLAYBACK, ADDON_SEARCH, OPEN_ADDON, SELECTION, OPEN_SECTION, VOLUME, SETTING }

	public enum PlaybackAction {
		PLAY, PAUSE, STOP, NEXT, PREVIOUS, BACK, OPEN_CURRENT, PLAY_FAVORITES
	}

	public enum SearchAction { PLAY, FIND, OPEN }

	public enum VolumeAction { UP, DOWN, MUTE }

	private final Kind kind;
	private final PlaybackAction playbackAction;
	private final SearchAction searchAction;
	private final String addon;
	private final String query;
	private final int selectionIndex;

	private VoiceIntent(Kind kind, PlaybackAction playbackAction, SearchAction searchAction,
													 String addon, String query, int selectionIndex) {
		this.kind = kind;
		this.playbackAction = playbackAction;
		this.searchAction = searchAction;
		this.addon = addon;
		this.query = query;
		this.selectionIndex = selectionIndex;
	}

	public static VoiceIntent playback(PlaybackAction action) {
		return new VoiceIntent(Kind.PLAYBACK, action, null, null, null, -1);
	}

	public static VoiceIntent search(SearchAction action, @Nullable String addon, String query) {
		return new VoiceIntent(Kind.ADDON_SEARCH, null, action, addon, query, -1);
	}

	public static VoiceIntent openAddon(String addon) {
		return new VoiceIntent(Kind.OPEN_ADDON, null, SearchAction.OPEN, addon, null, -1);
	}

	/** Opens an app destination that is not an addon: settings, equalizer, home, mirror... */
	public static VoiceIntent openSection(String section) {
		return new VoiceIntent(Kind.OPEN_SECTION, null, null, section, null, -1);
	}

	public static VoiceIntent volume(VolumeAction action) {
		return new VoiceIntent(Kind.VOLUME, null, null, null, action.name(), -1);
	}

	/** A display or playback setting, e.g. setting("video_scale", "fill"). */
	public static VoiceIntent setting(String name, String value) {
		return new VoiceIntent(Kind.SETTING, null, null, name, value, -1);
	}

	@Nullable
	public VolumeAction getVolumeAction() {
		return (kind == Kind.VOLUME) ? VolumeAction.valueOf(query) : null;
	}

	public static VoiceIntent selection(int index) {
		return new VoiceIntent(Kind.SELECTION, null, null, null, null, index);
	}

	public Kind getKind() {
		return kind;
	}

	@Nullable
	public PlaybackAction getPlaybackAction() {
		return playbackAction;
	}

	@Nullable
	public SearchAction getSearchAction() {
		return searchAction;
	}

	@Nullable
	public String getAddon() {
		return addon;
	}

	@Nullable
	public String getQuery() {
		return query;
	}

	public int getSelectionIndex() {
		return selectionIndex;
	}
}
