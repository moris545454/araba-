package me.aap.fermata.ui.view;

import static org.junit.Assert.assertTrue;

import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.w3c.dom.Element;

/** Uses Android text metrics with the actual XML geometry; the unit suite does not bundle app resources. */
@RunWith(RobolectricTestRunner.class)
public class MediaHubLayoutTest {
	private static final String ANDROID = "http://schemas.android.com/apk/res/android";

	@Test
	public void longFeaturedTitleAndSubtitleFitBelowDecorativeArtwork() throws Exception {
		Path resources = repository().resolve("fermata/src/main/res");
		Element card = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(resources.resolve("layout/dashboard_featured_item.xml").toFile()).getDocumentElement();
		Element art = element(card, "dashboard_hero_art");
		Element title = element(card, "dashboard_item_title");
		Element subtitle = element(card, "dashboard_item_subtitle");
		float padding = dimension(resources, card.getAttribute("android:padding"));
		float height = dimension(resources, card.getAttribute("android:layout_height"));
		float artHeight = dimension(resources, art.getAttribute("android:layout_height"));
		for (int width : new int[]{208, 240, 320}) {
			int titleWidth = (int) (width - 2 * padding);
			int subtitleWidth = titleWidth - (int) dimension(resources,
					subtitle.getAttribute("android:layout_marginEnd"));
			float occupied = 2 * padding + artHeight +
					dimension(resources, title.getAttribute("android:layout_marginTop")) +
					textHeight("Ekran yansıtma", titleWidth,
							dimension(resources, title.getAttribute("android:textSize")) * 1.15F, true) +
					dimension(resources, subtitle.getAttribute("android:layout_marginTop")) +
					textHeight("Telefon ekranını göster", subtitleWidth,
							dimension(resources, subtitle.getAttribute("android:textSize")), false);
			assertTrue("Featured text needs " + occupied + "dp at " + width + "dp width, available " + height,
					occupied <= height);
		}
	}

	private static int textHeight(String text, int width, float size, boolean bold) {
		TextPaint paint = new TextPaint();
		paint.setTextSize(size);
		paint.setTypeface(Typeface.create(Typeface.DEFAULT, bold ? Typeface.BOLD : Typeface.NORMAL));
		return StaticLayout.Builder.obtain(text, 0, text.length(), paint, width)
				.setAlignment(Layout.Alignment.ALIGN_NORMAL).setIncludePad(true)
				.setMaxLines(2).setEllipsize(TextUtils.TruncateAt.END).build().getHeight();
	}

	private static Element element(Element root, String id) {
		var children = root.getElementsByTagName("*");
		for (int i = 0; i < children.getLength(); i++) {
			Element child = (Element) children.item(i);
			if (("@+id/" + id).equals(child.getAttribute("android:id"))) return child;
		}
		throw new AssertionError("Missing " + id);
	}

	private static float dimension(Path resources, String value) throws Exception {
		if (value.startsWith("@dimen/")) {
			var document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(resources.resolve("values/dimens.xml").toFile());
			var dimensions = document.getElementsByTagName("dimen");
			for (int i = 0; i < dimensions.getLength(); i++) {
				Element dimen = (Element) dimensions.item(i);
				if (value.substring(7).equals(dimen.getAttribute("name"))) {
					value = dimen.getTextContent();
					break;
				}
			}
		}
		return Float.parseFloat(value.replace("dp", "").replace("sp", ""));
	}

	private static Path repository() {
		Path path = Path.of(System.getProperty("user.dir")).toAbsolutePath();
		while (path != null) {
			if (Files.exists(path.resolve("settings.gradle"))) return path;
			path = path.getParent();
		}
		throw new AssertionError("Repository not found");
	}
}
