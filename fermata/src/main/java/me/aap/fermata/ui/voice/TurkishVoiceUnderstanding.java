package me.aap.fermata.ui.voice;

import androidx.annotation.Nullable;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Offline understanding of free Turkish speech, in front of the strict command grammar.
 *
 * <p>It removes what people naturally add around a command (politeness, fillers, the wake
 * word), recognises the many forms of the same verb ("açar mısın", "açabilir misin", "açsana",
 * "izleyelim", "dinlemek istiyorum", "koy", "çal"...), drops Turkish case endings from names
 * ("Show TV'yi", "Kral FM'e") and maps section nouns ("televizyon", "ayarlar", "ekolayzır").
 * What it fully understands is returned as a {@link VoiceIntent}; otherwise it hands a cleaned,
 * canonical sentence ("show tv aç") to the existing grammar.</p>
 */
public final class TurkishVoiceUnderstanding {
	private static final Locale TR = Locale.forLanguageTag("tr");

	/** Words people add around a command; never part of a channel or section name. */
	private static final Pattern FILLERS = Pattern.compile(
			"(?:^|\\s)(?:lütfen|rica ederim|hadi|haydi|hemen|şimdi|şuan|şu an|artık|acaba|bana|bize|" +
					"biraz|bi|bakalım|bakayım|ya|yav|yahu|tamam|evet|bir zahmet)(?=\\s|$)");
	private static final Pattern WAKE = Pattern.compile("^(?:hey |ok |tamam )?mola\\b\\s*");

	/** Optional endings after a verb stem: "açar mısın", "açabilir misin", "açsana", "açalım"... */
	private static final String VERB_TAIL =
			"(?:" +
					"[aeıiuü]?r\\s*m[ıiuü]s[ıiuü]n(?:[ıiuü]z)?" +        // açar mısın, izler misin
					"|[ae]?bil[ıi]r\\s*m[ıiuü]s[ıiuü]n(?:[ıiuü]z)?" +   // açabilir misin
					"|s[ae]n[ae]" +                                     // açsana
					"|y?[ae]l[ıi]m" +                                   // açalım, izleyelim
					"|y?[ae]y[ıi]m" +                                   // açayım
					"|m[ae]k\\s+[ıi]st[ie]y?[ou]r(?:um|uz)?" +          // açmak istiyorum
					"|m[ae]k\\s+[ıi]st[ie]r[ıi]m" +                     // izlemek isterim
					"|[ıiuü]?n(?:[ıiuü]z)?" +                           // açın, açınız
					"|[ıiuü]ver" +                                      // açıver
					")?";

	private enum Verb { PLAY, OPEN, FIND, STOP, PAUSE, RESUME }

	/** Verb stems by meaning; the longest form is tried first. */
	private static final Map<String, Verb> VERBS = new LinkedHashMap<>();

	static {
		for (String s : new String[]{"oynat", "başlat", "çal", "koy", "izle", "dinle", "getir"})
			VERBS.put(s, Verb.PLAY);
		for (String s : new String[]{"aç", "göster", "geç", "git", "gir", "götür", "dön"})
			VERBS.put(s, Verb.OPEN);
		for (String s : new String[]{"arat", "ara", "bul"}) VERBS.put(s, Verb.FIND);
		for (String s : new String[]{"durdur", "kapat", "sustur", "bitir"}) VERBS.put(s, Verb.STOP);
		for (String s : new String[]{"duraklat", "beklet"}) VERBS.put(s, Verb.PAUSE);
		for (String s : new String[]{"devam et", "devam ettir", "sürdür"}) VERBS.put(s, Verb.RESUME);
	}

	private static final Pattern TRAILING_VERB;

	static {
		StringBuilder alt = new StringBuilder();
		VERBS.keySet().stream().sorted((a, b) -> b.length() - a.length()).forEach(v -> {
			if (alt.length() != 0) alt.append('|');
			alt.append(Pattern.quote(v));
		});
		TRAILING_VERB = Pattern.compile("(?:^|\\s)(" + alt + ")" + VERB_TAIL + "$");
	}

