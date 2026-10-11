package me.aap.fermata.auto;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProjectionLauncherContractTest {
	@Test
	public void autoLauncherRoutesOnlyPendingFullscreenRequestsToPermissions() throws Exception {
		Path root = Paths.get("src", "auto");
		String manifest = read(root.resolve("AndroidManifest.xml"));
		String launcher = read(root.resolve(Paths.get("java", "me", "app", "fermatax",
				"auto", "PhoneLauncherActivity.java")));

		assertTrue(manifest.contains("me.app.fermatax.auto.PhoneLauncherActivity"));
		assertTrue(manifest.contains("android.intent.category.LAUNCHER"));
		assertTrue(launcher.contains("ProjectionActivity.resumePendingRequest(this)"));
		assertTrue(launcher.contains("new Intent(this, MainActivity.class)"));
		assertTrue(launcher.contains("target.setAction(Intent.ACTION_MAIN)"));
		assertTrue(launcher.contains("FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP"));
		assertFalse(launcher.contains("new Intent(getIntent())"));
		assertFalse(launcher.contains("target.addCategory"));
	}

	@Test
	public void carCaptureRequestsPhoneConsentWithNotificationFallback() throws Exception {
		String source = read(Paths.get("src", "auto", "java", "me", "app", "fermatax",
				"auto", "ProjectionActivity.java"));

		assertTrue(source.contains("static boolean resumePendingRequest(Context context)"));
		assertTrue(source.contains("showPermissionNotification(FermataApplication.get())"));
        assertTrue(source.contains("resumePendingRequest(phone != null && phone.hasWindowFocus() ?"));
        assertFalse(source.contains("if (viewingOnly && phone != null"));
		assertFalse(source.contains("REQUEST_TIMEOUT_MS"));
		assertFalse(source.contains("Projection permission screen did not open"));
	}

	@Test
	public void captureNeverRequestsRootOverlayAccessibilityOrGlobalSettings() throws Exception {
		String source = read(Paths.get("src", "auto", "java", "me", "app", "fermatax",
				"auto", "ProjectionActivity.java"));

		assertFalse(source.contains("ContentObserver"));
		assertTrue(source.contains("capture = requestScreenCapturePermission()"));
		assertFalse(source.contains("ACTION_MANAGE_OVERLAY_PERMISSION"));
		assertFalse(source.contains("ACTION_MANAGE_WRITE_SETTINGS"));
		assertFalse(source.contains("ACTION_ACCESSIBILITY_SETTINGS"));
		assertFalse(source.contains("Su.get()"));
		assertFalse(source.contains("autoClickOnButton"));
	}

	private static String read(Path path) throws Exception {
		return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
	}
}
