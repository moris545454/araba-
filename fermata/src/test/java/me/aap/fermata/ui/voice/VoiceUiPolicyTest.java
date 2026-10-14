package me.aap.fermata.ui.voice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static me.aap.fermata.ui.policy.RuntimeHostMode.AA_PROJECTION;
import static me.aap.fermata.ui.policy.RuntimeHostMode.MIRROR;
import static me.aap.fermata.ui.policy.RuntimeHostMode.PHONE;

import org.junit.Test;

public class VoiceUiPolicyTest {
	@Test
	public void microphoneFollowsTheRailOrTheFloatingButtons() {
		assertTrue(VoiceUiPolicy.showToolbarButton(PHONE, true, false));
		assertFalse(VoiceUiPolicy.showNavBarButton(PHONE, true, false));
		assertFalse(VoiceUiPolicy.showToolbarButton(PHONE, true, true));
		assertTrue(VoiceUiPolicy.showNavBarButton(PHONE, true, true));
	}

	@Test
	public void carScreenUsesOnlyItsOwnMicrophoneAboveHome() {
		for (var host : new me.aap.fermata.ui.policy.RuntimeHostMode[]{AA_PROJECTION, MIRROR}) {
			for (boolean rail : new boolean[]{false, true}) {
				assertFalse(VoiceUiPolicy.showToolbarButton(host, true, rail));
				assertFalse(VoiceUiPolicy.showNavBarButton(host, true, rail));
			}
			assertTrue(VoiceUiPolicy.showCarButton(host));
		}
		assertFalse(VoiceUiPolicy.showCarButton(PHONE));
	}

	@Test
	public void disabledVoiceHasNoSharedTouchEntry() {
		for (var host : new me.aap.fermata.ui.policy.RuntimeHostMode[]{
				PHONE, AA_PROJECTION, MIRROR}) {
			for (boolean rail : new boolean[]{false, true}) {
				assertFalse(VoiceUiPolicy.showToolbarButton(host, false, rail));
				assertFalse(VoiceUiPolicy.showNavBarButton(host, false, rail));
			}
		}
	}

	@Test
	public void enabledVoiceHasExactlyOnePrimarySurfacePerHost() {
		for (var host : new me.aap.fermata.ui.policy.RuntimeHostMode[]{
				PHONE, AA_PROJECTION, MIRROR}) {
			for (boolean rail : new boolean[]{false, true}) {
				int surfaces = (VoiceUiPolicy.showToolbarButton(host, true, rail) ? 1 : 0) +
						(VoiceUiPolicy.showNavBarButton(host, true, rail) ? 1 : 0) +
						(VoiceUiPolicy.showCarButton(host) ? 1 : 0);
				assertEquals(host.name(), 1, surfaces);
			}
		}
	}
}
