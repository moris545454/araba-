package me.aap.fermata.ui.fragment;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.ContextCompat;

import java.io.File;

import me.aap.fermata.R;
import me.aap.fermata.media.lib.MediaLib.Folders;
import me.aap.fermata.ui.activity.MainActivityDelegate;
import me.aap.utils.function.BooleanSupplier;
import me.aap.utils.pref.PreferenceStore;
import me.aap.utils.ui.UiUtils;

/**
 * The home screen Media card: asks once for the phone's video and music access, adds the usual
 * media folders and opens them. In the car the access can only be granted on the phone.
 */
final class MediaLibraryAccess {
	private static final PreferenceStore.Pref<BooleanSupplier> FOLDERS_ADDED =
			PreferenceStore.Pref.b("MEDIA_LIBRARY_FOLDERS_ADDED_V1", false);
	static final String[] STANDARD_FOLDERS = {
			Environment.DIRECTORY_DCIM, Environment.DIRECTORY_MOVIES, Environment.DIRECTORY_DOWNLOADS,
			Environment.DIRECTORY_MUSIC};

	private MediaLibraryAccess() {
	}

	static String[] permissions(int sdk) {
		if (sdk >= Build.VERSION_CODES.TIRAMISU) {
			return new String[]{Manifest.permission.READ_MEDIA_VIDEO,
					Manifest.permission.READ_MEDIA_AUDIO};
		}
		return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
	}

	static boolean isGranted(Context ctx) {
		return ContextCompat.checkSelfPermission(ctx, permissions(Build.VERSION.SDK_INT)[0]) ==
				PERMISSION_GRANTED;
	}

	static void open(MainActivityDelegate a) {
		if (isGranted(a.getContext())) {
			addStandardFoldersOnce(a);
			return;
		}
		if (a.isCarActivityNotMirror()) {
			UiUtils.showAlert(a.getContext(), R.string.dashboard_media_grant_on_phone);
			show(a);
			return;
		}
		a.getAppActivity().checkPermissions(permissions(Build.VERSION.SDK_INT)).main()
				.onCompletion((result, error) -> {
					if (isGranted(a.getContext())) addStandardFoldersOnce(a);
					else show(a);
				});
	}

	private static void addStandardFoldersOnce(MainActivityDelegate a) {
		PreferenceStore store = a.getPrefs();
		if (store.getBooleanPref(FOLDERS_ADDED)) {
			show(a);
			return;
		}
		store.applyBooleanPref(FOLDERS_ADDED, true);
		Folders folders = a.getLib().getFolders();
		File root = Environment.getExternalStorageDirectory();
		me.aap.utils.async.FutureSupplier<?> chain = me.aap.utils.async.Completed.completedVoid();
		for (String name : STANDARD_FOLDERS) {
			File dir = new File(root, name);
			if (!dir.isDirectory()) continue;
			Uri uri = Uri.fromFile(dir);
			chain = chain.then(v -> folders.addItem(uri));
		}
		chain.main().onCompletion((v, err) -> show(a));
	}

	private static void show(MainActivityDelegate a) {
		a.setActiveNavItemId(R.id.dashboard_fragment);
		a.showFragmentWhenReady(R.id.folders_fragment);
	}
}
