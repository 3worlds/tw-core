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

import java.util.concurrent.Semaphore;
import au.edu.anu.twcore.experiment.runtime.Deployer;

/**
 * The thread in which a (single) simulator is running
 *
 * @author Jacques Gignoux - 30 août 2019 - copied from Shayne's
 *         Simulator$RunningStateThread
 *
 */
public class SimulatorThread implements Runnable {

	private Deployer dep = null;

	public SimulatorThread(Deployer dep) {
		super();
		this.dep = dep;
	}

	// code found there:
	// https://stackoverflow.com/questions/16758346/how-pause-and-then-resume-a-thread
	private volatile boolean running = true;
	private volatile boolean paused = false;
	private final Object pauseLock = new Object();
	// cf:
	// https://stackoverflow.com/questions/17825508/fairness-setting-in-semaphore-class
	// The fair method does not seem to solve the problem of the main loop hogging
	// the semaphore
//	private volatile Semaphore stepLock = new Semaphore(1,true);// a "fair" method 
//	private volatile Semaphore stepLock = new Semaphore(1);// a "unfair" method
//	private final Semaphore stepLock = new Semaphore(1); // makes not difference

//	private int counter = 0;
	@Override
	public void run() {
		while (running) {
			synchronized (pauseLock) {
				if (!running) { // may have changed while waiting to
					// synchronize on pauseLock
					break;
				}
				if (paused) {
					try {
						synchronized (pauseLock) {
							pauseLock.wait(); // will cause this Thread to block until
							// another thread calls pauseLock.notifyAll()
							// Note that calling wait() will
							// relinquish the synchronized lock that this
							// thread holds on pauseLock so another thread
							// can acquire the lock to call notifyAll()
							// (link with explanation below this code)
						}
					} catch (InterruptedException ex) {
						break;
					}
					if (!running) { // running might have changed since we paused
						break;
					}
				}
			} // end of pause lock

//			try {
//				try {
//					/** Make Pause wait until the step completes */
//					stepLock.acquire();
					dep.stepSimulators();
////					System.out.println("STEP: "+(++counter));
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			} finally {
//				stepLock.release();
////				System.out.println("STEP RELEASE");
//			}
//			/** Needed this to prevent hogging the semaphore. */
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	public void stop() {
		running = false;
		// you might also want to interrupt() the Thread that is
		// running this Runnable, too, or perhaps call:
		resume();
		// to unblock
	}

	public void pause() {
		// you may want to throw an IllegalStateException if !running
		// This is not the same thread as the run() loop
//		try {
//			try {
				/** Force thread to wait until the current step completes */
//				stepLock.acquire();
				paused = true;

//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		} finally {
//			stepLock.release();
//		}
	}

	public void resume() {
		synchronized (pauseLock) {
			paused = false;
			pauseLock.notifyAll(); // Unblocks thread
//			System.out.println("RESUME SYNC");
		}
	}

// Shayne's code

//	private boolean quit = false;
//	private final Object lock = new Object();
//	private Deployer dep = null;
//
//
//
//	@Override
//	public void run() {
//		while (runContinue()) {
//			dep.stepProc();
//			Thread.yield();
//		}
//	}
//
//	public void quit() {
//		synchronized (lock) {
//			quit = true;
//		}
//	}
//
//
//	private boolean runContinue() {
//		synchronized (lock) {
//			return !quit;
//		}
//	}

}
