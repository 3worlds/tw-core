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
 *
 *         May be this thread should be created and destroyed on finish or
 *         reset. meaning the executor will be empty when all threads are
 *         finished.
 * 
 *         BUT this is not a thread - it contains a pool of threads (IDD)
 * 
 *         So Simulator thread would loop while (!quit) and quit becomes true
 *         when finished or reset from paused block. !quit = running =
 *         threadAlive or whatever name Therefore the deployer must keep a list
 *         of sims for resubmitting
 * 
 *         This deployer can only run and reset
 */
public class ParallelDeployer extends Deployer {
	final ThreadPoolExecutor executor;
//	final List<Future<SimulatorThread>> futures;
	final Map<Simulator, SimulatorThread> availableSims;

	final Map<Simulator, SimulatorThread> finishedSims;

	// what is the clean way to call the runnable
	public ParallelDeployer() {
		// We need a policy to pool and release threads some how
		executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
//		futures = new LinkedList<>();
		availableSims = new ConcurrentHashMap<>();
		finishedSims = new ConcurrentHashMap<>();
	}

	@Override
	public void attachSimulator(Simulator sim) {
		// Maybe we need a CallBack r
		SimulatorThread runnable = new SimulatorThread(this, sim);
		availableSims.put(sim, runnable);
//		futures.add((Future<SimulatorThread>) executor.submit(runnable));
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
		System.out.println("runProc() called");
		for (Map.Entry<Simulator, SimulatorThread> entry : availableSims.entrySet())
			entry.getValue().resume();
	}

	@Override
	public void waitProc() {
		System.out.println("waitProc() called");
		for (Map.Entry<Simulator, SimulatorThread> entry : availableSims.entrySet())
			entry.getKey().preProcess();
	}

	@Override
	public void stepProc() {
		System.out.println("stepProc()");
		for (Map.Entry<Simulator, SimulatorThread> entry : availableSims.entrySet()) {
			synchronized (this) {
				entry.getValue().resume();
				entry.getValue().pause();
			}
		}
	}

	@Override
	public void finishProc() {
//		for (Map.Entry<Simulator, SimulatorThread> entry : availableSims.entrySet())
//			entry.getValue().pause();
	}

	@Override
	public void pauseProc() {
		System.out.println("pauseProc() ");
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
		System.out.println("resetProc()");
		if (!finishedSims.isEmpty())
			System.out.println("RESET before Finished - shouldn't happen!");
		for (Map.Entry<Simulator, SimulatorThread> entry : availableSims.entrySet())
			entry.getKey().postProcess();
	}

//	@Override
//	public void stepSimulators() {
//		// THis is complete shit. We are not multi-tasking!!!!
//		for (Map.Entry<Simulator, SimulatorThread> entry : availableSims.entrySet()) {
//			Simulator sim = entry.getKey();
//			System.out.println("Finished: " + finishedSims.size());
//			if (!finishedSims.containsKey(sim)) {
//				if (sim.stop()) {
//					System.out.println("Stopped");
//					finishedSims.put(sim, entry.getValue());
//				} else
//					sim.step();
////				if (!sim.isFinished()) {
////					sim.step();
////					if (sim.stop()) {
////						finishedSims.put(sim, entry.getValue());
////						System.out.println("Stopped 2");
////					}
////				}
//			}
//		}
//
//		if (finishedSims.size() == availableSims.size()) {
//			finishedSims.clear();
//			System.out.println("All stopped: finialise event sent");
//			RVMessage message = new RVMessage(finalise.event().getMessageType(), null, this, this);
//			callRendezvous(message);
//		}
//	}

	@Override
	public void ended(Simulator sim) {
		finishedSims.put(sim, availableSims.get(sim));
		if (finishedSims.size() == availableSims.size()) {
			finishedSims.clear();
			System.out.println("All stopped: finialise event sent");
			RVMessage message = new RVMessage(finalise.event().getMessageType(), null, this, this);
			callRendezvous(message);
		}

	}
}
