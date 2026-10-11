package me.app.fermatax.auto;

/** Owns exactly one display per consent grant, independent of Android surface callbacks. */
final class MirrorSurfaceLease<S> implements AutoCloseable {
    interface Factory<S> { Display<S> create(S surface, int width, int height, int density); }
    interface Display<S> {
        void attach(S surface, int width, int height, int density);
        void detach();
        void release();
    }
    private final Factory<S> factory;
    private Display<S> display;
    private boolean closed;

    MirrorSurfaceLease(Factory<S> factory) { this.factory = factory; }

    boolean attach(S surface, int width, int height, int density) {
        if (closed || surface == null || width <= 0 || height <= 0 || density <= 0) return false;
        if (display == null) {
            display = factory.create(surface, width, height, density);
            if (display == null) throw new IllegalStateException("Capture display unavailable");
        } else display.attach(surface, width, height, density);
        return true;
    }

    void detach() { if (!closed && display != null) display.detach(); }

    @Override public void close() {
        if (closed) return;
        closed = true;
        Display<S> owned = display;
        display = null;
        if (owned != null) owned.release();
    }
}
