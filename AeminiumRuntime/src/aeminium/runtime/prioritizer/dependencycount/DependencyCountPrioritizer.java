package aeminium.runtime.prioritizer.dependencycount;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;

import aeminium.runtime.implementations.Flags;
import aeminium.runtime.prioritizer.AbstractPrioritizer;
import aeminium.runtime.scheduler.RuntimeScheduler;
import aeminium.runtime.task.RuntimeTask;

public class DependencyCountPrioritizer<T extends RuntimeTask> extends AbstractPrioritizer<T> {

	public DependencyCountPrioritizer(RuntimeScheduler<T> scheduler, EnumSet<Flags> flags) {
		super(scheduler, flags);
	}

	@Override
	public void init() {
	}

	@Override
	public void scheduleTasks(T... tasks) {
		Arrays.sort(tasks, new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return (int)(o1.getDescription().getDependentTaskCount() - o2.getDescription().getDependentTaskCount());
			}
		});
		scheduler.scheduleTasks(tasks);
		
	}

	@Override
	public void shutdown() {
	}

}