	/** Section nouns, in any common inflection, to the section they open. */
	private static final Map<Pattern, String> SECTIONS = new LinkedHashMap<>();

	private static void section(String key, String... forms) {
		for (String f : forms) SECTIONS.put(Pattern.compile("^(?:" + f + ")$"), key);
	}

	static {
		section("tv", "tv(?:'?y[ei])?", "tivi(?:y[ie])?", "televizyon(?:u|a|da)?", "kanallar(?:ı|a)?",
				"canlı tv", "canlı yayın(?:ı|a)?", "iptv");
		section("radio", "radyo(?:yu|ya|da)?", "radyolar(?:ı|a)?", "radio");
		section("youtube", "youtube(?:'?u|'?a|'?da)?", "you tube", "yutup", "yutub", "yt");
		section("web", "web", "tarayıcı(?:yı|ya)?", "internet(?:i|e)?", "internet tarayıcısı(?:nı)?");
		section("podcast", "podcast(?:'?i|'?e|'?ler|'?leri)?", "podkast(?:ı|a)?");
		section("folders", "medya(?:yı|ya)?", "dosyalar(?:ı|a)?", "videolar(?:ım)?(?:ı)?",
				"müzikler(?:im)?(?:i)?", "telefondaki (?:video|müzik)(?:lar|ler)?(?:ı|i)?");
		section("favorites", "favoriler(?:im)?(?:i|e)?");
		section("settings", "ayar(?:lar)?(?:ı|a)?", "ayarlar(?:ı|a)?");
		section("equalizer", "ekolayzır(?:ı|a)?", "ekolayzer(?:ı|a)?", "equalizer", "ekolayzır ayarları",
				"ses ayarları(?:nı)?", "bas ayarları(?:nı)?", "eq");
		section("home", "ana ?sayfa(?:yı|ya)?", "ana ?menü(?:yü|ye)?", "ana ?ekran(?:ı|a)?", "başa", "başlangıç");
		section("mirror", "ekran yansıtma(?:yı)?", "yansıtma(?:yı)?", "ekranı yansıt", "ekran paylaşımı(?:nı)?");
	}

	/** Addon sections the existing grammar can open itself. */
	private static final Map<String, String> ADDON_SECTIONS = Map.of(
			"tv", "tv", "radio", "radio", "youtube", "youtube", "web", "web", "podcast", "podcast");

	/** "Tarkan YouTube'da", "YouTube'da Tarkan", "internette hava durumu". */
	private static final Pattern LOCATED = Pattern.compile(
			"^(?:(.+?)\\s+)?(youtube|you tube|yutup|televizyon|tv|radyo|internet|google|podcast)" +
					"'?(?:d[ae]|t[ae]|n[ıi]n|[ıi]n)(?:\\s+(.+))?$");
	/** "TV TRT 1", "radyo Kral FM", "YouTube Tarkan": the section named first, then the name. */
	private static final Pattern PREFIXED = Pattern.compile(
			"^(tv|televizyon|tivi|radyo|radio|youtube|you tube|yutup|podcast|web|internet)\\s+(.+)$");
	private static final Pattern CHANNEL = Pattern.compile("^(.+?)\\s+kanal(?:ı|ını|ına|ında)?$");
	private static final Pattern STATION = Pattern.compile("^(.+?)\\s+radyo(?:su|sunu|suna|sunda)$");
	private static final Pattern FM = Pattern.compile("^(.+\\s(?:fm|radyo|radio))$");

	private static final Pattern VOLUME = Pattern.compile(
			"^(?:ses(?:i|ini)?|volüm(?:ü)?)(?:\\s+(?:çok|daha|az|azıcık))*\\s+(.+)$");
	private static final Pattern MUTE = Pattern.compile(
			"^(?:sessize al|sessiz(?:e)?(?: mod)?|sesi(?:ni)? kapat|sesi(?:ni)? kes|sustur)$");

	public static final String SETTING_VIDEO_SCALE = "video_scale";
	public static final String SETTING_TEXT_SIZE = "text_size";
	public static final String SETTING_THEME = "theme";

