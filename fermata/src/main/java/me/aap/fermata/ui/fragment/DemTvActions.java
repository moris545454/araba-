package me.aap.fermata.ui.fragment;

import android.content.ComponentName;
import android.content.Intent;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import me.aap.fermata.R;
import me.aap.fermata.auto.AutomotiveNavigationController;
import me.aap.fermata.ui.activity.MainActivityDelegate;

/** Home actions use the existing host bridge and the documented ScreenOnAuto public API. */
final class DemTvActions {
    static final String HOME_URL = "https://demtv.demiralayboncuk-092.workers.dev/";
    static final String SCREEN_ON_AUTO_PACKAGE = "idv.lzn.screenonauto";
    private DemTvActions() { }

    static void openWeb(MainActivityDelegate activity) {
        activity.showFragmentWhenReady(R.id.web_browser_fragment, HOME_URL);
    }

    static void mirror(MainActivityDelegate activity) {
        AutomotiveNavigationController.get().open(R.id.dashboard_mirror_action).main()
                .onCompletion((result, error) -> {
                    if (result == AutomotiveNavigationController.OpenResult.OPENED) return;
                    MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(activity.getContext())
                            .setTitle(R.string.dashboard_mirror_title)
                            .setMessage(R.string.dem_mirror_connect)
                            .setPositiveButton(android.R.string.ok, null);
                    Intent external = new Intent("idv.lzn.screenonauto.action.START_MIRROR")
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .setComponent(new ComponentName(SCREEN_ON_AUTO_PACKAGE,
                                    SCREEN_ON_AUTO_PACKAGE + ".MirrorControlActivity"));
                    if (external.resolveActivity(activity.getContext().getPackageManager()) != null) {
                        dialog.setNeutralButton(R.string.dem_mirror_external, (d, which) -> {
                            try { activity.getContext().startActivity(external); }
                            catch (RuntimeException failure) {
                                new MaterialAlertDialogBuilder(activity.getContext())
                                        .setMessage(R.string.dem_mirror_external_failed)
                                        .setPositiveButton(android.R.string.ok, null).show();
                            }
                        });
                    }
                    dialog.show();
                });
    }
}
