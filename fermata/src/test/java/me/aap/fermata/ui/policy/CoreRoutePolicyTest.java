package me.aap.fermata.ui.policy;

import org.junit.Test;
import me.aap.fermata.R;
import static org.junit.Assert.assertEquals;

public class CoreRoutePolicyTest {
    @Test public void preservesCorePagesWhenUpdating() {
        for (int id : new int[]{R.id.dashboard_fragment, R.id.settings_fragment,
                R.id.initial_setup_fragment, R.id.tv_fragment, R.id.youtube_fragment,
                R.id.web_browser_fragment}) assertEquals(id, CoreRoutePolicy.restore(id));
    }
    @Test public void removedOrUnknownPagesReturnHomeInsteadOfRecreatingAnAbsentFeature() {
        for (int id : new int[]{R.id.radio_fragment, R.id.podcast_fragment, R.id.folders_fragment,
                R.id.stremio_fragment, 0, -1})
            assertEquals(R.id.dashboard_fragment, CoreRoutePolicy.restore(id));
    }
}
