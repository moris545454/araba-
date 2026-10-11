package me.aap.fermata.ui.policy;

import me.aap.fermata.R;

/** An upgrade must not reopen a page belonging to a removed feature. */
public final class CoreRoutePolicy {
    private CoreRoutePolicy() { }

    public static int restore(int destination) {
        if (destination == R.id.dashboard_fragment || destination == R.id.settings_fragment ||
                destination == R.id.initial_setup_fragment || destination == R.id.tv_fragment ||
                destination == R.id.youtube_fragment || destination == R.id.web_browser_fragment)
            return destination;
        return R.id.dashboard_fragment;
    }
}
