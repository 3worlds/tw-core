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

import static au.edu.anu.twcore.ecosystem.runtime.simulator.SimulatorEvents.finalise;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import au.edu.anu.twcore.ecosystem.runtime.simulator.Simulator;
import au.edu.anu.twcore.experiment.runtime.Deployer;
import fr.cnrs.iees.rvgrid.rendezvous.RVMessage;

/**
 * @author Ian Davies
 *
 * @date 2 Dec. 2020
 */

/**
 * Deploy unattended simulators without user control or intervention. It uses
 * the {@linkplain UnattendedThread} to independently run any number of
 * simulations using the work-stealing pool algorithm.
 * 
 * Any experiment design should be able to use this
 */
@Deprecated // keep until sure it's not needed. The DeployerImpl does all this except for
			// the use of the "UnattendedThread".
public class UnattendedDeployer extends Deployer {
	final ThreadPoolExecutor executor;
	private int count;

	public UnattendedDeployer() {
		executor = (ThreadPoolExecutor) Executors.newWorkStealingPool();
		count = 0;
	}

	@Override
	public void attachSimulator(Simulator sim) {
		UnattendedThread runnable = new UnattendedThread(this, sim);
		count++;
		executor.submit(runnable);
	}

	@Override
	public void quitProc() {
		executor.shutdown();
	}

	@Override
	public synchronized void ended(Simulator sim) {
		count--;
		if (count <= 0) {
			RVMessage message = new RVMessage(finalise.event().getMessageType(), null, this, this);
			callRendezvous(message);
		}
	}

	@Override
	public void detachSimulator(Simulator sim) {
		// ???
	}

	@Override
	public void runProc() {
	}

	@Override
	public void waitProc() {
	}

	@Override
	public void stepProc() {
	}

	@Override
	public void pauseProc() {
	}

	@Override
	public void resetProc() {
	}

	@Override
	public void finishProc() {
	}

}
