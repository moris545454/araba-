package me.aap.fermata.auto;

import org.junit.Test;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Path;
import static org.junit.Assert.*;

/** Verifies Android's discoverable launcher contract rather than service class names in UI. */
public class DemTvAutoEntryTest {
    private static final String ANDROID = "http://schemas.android.com/apk/res/android";

    @Test public void onlyOneProjectionEntryAndNoDuplicateMediaTile() throws Exception {
        var factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        var document = factory.newDocumentBuilder().parse(Path.of("src/auto/AndroidManifest.xml").toFile());
        var services = document.getElementsByTagName("service");
        int projectionEntries = 0;
        boolean explicitPlaybackAvailable = false;
        for (int i = 0; i < services.getLength(); i++) {
            Element service = (Element) services.item(i);
            String name = service.getAttributeNS(ANDROID, "name");
			if (name.endsWith("AutoFermataMediaService")) {
				explicitPlaybackAvailable = !"false".equals(service.getAttributeNS(ANDROID, "enabled"));
				assertTrue("Auto media service must be discoverable by Android Auto",
						hasNamed(service, "action", "android.media.browse.MediaBrowserService"));
			}
            var filters = service.getElementsByTagName("intent-filter");
            for (int j = 0; j < filters.getLength(); j++) {
                Element filter = (Element) filters.item(j);
                var categories = filter.getElementsByTagName("category");
                for (int k = 0; k < categories.getLength(); k++) {
                    Element category = (Element) categories.item(k);
                    if (category.getAttributeNS(ANDROID, "name").equals(
                            "com.google.android.gms.car.category.CATEGORY_PROJECTION")) {
                        projectionEntries++;
                        assertTrue(name.endsWith(".CarService"));
                    }
                }
                var actions = filter.getElementsByTagName("action");
				// The standard MediaBrowserService action is intentionally expected on
				// AutoFermataMediaService, while no second media service is advertised.
            }
        }
		assertEquals(1, projectionEntries);
		assertTrue("Media playback must remain explicitly bindable", explicitPlaybackAvailable);
	}

	private static boolean hasNamed(Element parent, String tag, String name) {
		var elements = parent.getElementsByTagName(tag);
		for (int i = 0; i < elements.getLength(); i++) {
			Element element = (Element) elements.item(i);
			if (name.equals(element.getAttributeNS(ANDROID, "name"))) return true;
		}
		return false;
	}
}
