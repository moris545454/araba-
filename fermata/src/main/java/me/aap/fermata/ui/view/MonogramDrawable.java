package me.aap.fermata.ui.view;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

import java.util.Locale;

/** A calm initials badge for channels that have no logo, instead of a generic TV icon. */
public final class MonogramDrawable extends Drawable {
	private static final Locale TURKISH = new Locale("tr", "TR");
	private final String initials;
	private final int accent;
	private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint text = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final RectF box = new RectF();

	public MonogramDrawable(@Nullable CharSequence name, int accent) {
		this.initials = initials(name);
		this.accent = accent;
		text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
		text.setTextAlign(Paint.Align.CENTER);
		text.setColor(0xFFFBF4EA);
	}

	/** "TRT HABER" → "TH", "Kanal D" → "KD", "Tabii" → "TA"; numbering like "5. " is skipped. */
	static String initials(@Nullable CharSequence name) {
		if (name == null) return "TV";
		String clean = name.toString().replaceFirst("^\\s*\\d+\\s*[.)-]\\s*", "").trim();
		StringBuilder sb = new StringBuilder(2);
		for (String word : clean.split("\\s+")) {
			if (word.isEmpty()) continue;
			int cp = word.codePointAt(0);
			if (!Character.isLetterOrDigit(cp)) continue;
			sb.appendCodePoint(cp);
			if (sb.length() >= 2) break;
		}
		if ((sb.length() == 1) && (clean.length() > 1)) {
			int second = clean.codePointAt(clean.offsetByCodePoints(0, 1));
			if (Character.isLetterOrDigit(second)) sb.appendCodePoint(second);
		}
		if (sb.length() == 0) return "TV";
		return sb.toString().toUpperCase(TURKISH);
	}

	@Override
	protected void onBoundsChange(@NonNull Rect bounds) {
		super.onBoundsChange(bounds);
		int top = ColorUtils.blendARGB(0xFF2B231E, accent, 0.35F);
		int bottom = ColorUtils.blendARGB(0xFF1A1512, accent, 0.12F);
		fill.setShader(new LinearGradient(bounds.left, bounds.top, bounds.right, bounds.bottom,
				top, bottom, Shader.TileMode.CLAMP));
	}

	@Override
	public void draw(@NonNull Canvas canvas) {
		Rect b = getBounds();
		float size = Math.min(b.width(), b.height());
		if (size <= 0) return;
		float cx = b.exactCenterX();
		float cy = b.exactCenterY();
		float half = size / 2F;
		box.set(cx - half, cy - half, cx + half, cy + half);
		float radius = size * 0.24F;
		canvas.drawRoundRect(box, radius, radius, fill);
		text.setTextSize(size * ((initials.length() > 1) ? 0.36F : 0.44F));
		float baseline = cy - ((text.descent() + text.ascent()) / 2F);
		canvas.drawText(initials, cx, baseline, text);
	}

	@Override
	public void setAlpha(int alpha) {
		fill.setAlpha(alpha);
		text.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(@Nullable ColorFilter colorFilter) {
		fill.setColorFilter(colorFilter);
		text.setColorFilter(colorFilter);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}
}
