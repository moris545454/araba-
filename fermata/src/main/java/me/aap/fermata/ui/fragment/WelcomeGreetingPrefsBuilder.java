package me.aap.fermata.ui.fragment;

import me.aap.fermata.FermataApplication;
import me.aap.fermata.R;
import me.aap.fermata.auto.WelcomeGreeting;
import me.aap.utils.pref.PreferenceSet;
import me.aap.utils.pref.PreferenceStore;

final class WelcomeGreetingPrefsBuilder {
	private WelcomeGreetingPrefsBuilder() {
	}

	static void add(PreferenceSet parent) {
		PreferenceStore store = FermataApplication.get().getPreferenceStore();
		PreferenceSet set = parent.subSet(o -> o.title = R.string.greeting_settings);
		set.addBooleanPref(o -> {
			o.store = store;
			o.pref = WelcomeGreeting.ENABLED;
			o.title = R.string.greeting_enabled;
			o.subtitle = R.string.greeting_enabled_sub;
		});
		set.addStringPref(o -> {
			o.store = store;
			o.pref = WelcomeGreeting.NAME;
			o.title = R.string.greeting_name;
			o.hint = R.string.greeting_name_hint;
			o.trim = true;
			// Save on Done/OK only: storing every keystroke broadcast app-wide and closed the keyboard.
			o.storeOnSubmit = true;
			o.submitOnEnter = true;
			o.imeOptions = android.view.inputmethod.EditorInfo.IME_ACTION_DONE;
			o.inputType = android.text.InputType.TYPE_CLASS_TEXT |
					android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
					android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS;
		});
		set.addBooleanPref(o -> {
			o.store = store;
			o.pref = WelcomeGreeting.ONCE_PER_DAY;
			o.title = R.string.greeting_once_per_day;
		});
	}
}
