package aeminium.runtime.scheduler.workstealing;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

import aeminium.runtime.task.RuntimeTask;

public final class WorkerThread<T extends RuntimeTask> extends Thread {
	protected final Deque<T> taskQueue;
	protected final int index;
	protected volatile boolean shutdown = false;
	protected final WorkStealingScheduler<T> scheduler;
	protected final int POLL_COUNT = 5;
	
	public WorkerThread(int index, WorkStealingScheduler<T> scheduler) {
		this.taskQueue =  new LinkedBlockingDeque<T>();
		this.index = index;
		this.scheduler = scheduler;
	}

	public int getIndex() {
		return index;
	}
	
	public Deque<T> getTaskList() {
		return taskQueue;
	}
	
	public void shutdown() {
		shutdown = true;
	}
	
	@Override
	public void run() {
		int pollCounter = POLL_COUNT;
		scheduler.registerThread(this);
		while (!shutdown) {
			T task = null;
			task = taskQueue.pollFirst();
			if ( task != null ) {
				executeTask(task);
			} else {
				// scan for other queues
				task = scheduler.scanQueues();
				if ( task != null ) {
					executeTask(task);
				} else {
					if ( pollCounter == 0) {
						scheduler.parkThread(this);
						pollCounter = POLL_COUNT;
					} else {
						pollCounter--;
					}
				}
			}
		}
		scheduler.unregisterThread(this);
	}
	
	protected void executeTask(T task) {
		try {
			task.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String toString() {
		return "WorkerThread<" + index + ">";
	}
}