	/** Display settings by voice: video scale, text size and theme. Values are setting keywords. */
	private static final Map<Pattern, String[]> SETTINGS = new LinkedHashMap<>();

	private static void setting(String name, String value, String... phrases) {
		for (String p : phrases) SETTINGS.put(Pattern.compile("^(?:" + p + ")$"), new String[]{name, value});
	}

	static {
		String subject = "(?:(?:görüntü|ekran|video|resim)(?:yü|yu|yı|yi|ü|u|ı|i|ya|ye|a|e)?\\s+)?";
		String tail = VERB_TAIL;
		setting(SETTING_VIDEO_SCALE, "fill",
				subject + "(?:doldur|genişlet|yakınlaştır|büyüt|tam ekran yap)" + tail,
				subject + "ekrana? (?:sığdır|yay)" + tail, "tam ekran(?: yap)?", "ekranı kapla");
		setting(SETTING_VIDEO_SCALE, "best",
				subject + "(?:sığdır|uzaklaştır|küçült|otomatik(?: boyut)?|en iyi boyut|normal boyut)" + tail,
				"en iyi boyut", "otomatik boyut", "normal boyut");
		setting(SETTING_VIDEO_SCALE, "original",
				subject + "(?:orijinal|gerçek|asıl) (?:boyut|boyuta|boyutta)(?: (?:getir|al|yap|göster))?" + tail);
		setting(SETTING_VIDEO_SCALE, "4:3",
				subject + "(?:4[: ]?3|dört(?:e)? üç)(?: yap| oranı(?:na)?(?: getir| al)?)?" + tail);
		setting(SETTING_VIDEO_SCALE, "16:9",
				subject + "(?:16[: ]?9|on altı(?:ya)? dokuz|geniş ekran)(?: yap| oranı(?:na)?(?: getir| al)?)?" + tail);
		setting(SETTING_VIDEO_SCALE, "next",
				"(?:ölçe(?:ği|k)|boyut(?:u)?|görüntü (?:boyutunu|ölçeğini)) değiştir" + tail, "ölçekle", "ölçek");
		setting(SETTING_TEXT_SIZE, "up",
				"(?:yazı(?:ları)?|yazı boyutunu|harfleri|metni|simgeleri) (?:büyüt|büyült|arttır|artır)" + tail);
		setting(SETTING_TEXT_SIZE, "down",
				"(?:yazı(?:ları)?|yazı boyutunu|harfleri|metni|simgeleri) (?:küçült|azalt)" + tail);
		setting(SETTING_THEME, "dark", "(?:karanlık|koyu|gece) (?:tema|mod|modu)(?:ya| geç| aç| yap)*" + tail);
		setting(SETTING_THEME, "light", "(?:aydınlık|açık|gündüz) (?:tema|mod|modu)(?:ya| geç| aç| yap)*" + tail);
	}

	@Nullable
	private static VoiceIntent setting(String t) {
		for (Map.Entry<Pattern, String[]> e : SETTINGS.entrySet()) {
			if (e.getKey().matcher(t).matches()) return VoiceIntent.setting(e.getValue()[0], e.getValue()[1]);
		}
		return null;
	}

	private TurkishVoiceUnderstanding() {
	}

	/** Lower-cases the Turkish way, unifies apostrophes and removes punctuation and fillers. */
	static String clean(String phrase) {
		if (phrase == null) return "";
		String t = phrase.toLowerCase(TR).replace('’', '\'').replace('`', '\'')
				// A dot inside a word is part of an address ("google.com"), at a word end it is punctuation.
				.replaceAll("[,!?;:\"“”()]|\\.(?=\\s|$)", " ").replaceAll("\\s+", " ").trim();
		t = WAKE.matcher(t).replaceFirst("");
		String before;
		do {
			before = t;
			t = FILLERS.matcher(t).replaceAll(" ").replaceAll("\\s+", " ").trim();
		} while (!t.equals(before));
		// "bir" is a filler before a name ("bir show tv aç") but a selection on its own ("bir").
		if (t.startsWith("bir ") && !t.startsWith("bir sonraki") && !t.startsWith("bir önceki"))
			t = t.substring(4);
		return t;
	}

