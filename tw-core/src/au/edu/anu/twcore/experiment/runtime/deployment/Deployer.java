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
import java.util.logging.Logger;

import au.edu.anu.twcore.ecosystem.runtime.simulator.Simulator;
import au.edu.anu.twcore.experiment.runtime.Deployable;
import au.edu.anu.twcore.rngFactory.RngFactory;
import fr.cnrs.iees.rvgrid.rendezvous.RVMessage;
import fr.ens.biologie.generic.utils.Logging;

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
public class Deployer extends Deployable {
	private final ExecutorService executor;
	/* Simply list of simulators */
	private final List<Simulator> attachedSims;
	/* Mapping simulator to running 'AttendedThread' */
	final Map<Simulator, SimulatorThread> runningSims;
	
	private static final Logger log = Logging.getLogger(Deployer.class);

	public Deployer() {
		/* Executor with work-stealing policy */
		// default is all available processors at runtime
		executor = Executors.newWorkStealingPool();

//		we could limit this if necessary.
//		executor = Executors.newWorkStealingPool(nProcessors);

		runningSims = new ConcurrentHashMap<>();
		attachedSims = new ArrayList<>();
	}

	@Override
	public void attachSimulator(Simulator sim) {
		attachedSims.add(sim);
	}

	/* Remove sim even if running. */
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

	/* Create thread for all currently attached simulators. */
	@Override
	public void waitProc() {
		log.info(()->"stop #" + runningSims.size()+" thread(s).");
		/*
		 * This proc may be called after a paused state. Therefore, any remaining
		 * threads must be stopped so the executor will release them from its queue.
		 */

		runningSims.forEach((s, t) -> {
			t.stop();
		});
		runningSims.clear();
		// create and (re)submit threads for all attached simulators
		log.info(()->"preProcess and submit #" + attachedSims.size()+" simulator(s).");
		// reset all relevant seeds
		RngFactory.resetRun();

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
			log.info(()->"Finialise");
			RVMessage message = new RVMessage(finalise.event().getMessageType(), null, this, this);
			callRendezvous(message);
		}
	}

	@Override
	public void runProc() {
		log.info(()->"resume #" + runningSims.size()+" thread(s).");
		runningSims.forEach((s, t) -> {
			t.resume();
		});
	}

	@Override
	public void stepProc() {
		log.info(()->"resume/pause #" + runningSims.size()+" thread(s).");
		runningSims.forEach((s, t) -> {
			synchronized (this) {
				t.resume();
				t.pause();
			}
		});
	}

	@Override
	public void pauseProc() {
		log.info(()->"pause #" + runningSims.size()+" thread(s).");
		runningSims.forEach((s, t) -> {
			t.pause();
		});
	}

	@Override
	public void quitProc() {
		log.info(()->"stop and shutdown #" + runningSims.size()+" thread(s).");
		runningSims.forEach((s, t) -> {
			t.stop();
		});
		executor.shutdown();
	}

	@Override
	public void resetProc() {
		log.info(()->"postProcess #" + attachedSims.size()+" simulator(s).");
		attachedSims.forEach((s) -> {
			s.postProcess();
		});
	}

	@Override
	public void finishProc() {
		log.info(()->"-does nothing!-");
	}

}
