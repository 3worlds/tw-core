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
 * @author Ian Davies
 *
 * @date 1 Dec. 2020
 */
/**
 * A sim thread could be as simple as this if we find a way to use a thread pool
 * that pauses/resumes its self. So far I have not been able to make cf work:
 * 
 * https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ThreadPoolExecutor.html
 * 
 * The current system of pausing/resuming at the thread level seems to work well
 * if used by a work-stealing service (1,000,000 sim tested)
 */
@Deprecated
public class SimulatorThread2 implements Runnable {

	private final Simulator sim;
	private final Deployable dep;

	public SimulatorThread2(Deployable dep, Simulator sim) {
		super();
		this.sim = sim;
		this.dep = dep;
	}

	@Override
	public void run() {
		while (!sim.stop())
			sim.step();
		dep.ended(sim);
	}

	public int id() {
		return sim.id();
	}

}
