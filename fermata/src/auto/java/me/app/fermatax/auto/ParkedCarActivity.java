package me.app.fermatax.auto;

import static me.aap.utils.async.Completed.completed;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.function.BooleanSupplier;

import me.aap.fermata.FermataApplication;
import me.aap.fermata.R;
import me.aap.fermata.addon.AddonInfo;
import me.aap.fermata.addon.AddonManager;
import me.aap.fermata.addon.AddonState;
import me.aap.fermata.auto.AutomotiveConnectionState;
import me.aap.fermata.auto.AutomotiveNavigationController;
import me.aap.fermata.auto.AutomotiveNavigationController.OpenResult;
import me.aap.fermata.auto.OpenOnCarKind;
import me.aap.fermata.auto.OpenOnCarMediaRouting;
import me.aap.fermata.auto.OpenOnCarRequest;
import me.aap.fermata.auto.OpenOnCarRequestHandler;
import me.aap.fermata.ui.activity.MainActivity;
import me.aap.fermata.ui.activity.MainActivityDelegate;
import me.aap.fermata.ui.policy.ItemRoutePolicy;
import me.aap.fermata.ui.policy.RuntimeHostMode;
import me.aap.utils.async.FutureSupplier;
import me.aap.utils.async.Promise;
import me.aap.utils.ui.fragment.ActivityFragment;

/**
 * Android 15+ parked-app entry: Android Auto starts this activity directly on the head unit
 * display. It acts as the projected car host, exactly like {@link MainCarActivity}: it uses the
 * automotive presentation, owns car playback and accepts "open on car" requests from the phone.
 */
public final class ParkedCarActivity extends MainActivity {
	private final Object controlVisibilityOwner = new Object();
	private final AutomotiveNavigationController.Navigator phoneNavigator =
			new AutomotiveNavigationController.Navigator() {
				@Override
				public FutureSupplier<OpenResult> open(int destinationId, BooleanSupplier stillCurrent) {
					return openFromPhone(destinationId, stillCurrent);
				}

				@Override
				public FutureSupplier<OpenResult> open(OpenOnCarRequest request,
						BooleanSupplier stillCurrent) {
					return openFromPhone(request, stillCurrent);
				}
			};
	private InlineMirrorView inlineMirror;
	private boolean resumed;
	private boolean destroyed;

	@Override
	public boolean isCarActivity() {
		return true;
	}

	@Override
	public boolean isParkedCarActivity() {
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AutoSessionShutdown.sessionStarted();
	}

	@Override
	protected void onResume() {
		super.onResume();
		resumed = true;
		AutomotiveConnectionState.get().appVisibilityChanged(controlVisibilityOwner, true);
		AutomotiveNavigationController.get().register(phoneNavigator);
	}

	@Override
	protected void onPause() {
		stopInput();
		AutomotiveNavigationController.get().unregister(phoneNavigator);
		AutomotiveConnectionState.get().appVisibilityChanged(controlVisibilityOwner, false);
		resumed = false;
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		stopInlineMirror();
		AutomotiveNavigationController.get().unregister(phoneNavigator);
		AutomotiveConnectionState.get().appVisibilityChanged(controlVisibilityOwner, false);
		destroyed = true;
		super.onDestroy();
	}

	private MainActivityDelegate liveDelegate() {
		return (resumed && !destroyed) ? getActivityDelegate().peek() : null;
	}

	private void stopInlineMirror() {
		InlineMirrorView view = inlineMirror;
		inlineMirror = null;
		if (view == null) return;
		view.close();
		if (view.getParent() instanceof ViewGroup parent) parent.removeView(view);
	}

	private FutureSupplier<OpenResult> openFromPhone(int destinationId,
			BooleanSupplier stillCurrent) {
		MainActivityDelegate delegate = liveDelegate();
		if (!stillCurrent.getAsBoolean()) return completed(OpenResult.CANCELLED);
		if (delegate == null) return completed(OpenResult.NOT_READY);
		BooleanSupplier guard = () -> stillCurrent.getAsBoolean() && (liveDelegate() == delegate);
		Promise<OpenResult> result = new Promise<>();
		FermataApplication.get().getHandler().post(() -> {
			if (!guard.getAsBoolean()) {
				result.complete(OpenResult.CANCELLED);
				return;
			}
			if (destinationId == R.id.dashboard_mirror_action) {
				ViewGroup root = findViewById(R.id.main_activity);
				if (root == null) {
					result.complete(OpenResult.NOT_READY);
					return;
				}
				stopInlineMirror();
				inlineMirror = new InlineMirrorView(root.getContext(), this::stopInlineMirror);
				ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(0, 0);
				params.leftToLeft = params.rightToRight = params.topToTop = params.bottomToBottom = 0;
				root.addView(inlineMirror, params);
				inlineMirror.setTranslationZ(100 * root.getResources().getDisplayMetrics().density);
				inlineMirror.requestFocus();
				result.complete(OpenResult.OPENED);
				return;
			}
			stopInlineMirror();
			delegate.showFragmentWhenReadyGuarded(destinationId, null, guard)
					.onCompletion((opened, error) -> {
						if (!guard.getAsBoolean()) result.complete(OpenResult.CANCELLED);
						else if (error != null) result.complete(OpenResult.FAILED);
						else if (Boolean.TRUE.equals(opened)) result.complete(OpenResult.OPENED);
						else result.complete(failedOpenResult(destinationId));
					});
		});
		return result;
	}

