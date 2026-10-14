package me.aap.fermata.ui.fragment;

import static org.junit.Assert.assertArrayEquals;

import android.Manifest;

import org.junit.Test;

public class MediaLibraryAccessTest {
	@Test
	public void asksOnlyForVideoAndMusicNeverAllFiles() {
		assertArrayEquals(new String[]{Manifest.permission.READ_MEDIA_VIDEO,
				Manifest.permission.READ_MEDIA_AUDIO}, MediaLibraryAccess.permissions(33));
		assertArrayEquals(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
				MediaLibraryAccess.permissions(32));
	}
}
