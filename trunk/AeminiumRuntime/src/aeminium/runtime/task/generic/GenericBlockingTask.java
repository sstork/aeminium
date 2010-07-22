package aeminium.runtime.task.generic;

import java.util.Collection;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.task.RuntimeBlockingTask;

public class GenericBlockingTask extends GenericTask implements RuntimeBlockingTask {

	public GenericBlockingTask(RuntimeGraph<GenericTask> graph, Body body,
			Collection<Hints> hints) {
		super(graph, body, hints);
	}

}