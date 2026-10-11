package me.app.fermatax.auto;

import android.content.Context;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.Surface;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.TextView;

import me.aap.fermata.R;
import me.aap.utils.async.FutureSupplier;
import me.aap.utils.log.Log;

/** Full allocated Auto viewport, ordinary Android consent, no root or input injection. */
final class InlineMirrorView extends FrameLayout implements SurfaceHolder.Callback {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final SurfaceView screen;
    private final TextView status;
    private final Runnable exit;
    private final MediaProjection.Callback callback = new MediaProjection.Callback() {
        @Override public void onStop() {
            if (!closed) exit.run();
        }
    };
    private FutureSupplier<MediaProjection> pending;
    private MediaProjection projection;
    private MirrorSurfaceLease<Surface> lease;
    private boolean requested;
    private boolean closed;

    InlineMirrorView(Context context, Runnable exit) {
        super(context);
        this.exit = exit;
        setBackgroundColor(Color.BLACK);
        setFocusableInTouchMode(true);
        screen = new SurfaceView(context);
        screen.getHolder().addCallback(this);
        addView(screen, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        status = new TextView(context);
        status.setText(R.string.dem_mirror_waiting);
        status.setTextColor(0xFFF3EFE7);
        status.setTextSize(20);
        status.setGravity(Gravity.CENTER);
        status.setPadding(dp(32), dp(24), dp(32), dp(24));
        addView(status, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        TextView close = new TextView(context);
        close.setText(R.string.dem_mirror_stop);
        close.setTextColor(0xFFF3EFE7);
        close.setTextSize(16);
        close.setGravity(Gravity.CENTER);
        close.setFocusable(true);
        close.setBackground(me.aap.fermata.ui.view.MediaHubStyle.card(context, 0xFF76DFC5, false));
        close.setOnClickListener(v -> exit.run());
        LayoutParams button = new LayoutParams(LayoutParams.WRAP_CONTENT, dp(52), Gravity.TOP | Gravity.END);
        button.setMargins(dp(16), dp(16), dp(16), dp(16));
        close.setPadding(dp(24), 0, dp(24), 0);
        addView(close, button);
    }

    @Override public void surfaceCreated(SurfaceHolder holder) { }

    @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (closed || width <= 0 || height <= 0) return;
        if (projection != null) attach(holder, width, height);
        else if (!requested) {
            requested = true;
            pending = ProjectionService.start(true);
            pending.main().onCompletion((capture, error) -> {
                if (closed) { if (capture != null) capture.stop(); return; }
                if (error != null || capture == null) {
                    status.setText(R.string.dem_mirror_rejected);
                    return;
                }
                try {
                projection = capture;
                projection.registerCallback(callback, handler);
                lease = new MirrorSurfaceLease<>((surface, w, h, density) -> {
                    VirtualDisplay display = projection.createVirtualDisplay("Dem TV / Ekran", w, h,
                            density, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                            surface, null, handler);
                    if (display == null) throw new IllegalStateException("Display unavailable");
                    return new MirrorSurfaceLease.Display<Surface>() {
                        @Override public void attach(Surface next, int width, int height, int dpi) {
                            display.resize(width, height, dpi);
                            display.setSurface(next);
                        }
                        @Override public void detach() { display.setSurface(null); }
                        @Override public void release() { display.release(); }
                    };
                });
                if (screen.getHolder().getSurface().isValid()) {
                    attach(screen.getHolder(), screen.getWidth(), screen.getHeight());
                }
                } catch (RuntimeException failure) {
                    Log.e(failure, "Dem TV capture grant unavailable");
                    status.setText(R.string.dem_mirror_rejected);
                    releaseCapture();
                }
            });
        }
    }

    private void attach(SurfaceHolder holder, int width, int height) {
        if (closed || width <= 0 || height <= 0) return;
        try {
            int density = getResources().getDisplayMetrics().densityDpi;
            // One virtual display per grant (Android 14+); surfaces and sizes may change.
            if (lease == null || !lease.attach(holder.getSurface(), width, height, density)) return;
            status.setVisibility(GONE);
        } catch (RuntimeException error) {
            Log.e(error, "Dem TV screen capture failed");
            status.setVisibility(VISIBLE);
            status.setText(R.string.dem_mirror_rejected);
            releaseCapture();
        }
    }

    @Override public void surfaceDestroyed(SurfaceHolder holder) {
        if (lease != null) lease.detach();
    }

    @Override protected void onDetachedFromWindow() {
        close();
        super.onDetachedFromWindow();
    }

    void close() {
        if (closed) return;
        closed = true;
        screen.getHolder().removeCallback(this);
        if (pending != null && !pending.isDone()) pending.cancel();
        try { releaseCapture(); }
        finally { ProjectionService.stop(); }
    }

    private void releaseCapture() {
        MirrorSurfaceLease<Surface> owned = lease;
        lease = null;
        MediaProjection capture = projection;
        projection = null;
        try {
            if (owned != null) owned.close();
        } finally {
            if (capture != null) {
                try { capture.unregisterCallback(callback); }
                finally { capture.stop(); }
            }
        }
    }

    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
