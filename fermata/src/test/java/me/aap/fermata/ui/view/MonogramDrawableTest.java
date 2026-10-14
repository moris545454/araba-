package me.aap.fermata.ui.view;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MonogramDrawableTest {
	@Test
	public void initialsSkipNumberingAndUseTurkishCase() {
		assertEquals("TH", MonogramDrawable.initials("15. TRT HABER"));
		assertEquals("KD", MonogramDrawable.initials("6. Kanal D"));
		assertEquals("Tİ", MonogramDrawable.initials("tivibu"));
		assertEquals("TV", MonogramDrawable.initials(null));
		assertEquals("TV", MonogramDrawable.initials("  "));
	}
}
