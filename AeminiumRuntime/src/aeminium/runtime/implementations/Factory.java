package aeminium.runtime.implementations;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import aeminium.runtime.Runtime;
import aeminium.runtime.RuntimeError;
import aeminium.runtime.datagroup.DataGroupFactory;
import aeminium.runtime.datagroup.fifo.FifoDataGroup;
import aeminium.runtime.graph.implicit.ImplicitGraph;
import aeminium.runtime.implementations.generic.GenericRuntime;
import aeminium.runtime.prioritizer.lowestlevelfirst.LowestLevelFirstPrioritizer;
import aeminium.runtime.scheduler.forkjoin.ForkJoinScheduler;
import aeminium.runtime.scheduler.linear.LinearScheduler;
import aeminium.runtime.scheduler.singlethreadpool.fixed.SingleFixedThreadPoolScheduler;
import aeminium.runtime.scheduler.workstealing.blocking.BlockingWorkStealingScheduler;
import aeminium.runtime.scheduler.workstealing.polling.PollingWorkStealingScheduler;
import aeminium.runtime.task.RuntimeTask;
import aeminium.runtime.task.TaskFactory;
import aeminium.runtime.task.implicit.ImplicitTask;

public class Factory {
	
	public abstract class RuntimeConfiguration<T extends RuntimeTask> {
		protected final String name;
		protected final String description;
		protected Runtime runtime;
		
		public RuntimeConfiguration(String name, String description) {
			this.name=name;
			this.description = description;
		}
		
		public final Runtime getInstance() {
			if ( runtime == null ) {
				runtime = createInstance();
			}
			return runtime;
		}
		
		abstract Runtime createInstance();
		
		public final String getName() {
			return name;
		}
		
