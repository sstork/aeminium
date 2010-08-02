package aeminium.runtime.scheduler.hybridthreadpools;

import java.util.Collection;
import java.util.EnumSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aeminium.runtime.NonBlockingTask;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.scheduler.AbstractScheduler;
import aeminium.runtime.scheduler.RuntimeScheduler;
import aeminium.runtime.task.RuntimeTask;

public class HybridThreadPoolsScheduler<T extends RuntimeTask> extends AbstractScheduler<T> implements RuntimeScheduler<T>, RuntimePrioritizer<T> {
	private ExecutorService blockingService;
	private ExecutorService nonblockingService; 

	public HybridThreadPoolsScheduler(EnumSet<Flags> flags) {
		super(flags);
	}

	public void init() {
		blockingService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);
		nonblockingService = Executors.newFixedThreadPool((int)(Runtime.getRuntime().availableProcessors()*1.5));
	}
	
	@Override
	public void scheduleTasks(Collection<T> tasks) {
		for ( T t : tasks ) {
			scheduleTask(t);
		}
	}


	@Override
	public void scheduleTask(T task) {
		try {
			if ( task instanceof NonBlockingTask ) {
				nonblockingService.submit(task);
			} else {
				blockingService.submit(task);
			}	
		} catch (Exception ex) {
			ex.printStackTrace();
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
