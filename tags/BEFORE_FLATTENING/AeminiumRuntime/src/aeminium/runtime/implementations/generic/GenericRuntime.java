package aeminium.runtime.implementations.generic;

import java.util.Collection;

import aeminium.runtime.AtomicTask;
import aeminium.runtime.BlockingTask;
import aeminium.runtime.Body;
import aeminium.runtime.DataGroup;
import aeminium.runtime.NonBlockingTask;
import aeminium.runtime.Task;
import aeminium.runtime.datagroup.DataGroupFactory;
import aeminium.runtime.datagroup.RuntimeDataGroup;
import aeminium.runtime.events.EventManager;
import aeminium.runtime.events.RuntimeEventManager;
import aeminium.runtime.graph.RuntimeGraph;
import aeminium.runtime.implementations.AbstractRuntime;
import aeminium.runtime.prioritizer.RuntimePrioritizer;
import aeminium.runtime.scheduler.RuntimeScheduler;
import aeminium.runtime.task.RuntimeTask;
import aeminium.runtime.task.TaskFactory;

public class GenericRuntime<T extends RuntimeTask> extends AbstractRuntime {
	protected final RuntimeScheduler<T> scheduler;
	protected final RuntimePrioritizer<T> prioritizer;
	protected final RuntimeGraph<T> graph;
	protected final DataGroupFactory<T> dataGroupFactory;
	protected final TaskFactory<T> taskFactory;
	protected RuntimeEventManager eventManager;
	private GenericRuntimeState state = GenericRuntimeState.UNINITIALIZED; 
	
	private enum GenericRuntimeState {
		UNINITIALIZED,
		INITIALIZED,
		SHUTTING_DOWN
	};
		
	public GenericRuntime(RuntimeScheduler<T> scheduler,
						  RuntimePrioritizer<T> prioritizer,
						  RuntimeGraph<T> graph,
						  DataGroupFactory<T> dataGroupFactory,
						  TaskFactory<T> taskFactory) {
		this.scheduler = scheduler;
		this.prioritizer = prioritizer;
		this.graph = graph;
		this.dataGroupFactory = dataGroupFactory;
		this.taskFactory = taskFactory;
	}
	
	public GenericRuntime(GenericRuntime<T> runtime) {
		this.scheduler        = runtime.scheduler;
		this.prioritizer      = runtime.prioritizer;
		this.graph            = runtime.graph;
		this.dataGroupFactory = runtime.dataGroupFactory;
		this.taskFactory      = runtime.taskFactory;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public final AtomicTask createAtomicTask(Body body, DataGroup group, short hints) {
		assert( state == GenericRuntimeState.INITIALIZED);
		return taskFactory.createAtomicTask(body, (RuntimeDataGroup<T>)group, hints);
	}

	@Override
	public final BlockingTask createBlockingTask(Body body, short hints) {
		assert( state == GenericRuntimeState.INITIALIZED);
		return taskFactory.createBlockingTask(body, hints);
	}

	@Override
	public final NonBlockingTask createNonBlockingTask(Body body, short hints) {
		return taskFactory.createNonBlockingTask(body, hints);
	}
	
	@Override
	public final DataGroup createDataGroup() {
		assert( state == GenericRuntimeState.INITIALIZED);
		return dataGroupFactory.createDataGroup();
	}

	@Override
	public final void init() {
		assert( state == GenericRuntimeState.UNINITIALIZED);
		AbstractRuntime.setRuntime(this);
		eventManager = new EventManager();
		eventManager.init();
		graph.init(eventManager);
		if ( prioritizer != scheduler ) {
			prioritizer.init(eventManager);
		}
		scheduler.init(eventManager);
		taskFactory.init();
		dataGroupFactory.init();
		state = GenericRuntimeState.INITIALIZED;
		assert ( state == GenericRuntimeState.INITIALIZED );
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void schedule(Task task, Task parent, Collection<Task> deps) {
		graph.addTask((T)task, parent, (Collection)deps);
	}

	@Override
	public final void shutdown() {
		assert ( state == GenericRuntimeState.INITIALIZED );
		state = GenericRuntimeState.SHUTTING_DOWN;
		
		graph.waitToEmpty();
		if ( prioritizer != scheduler ) {
			prioritizer.shutdown();
		}
		scheduler.shutdown();
		graph.shutdown();
		taskFactory.shutdown();
		dataGroupFactory.shutdown();
		eventManager.shutdown();
		
		state = GenericRuntimeState.UNINITIALIZED;
		assert ( state == GenericRuntimeState.UNINITIALIZED );
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}