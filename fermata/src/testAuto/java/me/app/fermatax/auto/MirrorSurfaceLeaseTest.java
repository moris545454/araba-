package me.app.fermatax.auto;

import org.junit.Test;
import static org.junit.Assert.*;

public class MirrorSurfaceLeaseTest {
    private static final class Fake implements MirrorSurfaceLease.Display<String>, MirrorSurfaceLease.Factory<String> {
        int grants, resizes, releases, detaches, width, height;
        String surface;
        @Override public MirrorSurfaceLease.Display<String> create(String s, int w, int h, int dpi) {
            grants++; surface = s; width = w; height = h; return this;
        }
        @Override public void attach(String s, int w, int h, int dpi) {
            resizes++; surface = s; width = w; height = h;
        }
        @Override public void detach() { detaches++; surface = null; }
        @Override public void release() { releases++; }
    }
    @Test public void rotationAndReconnectReuseTheSameConsentGrant() {
        Fake backend = new Fake();
        var lease = new MirrorSurfaceLease<String>(backend);
        assertTrue(lease.attach("first", 1280, 720, 160));
        lease.detach();
        assertNull(backend.surface);
        assertTrue(lease.attach("rotated", 720, 1280, 160));
        assertEquals(1, backend.grants);
        assertEquals(1, backend.resizes);
        assertEquals(720, backend.width);
        assertEquals(1280, backend.height);
        assertEquals("rotated", backend.surface);
    }
    @Test public void closedHostRejectsLatePermissionAndSurfaceCallbacks() {
        Fake backend = new Fake();
        var lease = new MirrorSurfaceLease<String>(backend);
        lease.close();
        assertFalse(lease.attach("late", 1280, 720, 160));
        lease.detach();
        assertEquals(0, backend.grants);
        assertEquals(0, backend.detaches);
    }
    @Test public void stopAndDisconnectReleaseOnlyOnce() {
        Fake backend = new Fake();
        var lease = new MirrorSurfaceLease<String>(backend);
        lease.attach("live", 1280, 720, 160);
        lease.close();
        lease.close();
        lease.detach();
        assertFalse(lease.attach("new", 720, 1280, 160));
        assertEquals(1, backend.releases);
        assertEquals(1, backend.grants);
    }
    @Test public void unmeasuredSurfacesDoNotConsumePermission() {
        Fake backend = new Fake();
        var lease = new MirrorSurfaceLease<String>(backend);
        assertFalse(lease.attach(null, 1280, 720, 160));
        assertFalse(lease.attach("zero", 0, 720, 160));
        assertFalse(lease.attach("density", 1280, 720, 0));
        assertEquals(0, backend.grants);
        assertTrue(lease.attach("valid", 1280, 720, 160));
        assertEquals(1, backend.grants);
    }
}
