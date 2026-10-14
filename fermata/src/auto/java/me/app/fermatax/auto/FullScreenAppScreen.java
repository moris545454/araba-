package me.app.fermatax.auto;

import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;

import androidx.annotation.NonNull;
import androidx.car.app.AppManager;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.SurfaceCallback;
import androidx.car.app.SurfaceContainer;
import androidx.car.app.model.Action;
import androidx.car.app.model.ActionStrip;
import androidx.car.app.model.CarIcon;
import androidx.core.graphics.drawable.IconCompat;
import androidx.car.app.model.Template;
import androidx.car.app.navigation.model.NavigationTemplate;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import me.aap.fermata.R;
import me.aap.utils.log.Log;

/**
 * Takes the navigation map surface (the whole car screen) and shows the Mola UI on it through a
 * private virtual display rendered by {@link FullScreenAppHost}.
 */
final class FullScreenAppScreen extends Screen implements SurfaceCallback,
		DefaultLifecycleObserver {
	private VirtualDisplay display;
	private FullScreenAppHost host;
	private boolean closed;

	FullScreenAppScreen(@NonNull CarContext carContext) {
		super(carContext);
		getLifecycle().addObserver(this);
		carContext.getCarService(AppManager.class).setSurfaceCallback(this);
	}

	@NonNull
	@Override
	public Template onGetTemplate() {
		// The template requires one action; an icon-only Back keeps the host button small.
		Action back = new Action.Builder()
				.setIcon(new CarIcon.Builder(IconCompat.createWithResource(getCarContext(),
						R.drawable.back)).build())
				.setOnClickListener(() -> { if (host != null) host.back(); }).build();
		// Pan mode is how the host delivers drag (scroll) gestures to the surface.
		return new NavigationTemplate.Builder()
				.setActionStrip(new ActionStrip.Builder().addAction(back).build())
				.setMapActionStrip(new ActionStrip.Builder().addAction(Action.PAN).build())
				.setPanModeListener(isInPanMode -> { })
				.build();
	}

	@Override
	public void onSurfaceAvailable(@NonNull SurfaceContainer c) {
		if (closed || c.getSurface() == null || c.getWidth() <= 0 || c.getHeight() <= 0) return;
		try {
			if (display != null) {
				display.resize(c.getWidth(), c.getHeight(), c.getDpi());
				display.setSurface(c.getSurface());
				return;
			}
			var app = getCarContext().getApplicationContext();
			DisplayManager dm = app.getSystemService(DisplayManager.class);
			display = dm.createVirtualDisplay("Mola", c.getWidth(), c.getHeight(), c.getDpi(),
					c.getSurface(), DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY);
			var d = display.getDisplay();
			host = new FullScreenAppHost(app.createDisplayContext(d), d,
					() -> getCarContext().finishCarApp());
			host.start();
		} catch (RuntimeException error) {
			Log.e(error, "Mola full screen display failed");
		}
	}

	@Override
	public void onSurfaceDestroyed(@NonNull SurfaceContainer c) {
		if (display != null) display.setSurface(null);
	}

	@Override
	public void onClick(float x, float y) {
		if (host != null) host.tap(x, y);
	}

	@Override
	public void onScroll(float distanceX, float distanceY) {
		if (host != null) host.scroll(distanceX, distanceY);
	}

	@Override
	public void onDestroy(@NonNull LifecycleOwner owner) {
		if (closed) return;
		closed = true;
		try {
			getCarContext().getCarService(AppManager.class).setSurfaceCallback(null);
		} catch (RuntimeException error) {
			Log.d(error, "Failed to clear surface callback");
		}
		FullScreenAppHost h = host;
		host = null;
		VirtualDisplay vd = display;
		display = null;
		try {
			if (h != null) h.destroy();
		} finally {
			if (vd != null) vd.release();
		}
	}
}
