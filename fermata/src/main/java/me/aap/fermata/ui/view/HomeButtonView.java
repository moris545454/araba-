package me.aap.fermata.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageButton;
import android.content.res.ColorStateList;
import me.aap.fermata.R;
import me.aap.fermata.ui.activity.MainActivityDelegate;

/** A root-level home action remains reachable when video or a web page hides shell chrome. */
public final class HomeButtonView extends AppCompatImageButton {
    public HomeButtonView(Context context, AttributeSet attributes) {
        super(context, attributes);
        setImageResource(R.drawable.home);
        setContentDescription(context.getString(R.string.home));
        setImageTintList(ColorStateList.valueOf(MediaHubStyle.color(context,
                android.R.attr.textColorPrimary, 0xFFF3EFE7)));
        setBackground(MediaHubStyle.card(context, 0xFF76DFC5, false));
        int padding = Math.round(13 * getResources().getDisplayMetrics().density);
        setPadding(padding, padding, padding, padding);
        setFocusable(true);
        setOnClickListener(v -> MainActivityDelegate.get(getContext()).returnToDashboard());
    }
}
