package me.aap.fermata.ui.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.LinearGradient;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
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
    private final Paint fade = new Paint(Paint.ANTI_ALIAS_FLAG);
    {
        fade.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    }

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
        if (hero) {
            drawHero(canvas, destination);
            return;
        }
        float radius = Math.min(destination.width(), destination.height()) * .18F;
        Path clip = new Path();
        clip.addRoundRect(destination, radius, radius, Path.Direction.CW);
        int checkpoint = canvas.save();
        canvas.clipPath(clip);
        canvas.drawBitmap(bitmap, source, destination, paint);
        canvas.restoreToCount(checkpoint);
    }

    /**
     * Split hero: the title owns the left of the card, the artwork's scene (its right part, past
     * the decorative icon the title already names) fills the right and melts into the card
     * surface towards the text. Nothing is drawn under the words, and the card border stays.
     */
    private void drawHero(Canvas canvas, RectF card) {
        float inset = Math.max(1F, card.height() / 160F);
        RectF area = new RectF(card.left + inset, card.top + inset, card.right - inset,
                card.bottom - inset);
        Rect scene = new Rect(source.left + Math.round(source.width() * SCENE_START), source.top,
                source.right, source.bottom);
        float scale = Math.max(area.height() / scene.height(), area.width() * .64F / scene.width());
        float w = scene.width() * scale;
        float h = scene.height() * scale;
        RectF image = new RectF(area.right - w, area.centerY() - h / 2, area.right,
                area.centerY() + h / 2);
        float radius = Math.min(card.width(), card.height()) * .1F;
        Path clip = new Path();
        clip.addRoundRect(area, radius, radius, Path.Direction.CW);
        int layer = canvas.saveLayer(area, null);
        canvas.clipPath(clip);
        canvas.drawBitmap(bitmap, scene, image, paint);
        float fadeStart = Math.max(area.left, image.left);
        fade.setShader(new LinearGradient(fadeStart, 0, fadeStart + w * .5F, 0,
                0x00000000, 0xFF000000, Shader.TileMode.CLAMP));
        canvas.drawRect(area, fade);
        canvas.restoreToCount(layer);
    }

    /** Where the scene starts in each hero region: left of it is the icon the title replaces. */
    private static final float SCENE_START = .38F;

    @Override public int getIntrinsicWidth() { return source.width(); }
    @Override public int getIntrinsicHeight() { return source.height(); }
    @Override public void setAlpha(int alpha) { paint.setAlpha(alpha); invalidateSelf(); }
    @Override public void setColorFilter(@Nullable ColorFilter filter) {
        paint.setColorFilter(filter); invalidateSelf();
    }
    @Override public int getOpacity() { return PixelFormat.TRANSLUCENT; }
}
