package aeminiumruntime.linear;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import aeminiumruntime.AtomicTask;
import aeminiumruntime.BlockingTask;
import aeminiumruntime.Body;
import aeminiumruntime.DataGroup;
import aeminiumruntime.Hint;
import aeminiumruntime.NonBlockingTask;
import aeminiumruntime.Runtime;
import aeminiumruntime.RuntimeTask;
import aeminiumruntime.Task;
import aeminiumruntime.graphs.LinearTaskGraph;
import aeminiumruntime.schedulers.LinearScheduler;


public class LinearRuntime extends Runtime {

    private LinearTaskGraph graph;
    private LinearScheduler scheduler;
    private int idCounter;
    
    @Override
    public void init() {
        graph = new LinearTaskGraph();
        scheduler = new LinearScheduler(graph);
        idCounter = 0;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public DataGroup createDataGroup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BlockingTask createBlockingTask(Body b, Collection<Hint> hints) {
        try {
            return new LinearBlockingTask(b, idCounter++);
        } catch (Exception ex) {
            Logger.getLogger(LinearRuntime.class.getName()).log(Level.SEVERE, "Error creating Task.", ex);
            return null;
        }
    }

    @Override
    public NonBlockingTask createNonBlockingTask(Body b, Collection<Hint> hints) {
        try {
            return new LinearNonBlockingTask(b, idCounter++);
        } catch (Exception ex) {
            Logger.getLogger(LinearRuntime.class.getName()).log(Level.SEVERE, "Error creating Task.", ex);
            return null;
        }
    }

    @Override
    public AtomicTask createAtomicTask(Body b, DataGroup g, Collection<Hint> hints) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

	@Override
	public boolean schedule(Task task, Task parent, Collection<Task> deps) {

        // TODO Stupid casting
        Collection<RuntimeTask> rdeps = new ArrayList<RuntimeTask>();
        if (deps != null) {
            for (Task t: deps) rdeps.add((RuntimeTask) t);
        }
        
        RuntimeTask rparent = null;
        if (parent instanceof RuntimeTask) {
        	rparent = (RuntimeTask) parent;
        }

        graph.add((RuntimeTask) task, rparent, rdeps);
        scheduler.scheduleAllTasks();
        
        return true;
	}

}
