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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
 */
public class ParallelDeployer extends Deployer {
	final ThreadPoolExecutor executor;
	final List<Future<SimulatorThread>> futures;
	final Map<Simulator, SimulatorThread> simMap;

	// what is the clean way to call the runnable
	public ParallelDeployer() {
		executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		futures = new LinkedList<>();
		simMap = new ConcurrentHashMap<>();
	}

	@Override
	public void attachSimulator(Simulator sim) {
		// Maybe we need a CallBack r
		SimulatorThread runnable = new SimulatorThread(this);
		simMap.put(sim, runnable);
		futures.add((Future<SimulatorThread>) executor.submit(runnable));
	}

	@Override
	public void detachSimulator(Simulator sim) {
		// TODO Auto-generated method stub

	}

	@Override
	public void runProc() {
		// This looks very bad. Works but not for long i presume
		for (Map.Entry<Simulator, SimulatorThread> entry : simMap.entrySet()) {
			entry.getValue().resume();
		}
	}

	@Override
	public void waitProc() {
		for (Map.Entry<Simulator, SimulatorThread> entry : simMap.entrySet()) {
			entry.getKey().preProcess();
		}

	}

	@Override
	public void stepProc() {
		for (Map.Entry<Simulator, SimulatorThread> entry : simMap.entrySet()) {
			synchronized (this) {
				SimulatorThread t = entry.getValue();
				t.resume();
				t.pause();
			}
		}

	}

	@Override
	public void finishProc() {
		for (Map.Entry<Simulator, SimulatorThread> entry : simMap.entrySet()) {
			entry.getValue().pause();
		}

	}

	@Override
	public void pauseProc() {
		for (Map.Entry<Simulator, SimulatorThread> entry : simMap.entrySet()) {
			entry.getValue().pause();
		}

	}

	@Override
	public void quitProc() {
		for (Future<SimulatorThread> f:futures) {
			f.cancel(true);
		}

	}

	@Override
	public void resetProc() {
		for (Map.Entry<Simulator, SimulatorThread> entry : simMap.entrySet()) {
			entry.getKey().postProcess();
		}

	}

	@Override
	public void stepSimulators() {
//		What can you do here?? This can't work - we have to wait for all to finish then send the msg
		for (Map.Entry<Simulator, SimulatorThread> entry : simMap.entrySet()) {

			Simulator sim = entry.getKey();
			if (sim.stop()) {
				// this sends a message to itself to switch to the finished state
				RVMessage message = new RVMessage(finalise.event().getMessageType(), null, this, this);
				callRendezvous(message);
			}
			if (!sim.isFinished()) {
				sim.step();
				if (sim.stop()) {
					// this sends a message to itself to switch to the finished state
					RVMessage message = new RVMessage(finalise.event().getMessageType(), null, this, this);
					callRendezvous(message);

				}
			}

		}

	}

}
