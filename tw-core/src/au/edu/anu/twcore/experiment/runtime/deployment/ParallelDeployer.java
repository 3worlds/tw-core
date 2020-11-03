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

import java.util.ArrayList;
import java.util.List;

import au.edu.anu.twcore.ecosystem.runtime.simulator.Simulator;
import au.edu.anu.twcore.experiment.runtime.Deployer;

/**
 * A class to deploy a series of simulator on a threadPool
 *
 * TODO: complete implementation
 *
 * @author Jacques Gignoux - 3 sept. 2019
 *
 */
public class ParallelDeployer extends Deployer {
	// TODO Make proper use of new Java concurrency thread pool executor
//	private final List<SimulatorThread> runnables;
//	private final List<Simulator> sims;
	public ParallelDeployer() {
//		runnables = new ArrayList<>();
//		sims = new ArrayList<>();
		// use the PausableThreadPoolExecutor to run simulators
		
	}
	@Override
	public void attachSimulator(Simulator sim) {
//		sims.add(sim);
//		SimulatorThread st = new SimulatorThread(this);
//		runnables.add(st);
//		
//		Thread t = new Thread(st);
//		t.start();
		
	}

	@Override
	public void detachSimulator(Simulator sim) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void runProc() {
		// TODO Auto-generated method stub

	}

	@Override
	public void waitProc() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stepProc() {
		// TODO Auto-generated method stub

	}

	@Override
	public void finishProc() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pauseProc() {
		// TODO Auto-generated method stub

	}

	@Override
	public void quitProc() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetProc() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stepSimulators() {
		// TODO Auto-generated method stub

	}


}
