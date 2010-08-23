package aeminium.runtime.examples.fjtests;

import aeminium.runtime.Body;
import aeminium.runtime.ResultBody;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;
import aeminium.runtime.implementations.Factory;
import aeminium.runtime.tools.benchmark.forkjoin.fibonacci.FibonacciConstants;

public class AeminiumFibonacci  implements FibonacciConstants{

	public static class FibBody implements ResultBody {
		private FibBody b1;
		private FibBody b2;
		public volatile int value;
		
		FibBody(int n) {
			this.value = n;
		}
		
		@Override
		public void completed() {
			if ( b1 != null && b2 != null ) {
				value = b1.value + b2.value;
				b1 = null;
				b2 = null;
			} else {
				value = 1;
			}
		}
		
		public int seqFib(int n) {
			if (n <= 2) return 1;
			else return (seqFib(n - 1) + seqFib(n - 2));
		}
		
		@Override
		public void execute(Runtime rt, Task current) {
			if ( value <= THRESHOLD  ) {
				value = seqFib(value);
			} else {
				b1 = new FibBody(value - 1);
				Task t1 = rt.createNonBlockingTask(b1, Runtime.NO_HINTS);
				rt.schedule(t1, current, Runtime.NO_DEPS);

				b2 = new FibBody(value - 2);
				Task t2 = rt.createNonBlockingTask(b2, Runtime.NO_HINTS);
				rt.schedule(t2, current, Runtime.NO_DEPS);
			} 
		}
	}

	public static Body createFibBody(final Runtime rt, final int n) {
		return new  AeminiumFibonacci.FibBody(n);
	}

	public static void main(String[] args) {
		Runtime rt = Factory.getRuntime();
		rt.init();

		Task t1 = rt.createNonBlockingTask(new  AeminiumFibonacci.FibBody(MAX_CALC), Runtime.NO_HINTS);
		rt.schedule(t1, Runtime.NO_PARENT, Runtime.NO_DEPS);
		rt.shutdown();
	}
}