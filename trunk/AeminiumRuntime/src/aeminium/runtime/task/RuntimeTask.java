package aeminium.runtime.task;

import java.util.Collection;
import java.util.concurrent.Callable;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.Task;
import aeminium.runtime.scheduler.RuntimeScheduler;

public interface RuntimeTask extends Task, Callable<Object> {
	
	public void setScheduler(RuntimeScheduler<?> scheduler);
	
	public Collection<Hints> getHints();
	
	public Body getBody();
	
	public void taskFinished();
	
	public void taskCompleted();
}