	/** Removes a Turkish case ending from the end of a name: "show tv'yi" → "show tv". */
	static String stripCase(String text) {
		String t = text.replaceAll("'[a-zçğıöşü]{1,5}$", "").trim();
		t = t.replaceAll("\\s+(?:y?[ıiuü]|y?[ae]|n[ıiuü]n|[nd][ae]n|t[ae]n|d[ae]|n[ıiuü])$", "").trim();
		return t;
	}

	/** Turkish number words after the first word become digits: "trt bir" → "trt 1". */
	static String digits(String text) {
		String[] w = text.split(" ");
		for (int i = 1; i < w.length; i++) {
			String d = switch (w[i]) {
				case "bir" -> "1";
				case "iki" -> "2";
				case "üç" -> "3";
				case "dört" -> "4";
				case "beş" -> "5";
				case "altı" -> "6";
				case "yedi" -> "7";
				case "sekiz" -> "8";
				case "dokuz" -> "9";
				case "on" -> "10";
				default -> null;
			};
			if (d != null) w[i] = d;
		}
		return String.join(" ", w);
	}

	@Nullable
	static String section(String noun) {
		String n = stripCase(noun);
		for (Map.Entry<Pattern, String> e : SECTIONS.entrySet()) {
			if (e.getKey().matcher(noun).matches() || e.getKey().matcher(n).matches()) return e.getValue();
		}
		return null;
	}

	/**
	 * Understands what the grammar alone would not. Returns null when the sentence should go to
	 * the existing grammar unchanged (playback words, subtitles, selections, unknown speech).
	 */
	@Nullable
	public static VoiceIntent understand(@Nullable String phrase) {
		String t = clean(phrase);
		if (t.isEmpty()) return null;
		// Subtitle and audio-track commands belong to the media grammar.
		if (t.startsWith("altyazı") || t.startsWith("ses kanal") || t.startsWith("ses dil")) return null;

		VoiceIntent volume = volume(t);
		if (volume != null) return volume;
		VoiceIntent setting = setting(t);
		if (setting != null) return setting;

		Matcher vm = TRAILING_VERB.matcher(t);
		Verb verb = null;
		String content = t;
		if (vm.find()) {
			verb = VERBS.get(vm.group(1));
			content = t.substring(0, vm.start()).trim();
		}
		// "devam", "ana sayfa", "ayarlar" without a verb still name a destination or action.
		if (verb == null) {
			String sec = section(t);
			if ((sec != null) && !ADDON_SECTIONS.containsKey(sec)) return VoiceIntent.openSection(sec);
			if (t.equals("devam") || t.equals("devam et")) return VoiceIntent.playback(VoiceIntent.PlaybackAction.PLAY);
			return null;
		}

		switch (verb) {
			case STOP:
				if (content.isEmpty() || (section(content) != null) || content.matches("(?:yayın|müzik|video|şarkı|kanal)\\S*"))
					return VoiceIntent.playback(VoiceIntent.PlaybackAction.STOP);
				return null;
			case PAUSE:
				return VoiceIntent.playback(VoiceIntent.PlaybackAction.PAUSE);
			case RESUME:
				return VoiceIntent.playback(VoiceIntent.PlaybackAction.PLAY);
			default:
				break;
		}
		if (content.isEmpty()) {
			return (verb == Verb.PLAY) ? VoiceIntent.playback(VoiceIntent.PlaybackAction.PLAY) : null;
		}
		if (content.startsWith("sonraki") || content.startsWith("önceki") ||
				content.startsWith("bir sonraki") || content.startsWith("bir önceki") ||
				content.startsWith("diğer kanal") || content.startsWith("başka kanal")) {
			return VoiceIntent.playback(content.contains("önceki") ? VoiceIntent.PlaybackAction.PREVIOUS :
					VoiceIntent.PlaybackAction.NEXT);
		}

		if (content.equals("geri")) return VoiceIntent.playback(VoiceIntent.PlaybackAction.BACK);
		String sec = section(content);
		if (sec != null) {
			if (verb == Verb.FIND) return null;
			if ("favorites".equals(sec) && (verb == Verb.PLAY))
				return VoiceIntent.playback(VoiceIntent.PlaybackAction.PLAY_FAVORITES);
			return ADDON_SECTIONS.containsKey(sec) ? VoiceIntent.openAddon(ADDON_SECTIONS.get(sec)) :
					VoiceIntent.openSection(sec);
		}

		VoiceIntent.SearchAction action = (verb == Verb.FIND) ? VoiceIntent.SearchAction.FIND :
				VoiceIntent.SearchAction.PLAY;

		Matcher m = LOCATED.matcher(content);
		if (m.matches()) {
			String before = m.group(1);
			String after = m.group(3);
			String query = (after != null) ? after : before;
			if ((query != null) && !query.isBlank()) {
				String addon = switch (m.group(2)) {
					case "youtube", "you tube", "yutup" -> "youtube";
					case "televizyon", "tv" -> "tv";
					case "radyo" -> "radio";
					case "podcast" -> "podcast";
					default -> "web";
				};
				query = qualifiedName(query);
				if ("web".equals(addon)) action = VoiceIntent.SearchAction.FIND;
				return query.isEmpty() ? null : VoiceIntent.search(action, addon, query);
			}
		}
		if ((m = PREFIXED.matcher(content)).matches()) {
			String addon = switch (m.group(1)) {
				case "tv", "televizyon", "tivi" -> "tv";
				case "radyo", "radio" -> "radio";
				case "podcast" -> "podcast";
				case "web", "internet" -> "web";
				default -> "youtube";
			};
			String query = qualifiedName(m.group(2));
			if (!query.isEmpty()) return VoiceIntent.search(action, addon, query);
		}
		if ((m = CHANNEL.matcher(content)).matches())
			return VoiceIntent.search(action, "tv", name(m.group(1)));
		if ((m = STATION.matcher(content)).matches())
			return VoiceIntent.search(action, "radio", name(m.group(1)));

		String query = name(content);
		if (query.isEmpty()) return null;
		if (looksLikeAddress(query)) return VoiceIntent.search(VoiceIntent.SearchAction.OPEN, "web", query);
		if (FM.matcher(query).matches()) return VoiceIntent.search(action, "radio", query);
		// A bare name: the handler looks for it in TV channels, then radio stations.
		return VoiceIntent.search(action, null, query);
	}