		public final String getDescription() {
			return description;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	protected static final Map<String, RuntimeConfiguration> database = new HashMap<String, RuntimeConfiguration>();
	
	static {
		/**
		 * Format for string database
		 * GraphClass.PrioritizerClass.SchedulerClass.TaskFactoryClass.DataGroupFactoryClass
		 */
		Factory f = new Factory();

		@SuppressWarnings("unchecked")
		final RuntimeConfiguration<ImplicitTask> ImplicitGraph_None_LinearScheduler_ImplicitTask_FifoDataGroup = f.new RuntimeConfiguration<ImplicitTask>("ImplicitGraph.None.LinearScheduler.ImplicitTask.FifoDataGroup", "ImplicitGraph.None.LinearScheduler.ImplicitTask.FifoDataGroup") {
			@Override
			public final AbstractRuntime createInstance() {
				LinearScheduler<ImplicitTask> scheduler = new LinearScheduler<ImplicitTask>();
				ImplicitGraph<ImplicitTask> graph = new ImplicitGraph<ImplicitTask>(scheduler);
				DataGroupFactory<ImplicitTask> dgFactory = FifoDataGroup.createFactory(scheduler);
				TaskFactory<ImplicitTask> taskFactory = ImplicitTask.createFactory();
				return new GenericRuntime<ImplicitTask>(scheduler, 
														scheduler, 
													    graph,
													    dgFactory,
													    taskFactory);
			}
		};
		database.put(ImplicitGraph_None_LinearScheduler_ImplicitTask_FifoDataGroup.getName(), ImplicitGraph_None_LinearScheduler_ImplicitTask_FifoDataGroup);

		
		@SuppressWarnings("unchecked")
		final RuntimeConfiguration<ImplicitTask> ImplicitGraph_None_SingleFixedThreadPoolScheduler_ImplicitTask_FifoDataGroup = f.new RuntimeConfiguration<ImplicitTask>("ImplicitGraph.None.SingleFixedThreadPoolScheduler.ImplicitTask.FifoDataGroup", "ImplicitGraph.None.SingleFixedThreadPoolScheduler.ImplicitTask.FifoDataGroup") {
			@Override
			public final AbstractRuntime createInstance() {
				SingleFixedThreadPoolScheduler<ImplicitTask> scheduler = new SingleFixedThreadPoolScheduler<ImplicitTask>();
				ImplicitGraph<ImplicitTask> graph = new ImplicitGraph<ImplicitTask>(scheduler);
				DataGroupFactory<ImplicitTask> dgFactory = FifoDataGroup.createFactory(scheduler);
				TaskFactory<ImplicitTask> taskFactory = ImplicitTask.createFactory();
				return new GenericRuntime<ImplicitTask>(scheduler, 
														scheduler, 
													    graph,
													    dgFactory,
													    taskFactory);
			}
		};
		database.put(ImplicitGraph_None_SingleFixedThreadPoolScheduler_ImplicitTask_FifoDataGroup.getName(), ImplicitGraph_None_SingleFixedThreadPoolScheduler_ImplicitTask_FifoDataGroup);
		
		
		@SuppressWarnings("unchecked")
		final RuntimeConfiguration<ImplicitTask> ImplicitGraph_LowestLevelFirstPrioritizer_SingleFixedThreadPoolScheduler_ImplicitTask_FifoDataGroup = f.new RuntimeConfiguration<ImplicitTask>("ImplicitGraph.LowestLevelFirstPrioritizer.SingleFixedThreadPoolScheduler.ImplicitTask.FifoDataGroup", "ImplicitGraph.LowestLevelFirstPrioritizer.SingleFixedThreadPoolScheduler.ImplicitTask.FifoDataGroup") {
			@Override
			public final AbstractRuntime createInstance() {
				SingleFixedThreadPoolScheduler<ImplicitTask> scheduler = new SingleFixedThreadPoolScheduler<ImplicitTask>();
				LowestLevelFirstPrioritizer<ImplicitTask> prioritizer = new LowestLevelFirstPrioritizer<ImplicitTask>(scheduler);
				ImplicitGraph<ImplicitTask> graph = new ImplicitGraph<ImplicitTask>(prioritizer);
				DataGroupFactory<ImplicitTask> dgFactory = FifoDataGroup.createFactory(scheduler);
				TaskFactory<ImplicitTask> taskFactory = ImplicitTask.createFactory();
				return new GenericRuntime<ImplicitTask>(scheduler, 
														 prioritizer, 
													     graph,
													     dgFactory,
													     taskFactory);
			}
		};
		database.put(ImplicitGraph_LowestLevelFirstPrioritizer_SingleFixedThreadPoolScheduler_ImplicitTask_FifoDataGroup.getName(), ImplicitGraph_LowestLevelFirstPrioritizer_SingleFixedThreadPoolScheduler_ImplicitTask_FifoDataGroup);

		@SuppressWarnings("unchecked")
		final RuntimeConfiguration<ImplicitTask> ImplicitGraph_None_ForkJoinScheduler_ImplicitTask_FifoDataGroup = f.new RuntimeConfiguration<ImplicitTask>("ImplicitGraph.None.ForkJoinScheduler.ImplicitTask.FifoDataGroup", "ImplicitGraph.None.ForkJoinScheduler.ImplicitTask.FifoDataGroup") {
			@Override
			public final AbstractRuntime createInstance() {
				ForkJoinScheduler<ImplicitTask> scheduler = new ForkJoinScheduler<ImplicitTask>();
				//LowestLevelFirstPrioritizer<ImplicitForkJoinTask> prioritizer = new LowestLevelFirstPrioritizer<ImplicitTask>(scheduler, flags);
				ImplicitGraph<ImplicitTask> graph = new ImplicitGraph<ImplicitTask>(scheduler);
				DataGroupFactory<ImplicitTask> dgFactory = FifoDataGroup.createFactory(scheduler);
				TaskFactory<ImplicitTask> taskFactory = ImplicitTask.createFactory();
				return new GenericRuntime<ImplicitTask>(scheduler, 
														scheduler, 
													    graph,
													    dgFactory,
													    taskFactory);
			}
		};
		database.put(ImplicitGraph_None_ForkJoinScheduler_ImplicitTask_FifoDataGroup.getName(), ImplicitGraph_None_ForkJoinScheduler_ImplicitTask_FifoDataGroup);

		@SuppressWarnings("unchecked")
		final RuntimeConfiguration<ImplicitTask> ImplicitGraph_None_PollingWorkStealingScheduler_ImplicitTask_FifoDataGroup = f.new RuntimeConfiguration<ImplicitTask>("ImplicitGraph.None.PollingWorkStealingScheduler.ImplicitTask.FifoDataGroup", "ImplicitGraph.None.PollingWorkStealingScheduler.ImplicitTask.FifoDataGroup") {
			@Override
			public final AbstractRuntime createInstance() {
				PollingWorkStealingScheduler<ImplicitTask> scheduler = new PollingWorkStealingScheduler<ImplicitTask>();
				ImplicitGraph<ImplicitTask> graph = new ImplicitGraph<ImplicitTask>(scheduler);
				DataGroupFactory<ImplicitTask> dgFactory = FifoDataGroup.createFactory(scheduler);
				TaskFactory<ImplicitTask> taskFactory = ImplicitTask.createFactory();
				return new GenericRuntime<ImplicitTask>(scheduler, 
														scheduler, 
													    graph,
													    dgFactory,
													    taskFactory);
			}
		};
		database.put(ImplicitGraph_None_PollingWorkStealingScheduler_ImplicitTask_FifoDataGroup.getName(), ImplicitGraph_None_PollingWorkStealingScheduler_ImplicitTask_FifoDataGroup);

		@SuppressWarnings("unchecked")
		final RuntimeConfiguration<ImplicitTask> ImplicitGraph_None_BlockingWorkStealingScheduler_ImplicitTask_FifoDataGroup = f.new RuntimeConfiguration<ImplicitTask>("ImplicitGraph.None.BlockingWorkStealingScheduler.ImplicitTask.FifoDataGroup", "ImplicitGraph.None.BlockingWorkStealingScheduler.ImplicitTask.FifoDataGroup") {
			@Override
			public final AbstractRuntime createInstance() {
				BlockingWorkStealingScheduler<ImplicitTask> scheduler = new BlockingWorkStealingScheduler<ImplicitTask>();
				ImplicitGraph<ImplicitTask> graph = new ImplicitGraph<ImplicitTask>(scheduler);
				DataGroupFactory<ImplicitTask> dgFactory = FifoDataGroup.createFactory(scheduler);
				TaskFactory<ImplicitTask> taskFactory = ImplicitTask.createFactory();
				return new GenericRuntime<ImplicitTask>(scheduler, 
														scheduler, 
													    graph,
													    dgFactory,
													    taskFactory);
			}
		};
		database.put(ImplicitGraph_None_BlockingWorkStealingScheduler_ImplicitTask_FifoDataGroup.getName(), ImplicitGraph_None_BlockingWorkStealingScheduler_ImplicitTask_FifoDataGroup);
		
		// set default implementation
		//database.put("default", ImplicitGraph_None_ForkJoinScheduler_ImplicitTask_FifoDataGroup);
		//database.put("default", ImplicitGraph_LowestLevelFirstPrioritizer_SingleThreadPoolScheduler_ImplicitTask_FifoDataGroup);
		//database.put("default", ImplicitGraph_None_PollingWorkStealingScheduler_ImplicitTask_FifoDataGroup);
		database.put("default", ImplicitGraph_None_BlockingWorkStealingScheduler_ImplicitTask_FifoDataGroup);
	
		if ( !database.containsKey("default")) {
			throw new RuntimeError("Implementation database does not contain a 'default' implementation.");
		}
	}
	
	/**
	 * Prohibit Factory instantiation.
	 */
	protected Factory() {} 
	
	@SuppressWarnings("unchecked")
	protected final static Map<String, RuntimeConfiguration> getImplementations() {
		return Collections.unmodifiableMap(database);
	}
	
	/**
	 * Returns a new 'default' runtime object.
	 * @return
	 */
	public final static Runtime getRuntime() {
		return getRuntime(Configuration.getImplementation());
	}
	
	/**
	 * Loads the implementation specified by 'name'. If the name is
	 * not found in the database then it assumes the name specifies the 
	 * class to load.
	 * 
	 * @param name
	 * @return
	 */
	protected final static Runtime getRuntime(String name) {
		if ( database.containsKey(name)) {
			return database.get(name).getInstance();
		} else {
			System.out.println("WARNING: Cannot find implementation '" + name + "', falling back to 'default'.");
			return getRuntime("default");
		}
	}


}
