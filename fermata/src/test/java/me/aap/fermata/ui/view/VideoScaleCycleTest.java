package me.aap.fermata.ui.view;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VideoScaleCycleTest {
	@Test
	public void eachPressMovesToTheNextScaleAndWraps() {
		assertEquals(1, ControlPanelView.nextVideoScale(0));
		assertEquals(4, ControlPanelView.nextVideoScale(3));
		assertEquals(0, ControlPanelView.nextVideoScale(4));
		assertEquals(0, ControlPanelView.nextVideoScale(-1));
		assertEquals(0, ControlPanelView.nextVideoScale(99));
	}
}
