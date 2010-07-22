package aeminium.runtime.task;

import java.util.Collection;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.statistics.Statistics;

public abstract class AbstractTask implements RuntimeTask {
	private Object result;
	protected final Body body;
	protected final Collection<Hints> hints;
	protected final RuntimeGraph<RuntimeTask> graph;
	protected Statistics statistics;
	protected boolean completed = false;
	protected Object data;
	
	public AbstractTask(RuntimeGraph<RuntimeTask> graph, Body body, Collection<Hints> hints) {
		this.body = body;
		this.graph = graph;
		this.hints = hints;
	}
	
	@Override
	public Object call() throws Exception {
		body.execute(this);
		graph.taskFinished(this);
		return null;		
	}
	
	public Body getBody() {
		return body;
	}
	
	public Collection<Hints> getHints() {
		return hints;
	}
	
	@Override
	public void setResult(Object result) {
		this.result = result;
	}
	
	@Override
	public Object getResult() {
		return this.result;
	}
	
	@Override
	public void setStatistics(Statistics statistics) {
		this.statistics = statistics;
	}
	
	@Override
	public Statistics getStatistics() {
		return statistics;
	}
	
	@Override
	public TaskDescription<RuntimeTask> getDescription() {
		return graph.getTaskDescription(this);
	}

	public boolean isCompleted() {
		return completed;
	}
	
	public void setCompleted() {
		completed = true;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
	
	public Object getData() {
		return data;
	}
	
	@Override
	public void taskCompleted() {		
	}
}
