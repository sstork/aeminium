package aeminium.runtime.implementations;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import aeminium.runtime.Runtime;
import aeminium.runtime.datagroup.DataGroupFactory;
import aeminium.runtime.datagroup.fifo.FifoDataGroup;
import aeminium.runtime.graph.implicit2.ImplicitGraph2;
import aeminium.runtime.implementations.generic.GenericRuntime;
import aeminium.runtime.prioritizer.lowestlevelfirst.LowestLevelFirstPrioritizer;
import aeminium.runtime.scheduler.hybridthreadpools.HybridThreadPoolsScheduler;
import aeminium.runtime.scheduler.singlethreadpool.SingleThreadPoolScheduler;
import aeminium.runtime.task.RuntimeTask;
import aeminium.runtime.task.TaskFactory;
import aeminium.runtime.task.implicit2.ImplicitTask2;

public class Factory {
	protected static final String RT_PREFIX = "AEMINIUM_RT_";
	
	public abstract class RuntimeConfiguration<T extends RuntimeTask> {
		protected final String name;
		protected final String description;
		
		public RuntimeConfiguration(String name, String description) {
			this.name=name;
			this.description = description;
		}
		
		public String getName() {
			return name;
		}
		
		public String getDescription() {
			return description;
		}
		
		public abstract AbstractRuntime instanciate(EnumSet<Flags> flags);
	}
	
	@SuppressWarnings("unchecked")
	protected static final Map<String, RuntimeConfiguration> database = new HashMap<String, RuntimeConfiguration>();
	static {
		/**
		 * Format for string database
		 * GraphClass.SchedulerClass.PrioritizerClass.TaskFactoryClass.DataGroupFactoryClass
		 */
		Factory f = new Factory();
		
	
		final RuntimeConfiguration<ImplicitTask2> ImplicitGraph2_HybridThreadPoolsScheduler_None_ImplicitTask2_FifoDataGroup = f.new RuntimeConfiguration<ImplicitTask2>("ImplicitGraph2.HybridThreadPoolsScheduler.None.ImplicitTask2.FifoDataGroup", "ImplicitGraph2.HybridThreadPoolsScheduler.None.ImplicitTask2.FifoDataGroup") {
			@Override
			public AbstractRuntime instanciate(EnumSet<Flags> flags) {
				HybridThreadPoolsScheduler<ImplicitTask2> scheduler = new HybridThreadPoolsScheduler<ImplicitTask2>(flags);
				ImplicitGraph2<ImplicitTask2> graph = new ImplicitGraph2<ImplicitTask2>(scheduler, flags);
				DataGroupFactory<ImplicitTask2> dgFactory = FifoDataGroup.createFactory(scheduler, flags);
				TaskFactory<ImplicitTask2> taskFactory = ImplicitTask2.createFactory(flags);
				return new GenericRuntime<ImplicitTask2>(scheduler, 
														 scheduler, 
														 graph,
														 dgFactory,
														 taskFactory);
			}
		};
		database.put(ImplicitGraph2_HybridThreadPoolsScheduler_None_ImplicitTask2_FifoDataGroup.getName(), ImplicitGraph2_HybridThreadPoolsScheduler_None_ImplicitTask2_FifoDataGroup);

	
		final RuntimeConfiguration<ImplicitTask2> ImplicitGraph2_SingleThreadPoolScheduler_LowestLevelFirstPrioritizer_FifoDataGroup = f.new RuntimeConfiguration<ImplicitTask2>("ImplicitGraph2.SingleThreadPoolScheduler.LowestLevelFirstPrioritizer.ImplicitTask2.FifoDataGroup", "ImplicitGraph2.SingleThreadPoolScheduler.LowestLevelFirstPrioritizer.ImplicitTask2.FifoDataGroup") {
			@Override
			public AbstractRuntime instanciate(EnumSet<Flags> flags) {
				SingleThreadPoolScheduler<ImplicitTask2> scheduler = new SingleThreadPoolScheduler<ImplicitTask2>(flags);
				LowestLevelFirstPrioritizer<ImplicitTask2> prioritizer = new LowestLevelFirstPrioritizer<ImplicitTask2>(scheduler, flags);
				ImplicitGraph2<ImplicitTask2> graph = new ImplicitGraph2<ImplicitTask2>(prioritizer, flags);
				DataGroupFactory<ImplicitTask2> dgFactory = FifoDataGroup.createFactory(scheduler, flags);
				TaskFactory<ImplicitTask2> taskFactory = ImplicitTask2.createFactory(flags);
				return new GenericRuntime<ImplicitTask2>(scheduler, 
														 prioritizer, 
													     graph,
													     dgFactory,
													     taskFactory);
			}
		};
		database.put(ImplicitGraph2_SingleThreadPoolScheduler_LowestLevelFirstPrioritizer_FifoDataGroup.getName(), ImplicitGraph2_SingleThreadPoolScheduler_LowestLevelFirstPrioritizer_FifoDataGroup);

		// set default implementation
		database.put("default", ImplicitGraph2_SingleThreadPoolScheduler_LowestLevelFirstPrioritizer_FifoDataGroup);
	}
	
	/**
	 * Prohibit Factory instantiation.
	 */
	protected Factory() {} 
	
	@SuppressWarnings("unchecked")
	public static Map<String, RuntimeConfiguration> getImplementations() {
		return Collections.unmodifiableMap(database);
	}
	
	/**
	 * Returns a new 'default' runtime object.
	 * @return
	 */
	public static Runtime getRuntime() {
		return getRuntime(System.getenv(RT_PREFIX+"IMPLEMENTATION"), getFlagsFromEnvironment());
	}
	
	/**
	 * Loads the implementation specified by 'name'. If the name is
	 * not found in the database then it assumes the name specifies the 
	 * class to load.
	 * 
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Runtime getRuntime(String name, EnumSet<Flags> flags) {
		if ( database.containsKey(name)) {
			return database.get(name).instanciate(flags);
		} else {
			return getRuntime("default", flags);
		}
	}

	/**
	 * Computes flag set based on set environment variables.
	 * 
	 * @return
	 */
	public static EnumSet<Flags> getFlagsFromEnvironment() {
		EnumSet<Flags> flags = EnumSet.of(Flags.DEBUG);
		flags.clear();
		
		for ( Flags f : Flags.values() ) {
			Map<String, String> env = System.getenv();
			if ( env.containsKey(RT_PREFIX + f.name())) {
				flags.add(f);
			}
		}
				
		return flags;
	}

}