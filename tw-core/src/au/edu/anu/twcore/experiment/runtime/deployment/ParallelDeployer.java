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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import au.edu.anu.twcore.ecosystem.runtime.simulator.Simulator;
import au.edu.anu.twcore.experiment.runtime.Deployer;
import fr.cnrs.iees.rvgrid.rendezvous.RVMessage;

/**
 * A class to deploy a series of simulator on a threadPool
 *
 * TODO: complete implementation
 *
 * @author Jacques Gignoux - 3 sept. 2019
 *
 *         (IDD) Manages a pool of threads. Each thread has a ref to the sim
 *         which calls sim.step() at the appropriate time to do the work of
 *         simulation independently of other sims.
 * 
 *         I've possibly over-simplified this and it may be only suited to
 *         replicates. At least all options for the state machine are enabled by
 *         this deployer. There is no requirement for the sims to be the same
 *         model. I'm not sure what would happen for sims with different (or no)
 *         stopping condition but it may in fact work ok. A "Finished" message
 *         (is this really necessary?) is only sent to the controller once ALL
 *         sims are finished.
 * 
 *         We may need a deployer which is just an executor that runs any
 *         submitted sim to the end. It would have no interaction with a
 *         controller. In this case the sim must have a stopping condition.
 * 
 */
public class ParallelDeployer extends Deployer {
	final ThreadPoolExecutor executor;
	final Map<Simulator, SimulatorThread> availableSims;
	final Map<Simulator, SimulatorThread> finishedSims;

	public ParallelDeployer() {
		/**
		 * We may need a policy to pool and release threads limited by cpu resources.
		 * The executor factor has all these options
		 */
		executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		availableSims = new ConcurrentHashMap<>();
		finishedSims = new ConcurrentHashMap<>();
	}

	@Override
	public void attachSimulator(Simulator sim) {
		SimulatorThread runnable = new SimulatorThread(this, sim);
		availableSims.put(sim, runnable);
		executor.submit(runnable);
	}

	@Override
	public void detachSimulator(Simulator sim) {
		synchronized (this) {
			availableSims.get(sim).stop();
			availableSims.remove(sim);
		}
	}

	@Override
	public void runProc() {
		for (Map.Entry<Simulator, SimulatorThread> entry : availableSims.entrySet())
			entry.getValue().resume();
	}

	@Override
	public void waitProc() {
		for (Map.Entry<Simulator, SimulatorThread> entry : availableSims.entrySet())
			entry.getKey().preProcess();
	}

	@Override
	public void stepProc() {
		for (Map.Entry<Simulator, SimulatorThread> entry : availableSims.entrySet()) {
			synchronized (this) {
				entry.getValue().resume();
				entry.getValue().pause();
			}
		}
	}

	@Override
	public void pauseProc() {
		for (Map.Entry<Simulator, SimulatorThread> entry : availableSims.entrySet())
			entry.getValue().pause();
	}

	@Override
	public void quitProc() {
		for (Map.Entry<Simulator, SimulatorThread> entry : availableSims.entrySet())
			entry.getKey().stop();
		executor.shutdown();
	}

	@Override
	public void resetProc() {
		for (Map.Entry<Simulator, SimulatorThread> entry : availableSims.entrySet())
			entry.getKey().postProcess();
	}
	@Override
	public void finishProc() {
//		runnable.pause();no longer required??
	}

	@Override
	public void ended(Simulator sim) {
		finishedSims.put(sim, availableSims.get(sim));
		if (finishedSims.size() == availableSims.size()) {
			finishedSims.clear();
			RVMessage message = new RVMessage(finalise.event().getMessageType(), null, this, this);
			callRendezvous(message);
		}

	}
}
