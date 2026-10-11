package me.aap.fermata.ui.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.aap.fermata.R;

/** Original reference artwork; only decorative regions are rendered, never labels or host chrome. */
public final class DashboardArtwork extends Drawable {
    private static Bitmap reference;
    private final Bitmap bitmap;
    private final Rect source;
    private final boolean hero;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    private DashboardArtwork(Context context, Rect source, boolean hero) {
        if (reference == null) reference = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.dashboard_reference);
        bitmap = reference;
        this.source = source;
        this.hero = hero;
    }

    public static Drawable hero(Context context, @Nullable AddonUiMetadata.Role role) {
        Rect region = role == AddonUiMetadata.Role.TV ? new Rect(129, 52, 466, 193) :
                role == AddonUiMetadata.Role.YOUTUBE ? new Rect(485, 52, 834, 193) :
                        new Rect(852, 52, 1247, 193);
        return new DashboardArtwork(context, region, true);
    }

    @Nullable public static Drawable icon(Context context, int destination,
            @Nullable AddonUiMetadata.Role role) {
        Rect region = null;
        if (role == AddonUiMetadata.Role.RADIO) region = new Rect(145, 321, 201, 377);
        else if (role == AddonUiMetadata.Role.PODCAST) region = new Rect(371, 321, 427, 377);
        else if (role == AddonUiMetadata.Role.WEB) region = new Rect(597, 321, 653, 377);
        else if (destination == R.id.folders_fragment) region = new Rect(825, 321, 881, 377);
        else if (destination == R.id.favorites_fragment) region = new Rect(1054, 321, 1110, 377);
        return region == null ? null : new DashboardArtwork(context, region, false);
    }

    @Override public void draw(@NonNull Canvas canvas) {
        RectF destination = new RectF(getBounds());
        float radius = Math.min(destination.width(), destination.height()) * (hero ? .08F : .18F);
        Path clip = new Path();
        if (hero) clip.addRoundRect(destination,
                new float[]{radius,radius,radius,radius,0,0,0,0}, Path.Direction.CW);
        else clip.addRoundRect(destination, radius, radius, Path.Direction.CW);
        int checkpoint = canvas.save();
        canvas.clipPath(clip);
        canvas.drawBitmap(bitmap, source, destination, paint);
        canvas.restoreToCount(checkpoint);
    }
    @Override public int getIntrinsicWidth() { return source.width(); }
    @Override public int getIntrinsicHeight() { return source.height(); }
    @Override public void setAlpha(int alpha) { paint.setAlpha(alpha); invalidateSelf(); }
    @Override public void setColorFilter(@Nullable ColorFilter filter) {
        paint.setColorFilter(filter); invalidateSelf();
    }
    @Override public int getOpacity() { return PixelFormat.TRANSLUCENT; }
}
