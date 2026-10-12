package me.aap.fermata.auto;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.Calendar;
import java.util.Locale;

import me.aap.fermata.FermataApplication;
import me.aap.fermata.R;
import me.aap.utils.function.BooleanSupplier;
import me.aap.utils.function.LongSupplier;
import me.aap.utils.function.Supplier;
import me.aap.utils.log.Log;
import me.aap.utils.pref.PreferenceStore;
import me.aap.utils.pref.PreferenceStore.Pref;

/**
 * Optional spoken welcome on a new Android Auto connection. Off by default. It only speaks:
 * no microphone, no playback change. Media is ducked by the system while it speaks, and any
 * failure (no TTS engine, no voice for the language) is skipped silently.
 */
public final class WelcomeGreeting {
	public static final Pref<BooleanSupplier> ENABLED =
			Pref.b("WELCOME_GREETING", false).withInheritance(false);
	public static final Pref<Supplier<String>> NAME =
			Pref.s("WELCOME_GREETING_NAME", "").withInheritance(false);
	public static final Pref<BooleanSupplier> ONCE_PER_DAY =
			Pref.b("WELCOME_GREETING_DAILY", false).withInheritance(false);
	private static final Pref<LongSupplier> LAST_DAY =
			Pref.l("WELCOME_GREETING_LAST_DAY", 0L).withInheritance(false);
	private static final long START_DELAY_MS = 2500L;
	private static final long GIVE_UP_MS = 20000L;
	private static TextToSpeech active;

	private WelcomeGreeting() {
	}

	/** Called once per new projection session. Never throws. */
	public static void onCarConnected(Context context) {
		try {
			Context app = context.getApplicationContext();
			new Handler(Looper.getMainLooper()).postDelayed(() -> greet(app), START_DELAY_MS);
		} catch (Throwable failure) {
			Log.e(failure, "Welcome greeting scheduling failed");
		}
	}

	private static void greet(Context ctx) {
		try {
			PreferenceStore store = FermataApplication.get().getPreferenceStore();
			Calendar now = Calendar.getInstance();
			long today = now.get(Calendar.YEAR) * 1000L + now.get(Calendar.DAY_OF_YEAR);
			if (!WelcomeGreetingPolicy.shouldGreet(store.getBooleanPref(ENABLED),
					store.getBooleanPref(ONCE_PER_DAY), store.getLongPref(LAST_DAY), today)) return;
			if (active != null) return;
			store.applyLongPref(LAST_DAY, today);
			speak(ctx, text(ctx, store, now.get(Calendar.HOUR_OF_DAY)));
		} catch (Throwable failure) {
			Log.e(failure, "Welcome greeting failed");
		}
	}

	static String text(Context ctx, PreferenceStore store, int hour) {
		String salutation = ctx.getString(switch (WelcomeGreetingPolicy.partOfDay(hour)) {
			case MORNING -> R.string.greeting_morning;
			case DAY -> R.string.greeting_day;
			case EVENING -> R.string.greeting_evening;
			case NIGHT -> R.string.greeting_night;
		});
		String name = WelcomeGreetingPolicy.cleanName(store.getStringPref(NAME));
		return name.isEmpty() ? ctx.getString(R.string.greeting_text, salutation) :
				ctx.getString(R.string.greeting_text_name, salutation, name);
	}

	private static void speak(Context ctx, String text) {
		Locale locale = ctx.getResources().getConfiguration().getLocales().get(0);
		AudioManager audio = ctx.getSystemService(AudioManager.class);
		AudioAttributes attrs = new AudioAttributes.Builder()
				.setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
				.setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build();
		AudioFocusRequest focus = new AudioFocusRequest.Builder(
				AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK).setAudioAttributes(attrs).build();
		Handler main = new Handler(Looper.getMainLooper());
		TextToSpeech[] holder = new TextToSpeech[1];
		Runnable finish = new Runnable() {
			private boolean done;

			@Override
			public void run() {
				if (done) return;
				done = true;
				main.removeCallbacks(this);
				TextToSpeech tts = holder[0];
				if (active == tts) active = null;
				try {
					if (tts != null) tts.shutdown();
				} catch (Throwable ignore) {
				}
				try {
					if (audio != null) audio.abandonAudioFocusRequest(focus);
				} catch (Throwable ignore) {
				}
			}
		};
		holder[0] = active = new TextToSpeech(ctx, status -> main.post(() -> {
			TextToSpeech tts = holder[0];
			if ((status != TextToSpeech.SUCCESS) || (tts == null) ||
					(tts.isLanguageAvailable(locale) < TextToSpeech.LANG_AVAILABLE)) {
				finish.run();
				return;
			}
			tts.setLanguage(locale);
			tts.setAudioAttributes(attrs);
			tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
				@Override public void onStart(String id) { }
				@Override public void onDone(String id) { main.post(finish); }
				@Override public void onError(String id) { main.post(finish); }
			});
			if (audio != null) audio.requestAudioFocus(focus);
			if (tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "mola-welcome") != TextToSpeech.SUCCESS)
				finish.run();
		}));
		main.postDelayed(finish, GIVE_UP_MS);
	}
}
