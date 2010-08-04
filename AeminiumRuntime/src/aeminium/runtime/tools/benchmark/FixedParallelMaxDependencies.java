package aeminium.runtime.tools.benchmark;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;
import aeminium.runtime.implementations.Flags;

public class FixedParallelMaxDependencies implements Benchmark {
	private static final String name = "FixedParallelMaxDependencies";
	private final int[] COUNTS = {100, 1000, 10000};
	private static final int taskCount = 16;
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void run(String version, EnumSet<Flags> flags, Reporter reporter) {
		for (int COUNT : COUNTS) {
			reporter.flush();
			runTest(version, flags, reporter, COUNT);
		}
	}

	private void runTest(String version, EnumSet<Flags> flags, Reporter reporter, int count) {
		Runtime rt = Factory.getRuntime(version, flags);
		rt.init();
		
		long start = System.nanoTime();
		List<Task> firstTasks = createTasks(rt, taskCount, "Task-0");
		List<Task> previousTasks = firstTasks;
		scheduleTasks(rt, previousTasks, Runtime.NO_DEPS);
		
		
		for(int i = 1; i < count; i++ ) {
			List<Task> nextTasks = createTasks(rt, taskCount, "Task-"+i);
			scheduleTasks(rt, nextTasks, previousTasks);
			previousTasks = nextTasks;
		}
		
		rt.shutdown();
		long end = System.nanoTime();

		String result = String.format("Run %10d tasks in %12d ns ==> %10d ns per task | %6d tasks/second.", count, (end-start), ((end-start)/count),  (1000000000/((end-start)/count)));
		reporter.reportLn(result);

	}

	private void scheduleTasks(Runtime rt, List<Task> tasks, Collection<Task> deps) {
		for ( Task t : tasks ) {
			rt.schedule(t, Runtime.NO_PARENT, deps);
		}
	}
	
	private List<Task> createTasks(Runtime rt, int count, final String name) {
		List<Task> tasks = new ArrayList<Task>(count);
		
		for (int i = 0; i < count; i++) {
			tasks.add(createTask(rt, name+"["+i+"]"));
		}
		
		return tasks;
	}
	
	private Task createTask(Runtime rt, final String name) {
		return rt.createNonBlockingTask(new Body() {
			
			@Override
			public void execute(Task parent) {
				// DO NOTHING
			}
			
			@Override
			public String toString() {
				return name;
			}

		}, Runtime.NO_HINTS);
	}
}