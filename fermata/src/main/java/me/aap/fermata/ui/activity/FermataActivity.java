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

	/**
	 * Width the car host keeps for its own controls in the top right corner (pixels). The tool
	 * bar ends before it so a host button that appears and hides never covers an app button.
	 */
	default int getCarHostEndInset() {
		return 0;
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
