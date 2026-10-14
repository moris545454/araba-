package me.app.fermatax.auto;

import static me.aap.utils.async.Completed.completed;
import static me.aap.utils.async.Completed.failed;

import android.app.Presentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.Display;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView.OnEditorActionListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.FragmentController;
import androidx.fragment.app.FragmentHostCallback;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewTreeLifecycleOwner;

import java.util.function.BooleanSupplier;

import me.aap.fermata.FermataApplication;
import me.aap.fermata.R;
import me.aap.fermata.auto.AutomotiveConnectionState;
import me.aap.fermata.auto.AutomotiveNavigationController;
import me.aap.fermata.auto.AutomotiveNavigationController.OpenResult;
import me.aap.fermata.media.service.FermataMediaServiceConnection;
import me.aap.fermata.ui.activity.FermataActivity;
import me.aap.fermata.ui.activity.MainActivityDelegate;
import me.aap.utils.async.FutureSupplier;
import me.aap.utils.async.Promise;
import me.aap.utils.function.Function;
import me.aap.utils.function.Supplier;
import me.aap.utils.log.Log;
import me.aap.utils.ui.activity.ActivityDelegate;
import me.aap.utils.ui.view.TextChangedListener;

/**
 * Hosts the regular Mola car UI ({@link MainActivityDelegate}) in a {@link Presentation} on a
 * private virtual display, so it can be drawn on the navigation map surface (full screen).
 * It plays the role {@link MainCarActivity} plays for the legacy projection entry.
 */
