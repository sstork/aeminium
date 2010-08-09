package aeminium.runtime.tools.benchmark.forkjoin.fibonacci;

import java.util.EnumSet;

import aeminium.runtime.ResultBody;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Configuration;
import aeminium.runtime.implementations.Factory;
import aeminium.runtime.implementations.Flags;
import aeminium.runtime.tools.benchmark.Reporter;

public class AeminiumFibonacciBenchmark extends FibonacciBenchmark {

	public static class FibBody implements ResultBody {
		private final Runtime rt;
		private final int n;
		private FibBody b1;
		private FibBody b2;
		public volatile int value = 0;
		
		FibBody(int n, Runtime rt) {
			this.n = n;
			this.rt = rt;
		}
		
		@Override
		public final void completed() {
			if ( b1 != null && b2 != null ) {
				value = b1.value + b2.value;
			} else {
				value = 1;
			}
			b1 = null;
			b2 = null;
		}
		
		@Override
		public final void execute(Task current) {
			if ( n <= THRESHOLD  ) {
				Fibonacci.fibOf(n);
			} else {
				b1 = new FibBody(n-1, rt);
				Task t1 = rt.createNonBlockingTask(b1, Runtime.NO_HINTS);
				rt.schedule(t1, current, Runtime.NO_DEPS);

				b2 = new FibBody(n-2, rt);
				Task t2 = rt.createNonBlockingTask(b2, Runtime.NO_HINTS);
				rt.schedule(t2, current, Runtime.NO_DEPS);
			} 
		}
	}
	
	protected long runTest(Runtime rt, int n) {
		long start = System.nanoTime();
		rt.init();
		
		Task t1 = rt.createNonBlockingTask(new FibBody(n, rt),
				                           Runtime.NO_HINTS);
		rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
		
		rt.shutdown();

		long end = System.nanoTime();
		return (end - start);
	}

	@Override
	public void run(String version, EnumSet<Flags> flags, Reporter reporter) {
		Runtime rt = Factory.getRuntime(version, flags);
		long cold = runTest(rt, MAX_CALC);
		long warm = runTest(rt, MAX_CALC);
		reporter.reportLn(String.format(RESULT_FORMAT, Configuration.getProcessorCount(), cold, warm));
	}

}