	private FutureSupplier<OpenResult> openFromPhone(OpenOnCarRequest request,
			BooleanSupplier stillCurrent) {
		FermataApplication.get().getHandler().post(() -> {
			if (stillCurrent.getAsBoolean()) stopInlineMirror();
		});
		return MainCarActivity.dispatchPhoneRequest(request, stillCurrent,
				destinationId -> openFromPhone(destinationId, stillCurrent),
				typed -> openAddonFromPhone(typed, R.id.web_browser_fragment, stillCurrent),
				typed -> openMediaFromPhone(typed, stillCurrent),
				typed -> openAddonFromPhone(typed, R.id.stremio_fragment, stillCurrent));
	}

	/** Web and Stremio requests: the addon must already be loaded, the fragment handles the rest. */
	private FutureSupplier<OpenResult> openAddonFromPhone(OpenOnCarRequest request, int fragmentId,
			BooleanSupplier stillCurrent) {
		MainActivityDelegate captured = liveDelegate();
		if (!stillCurrent.getAsBoolean()) return completed(OpenResult.CANCELLED);
		if (captured == null) return completed(OpenResult.NOT_READY);
		BooleanSupplier guard = () -> stillCurrent.getAsBoolean() && (liveDelegate() == captured);
		Promise<OpenResult> result = new Promise<>();
		FermataApplication.get().getHandler().post(() -> {
			if (!guard.getAsBoolean()) {
				result.complete(OpenResult.CANCELLED);
				return;
			}
			AddonManager manager = AddonManager.get();
			AddonInfo info = manager.getAddonInfo(fragmentId);
			if ((info == null) || (manager.getAddonState(info) != AddonState.LOADED)) {
				result.complete(OpenResult.NOT_READY);
				return;
			}
			captured.showFragmentWhenReadyGuarded(fragmentId, null, guard)
					.onCompletion((opened, error) -> captured.post(() -> {
						if (!guard.getAsBoolean()) result.complete(OpenResult.CANCELLED);
						else if (error != null) result.complete(OpenResult.FAILED);
						else if (!Boolean.TRUE.equals(opened)) result.complete(OpenResult.NOT_READY);
						else {
							ActivityFragment fragment = captured.getActiveFragment();
							if ((fragment == null) || (fragment.getFragmentId() != fragmentId) ||
									!(fragment instanceof OpenOnCarRequestHandler handler))
								result.complete(OpenResult.NOT_READY);
							else result.complete(handler.openOnCar(request, guard));
						}
					}));
		});
		return result;
	}

	private FutureSupplier<OpenResult> openMediaFromPhone(OpenOnCarRequest request,
			BooleanSupplier stillCurrent) {
		MainActivityDelegate captured = liveDelegate();
		if (!stillCurrent.getAsBoolean()) return completed(OpenResult.CANCELLED);
		if (captured == null) return completed(OpenResult.NOT_READY);
		BooleanSupplier guard = () -> stillCurrent.getAsBoolean() && (liveDelegate() == captured);
		Promise<OpenResult> result = new Promise<>();
		captured.post(() -> {
			if (!guard.getAsBoolean()) {
				result.complete(OpenResult.CANCELLED);
				return;
			}
			if (hasCustomEngineProvider(captured)) {
				result.complete(OpenResult.NOT_READY);
				return;
			}
			if (request.kind() == OpenOnCarKind.YOUTUBE_VIDEO) {
				captured.showFragmentWhenReadyGuarded(R.id.youtube_fragment, null, guard)
						.onCompletion((opened, error) -> captured.post(() -> {
							if (!guard.getAsBoolean()) result.complete(OpenResult.CANCELLED);
							else if (error != null) result.complete(OpenResult.FAILED);
							else if (hasCustomEngineProvider(captured))
								result.complete(OpenResult.NOT_READY);
							else if (!Boolean.TRUE.equals(opened) ||
									!(captured.getActiveFragment() instanceof OpenOnCarRequestHandler handler))
								result.complete(OpenResult.NOT_READY);
							else result.complete(handler.openOnCar(request, guard));
						}));
				return;
			}
			var media = (OpenOnCarMediaRouting.MediaItem) request.payload();
			int destination = ItemRoutePolicy.getFragmentId(media.item());
			if (destination == 0) {
				result.complete(OpenResult.NOT_READY);
				return;
			}
			captured.showFragmentWhenReadyGuarded(destination, null, guard)
					.onCompletion((opened, error) -> captured.post(() -> {
						if (!guard.getAsBoolean()) result.complete(OpenResult.CANCELLED);
						else if (error != null) result.complete(OpenResult.FAILED);
						else if (!Boolean.TRUE.equals(opened)) result.complete(OpenResult.NOT_READY);
						else if (hasCustomEngineProvider(captured))
							result.complete(OpenResult.NOT_READY);
						else captured.getBody().playRoutedItem(media.item(), media.position(),
										new OpenOnCarMediaRouting.Admission(RuntimeHostMode.AA_PROJECTION, guard))
								.onCompletion((value, failure) ->
										result.complete(failure == null ? value : OpenResult.FAILED));
					}));
		});
		return result;
	}

	private static boolean hasCustomEngineProvider(MainActivityDelegate delegate) {
		return delegate.getMediaSessionCallback().hasCustomEngineProvider();
	}

	private static OpenResult failedOpenResult(int destinationId) {
		AddonInfo info = AddonManager.get().getAddonInfo(destinationId);
		if (info == null) return OpenResult.FAILED;
		return switch (AddonManager.get().getAddonState(info)) {
			case DISABLED -> OpenResult.DISABLED;
			case FAILED -> OpenResult.FAILED;
			default -> OpenResult.NOT_READY;
		};
	}
}
