package me.aap.fermata.ui.activity;

import android.view.Window;

import java.util.function.Supplier;

/** Android Auto owns its display brightness; optional phone adjustments must not stop playback. */
final class WindowBrightnessController {
    private final Supplier<Window> window;
    private float previous = -1f;

    WindowBrightnessController(Supplier<Window> window) {
        this.window = window;
    }

    void enter(boolean automotive, int value) {
        if (automotive) return;
        try {
            previous = window.get().getAttributes().screenBrightness;
            set(false, value);
        } catch (RuntimeException ignored) {
            // A host with no window attributes can still play media.
        }
    }

    void restore(boolean automotive) {
        apply(automotive, previous);
    }

    void reset(boolean automotive) {
        apply(automotive, -1f);
    }

    int get(boolean automotive) {
        if (automotive) return 255;
        try {
            float current = window.get().getAttributes().screenBrightness;
            return current < 0f ? 255 : Math.round(current * 255);
        } catch (RuntimeException ignored) {
            return 255;
        }
    }

    void set(boolean automotive, int value) {
        apply(automotive, Math.max(0, Math.min(255, value)) / 255f);
    }

    private void apply(boolean automotive, float value) {
        if (automotive) return;
        try {
            Window target = window.get();
            var attributes = target.getAttributes();
            attributes.screenBrightness = value;
            target.setAttributes(attributes);
        } catch (RuntimeException ignored) {
            // Window brightness is a presentation preference, never a playback requirement.
        }
    }
}
