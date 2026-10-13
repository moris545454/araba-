package me.aap.fermata.ui.activity;

import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.Nullable;

import me.aap.utils.ui.activity.AppActivity;

/**
 * @author Andrey Pavlenko
 */
public interface FermataActivity extends AppActivity {


	boolean isCarActivity();

	/** A regular activity that Android Auto runs on the head unit display (Android 15+). */
	default boolean isParkedCarActivity() {
		return false;
	}

    /** Release host-owned overlays before returning to the app home. */
    default void onHomeRequested() { stopInput(); }

	void setRequestedOrientation(int requestedOrientation);

	@Nullable
	default EditText startInput(TextWatcher w) {
		return null;
	}

	default void stopInput() {
	}

	default boolean isInputActive() {
		return false;
	}

	default boolean setTextInput(String text) {
		return false;
	}
}
