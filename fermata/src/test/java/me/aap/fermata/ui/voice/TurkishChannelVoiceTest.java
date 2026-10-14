package me.aap.fermata.ui.voice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Locale;

import org.junit.Test;

public class TurkishChannelVoiceTest {
	private static final Locale TR = new Locale("tr", "TR");

	@Test
	public void channelSwitchPhrasesMapToNextAndPrevious() {
		for (String phrase : new String[]{"kanal değiştir", "Sonraki kanal", "sonraki kanala geç",
				"diğer kanal"}) {
			VoiceIntent intent = VoiceIntentParser.parse(phrase, TR);
			assertNotNull(phrase, intent);
			assertEquals(phrase, VoiceIntent.PlaybackAction.NEXT, intent.getPlaybackAction());
		}
		for (String phrase : new String[]{"önceki kanal", "önceki kanala geç", "kanal geri"}) {
			VoiceIntent intent = VoiceIntentParser.parse(phrase, TR);
			assertNotNull(phrase, intent);
			assertEquals(phrase, VoiceIntent.PlaybackAction.PREVIOUS, intent.getPlaybackAction());
		}
	}

	@Test
	public void apostropheCaseEndingsAreDroppedFromTheChannelName() {
		assertEquals("aç show tv", VoiceIntentParser.stripTurkishCaseSuffixes("aç show tv'yi"));
		assertEquals("aç trt 1", VoiceIntentParser.stripTurkishCaseSuffixes("aç trt 1'i"));
		assertEquals("aç kanal d", VoiceIntentParser.stripTurkishCaseSuffixes("aç kanal d’yi"));
		VoiceIntent a = VoiceIntentParser.parse("Show TV'yi aç", TR);
		VoiceIntent b = VoiceIntentParser.parse("Show TV aç", TR);
		assertNotNull(a);
		assertNotNull(b);
		assertEquals(b.getKind(), a.getKind());
		assertEquals(b.getSearchAction(), a.getSearchAction());
		assertEquals(b.getQuery(), a.getQuery());
		assertEquals("show tv", a.getQuery());
	}
}
