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

	@Override
	public void run() {
		while (running) {
			synchronized (pauseLock) {
				if (!running) {
					/**
					 * may have changed while waiting to synchronize on pauseLock
					 */
					break;
				}
				if (paused) {
					try {
						synchronized (pauseLock) {
							pauseLock.wait();
							/**
							 * wait will cause this thread to block until another thread calls
							 * pauseLock.notifyAll(). Note that calling wait() will relinquish the
							 * synchronized lock that this thread holds on pauseLock so another thread can
							 * acquire the lock to call notifyAll() (link with explanation below this code)
							 */
						}
					} catch (InterruptedException ex) {
						break;
					}
					if (!running) {
						/** running might have changed since we paused */
						break;
					}
				}
			} // end of pause lock

			/** NB sim.step() is synchronized */
			dep.stepSimulators();

		}
	}

	public void stop() {
		// I don't understand this but anyway I don't think its ever called
		running = false;
		// you might also want to interrupt() the Thread that is
		// running this Runnable, too, or perhaps call:
		resume();
		// to unblock
	}

	public void pause() {
		paused = true;
	}

	public void resume() {
		synchronized (pauseLock) {
			paused = false;
			/** Unblocks thread */
			pauseLock.notifyAll(); //
		}
	}
}
