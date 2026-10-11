package me.app.fermatax.auto;

/** Starts the existing MediaProjection-backed Android Auto screen mirror from the unified dashboard. */
public final class MirrorLauncher {
    private MirrorLauncher() {}
    public static void launch() {
        ProjectionService.start();
    }
}
