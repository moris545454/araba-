package me.aap.fermata.ui.fragment;

import android.content.ComponentName;
import android.content.Intent;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import me.aap.fermata.R;
import me.aap.fermata.auto.AutomotiveNavigationController;
import me.aap.fermata.ui.activity.MainActivityDelegate;
import me.aap.utils.pref.PreferenceStore;
import me.aap.utils.ui.UiUtils;

/** Home actions use the existing host bridge and the documented ScreenOnAuto public API. */
final class DemTvActions {
    static final String WINDASIST_URL = "https://windasist.com";
    static final String HOME_URL = "https://demtv.demiralayboncuk-092.workers.dev/";
    static final String SCREEN_ON_AUTO_PACKAGE = "idv.lzn.screenonauto";
    private DemTvActions() { }

    static void openWeb(MainActivityDelegate activity) {
        openWeb(activity, HOME_URL);
    }

    static void openWeb(MainActivityDelegate activity, String url) {
        activity.showFragmentWhenReady(R.id.web_browser_fragment, url);
    }

    static void openShortcut(MainActivityDelegate activity, PreferenceStore store, String name) {
        int index = DashboardWebShortcuts.indexOf(name);
        java.util.List<DashboardWebShortcuts.Shortcut> list = DashboardItems.getWebShortcuts(store);
        if ((index >= 0) && (index < list.size())) openWeb(activity, list.get(index).url());
    }

    /** One menu adds a web shortcut or removes an existing one; nothing else is changed. */
    static void manageShortcuts(MainActivityDelegate activity, PreferenceStore store,
            Runnable changed) {
        java.util.List<DashboardWebShortcuts.Shortcut> list = DashboardItems.getWebShortcuts(store);
        activity.getContextMenu().show(b -> {
            b.setTitle(R.string.dashboard_shortcuts_menu);
            b.setSelectionHandler(item -> {
                if (item.getItemId() == R.id.dashboard_shortcut_new) {
                    addShortcut(activity, store, changed);
                } else if (item.getItemId() == R.id.dashboard_shortcut_remove) {
                    Integer index = item.getData();
                    java.util.List<DashboardWebShortcuts.Shortcut> current =
                            new java.util.ArrayList<>(DashboardItems.getWebShortcuts(store));
                    if ((index != null) && (index >= 0) && (index < current.size())) {
                        current.remove((int) index);
                        DashboardItems.setWebShortcuts(store, current);
                        changed.run();
                    }
                }
                return true;
            });
            b.addItem(R.id.dashboard_shortcut_new, R.drawable.dashboard_add_shortcut,
                    R.string.dashboard_shortcut_new);
            for (int i = 0; i < list.size(); i++) {
                b.addItem(R.id.dashboard_shortcut_remove, R.drawable.delete,
                        activity.getContext().getString(R.string.dashboard_shortcut_remove,
                                list.get(i).title())).setData(i);
            }
        });
    }

    private static void addShortcut(MainActivityDelegate activity, PreferenceStore store,
            Runnable changed) {
        android.content.Context ctx = activity.getContext();
        if (DashboardItems.getWebShortcuts(store).size() >= DashboardWebShortcuts.MAX_SHORTCUTS) {
            UiUtils.showAlert(ctx, R.string.dashboard_shortcut_limit,
                    DashboardWebShortcuts.MAX_SHORTCUTS);
            return;
        }
        UiUtils.queryText(ctx, R.string.dashboard_shortcut_url, R.drawable.web).main()
                .onCompletion((text, err) -> {
                    if ((err != null) || (text == null)) return;
                    String url = DashboardWebShortcuts.normalize(text);
                    if (url == null) {
                        UiUtils.showAlert(ctx, R.string.dashboard_shortcut_invalid);
                        return;
                    }
                    UiUtils.queryText(ctx, R.string.dashboard_shortcut_name, R.drawable.web,
                            DashboardWebShortcuts.suggestTitle(url)).main()
                            .onCompletion((title, cancel) -> {
                                if (cancel != null) return;
                                java.util.List<DashboardWebShortcuts.Shortcut> current =
                                        new java.util.ArrayList<>(DashboardItems.getWebShortcuts(store));
                                if (current.size() >= DashboardWebShortcuts.MAX_SHORTCUTS) return;
                                current.add(new DashboardWebShortcuts.Shortcut(
                                        (title == null) ? "" : title, url));
                                DashboardItems.setWebShortcuts(store, current);
                                changed.run();
                            });
                });
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
