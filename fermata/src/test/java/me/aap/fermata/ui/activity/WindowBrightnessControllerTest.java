package me.aap.fermata.ui.activity;

import android.app.Activity;
import android.app.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, manifest = Config.NONE, application = Application.class)
public class WindowBrightnessControllerTest {
    @Test public void carPlaybackNeverCallsAnUnsupportedWindowApi() {
        var controller = new WindowBrightnessController(() -> {
            throw new AssertionError("Android Auto window must not be touched");
        });
        controller.enter(true, 100);
        controller.set(true, 200);
        controller.restore(true);
        controller.reset(true);
        assertEquals(255, controller.get(true));
    }

    @Test public void phoneWindowRestoresBrightnessAfterVideoAndClampsValues() {
        try (var activity = Robolectric.buildActivity(Activity.class).setup()) {
            var window = activity.get().getWindow();
            var controller = new WindowBrightnessController(() -> window);
            assertEquals(-1f, window.getAttributes().screenBrightness, 0f);
            controller.enter(false, 128);
            assertEquals(128f / 255f, window.getAttributes().screenBrightness, 0.0001f);
            controller.set(false, 400);
            assertEquals(1f, window.getAttributes().screenBrightness, 0f);
            controller.restore(false);
            assertEquals(-1f, window.getAttributes().screenBrightness, 0f);
        }
    }

    @Test public void optionalBrightnessFailureCannotAbortPlayback() {
        var controller = new WindowBrightnessController(() -> {
            throw new UnsupportedOperationException("Host owns the display");
        });
        controller.enter(false, 128);
        controller.set(false, 128);
        controller.restore(false);
        controller.reset(false);
        assertEquals(255, controller.get(false));
    }
}