final class FullScreenAppHost extends ContextThemeWrapper implements FermataActivity,
		LifecycleOwner, CarKeyboardHost {
	private final Handler handler = new Handler(Looper.getMainLooper());
	private final LifecycleRegistry lifecycle = new LifecycleRegistry(this);
	private final Intent intent = new Intent();
	private final Presentation window;
	private final FragmentController fragments;
	private final Runnable onFinish;
	@SuppressWarnings("unchecked")
	@NonNull
	private FutureSupplier<MainActivityDelegate> delegate =
			(FutureSupplier<MainActivityDelegate>) NO_DELEGATE;
	private MainActivityDelegate created;
	private Function<Context, ActivityDelegate> contextDelegate;
	private FermataMediaServiceConnection connection;
	private View content;
	private InlineMirrorView inlineMirror;
	// Projected text input, same model as MainCarActivity: the in-app keyboard edits the input.
	private CarEditText editText;
	private EditText activeInput;
	private TextWatcher textWatcher;
	private boolean ownsEditorActionListener;
	private CarKeyboardOverlay keyboardOverlay;
	private CarTextInputSession textInputSession;
	private long inputGeneration;
	private final Object controlVisibilityOwner = new Object();
	// Same role as MainCarActivity's phoneNavigator: dashboard actions such as screen mirroring
	// are routed through it; without one they fall back to a phone dialog this host cannot show.
	private final AutomotiveNavigationController.Navigator navigator = this::open;
	private boolean finishing;
	private boolean destroyed;
	// Synthetic finger for SurfaceCallback.onScroll, released shortly after the last scroll.
	private boolean dragging;
	private long dragDownTime;
	private float dragX;
	private float dragY;
	private final Runnable endDrag = this::endDrag;

	FullScreenAppHost(Context displayContext, Display display, Runnable onFinish) {
		super(MainActivityDelegate.attachBaseContext(displayContext), 0);
		this.onFinish = onFinish;
		MainActivityDelegate.setTheme(this, true);
		window = new Presentation(this, display);
		window.setCancelable(false);
		Window w = window.getWindow();
		if (w != null) w.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
		fragments = FragmentController.createController(new Host());
		fragments.attachHost(null);
	}

	void start() {
		window.show();
		lifecycle.setCurrentState(Lifecycle.State.CREATED);
		// Same projected-session signal MainCarActivity sends on creation.
		AutoSessionShutdown.sessionStarted();
		Promise<MainActivityDelegate> promise = new Promise<>();
		delegate = promise;
		int color = FermataMediaServiceConnection.resolveNotificationColor(this);
		FermataMediaServiceConnection.connect(color).main().onCompletion((c, failure) -> {
			if (destroyed) {
				if (c != null) c.disconnect();
				promise.cancel();
				return;
			}
			if (failure != null) {
				Log.e(failure, "Full screen AA startup failed");
				promise.completeExceptionally(failure);
				return;
			}
			connection = c;
			try {
				MainActivityDelegate d = new MainActivityDelegate(this, c.createBinder());
				created = d;
				contextDelegate = ctx -> d;
				ActivityDelegate.setContextToDelegate(contextDelegate);
				delegate = completed(d);
				fragments.dispatchCreate();
				d.onActivityCreate(null);
				fragments.dispatchActivityCreated();
				fragments.dispatchStart();
				d.onHostForegrounded();
				lifecycle.setCurrentState(Lifecycle.State.STARTED);
				fragments.dispatchResume();
				d.onActivityResume();
				lifecycle.setCurrentState(Lifecycle.State.RESUMED);
				AutomotiveConnectionState.get().appVisibilityChanged(controlVisibilityOwner, true);
				AutomotiveNavigationController.get().register(navigator);
				fragments.execPendingActions();
				promise.complete(d);
			} catch (Throwable error) {
				Log.e(error, "Full screen AA startup failed while attaching UI");
				promise.completeExceptionally(error);
			}
		});
	}

	void destroy() {
		if (destroyed) return;
		destroyed = true;
		handler.removeCallbacks(endDrag);
		stopInput();
		stopInlineMirror();
		AutomotiveNavigationController.get().unregister(navigator);
		AutomotiveConnectionState.get().appVisibilityChanged(controlVisibilityOwner, false);
		MainActivityDelegate d = created;
		created = null;
		try {
			if (d != null) {
				d.onActivityPause();
				fragments.dispatchPause();
				fragments.dispatchStop();
				d.onActivityDestroy();
				fragments.dispatchDestroy();
			}
		} catch (Throwable error) {
			Log.e(error, "Full screen AA UI teardown failed");
		} finally {
			Function<Context, ActivityDelegate> owner = contextDelegate;
			contextDelegate = null;
			if ((owner != null) && (ActivityDelegate.getContextToDelegate() == owner)) {
				ActivityDelegate.setContextToDelegate(null);
			}
			if (lifecycle.getCurrentState() != Lifecycle.State.INITIALIZED) {
				lifecycle.setCurrentState(Lifecycle.State.DESTROYED);
			}
			Promise<?> p = (delegate instanceof Promise<?> pending) ? pending : null;
			if ((p != null) && !p.isDone()) p.cancel();
			delegate = (FutureSupplier<MainActivityDelegate>) NO_DELEGATE;
			FermataMediaServiceConnection c = connection;
			connection = null;
			if (c != null) c.disconnect();
			window.dismiss();
		}
	}

	private FutureSupplier<OpenResult> open(int destinationId, BooleanSupplier stillCurrent) {
		MainActivityDelegate delegate = created;
		if (!stillCurrent.getAsBoolean()) return completed(OpenResult.CANCELLED);
		if (destroyed || (delegate == null)) return completed(OpenResult.NOT_READY);
		Promise<OpenResult> result = new Promise<>();
		FermataApplication.get().getHandler().post(() -> {
			if (!stillCurrent.getAsBoolean() || destroyed || (created != delegate)) {
				result.complete(OpenResult.CANCELLED);
				return;
			}
			if (destinationId == R.id.dashboard_mirror_action) {
				android.view.ViewGroup root = findViewById(R.id.main_activity);
				if (root == null) {
					result.complete(OpenResult.NOT_READY);
					return;
				}
				stopInlineMirror();
				inlineMirror = new InlineMirrorView(root.getContext(), this::stopInlineMirror);
				androidx.constraintlayout.widget.ConstraintLayout.LayoutParams params =
						new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(0, 0);
				params.leftToLeft = params.rightToRight = params.topToTop = params.bottomToBottom = 0;
				root.addView(inlineMirror, params);
				inlineMirror.setTranslationZ(100 * root.getResources().getDisplayMetrics().density);
				inlineMirror.requestFocus();
				result.complete(OpenResult.OPENED);
				return;
			}
			stopInlineMirror();
			delegate.showFragmentWhenReadyGuarded(destinationId, null, () ->
					stillCurrent.getAsBoolean() && !destroyed && (created == delegate))
					.onCompletion((opened, error) -> {
						if (!stillCurrent.getAsBoolean() || destroyed || (created != delegate))
							result.complete(OpenResult.CANCELLED);
						else if (error != null) result.complete(OpenResult.FAILED);
						else if (Boolean.TRUE.equals(opened)) result.complete(OpenResult.OPENED);
						else result.complete(OpenResult.NOT_READY);
					});
		});
		return result;
	}

	private void stopInlineMirror() {
		InlineMirrorView view = inlineMirror;
		inlineMirror = null;
		if (view == null) return;
		view.close();
		if (view.getParent() instanceof android.view.ViewGroup parent) parent.removeView(view);
	}

	void back() {
		if (isInputActive()) {
			stopInput();
			return;
		}
		if (inlineMirror != null) {
			stopInlineMirror();
			return;
		}
		MainActivityDelegate d = created;
		if (d != null) d.onBackPressed();
	}

	// Text input: mirrors MainCarActivity so search fields and the web address bar open the
	// in-app car keyboard instead of a phone IME that cannot reach the car screen.

	@Override
	public EditText startInput(TextWatcher w) {
		cancelTextInputSession();
		inputGeneration++;
		if (editText == null) editText = new CarEditText(this);
		setActiveInput(editText, w);
		if (w instanceof OnEditorActionListener listener)
			editText.setOnEditorActionListener(listener);
		startCarInput(editText, false);
		return editText;
	}

	@Override
	public EditText startInput(EditText target, boolean submitOnEnter, TextWatcher w) {
		cancelTextInputSession();
		if (!(target instanceof CarEditText input)) return startInput(w);
		inputGeneration++;
		setActiveInput(input, w);
		startCarInput(input, submitOnEnter);
		return input;
	}

	@Override
	public FutureSupplier<String> requestTextInput(CharSequence title, CharSequence initialText,
			int inputType) {
		stopInput();
		CarEditText input = new CarEditText(this);
		input.setHint(title);
		input.setSingleLine(true);
		input.setInputType(inputType);
		input.setText(initialText == null ? "" : initialText);
		input.setSelection(input.getText().length());
		CarTextInputSession session = new CarTextInputSession();
		textInputSession = session;
		setActiveInput(input, (TextChangedListener) text -> {
		});
		input.setOnEditorActionListener((view, actionId, event) -> {
			if ((actionId != EditorInfo.IME_ACTION_DONE) &&
					(actionId != EditorInfo.IME_ACTION_GO) &&
					(actionId != EditorInfo.IME_ACTION_SEARCH) &&
					(actionId != EditorInfo.IME_ACTION_SEND) &&
					(actionId != EditorInfo.IME_ACTION_NEXT)) return false;
			if (textInputSession == session) session.submit(input.getText());
			return true;
		});
		startCarInput(input, true);
		session.getResult().onCompletion((value, error) ->
				handler.post(() -> finishTextInputSession(session)));
		return session.getResult();
	}

	@Override
	public EditText createEditText(Context ctx) {
		CarEditText et = new CarEditText(ctx);
		Runnable open = () -> {
			if (isInputActive()) return;
			cancelTextInputSession();
			inputGeneration++;
			setActiveEmbeddedInput(et);
			et.requestFocus();
			showKeyboardOverlay(et, false);
		};
		et.setOnClickListener(v -> open.run());
		// The first tap on an unfocused field only focuses it, so open the keyboard on touch up.
		et.setOnTouchListener((v, e) -> {
			if (e.getAction() == MotionEvent.ACTION_UP) handler.post(open);
			return false;
		});
		return et;
	}

	@Override
	public void stopInput() {
		inputGeneration++;
		cancelTextInputSession();
		stopInputView();
	}

	@Override
	public boolean isInputActive() {
		return (keyboardOverlay != null) && keyboardOverlay.isShowing();
	}

	@Override
	public boolean setTextInput(String text) {
		if (activeInput == null) return false;
		activeInput.setText(text);
		CarTextInputSession session = textInputSession;
		if (session == null) stopInput();
		else session.submit(text);
		return true;
	}

	@Override
	public boolean submitTextInput(EditText input) {
		if ((input == null) || (input != activeInput)) return false;
		CarTextInputSession session = textInputSession;
		if (session == null) return false;
		boolean submitted = session.submit(input.getText());
		stopInput();
		return submitted;
	}

	@Override
	public boolean isVoiceInputEnabled() {
		MainActivityDelegate d = created;
		return (d != null) && d.getPrefs().getVoiceControlEnabledPref();
	}

	@Override
	public void requestVoiceInput(EditText input) {
		MainActivityDelegate d = created;
		if ((d == null) || (input == null) || (input != activeInput)) return;
		long generation = inputGeneration;
		d.startSpeechRecognizer(null, true).onSuccess(results -> handler.post(() -> {
			if ((generation != inputGeneration) || (input != activeInput) ||
					(results == null) || results.isEmpty()) return;
			input.setText(results.get(0));
			input.setSelection(input.getText().length());
			if (keyboardOverlay != null) keyboardOverlay.refreshValue();
		}));
	}

	private void setActiveInput(EditText input, TextWatcher w) {
		releaseActiveInput();
		activeInput = input;
		textWatcher = w;
		ownsEditorActionListener = true;
		input.setOnEditorActionListener(null);
		input.addTextChangedListener(w);
	}

	private void setActiveEmbeddedInput(EditText input) {
		releaseActiveInput();
		activeInput = input;
		textWatcher = null;
		ownsEditorActionListener = false;
	}

	private void releaseActiveInput() {
		if ((activeInput != null) && (textWatcher != null))
			activeInput.removeTextChangedListener(textWatcher);
		if ((activeInput != null) && ownsEditorActionListener)
			activeInput.setOnEditorActionListener(null);
	}

	private void startCarInput(CarEditText input, boolean submitOnEnter) {
		handler.post(() -> {
			if (input != activeInput) return;
			input.requestFocus();
			showKeyboardOverlay(input, submitOnEnter);
		});
	}

	private void showKeyboardOverlay(EditText input, boolean submitOnEnter) {
		if (destroyed) return;
		if (keyboardOverlay == null) keyboardOverlay = new CarKeyboardOverlay(this);
		keyboardOverlay.show(input, submitOnEnter);
		notifyTextInputVisibilityChanged();
	}

	private void stopInputView() {
		if (keyboardOverlay != null) keyboardOverlay.dismiss();
		notifyTextInputVisibilityChanged();
		if (activeInput != null) {
			Object draft = activeInput.getTag(me.aap.utils.R.id.preference_input_draft);
			if (draft instanceof CharSequence value) {
				activeInput.setTag(me.aap.utils.R.id.preference_input_draft, null);
				activeInput.setText(value);
				activeInput.setSelection(activeInput.getText().length());
			}
			releaseActiveInput();
		}
		activeInput = null;
		textWatcher = null;
		ownsEditorActionListener = false;
	}

	private void notifyTextInputVisibilityChanged() {
		MainActivityDelegate d = created;
		if (d != null) d.onTextInputVisibilityChanged(isInputActive());
	}

	private void cancelTextInputSession() {
		CarTextInputSession session = textInputSession;
		if (session == null) return;
		textInputSession = null;
		if (!session.isDone()) session.cancel();
	}

	private void finishTextInputSession(CarTextInputSession session) {
		if (textInputSession != session) return;
		textInputSession = null;
		inputGeneration++;
		stopInputView();
	}

	// Android Auto delivers taps and scrolls on the map surface; replay them as touches.

	void tap(float x, float y) {
		if (destroyed) return;
		endDrag();
		long now = SystemClock.uptimeMillis();
		touch(now, now, MotionEvent.ACTION_DOWN, x, y);
		handler.postDelayed(() -> {
			if (!destroyed) touch(now, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y);
		}, 60);
	}

	void scroll(float distanceX, float distanceY) {
		if (destroyed || content == null) return;
		long now = SystemClock.uptimeMillis();
		if (!dragging) {
			dragging = true;
			dragDownTime = now;
			dragX = content.getWidth() / 2f;
			dragY = content.getHeight() / 2f;
			touch(now, now, MotionEvent.ACTION_DOWN, dragX, dragY);
		}
		dragX = clamp(dragX - distanceX, content.getWidth());
		dragY = clamp(dragY - distanceY, content.getHeight());
		touch(dragDownTime, now, MotionEvent.ACTION_MOVE, dragX, dragY);
		handler.removeCallbacks(endDrag);
		handler.postDelayed(endDrag, 150);
	}

	private void endDrag() {
		handler.removeCallbacks(endDrag);
		if (!dragging) return;
		dragging = false;
		touch(dragDownTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, dragX, dragY);
	}

	private void touch(long downTime, long time, int action, float x, float y) {
		MotionEvent e = MotionEvent.obtain(downTime, time, action, x, y, 0);
		e.setSource(InputDevice.SOURCE_TOUCHSCREEN);
		Window w = window.getWindow();
		if (w != null) {
			// Through the window's input pipeline, like a real finger: this also puts the window
			// in touch mode, so the focus-oriented car UI clicks on the first tap. The pipeline
			// recycles the event.
			w.injectInputEvent(e);
		} else {
			try {
				window.dispatchTouchEvent(e);
			} finally {
				e.recycle();
			}
		}
	}

	private static float clamp(float v, int max) {
		return Math.max(0f, Math.min(v, Math.max(0, max - 1)));
	}

	@NonNull
	@Override
	public Lifecycle getLifecycle() {
		return lifecycle;
	}

	@NonNull
	@Override
	public FutureSupplier<MainActivityDelegate> getActivityDelegate() {
		return delegate;
	}

	@Override
	public Window getWindow() {
		return window.getWindow();
	}

	@Override
	public View getCurrentFocus() {
		return window.getCurrentFocus();
	}

	@NonNull
	@Override
	public FragmentManager getSupportFragmentManager() {
		return fragments.getSupportFragmentManager();
	}

	@Override
	public void setContentView(int layoutResID) {
		View v = LayoutInflater.from(this).inflate(layoutResID, null, false);
		ViewTreeLifecycleOwner.set(v, this);
		content = v;
		window.setContentView(v);
	}

	@Override
	public <T extends View> T findViewById(int id) {
		return window.findViewById(id);
	}

	@Override
	public void recreate() {
	}

	@Override
	public void finish() {
		if (finishing) return;
		finishing = true;
		MainActivityDelegate d = created;
		if (d != null) d.onActivityFinish();
		handler.post(onFinish);
	}

	@Override
	public boolean isFinishing() {
		return finishing;
	}

	@Override
	public boolean isDestroyed() {
		return destroyed;
	}

	@Override
	public void startActivity(Intent intent, @Nullable Bundle options) {
		// Phone activities belong on the phone display, never on the car's virtual display.
		Intent i = new Intent(intent).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getApplicationContext().startActivity(i, options);
	}

	@Override
	public FutureSupplier<Intent> startActivityForResult(Supplier<Intent> intent) {
		return failed(new UnsupportedOperationException());
	}

	@Override
	public FutureSupplier<int[]> checkPermissions(String... perms) {
		return failed(new UnsupportedOperationException());
	}

	@Override
	public Intent getIntent() {
		return intent;
	}

	@Override
	public boolean isCarActivity() {
		return true;
	}

	/** Room for Android Auto's round action-strip button, shown over the top right corner. */
	@Override
	public int getCarHostEndInset() {
		return Math.round(72 * getResources().getDisplayMetrics().density);
	}

	@Override
	public void setRequestedOrientation(int requestedOrientation) {
	}

	private final class Host extends FragmentHostCallback<FullScreenAppHost> {
		Host() {
			super(FullScreenAppHost.this, handler, 0);
		}

		@Override
		public FullScreenAppHost onGetHost() {
			return FullScreenAppHost.this;
		}

		@NonNull
		@Override
		public LayoutInflater onGetLayoutInflater() {
			// A fresh clone per fragment, as FragmentActivity does: each fragment sets its own
			// factory on the inflater it receives, and a shared one throws on the second fragment.
			return LayoutInflater.from(FullScreenAppHost.this).cloneInContext(FullScreenAppHost.this);
		}

		@Nullable
		@Override
		public View onFindViewById(int id) {
			return window.findViewById(id);
		}

		@Override
		public boolean onHasView() {
			return content != null;
		}
	}
}
