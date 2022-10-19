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

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import au.edu.anu.twcore.ecosystem.runtime.simulator.Simulator;
import au.edu.anu.twcore.experiment.runtime.Deployable;
import au.edu.anu.twcore.rngFactory.RngFactory;
import fr.cnrs.iees.rvgrid.rendezvous.RVMessage;
import fr.cnrs.iees.omhtk.utils.Logging;

/**
 * @author Ian Davies - 3 Dec. 2020
  *
 * A class to deploy any number [0..inf] of simulators using a work-stealing
 * pool policy.
 * 
 * Note that sims without a stopping condition will hog the threads. This is
 * because threads are "up" during the simulation even if paused. It's not until
 * a sim is finished that it is removed from the pool allowing waiting
 * sims/threads to run.
 * 
 * This deployer does not care how many, and when sims are attached. Nor does it
 * care whether or not a GUI exists and so the concept of local/remote does not
 * arise.
 * 
 * To run on a unattended system (OpenMole), use a Headless controller (to start
 * the experiment) and headless widgets to produce data (to disk I presume).
 * 
 * At the moment, I think this deployer is all that is required to service any
 * experiment design.
 * 
 * Deployer is yet to be tested with an experimental design that uses a mix of
 * parallel and sequential sims.
 * 
 * Note that the finishProc() is unused. I've left it here in case it becomes
 * useful for submitting additional simulations that can only be attached after
 * a first set of simulations has completed.
 */

/**
 * NB: If / when we figure out how to remove exception suppression, we can just
 * use this deployer.
 * 
 * We will want to do that so we can debug cross-factorial experiments. Until
 * then, the SingleDeployer is used if the required number of simulators =1
 * 
 * cf:
 * https://stackoverflow.com/questions/2248131/handling-exceptions-from-java-executorservice-tasks
 */

public class ParallelDeployer extends Deployable {
	private final ExecutorService executor;
	/* Simply list of simulators */
	private final List<Simulator> attachedSims;
	/* Mapping simulator to running 'AttendedThread' */
	final Map<Simulator, SimulatorThread> runningSims;

	private static final Logger log = Logging.getLogger(ParallelDeployer.class);

	public ParallelDeployer() {
		/* Executor with work-stealing policy */
		// default is all available processors at runtime
		executor = Executors.newWorkStealingPool();
		runningSims = new HashMap<>();
		attachedSims = new ArrayList<>();
	}

	/**
	 * Add a simulator to the list of currently managed simulators.
	 */
	@Override
	public void attachSimulator(Simulator sim) {
		attachedSims.add(sim);
	}

	/**
	 * Remove simulator even if running. This is untested.
	 */
	@Override
	public void detachSimulator(Simulator sim) {
		synchronized (this) {
			SimulatorThread t = runningSims.get(sim);
			if (t != null) {
				t.stop();
				runningSims.remove(sim);
			}
			attachedSims.remove(sim);
		}
	}

	/**
	 * Call preProcess for all attached simulators then create and submit a thread
	 * for each to the pool executor.
	 * 
	 * This method may be called after a paused state. Therefore, any remaining
	 * threads must be stopped first so the executor will release them from its
	 * queue.
	 * 
	 * Before doing this, the relevant random number generators are reset.
	 * 
	 */
	@Override
	public void waitProc() {
		log.info(() -> "stop #" + runningSims.size() + " thread(s).");

		runningSims.forEach((s, t) -> {
			t.stop();
		});
		runningSims.clear();
		// create and (re)submit threads for all attached simulators
		log.info(() -> "preProcess and submit #" + attachedSims.size() + " simulator(s).");

		// reset all rngs that require resetting at the start of each run of a
		// simulation
		log.info(() -> "reset any 'onRunStart' rngs");
		RngFactory.resetRun();

		for (Simulator sim : attachedSims) {
			sim.preProcess();
			SimulatorThread runnable = new SimulatorThread(this, sim);
			runningSims.put(sim, runnable);
			executor.submit(runnable);
		}
	}

	/**
	 * The method is called from the simulator thread after it has reached its
	 * stopping condition (if any). Therefore it must be a thread-safe call. The
	 * reference to the thread is removed from a thread list. When this list is
	 * empty, a <em>finalise</em> event is sent to any listeners.
	 */
	@Override
	public synchronized void ended(Simulator sim) {
		// quit the thread to allow executor to dump it.
		runningSims.get(sim).stop();
		// remove from our list
		runningSims.remove(sim);
		// if last sim finished send finalise msg
		if (runningSims.isEmpty()) {
			log.info(() -> "Finialise");
			RVMessage message = new RVMessage(finalise.event().getMessageType(), null, this, this);
			callRendezvous(message);
		}
	}

	/**
	 * Resumes each simulator thread from a paused state.
	 */
	@Override
	public void runProc() {
		log.info(() -> "resume #" + runningSims.size() + " thread(s).");
		runningSims.forEach((s, t) -> {
			t.resume();
		});
	}

	/**
	 * Resumes and then immediately pauses each simulator thread.
	 */
	@Override
	public void stepProc() {
		log.info(() -> "resume/pause #" + runningSims.size() + " thread(s).");
		runningSims.forEach((s, t) -> {
//			synchronized (this) { 
			t.resume();
			t.pause();
//			}
		});
	}

	/**
	 * Pauses each simulator thread from a running state.
	 */
	@Override
	public void pauseProc() {
		log.info(() -> "pause #" + runningSims.size() + " thread(s).");
		runningSims.forEach((s, t) -> {
			t.pause();
		});
	}

	/**
	 * Shuts down the thread pool executor having first stopping any running
	 * simulations.
	 * 
	 * This method is currently only called when running unattended experiments.
	 */
	@Override
	public void quitProc() {
		log.info(() -> "stop #" + runningSims.size() + " thread(s).");
		runningSims.forEach((s, t) -> {
			t.stop();
		});

		runningSims.clear();

		log.info(() -> "shutdown the executor.");
		executor.shutdown();
	}

	/**
	 * Calls postProcess for each attached simulator.
	 */
	@Override
	public void resetProc() {
		log.info(() -> "postProcess #" + attachedSims.size() + " simulator(s).");
		attachedSims.forEach((s) -> {
			s.postProcess();
		});
	}

	/**
	 * Currently unused. May be required for experiments required a mix of
	 * parallel/sequential simulations.
	 */
	@Override
	public void finishProc() {
		log.info(() -> "-does nothing!-");
	}

}
