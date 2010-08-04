package aeminium.runtime.scheduler.workstealing;

import aeminium.runtime.task.RuntimeTask;

public interface WorkStealingScheduler<T extends RuntimeTask> {
	public void registerThread(WorkerThread<T> thread);
	public T scanQueues();
	public void parkThread(WorkerThread<T> thread);
}