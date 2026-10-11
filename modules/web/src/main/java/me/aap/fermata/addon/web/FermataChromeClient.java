package me.aap.fermata.addon.web;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static me.aap.utils.async.Completed.completedVoid;
import static me.aap.utils.ui.activity.ActivityListener.FRAGMENT_CONTENT_CHANGED;

import android.Manifest;
import android.content.Context;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.aap.fermata.BuildConfig;
import me.aap.fermata.addon.web.FermataWebClient.CustomViewEvent;
import me.aap.fermata.addon.web.FermataWebClient.DiagnosticsObserver;
import me.aap.fermata.addon.web.FermataWebClient.DiagnosticsSnapshot;
import me.aap.fermata.ui.activity.MainActivityDelegate;
import me.aap.fermata.ui.view.VideoView;
import me.aap.utils.app.App;
import me.aap.utils.async.FutureSupplier;
import me.aap.utils.async.Promise;
import me.aap.utils.log.Log;
import me.aap.utils.ui.activity.ActivityDelegate;
import me.aap.utils.ui.UiUtils;
import me.aap.utils.ui.view.FloatingButton;

/**
 * @author Andrey Pavlenko
 */
public class FermataChromeClient extends WebChromeClient {
	private final FermataWebView web;
	private final ViewGroup fullScreenView;
	private final WebVideoScaleController videoScale;
	private View customView;
	private CustomViewCallback customViewCallback;
	private Promise<Void> fullScreenReq;
	private long touchStamp;
	private boolean phonePermissionHintShown;
    private static final WebPermissionPolicy websitePermissions = new WebPermissionPolicy();
    private final java.util.Set<PermissionRequest> cancelledPermissions =
            java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());

	protected final DiagnosticsObserver diagnosticsObserver;

	public FermataChromeClient(FermataWebView web, ViewGroup fullScreenView) {
		this(web, fullScreenView, FermataWebClient.diagnosticsObserver());
	}

	protected FermataChromeClient(FermataWebView web, ViewGroup fullScreenView,
			DiagnosticsObserver diagnosticsObserver) {
		this.web = web;
		this.fullScreenView = fullScreenView;
		videoScale = new WebVideoScaleController(web);
		this.diagnosticsObserver = (diagnosticsObserver == null) ?
				FermataWebClient.diagnosticsObserver() : diagnosticsObserver;
	}

	public FermataWebView getWebView() {
		return web;
	}

	protected FermataChromeClient createReplacement(FermataWebView web) {
		return new FermataChromeClient(web, fullScreenView);
	}

	@Override
	public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
		Context ctx = view.getContext();
		ActivityDelegate.get(ctx).createDialogBuilder(ctx)
				.setTitle(android.R.drawable.ic_dialog_alert, android.R.string.dialog_alert_title)
				.setMessage(message)
				.setPositiveButton(android.R.string.ok, (d, w) -> result.confirm())
				.show();
		return true;
	}

	@Override
	public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
		Context ctx = view.getContext();
		ActivityDelegate.get(ctx).createDialogBuilder(ctx)
				.setMessage(message)
				.setNegativeButton(android.R.string.cancel, (d, w) -> result.cancel())
				.setPositiveButton(android.R.string.ok, (d, w) -> result.confirm())
				.show();
		return true;
	}

	@Override
	public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
		Context ctx = view.getContext();
		ActivityDelegate a = ActivityDelegate.get(ctx);
		EditText text = a.createEditText(ctx);
		text.setSingleLine();
		text.setText(defaultValue);
		a.createDialogBuilder(ctx)
				.setTitle(message).setView(text)
				.setNegativeButton(android.R.string.cancel, (d, i) -> result.cancel())
				.setPositiveButton(android.R.string.ok, (d, i) -> result.confirm(text.getText().toString())).show();
		return true;
	}

	@Override
	public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
		return onJsConfirm(view, url, message, result);
	}

	@Override
	public void onShowCustomView(View view, CustomViewCallback callback) {
		MainActivityDelegate a = getLiveActivity(view.getContext());
		if (a == null) {
			diagnosticsObserver.onCustomView(CustomViewEvent.ATTACH_REJECTED,
					customViewSnapshot(view, null));
			cancelPendingFullScreenEntry();
			callback.onCustomViewHidden();
			return;
		}

		if (view instanceof ViewGroup g) {
			View focus = g.getFocusedChild();

			if (focus != null) {
				focus.setOnTouchListener(this::onTouchEvent);
			} else {
				for (int i = 0, n = g.getChildCount(); i < n; i++) {
					g.getChildAt(i).setOnTouchListener(this::onTouchEvent);
				}
			}
		}

		customView = view;
		customViewCallback = callback;
		addCustomView(view);
		setFullScreen(a, true);
		if (shouldAttachVideoScale()) videoScale.attach();
		diagnosticsObserver.onCustomView(CustomViewEvent.ATTACHED,
				customViewSnapshot(view, a));
		view.postDelayed(() -> {
			if ((customView != view) || isCustomViewConfirmed(view)) return;
			diagnosticsObserver.onCustomView(CustomViewEvent.ATTACH_UNCONFIRMED,
					customViewSnapshot(view, getLiveActivity(view.getContext())));
		}, 1200L);

		if (fullScreenReq != null) {
			Promise<Void> req = fullScreenReq;
			fullScreenReq = null;
			req.complete(null);
		}

		a.fireBroadcastEvent(FRAGMENT_CONTENT_CHANGED);
	}

	/** Sites with their own fullscreen scale bridge can retain ownership of video geometry. */
	protected boolean shouldAttachVideoScale() {
		return true;
	}

	@Override
	public void onHideCustomView() {
		CustomViewCallback callback = customViewCallback;
		if (callback == null) return;
		View view = customView;
		customView = null;
		customViewCallback = null;
		touchStamp = 0;
		MainActivityDelegate a = getLiveActivity(view.getContext());
		if (a == null) a = getLiveActivity(web.getContext());
		diagnosticsObserver.onCustomView(CustomViewEvent.DETACH_REQUESTED,
				customViewSnapshot(view, a));
		videoScale.detach();
		removeCustomView(view);
		getWebView().setVisibility(VISIBLE);
		if (a != null) setFullScreen(a, false);
		callback.onCustomViewHidden();
		diagnosticsObserver.onCustomView(CustomViewEvent.DETACHED,
				customViewSnapshot(null, a));

		if (fullScreenReq != null) {
			Promise<Void> req = fullScreenReq;
			fullScreenReq = null;
			req.complete(null);
		}

		if (a != null) a.fireBroadcastEvent(FRAGMENT_CONTENT_CHANGED);
	}

	@Nullable
	protected static MainActivityDelegate getLiveActivity(Context context) {
		try {
			MainActivityDelegate activity = MainActivityDelegate.get(context);
			return (activity.getAppActivity().isFinishing() ||
					activity.getAppActivity().isDestroyed()) ? null : activity;
		} catch (RuntimeException ignored) {
			return null;
		}
	}

	public boolean isFullScreen() {
		return customView != null;
	}

	public ViewGroup getFullScreenView() {
		return fullScreenView;
	}

	protected void addCustomView(View view) {
		ViewGroup fs = getFullScreenView();
		fs.addView(view, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
		fs.setVisibility(VISIBLE);
	}

	protected void removeCustomView(View view) {
		ViewGroup fs = getFullScreenView();
		fs.removeView(view);
		fs.setVisibility(GONE);
	}

	protected void setFullScreen(MainActivityDelegate a, boolean fullScreen) {
		a.setVideoMode(fullScreen, null);
		a.getFloatingButton().setVisibility(GONE);
	}

	public boolean canEnterFullScreen() {
		return true;
	}

	@SuppressWarnings("UnusedReturnValue")
	public FutureSupplier<Void> enterFullScreen() {
		if (isFullScreen()) return completedVoid();

		Promise<Void> req;

		if (fullScreenReq != null) {
			req = fullScreenReq;
			fullScreenReq = null;
			req.cancel();
		}

		fullScreenReq = req = new Promise<>();

		if (!web.requestFullScreen()) {
			onShowCustomView(new FrameLayout(web.getContext()), () -> {
			});
		}

		return req;
	}

	/** Cancels an app-initiated fullscreen request that has not reached custom-view state yet. */
	public boolean cancelPendingFullScreenEntry() {
		if (fullScreenReq == null) return false;
		Promise<Void> req = fullScreenReq;
		fullScreenReq = null;
		req.cancel();
		return true;
	}

	public FutureSupplier<Void> exitFullScreen() {
		if (!isFullScreen()) return completedVoid();
		Promise<Void> req;

		if (fullScreenReq != null) {
			req = fullScreenReq;
			fullScreenReq = null;
			req.cancel();
		}

		fullScreenReq = req = new Promise<>();
		onHideCustomView();
		return req;
	}

	protected boolean onTouchEvent(View v, MotionEvent event) {
		if (!isFullScreen() || (event.getAction() != ACTION_DOWN)) return false;

		MainActivityDelegate a = MainActivityDelegate.get(v.getContext());
		FloatingButton fb = a.getFloatingButton();
		if (!a.getRuntimeHostMode().usesAutomotivePresentation()) {
			touchStamp = 0L;
			fb.setVisibility(GONE);
			return false;
		}
		long st = touchStamp = System.currentTimeMillis();

		fb.setVisibility(VISIBLE);
		App.get().getHandler().postDelayed(() -> {
			if (st == touchStamp) fb.setVisibility(GONE);
		}, 3000);

		return false;
	}

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback cb) {
        if (cb == null) return;
        if (!WebPermissionPolicy.sameOrigin(origin, web.getUrl())) {
            cb.invoke(origin, false, false);
            return;
        }
        MainActivityDelegate activity = MainActivityDelegate.get(web.getContext());
        Runnable request = () -> {
            if (!WebPermissionPolicy.sameOrigin(origin, web.getUrl())) {
                cb.invoke(origin, false, false);
                return;
            }
            if (BuildConfig.AUTO && activity.isCarActivityNotMirror()) {
                Context context = web.getContext();
                boolean permission = ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(context,
                                Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED;
                cb.invoke(origin, permission, false);
                if (!permission) showPhonePermissionHint(context);
                return;
            }
            activity.getAppActivity().checkPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION).onCompletion((permissions, error) -> {
                boolean ok = error == null && permissions.length == 2 &&
                        (permissions[0] == PERMISSION_GRANTED || permissions[1] == PERMISSION_GRANTED) &&
                        WebPermissionPolicy.sameOrigin(origin, web.getUrl());
                if (ok) websitePermissions.approve(origin, web.getUrl(), WebPermissionPolicy.LOCATION);
                cb.invoke(origin, ok, false);
            });
        };
        if (websitePermissions.allows(origin, web.getUrl(), WebPermissionPolicy.LOCATION)) request.run();
        else if (BuildConfig.AUTO && activity.isCarActivityNotMirror()) {
            cb.invoke(origin, false, false);
            showPhonePermissionHint(web.getContext());
        } else {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(web.getContext())
                    .setMessage(WebPermissionPolicy.origin(origin) + "\n\n" +
                            web.getContext().getString(me.aap.fermata.R.string.web_location_consent))
                    .setNegativeButton(android.R.string.cancel, (d, w) -> cb.invoke(origin, false, false))
                    .setPositiveButton(android.R.string.ok, (d, w) -> request.run())
                    .setOnCancelListener(d -> cb.invoke(origin, false, false)).show();
        }
    }

    @Override
    public void onPermissionRequestCanceled(PermissionRequest request) {
        cancelledPermissions.add(request);
    }

    @Override
    public void onPermissionRequest(PermissionRequest request) {
        String origin = request.getOrigin().toString();
        if (!WebPermissionPolicy.sameOrigin(origin, web.getUrl())) {
            request.deny();
            return;
        }
        Map<String, String> perms = new java.util.LinkedHashMap<>();
        boolean protectedMedia = false;
        for (String resource : request.getResources()) {
            switch (resource) {
                case PermissionRequest.RESOURCE_AUDIO_CAPTURE ->
                        perms.put(Manifest.permission.RECORD_AUDIO, resource);
                case PermissionRequest.RESOURCE_VIDEO_CAPTURE ->
                        perms.put(Manifest.permission.CAMERA, resource);
                case PermissionRequest.RESOURCE_PROTECTED_MEDIA_ID -> protectedMedia = true;
                default -> { } // Unknown WebView permissions are never granted.
            }
        }
        final boolean drm = protectedMedia;
        if (perms.isEmpty()) {
            if (drm) request.grant(new String[]{PermissionRequest.RESOURCE_PROTECTED_MEDIA_ID});
            else request.deny();
            return;
        }
        MainActivityDelegate activity = MainActivityDelegate.get(web.getContext());
        Runnable grant = () -> {
            if (cancelledPermissions.remove(request)) return;
            if (!WebPermissionPolicy.sameOrigin(origin, web.getUrl())) { request.deny(); return; }
            String[] keys = perms.keySet().toArray(new String[0]);
            if (BuildConfig.AUTO && activity.isCarActivityNotMirror()) {
                List<String> allowed = new ArrayList<>();
                for (String permission : keys) {
                    if (websitePermissions.allows(origin, web.getUrl(), perms.get(permission)) &&
                            ContextCompat.checkSelfPermission(web.getContext(), permission) == PERMISSION_GRANTED)
                        allowed.add(perms.get(permission));
                }
                if (drm) allowed.add(PermissionRequest.RESOURCE_PROTECTED_MEDIA_ID);
                if (allowed.isEmpty()) { request.deny(); showPhonePermissionHint(web.getContext()); }
                else request.grant(allowed.toArray(new String[0]));
                return;
            }
            activity.getAppActivity().checkPermissions(keys).onCompletion((permissions, error) -> {
                if (cancelledPermissions.remove(request)) return;
                if (error != null || !WebPermissionPolicy.sameOrigin(origin, web.getUrl())) {
                    request.deny(); return;
                }
                List<String> allowed = new ArrayList<>();
                for (int i = 0; i < Math.min(keys.length, permissions.length); i++) {
                    if (permissions[i] == PERMISSION_GRANTED) {
                        String resource = perms.get(keys[i]);
                        websitePermissions.approve(origin, web.getUrl(), resource);
                        allowed.add(resource);
                    }
                }
                if (drm) allowed.add(PermissionRequest.RESOURCE_PROTECTED_MEDIA_ID);
                if (allowed.isEmpty()) request.deny();
                else request.grant(allowed.toArray(new String[0]));
            });
        };
        boolean approved = perms.values().stream().allMatch(resource ->
                websitePermissions.allows(origin, web.getUrl(), resource));
        if (approved) grant.run();
        else if (BuildConfig.AUTO && activity.isCarActivityNotMirror()) {
            request.deny(); showPhonePermissionHint(web.getContext());
        } else {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(web.getContext())
                    .setMessage(WebPermissionPolicy.origin(origin) + "\n\n" +
                            web.getContext().getString(me.aap.fermata.R.string.web_capture_consent))
                    .setNegativeButton(android.R.string.cancel, (d, w) -> {
                        if (!cancelledPermissions.remove(request)) request.deny();
                    })
                    .setPositiveButton(android.R.string.ok, (d, w) -> grant.run())
                    .setOnCancelListener(d -> {
                        if (!cancelledPermissions.remove(request)) request.deny();
                    }).show();
        }
    }

	private void showPhonePermissionHint(Context context) {
		if (phonePermissionHintShown) return;
		phonePermissionHintShown = true;
		UiUtils.showAlert(context, context.getString(me.aap.fermata.R.string.use_phone_to_grant_perm));
	}

	@Override
	public boolean onConsoleMessage(ConsoleMessage m) {
		// Do not persist JavaScript payloads; they may contain page data or credentials.
		Log.d("Web console message received");
		return true;
	}

	private DiagnosticsSnapshot customViewSnapshot(View view, MainActivityDelegate activity) {
		try {
			boolean webVisible = (web != null) && (web.getVisibility() == VISIBLE);
			boolean webAttached = (web != null) && web.isAttachedToWindow();
			int webWidth = (web == null) ? 0 : web.getWidth();
			int webHeight = (web == null) ? 0 : web.getHeight();
			SurfaceView surface = (fullScreenView instanceof VideoView video) ?
					video.getVideoSurface() : null;
			boolean surfaceKnown = surface != null;
			boolean surfaceValid = surfaceKnown && isCustomViewSurfaceReady(surface);
			return DiagnosticsSnapshot.builder()
					.view((view != null) && (view.getVisibility() == VISIBLE),
							(view != null) && view.isAttachedToWindow(),
							(view == null) ? 0 : view.getWidth(),
							(view == null) ? 0 : view.getHeight())
					.web(webVisible, webAttached, webWidth, webHeight)
					.surface(surfaceKnown, surfaceValid)
					.fullscreen(false, (activity != null) && activity.isVideoMode())
					.build();
		} catch (RuntimeException ignored) {
			return DiagnosticsSnapshot.builder().build();
		}
	}

	private boolean isCustomViewConfirmed(View view) {
		try {
			if ((view == null) || !view.isShown() || !view.isAttachedToWindow() ||
					(view.getWidth() <= 0) || (view.getHeight() <= 0)) return false;
			SurfaceView surface = (fullScreenView instanceof VideoView video) ?
					video.getVideoSurface() : null;
			return isCustomViewSurfaceReady(surface);
		} catch (RuntimeException ignored) {
			return false;
		}
	}

	/** A browser-backed fullscreen host has no native decoder Surface to wait for. */
	static boolean isCustomViewSurfaceReady(@Nullable SurfaceView surface) {
		return (surface == null) || surface.getHolder().getSurface().isValid();
	}
}
