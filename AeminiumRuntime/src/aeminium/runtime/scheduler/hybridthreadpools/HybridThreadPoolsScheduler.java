package aeminium.runtime.scheduler.hybridthreadpools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aeminium.runtime.BlockingTask;
import aeminium.runtime.NonBlockingTask;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.scheduler.RuntimeScheduler;
import aeminium.runtime.task.RuntimeTask;

public class HybridThreadPoolsScheduler<T extends RuntimeTask> implements RuntimeScheduler<T>, RuntimePrioritizer<T> {
	private ExecutorService blockingService;
	private ExecutorService nonblockingService; 

	public void init() {
		blockingService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*10);
		nonblockingService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	}
	
	@Override
	public void scheduleTasks(T ... tasks) {
		for ( int i = 0; i < tasks.length; i++ ) {
			scheduleTask(tasks[i]);
		}
	}

	protected void scheduleTask(T task) {
		if ( task instanceof NonBlockingTask ) {
			nonblockingService.submit(task);
		} else {
			blockingService.submit(task);
		}		
	}

	@Override
	public void shutdown() {
		if ( blockingService != null ) {
			blockingService.shutdown();
			blockingService = null;
		}
		if ( nonblockingService != null) {
			nonblockingService.shutdown();
			nonblockingService = null;
		}
	}

}