	/** A channel or station name as spoken: "Kanal D" stays "kanal d". */
	private static String name(String text) {
		return digits(stripCase(text.trim())).trim();
	}

	/** A query inside a section, where "video", "kanal"... only describe it: "video istanbul". */
	private static String qualifiedName(String text) {
		return VoiceIntentParser.removeContentQualifier(name(text)).trim();
	}

	@Nullable
	private static VoiceIntent volume(String t) {
		if (MUTE.matcher(t).matches()) return VoiceIntent.volume(VoiceIntent.VolumeAction.MUTE);
		Matcher m = VOLUME.matcher(t);
		if (!m.matches()) return null;
		String rest = m.group(1);
		if (rest.matches("(?:aç|yükselt|artır|arttır|yüksel|fazlalaştır|çoğalt)\\S*.*"))
			return VoiceIntent.volume(VoiceIntent.VolumeAction.UP);
		if (rest.matches("(?:kıs|azalt|düşür|alçalt|indir)\\S*.*"))
			return VoiceIntent.volume(VoiceIntent.VolumeAction.DOWN);
		if (rest.matches("(?:kapat|kes|sustur)\\S*.*"))
			return VoiceIntent.volume(VoiceIntent.VolumeAction.MUTE);
		return null;
	}

	static boolean looksLikeAddress(String text) {
		if (text.contains(" ")) return false;
		String host = text.split("[/?#]", 2)[0];
		int dot = host.lastIndexOf('.');
		return (dot > 0) && (dot < host.length() - 2) &&
				host.substring(dot + 1).matches("[a-z]{2,24}") && host.matches("[a-z0-9.-]+");
	}
}
