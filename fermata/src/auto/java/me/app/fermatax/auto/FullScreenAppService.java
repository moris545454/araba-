package me.app.fermatax.auto;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.car.app.CarAppService;
import androidx.car.app.Screen;
import androidx.car.app.Session;
import androidx.car.app.validation.HostValidator;

import me.aap.utils.log.Log;

/**
 * Second Android Auto entry: the Mola UI drawn full screen on the navigation map surface.
 * The legacy {@link CarService} entry is unchanged.
 */
public class FullScreenAppService extends CarAppService {

	@NonNull
	@Override
	public HostValidator createHostValidator() {
		return new HostValidator.Builder(getApplicationContext())
				.addAllowedHosts(androidx.car.app.R.array.hosts_allowlist_sample).build();
	}

	@NonNull
	@Override
	public Session onCreateSession() {
		return new Session() {
			@NonNull
			@Override
			public Screen onCreateScreen(@NonNull Intent intent) {
				return new FullScreenAppScreen(getCarContext());
			}
		};
	}

	@Override
	public void onCreate() {
		Log.d("Creating FullScreenAppService: " + this);
		super.onCreate();
		AutoConnectionMonitor.hostCreated(this);
	}

	@Override
	public void onDestroy() {
		Log.d("Destroying FullScreenAppService: " + this);
		try {
			AutoConnectionMonitor.hostDestroyed(this);
		} finally {
			super.onDestroy();
		}
	}
}
