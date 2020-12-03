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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import au.edu.anu.twcore.ecosystem.runtime.simulator.Simulator;
import au.edu.anu.twcore.experiment.runtime.Deployer;
import fr.cnrs.iees.rvgrid.rendezvous.RVMessage;

/**
 *
 *         A class to deploy any number [0..inf] of simulators using a
 *         work-stealing pool policy.
 * 
 *         Note that sims without a stopping condition will hog the threads.
 * 
 *         Also not that the thread is "up" during the simulation even if
 *         paused. It's not until a sim is finished that it is removed from the
 *         pool allowing waiting sims to run.
 * 
 *         This deployer does not care how many, and when sims are attached. It
 *         does not care whether or not the exists a ui.
 * 
 *         To run on a unattended system (OpenMole) use a Headless controller
 *         (to start the experiment) and headless widgets to produce data (to
 *         disk I presume).
 * 
 *         At the moment, I think this deployer is all that is required to
 *         service any experiment design.
 * 
 * 
 */
/**
 * @author Ian Davies
 *
 * @date 3 Dec. 2020
 */
public class DeployerImpl extends Deployer {
	final ExecutorService executor;
	/* Simply list of simulators */
	final List<Simulator> attachedSims;
	/* Mapping simulator to running 'AttendedThread' */
	final Map<Simulator, SimulatorThread> runningSims;

	public DeployerImpl() {
		// Test if its necessary to leave some processors free??
		int nProcessors = (int) Math.round(Runtime.getRuntime().availableProcessors() * 0.8);
		nProcessors = Math.max(nProcessors, 1);
		/* Executor with work-stealing policy */
		executor = Executors.newWorkStealingPool(nProcessors);
		runningSims = new ConcurrentHashMap<>();
		attachedSims = new ArrayList<>();
	}

	@Override
	public void attachSimulator(Simulator sim) {
//		System.out.println("Attach");
		attachedSims.add(sim);
	}

	/*
	 * Assumes the sim is has already been removed from runningSims. You can't
	 * detach a running simulator
	 */
	@Override
	public void detachSimulator(Simulator sim) {
//		System.out.println("Detached");
		attachedSims.remove(sim);
	}

	/* Create thread for all currently attached simulators. */
	@Override
	public void waitProc() {
//		System.out.println("waitProc()");
		// create and (re)sumbit threads for all attached simulators
		runningSims.clear();
		for (Simulator sim : attachedSims) {
			sim.preProcess();
			SimulatorThread runnable = new SimulatorThread(this, sim);
			runningSims.put(sim, runnable);
			executor.submit(runnable);
		}
	}

	/* If simulator has reached stopping condition it's thread is shut down */
	@Override
	public synchronized void ended(Simulator sim) {
		// quit the thread to allow executor to dump it.
		runningSims.get(sim).stop();
		// remove from our list
		runningSims.remove(sim);
		// if last sim finished send finalise msg
		if (runningSims.isEmpty()) {
			// something in the exp design must catch this to know a suite of parallel sims
			// has finished - not sure yet
			RVMessage message = new RVMessage(finalise.event().getMessageType(), null, this, this);
			callRendezvous(message);
		}

	}

	@Override
	public void runProc() {
//		System.out.println("runProc()");
		for (Map.Entry<Simulator, SimulatorThread> entry : runningSims.entrySet())
			entry.getValue().resume();
	}

	@Override
	public void stepProc() {
//		System.out.println("stepProc()");
		for (Map.Entry<Simulator, SimulatorThread> entry : runningSims.entrySet()) {
			synchronized (this) {
				entry.getValue().resume();
				entry.getValue().pause();
			}
		}
	}

	@Override
	public void pauseProc() {
//		System.out.println("pauseProc()");
		for (Map.Entry<Simulator, SimulatorThread> entry : runningSims.entrySet())
			entry.getValue().pause();
	}

	@Override
	public void quitProc() {
//		System.out.println("quitProc()");
		for (Map.Entry<Simulator, SimulatorThread> entry : runningSims.entrySet())
			entry.getKey().stop();
		executor.shutdown();
	}

	@Override
	public void resetProc() {
//		System.out.println("resetProc()");
		for (Map.Entry<Simulator, SimulatorThread> entry : runningSims.entrySet())
			entry.getKey().postProcess();
	}

	@Override
	public void finishProc() {
//		System.out.println("finishedProc()");
//		runnable.pause();no longer required??
	}

}
