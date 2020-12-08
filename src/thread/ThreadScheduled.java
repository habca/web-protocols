package thread;

import java.util.concurrent.*;

public class ThreadScheduled {
		
	private ScheduledThreadPoolExecutor pool;
	private ScheduledFuture<?> task;
	
	private long delay_rate; // ms unit
	
	public ThreadScheduled(int pool_size, long delay_rate) {
		this.delay_rate = delay_rate;
		pool = new ScheduledThreadPoolExecutor(pool_size);
		pool.setRemoveOnCancelPolicy(true); // remove task on cancel
	}

	public final void setTask(Runnable runnable) {
		cancelTask(); // max one task
		task = pool.scheduleAtFixedRate(runnable, 0, delay_rate, TimeUnit.MILLISECONDS);
	}

	public final void cancelTask() {
		if (task != null && !task.isCancelled()) {
			task.cancel(true);
		}
	}
	
	public final void close() {
		pool.shutdownNow();
	}
	
}
