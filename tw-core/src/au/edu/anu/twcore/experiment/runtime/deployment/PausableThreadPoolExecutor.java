/**************************************************************************
 *  TW-CORE - 3Worlds Core classes and methods                            *
 *                                                                        *
 *  Copyright 2018: Shayne Flint, Jacques Gignoux & Ian D. Davies         *
 *       shayne.flint@anu.edu.au                                          * 
 *       jacques.gignoux@upmc.fr                                          *
 *       ian.davies@anu.edu.au                                            * 
 *                                                                        *
 *  TW-CORE is a library of the principle components required by 3W       *
 *                                                                        *
 **************************************************************************                                       
 *  This file is part of TW-CORE (3Worlds Core).                          *
 *                                                                        *
 *  TW-CORE is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  TW-CORE is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *                         
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with TW-CORE.                                                   *
 *  If not, see <https://www.gnu.org/licenses/gpl.html>                   *
 *                                                                        *
 **************************************************************************/
package au.edu.anu.twcore.experiment.runtime.deployment;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A ThreadPool to run multiple simulators in one deployment.
 * 
 * https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ThreadPoolExecutor.html
 * 
 * TODO: complete implementation
 * 
 * @author Jacques Gignoux - 3 sept. 2019
 *
 */
/*
 * Try this next
 */
@Deprecated // not sure what to do with this.
public class PausableThreadPoolExecutor extends ThreadPoolExecutor {

	private boolean isPaused;
	private ReentrantLock pauseLock = new ReentrantLock();
	private Condition unpaused = pauseLock.newCondition();

	public PausableThreadPoolExecutor(BlockingQueue<Runnable> workQueue) {
		super(Runtime.getRuntime().availableProcessors(), Integer.MAX_VALUE, Long.MAX_VALUE, TimeUnit.MILLISECONDS,
				workQueue);
	}

	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		pauseLock.lock();
		try {
			while (isPaused)
				unpaused.await();
		} catch (InterruptedException ie) {
			t.interrupt();
		} finally {
			pauseLock.unlock();
		}
	}

	public void pause() {
		pauseLock.lock();
		try {
			isPaused = true;
		} finally {
			pauseLock.unlock();
		}
	}

	public void resume() {
		pauseLock.lock();
		try {
			isPaused = false;
			unpaused.signalAll();
		} finally {
			pauseLock.unlock();
		}
	}

}
//public PausableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
//BlockingQueue<Runnable> workQueue) {
//super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
//// TODO Auto-generated constructor stub
//}

//public PausableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
//BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
//super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
//// TODO Auto-generated constructor stub
//}

/*-
* Parameters:
corePoolSize the number of threads to keep in the pool, even if they are idle, unless allowCoreThreadTimeOut is set
maximumPoolSize the maximum number of threads to allow in the pool
keepAliveTime when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating.
unit the time unit for the keepAliveTime argument
workQueue the queue to use for holding tasks before they are executed. This queue will hold only the Runnable tasks submitted by the execute method.
handler the handler to use when execution is blocked because the thread bounds and queue capacities are reached
Throws:
IllegalArgumentException - if one of the following holds:
corePoolSize < 0
keepAliveTime < 0
maximumPoolSize <= 0
maximumPoolSize < corePoolSize
NullPointerException - if workQueue or handler is null*/
//public PausableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
//BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
//super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
//// TODO Auto-generated constructor stub
//}

//public PausableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
//BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
//super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
//// TODO Auto-generated constructor stub
//}
