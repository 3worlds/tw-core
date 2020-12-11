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

import au.edu.anu.twcore.ecosystem.runtime.simulator.Simulator;
import au.edu.anu.twcore.experiment.runtime.Deployable;

/**
 * The thread in which a (single) simulator is running
 *
 * @author Jacques Gignoux - 30 août 2019 - copied from Shayne's
 *         Simulator$RunningStateThread
 *
 */
public class SimulatorThread implements Runnable {

	private final Simulator sim; // to call step() and stop()
	private final Deployable dep;// to inform the deployer that this sim has finished
	/**
	 * cf:
	 * https://stackoverflow.com/questions/16758346/how-pause-and-then-resume-a-thread
	 */
	private volatile boolean threadUp = true;
	// Start in paused state to avoid double initial step bug (IDD)
	private volatile boolean paused = true;
	private final Object pauseLock = new Object();

	public SimulatorThread(Deployable dep, Simulator sim) {
		super();
		this.sim = sim;
		this.dep = dep;
	}

	@Override
	public void run() {
//		System.out.println("Sim thread up: " + Thread.currentThread().getId());
		while (threadUp) {
			synchronized (pauseLock) {
				if (!threadUp) {
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
					if (!threadUp) {
						/** running might have changed since we paused */
						break;
					}
				}
			} // end of pause lock

			if (sim.stop()) {
				paused = true;
				// inform deployer to send Finished msg to controller if appropriate
				dep.ended(sim);
			} else
				//do the work of this thread
				sim.step();

		}
	}

	public void stop() {
		threadUp = false;
		/**
		 * Thread may be paused so call resume() to unblock and allow it to run and
		 * exit.
		 */
		resume();
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
