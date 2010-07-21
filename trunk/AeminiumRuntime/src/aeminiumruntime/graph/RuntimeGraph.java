package aeminiumruntime.graph;

import java.util.Collection;

import aeminiumruntime.Task;
import aeminiumruntime.task.RuntimeTask;
import aeminiumruntime.task.TaskDescription;

public interface RuntimeGraph <T extends RuntimeTask> {
	
	/**
	 * Add a new task to the graph.
	 * @param task
	 */
	public abstract void addTask(T task, Task parent, Collection<T> deps);
	
	/**
	 * Callback function for task that have finished. Finished 
	 * means that the body of the corresponding graph has 
	 * finished its execution. Note that there might still be 
	 * some sub-task pending.
	 * 
	 * @param task
	 */
	public abstract void taskFinished(T task);
	
	
	/**
	 * Method to wait until all task have completed.
	 */
	public abstract void waitToEmpty();

	
	public abstract TaskDescription<T> getTaskDescription(T task);
}
