package aeminium.runtime.tools.benchmark.forkjoin.integrate;

import aeminium.runtime.tools.benchmark.Benchmark;
import aeminium.runtime.tools.benchmark.Reporter;

public abstract class IntegrateBenchmark implements Benchmark {

	protected double START = -2101.0;
	protected double END = 200.0;
	
	@Override
	public String getName() {
		return "Integrate Benchmark";
	}

	@Override
	public abstract void run(Reporter reporter);

}