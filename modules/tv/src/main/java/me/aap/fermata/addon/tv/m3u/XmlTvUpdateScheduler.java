package me.aap.fermata.addon.tv.m3u;

import static me.aap.utils.async.Completed.completedNull;
import static me.aap.utils.async.Completed.completedVoid;

import java.io.Closeable;
import java.util.Objects;
import java.util.function.BooleanSupplier;

import me.aap.utils.async.Async;
import me.aap.utils.async.FutureSupplier;
import me.aap.utils.function.CheckedSupplier;

final class XmlTvUpdateScheduler implements Closeable {
	/** A due update waits for the app to be in use, checking again after this delay. */
	static final long IDLE_RECHECK_MS = 5 * 60 * 1000L;
	private final Scheduler scheduler;
	private final BooleanSupplier inUse;
	private FutureSupplier<?> scheduled = completedVoid();
	private long generation;
	private boolean closed;

	XmlTvUpdateScheduler() {
		this(new Scheduler() {
			@Override
			public <T> FutureSupplier<T> schedule(
					CheckedSupplier<FutureSupplier<T>, Throwable> task, long delay) {
				return Async.schedule(task, delay);
			}
		}, me.aap.fermata.util.AppUsage::isInUse);
	}

	XmlTvUpdateScheduler(Scheduler scheduler) {
		this(scheduler, () -> true);
	}

	XmlTvUpdateScheduler(Scheduler scheduler, BooleanSupplier inUse) {
		this.scheduler = scheduler;
		this.inUse = inUse;
	}

	synchronized <T> FutureSupplier<T> schedule(
			CheckedSupplier<FutureSupplier<T>, Throwable> task, long delay) {
		if (closed) return completedNull();

		scheduled.cancel();
		long generation = ++this.generation;
		FutureSupplier<T> next = scheduler.schedule(() -> {
			synchronized (this) {
				if (closed || (generation != this.generation)) return completedNull();
				scheduled = completedVoid();
			}
			// No network refresh while nobody is using the app; try again later.
			if (!inUse.getAsBoolean()) return schedule(task, IDLE_RECHECK_MS);
			return task.get();
		}, Math.max(0L, delay));
		scheduled = Objects.requireNonNull(next);
		return next;
	}

	synchronized boolean isClosed() {
		return closed;
	}

	@Override
	public synchronized void close() {
		if (closed) return;
		closed = true;
		generation++;
		scheduled.cancel();
		scheduled = completedVoid();
	}

	interface Scheduler {
		<T> FutureSupplier<T> schedule(CheckedSupplier<FutureSupplier<T>, Throwable> task,
				long delay);
	}
}
