package me.app.fermatax.auto;

import android.view.Window;
import android.widget.EditText;

/** What {@link CarKeyboardOverlay} needs from the car screen that shows it. */
interface CarKeyboardHost {
	Window getWindow();

	void stopInput();

	boolean isVoiceInputEnabled();

	void requestVoiceInput(EditText input);

	boolean submitTextInput(EditText input);
}
