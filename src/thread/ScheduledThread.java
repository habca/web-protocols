package thread;

import java.util.concurrent.*;

public class ScheduledThread {
	
	private ScheduledThreadPoolExecutor pool;
	private ScheduledFuture<?> task;
	private ScheduledFuture<?> stop;
	
	private long interval; // ms unit
	private long timeout; // ms unit
	
	public ScheduledThread(int interval, int timeout) {
		this.interval = interval;
		this.timeout = timeout;
				
		pool = new ScheduledThreadPoolExecutor(2); // interval and timeout
		pool.setRemoveOnCancelPolicy(true); // remove task on cancel
	}

	public final void setTask(Runnable runnableTask, Runnable runnableStop) {
		cancelTask(); // max one task group
		task = pool.scheduleAtFixedRate(runnableTask, interval, interval, TimeUnit.MILLISECONDS);
		stop = pool.schedule(runnableStop, timeout, TimeUnit.MILLISECONDS);
	}

	public final void cancelTask() {
		if (task != null && !task.isCancelled()) {
			task.cancel(true);
		}
		if (stop != null && !stop.isCancelled()) {
			stop.cancel(true);
		}
	}
	
	public final void close() {
		pool.shutdownNow();
	}
	
}
