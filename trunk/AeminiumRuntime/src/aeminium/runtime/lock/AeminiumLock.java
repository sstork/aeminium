package aeminium.runtime.lock;

import java.util.concurrent.locks.ReentrantLock;

public class AeminiumLock extends ReentrantLock {
	private static final long serialVersionUID = -3309447131522700502L;
	private static final AeminiumLock globalLock = new AeminiumLock();
	
	public static void enterGlobalLock() {
		while ( globalLock.tryLock() == false ) {
			((aeminium.runtime.implementations.implicitworkstealing.scheduler.WorkStealingThread)Thread.currentThread()).processOtherTasks();
		}
		((aeminium.runtime.implementations.implicitworkstealing.scheduler.WorkStealingThread)Thread.currentThread()).enterAtomic();
	}

	public static void leaveGlobalLock() {
		globalLock.unlock();
		((aeminium.runtime.implementations.implicitworkstealing.scheduler.WorkStealingThread)Thread.currentThread()).leaveAtomic();
	}
}
