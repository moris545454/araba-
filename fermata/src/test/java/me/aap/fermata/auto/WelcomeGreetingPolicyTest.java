package me.aap.fermata.auto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class WelcomeGreetingPolicyTest {
	@Test
	public void choosesPartOfDayByHour() {
		assertEquals(WelcomeGreetingPolicy.PartOfDay.NIGHT, WelcomeGreetingPolicy.partOfDay(4));
		assertEquals(WelcomeGreetingPolicy.PartOfDay.MORNING, WelcomeGreetingPolicy.partOfDay(5));
		assertEquals(WelcomeGreetingPolicy.PartOfDay.MORNING, WelcomeGreetingPolicy.partOfDay(11));
		assertEquals(WelcomeGreetingPolicy.PartOfDay.DAY, WelcomeGreetingPolicy.partOfDay(12));
		assertEquals(WelcomeGreetingPolicy.PartOfDay.EVENING, WelcomeGreetingPolicy.partOfDay(18));
		assertEquals(WelcomeGreetingPolicy.PartOfDay.EVENING, WelcomeGreetingPolicy.partOfDay(22));
		assertEquals(WelcomeGreetingPolicy.PartOfDay.NIGHT, WelcomeGreetingPolicy.partOfDay(23));
	}

	@Test
	public void greetsOnlyWhenEnabledAndRespectsOncePerDay() {
		assertFalse(WelcomeGreetingPolicy.shouldGreet(false, false, 0, 2026285));
		assertTrue(WelcomeGreetingPolicy.shouldGreet(true, false, 2026285, 2026285));
		assertTrue(WelcomeGreetingPolicy.shouldGreet(true, true, 2026284, 2026285));
		assertFalse(WelcomeGreetingPolicy.shouldGreet(true, true, 2026285, 2026285));
	}

	@Test
	public void cleansSpokenName() {
		assertEquals("Ahmet", WelcomeGreetingPolicy.cleanName("  Ahmet  "));
		assertEquals("Ali Rıza", WelcomeGreetingPolicy.cleanName("Ali\n  Rıza"));
		assertEquals("Ayşe", WelcomeGreetingPolicy.cleanName("Ayşe!? <>"));
		assertEquals("", WelcomeGreetingPolicy.cleanName(null));
		assertTrue(WelcomeGreetingPolicy.cleanName("x".repeat(100)).length() <= 24);
	}
}
