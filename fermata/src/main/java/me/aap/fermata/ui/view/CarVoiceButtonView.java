package me.aap.fermata.ui.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageButton;

import me.aap.fermata.R;
import me.aap.fermata.ui.activity.MainActivityDelegate;

/** Car screen microphone: floats at the bottom left, right above the Home button. */
public final class CarVoiceButtonView extends AppCompatImageButton {
	public CarVoiceButtonView(Context context, AttributeSet attributes) {
		super(context, attributes);
		setImageResource(R.drawable.voice_microphone);
		setContentDescription(context.getString(R.string.action_activate_voice_ctrl));
		setImageTintList(ColorStateList.valueOf(MediaHubStyle.color(context,
				android.R.attr.textColorPrimary, 0xFFF3EFE7)));
		setBackground(MediaHubStyle.card(context, 0xFFF0B45A, false));
		int padding = Math.round(15 * getResources().getDisplayMetrics().density);
		setPadding(padding, padding, padding, padding);
		setScaleType(ScaleType.FIT_CENTER);
		setFocusable(true);
		setOnClickListener(v -> MainActivityDelegate.get(getContext()).startGlobalVoiceControl());
	}
}
