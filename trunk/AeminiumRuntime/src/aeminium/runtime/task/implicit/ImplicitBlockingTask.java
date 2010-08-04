package aeminium.runtime.task.implicit;

import java.util.Collection;
import java.util.EnumSet;

import aeminium.runtime.Body;
import aeminium.runtime.Hints;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.task.RuntimeBlockingTask;

public class ImplicitBlockingTask<T extends ImplicitTask> extends ImplicitTask<T> implements 	RuntimeBlockingTask {

	public ImplicitBlockingTask(Body body, Collection<Hints> hints, EnumSet<Flags> flags) {
		super(body, hints, flags);
	}
}