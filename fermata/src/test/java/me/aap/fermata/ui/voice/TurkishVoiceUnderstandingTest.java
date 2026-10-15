package me.aap.fermata.ui.voice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import java.util.Locale;

public class TurkishVoiceUnderstandingTest {
	private static final Locale TR = Locale.forLanguageTag("tr");

	private static VoiceIntent p(String phrase) {
		return VoiceIntentParser.parse(phrase, TR);
	}

	private static void search(String phrase, VoiceIntent.SearchAction action, String addon,
			String query) {
		VoiceIntent i = p(phrase);
		org.junit.Assert.assertNotNull(phrase, i);
		assertEquals(phrase, VoiceIntent.Kind.ADDON_SEARCH, i.getKind());
		assertEquals(phrase, action, i.getSearchAction());
		assertEquals(phrase, addon, i.getAddon());
		assertEquals(phrase, query, i.getQuery());
	}

	private static void section(String phrase, String section) {
		VoiceIntent i = p(phrase);
		org.junit.Assert.assertNotNull(phrase, i);
		assertEquals(phrase, VoiceIntent.Kind.OPEN_SECTION, i.getKind());
		assertEquals(phrase, section, i.getAddon());
	}

	private static void addon(String phrase, String addon) {
		VoiceIntent i = p(phrase);
		org.junit.Assert.assertNotNull(phrase, i);
		assertEquals(phrase, VoiceIntent.Kind.OPEN_ADDON, i.getKind());
		assertEquals(phrase, addon, i.getAddon());
	}

	@Test
	public void namesWithAnyVerbFormPlayFromAnywhere() {
		VoiceIntent.SearchAction play = VoiceIntent.SearchAction.PLAY;
		search("Show TV aç", play, null, "show tv");
		search("Show TV'yi açar mısın", play, null, "show tv");
		search("lütfen show tv'yi açabilir misin", play, null, "show tv");
		search("hadi Show TV açsana", play, null, "show tv");
		search("Show TV izlemek istiyorum", play, null, "show tv");
		search("biraz Show TV izleyelim", play, null, "show tv");
		search("Mola, Kanal D koy", play, null, "kanal d");
		search("TRT bir aç", play, null, "trt 1");
		search("Show TV'ye geç", play, null, "show tv");
	}

	@Test
	public void stationsChannelsAndPlacesGoToTheRightSection() {
		VoiceIntent.SearchAction play = VoiceIntent.SearchAction.PLAY;
		search("biraz Kral FM dinleyelim", play, "radio", "kral fm");
		search("Kral FM'i çalar mısın", play, "radio", "kral fm");
		search("Süper radyosunu aç", play, "radio", "süper");
		search("ATV kanalını aç", play, "tv", "atv");
		search("YouTube'da Tarkan aç", play, "youtube", "tarkan");
		search("Tarkan'ı YouTube'da oynat", play, "youtube", "tarkan");
		search("TV TRT 1 oynat", play, "tv", "trt 1");
		search("internette hava durumu ara", VoiceIntent.SearchAction.FIND, "web", "hava durumu");
		search("google.com aç", VoiceIntent.SearchAction.OPEN, "web", "google.com");
	}

	@Test
	public void sectionsOpenInAnyWording() {
		addon("YouTube aç", "youtube");
		addon("televizyonu açar mısın", "tv");
		addon("radyoyu aç", "radio");
		section("ayarları aç", "settings");
		section("ayarlar", "settings");
		section("ekolayzırı göster", "equalizer");
		section("ana sayfaya dön", "home");
		section("ana menü", "home");
		section("ekran yansıtmayı aç", "mirror");
		section("favorilerimi aç", "favorites");
	}

	@Test
	public void volumeAndPlaybackInNaturalWords() {
		assertEquals(VoiceIntent.VolumeAction.UP, p("sesi aç").getVolumeAction());
		assertEquals(VoiceIntent.VolumeAction.UP, p("sesi biraz yükselt lütfen").getVolumeAction());
		assertEquals(VoiceIntent.VolumeAction.DOWN, p("sesi kıs").getVolumeAction());
		assertEquals(VoiceIntent.VolumeAction.DOWN, p("sesi biraz azaltır mısın").getVolumeAction());
		assertEquals(VoiceIntent.VolumeAction.MUTE, p("sessize al").getVolumeAction());
		assertEquals(VoiceIntent.PlaybackAction.STOP, p("radyoyu kapat").getPlaybackAction());
		assertEquals(VoiceIntent.PlaybackAction.PAUSE, p("duraklatır mısın").getPlaybackAction());
		assertEquals(VoiceIntent.PlaybackAction.PLAY, p("devam et").getPlaybackAction());
		assertEquals(VoiceIntent.PlaybackAction.NEXT, p("bir sonraki kanala geç").getPlaybackAction());
		assertEquals(VoiceIntent.PlaybackAction.PREVIOUS, p("önceki kanalı aç").getPlaybackAction());
	}

	private static void setting(String phrase, String name, String value) {
		VoiceIntent i = p(phrase);
		org.junit.Assert.assertNotNull(phrase, i);
		assertEquals(phrase, VoiceIntent.Kind.SETTING, i.getKind());
		assertEquals(phrase, name, i.getAddon());
		assertEquals(phrase, value, i.getQuery());
	}

	@Test
	public void displaySettingsBySpeech() {
		String scale = TurkishVoiceUnderstanding.SETTING_VIDEO_SCALE;
		setting("ekranı doldur", scale, "fill");
		setting("görüntüyü tam ekran yap", scale, "fill");
		setting("videoyu yakınlaştırır mısın", scale, "fill");
		setting("ekrana sığdır", scale, "fill");
		setting("sığdır", scale, "best");
		setting("orijinal boyut", scale, "original");
		setting("dört üç yap", scale, "4:3");
		setting("16 9", scale, "16:9");
		setting("ölçeği değiştir", scale, "next");
		setting("yazıları büyüt", TurkishVoiceUnderstanding.SETTING_TEXT_SIZE, "up");
		setting("yazıları küçült lütfen", TurkishVoiceUnderstanding.SETTING_TEXT_SIZE, "down");
		setting("karanlık temaya geç", TurkishVoiceUnderstanding.SETTING_THEME, "dark");
		setting("gündüz modu", TurkishVoiceUnderstanding.SETTING_THEME, "light");
	}

	@Test
	public void unrelatedSpeechAndMediaGrammarStayUntouched() {
		assertNull(p("hava nasıl"));
		assertNull(p("altyazıları aç"));
		assertNull(p("ses kanalını değiştir"));
		assertEquals(1, p("iki").getSelectionIndex());
		assertEquals(0, p("bir").getSelectionIndex());
	}
}
