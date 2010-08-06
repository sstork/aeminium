package aeminium.runtime.scheduler.workstealing.blocking;

import java.util.Collection;
import java.util.Deque;
import java.util.EnumSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import aeminium.runtime.implementations.Flags;
import aeminium.runtime.scheduler.AbstractScheduler;
import aeminium.runtime.scheduler.workstealing.WorkStealingScheduler;
import aeminium.runtime.scheduler.workstealing.WorkerThread;
import aeminium.runtime.task.RuntimeTask;

public class BlockingWorkStealingScheduler<T extends RuntimeTask> extends AbstractScheduler<T> implements WorkStealingScheduler<T>{
	protected ConcurrentLinkedQueue<WorkerThread<T>> parkedThreads;
	protected ThreadLocal<WorkerThread<T>> currentThread;
	protected WorkerThread<T>[] threads;
	protected Deque<T>[] taskQueues;
	protected AtomicInteger counter;
	protected volatile boolean shutdown = false;
	
	public BlockingWorkStealingScheduler(EnumSet<Flags> flags) {
		super(flags);
	}

	public BlockingWorkStealingScheduler(int maxParallelism, EnumSet<Flags> flags) {
		super(maxParallelism, flags);
	}
	
	@Override
	public void registerThread(WorkerThread<T> thread) {
		currentThread.set(thread);
	}
	
	@Override
	public void unregisterThread(WorkerThread<T> thread) {
		if ( 0 == counter.decrementAndGet() ) {
			// last thread cleansup resources 
			threads = null;
			taskQueues = null;
			currentThread = null;
			parkedThreads = null;
			counter = null;
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void init() {
		parkedThreads = new ConcurrentLinkedQueue<WorkerThread<T>>();
		currentThread = new ThreadLocal<WorkerThread<T>>();
		threads =  new WorkerThread[getMaxParallelism()];
		taskQueues = new Deque[threads.length];
		counter = new AtomicInteger(threads.length);
				
		// initialize data structures
		for ( int i = 0; i < threads.length; i++ ) {
			threads[i] = new WorkerThread<T>(i, this);
			taskQueues[i] = threads[i].getTaskList();
		}
		
		// start and register threads threads
		for ( WorkerThread<T> thread : threads ) {
			thread.start();
		}
	}

	@Override
	public void shutdown() {
		shutdown = true;
		for ( WorkerThread<T> thread : threads ){
			thread.shutdown();
			LockSupport.unpark(thread);
		}


	}

	@Override
	public void scheduleTask(T task) {
		WorkerThread<T> thread = getNextThread();
		Deque<T> taskQueue = taskQueues[thread.getIndex()];
		addTask(taskQueue, task);
		signalWork(thread);
	}

	@Override
	public void scheduleTasks(Collection<T> tasks) {
		WorkerThread<T> thread = getNextThread();
		Deque<T> taskQueue = taskQueues[thread.getIndex()];
		for ( T task : tasks ) {
			addTask(taskQueue, task);
		}
		signalWork(thread);
	}

	protected void addTask(Deque<T> q, T task) {
		task.setScheduler(this);
		while ( !q.offerFirst(task) ) {
			// loop until we could add it 
		}
	}
	
	protected WorkerThread<T> currentThread() {
		WorkerThread<T> current = currentThread.get();
		if ( current == null ) {
			current = parkedThreads.poll();
			if ( current == null ) {
				current = threads[0];
			}
		}
		return current;
	}
	
	protected WorkerThread<T> getNextThread() {
		WorkerThread<T> thread = currentThread.get();
		if ( thread == null ) {
			thread = parkedThreads.poll();
			if ( thread == null ) {
				// TODO: should distribute work better
				thread = threads[0];
			}
		}
		return thread;
	}
	
	public void signalWork(WorkerThread<T> thread) {
		LockSupport.unpark(thread);
		WorkerThread<T> threadParked = parkedThreads.poll();
		if ( threadParked != null ) {
			LockSupport.unpark(threadParked);
		}
	}
	
	@Override
	public void parkThread(WorkerThread<T> thread) {
		if ( !shutdown ) {
			parkedThreads.add(thread);
			LockSupport.park(thread);
		}
	}

	@Override
	public T scanQueues() {
		for ( Deque<T> q : taskQueues ) {
			T task = q.pollLast();
			if ( task != null ) {
				return task;
			}
		}
		return null;
	}

	@Override
	public void taskFinished(T task) {
		// disable running count of abstract super class
	}

	@Override
	public void taskPaused(T task) {
		// disable paused count of abstract super class
	}

	@Override
	public void taskResume(T task) {
		scheduleTask(task);
	}
}