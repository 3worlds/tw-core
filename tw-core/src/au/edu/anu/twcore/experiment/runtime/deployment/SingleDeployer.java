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
import fr.cnrs.iees.rvgrid.rendezvous.RVMessage;
import fr.cnrs.iees.omhtk.utils.Logging;

import java.util.logging.Logger;
import static au.edu.anu.twcore.ecosystem.runtime.simulator.SimulatorEvents.*;

/**
 * A simple deployer running a single simulator in a single thread (copied from
 * Shayne' Simulator)
 *
 * @author Jacques Gignoux - 30 août 2019
 *
 */
@Deprecated
public class SingleDeployer extends Deployable {
	private static final Logger log = Logging.getLogger(SingleDeployer.class);
	private Simulator sim = null;
	private SimulatorThread runnable;
	public SingleDeployer() {
		super();
	}

	@Override
	public void attachSimulator(Simulator sim) {
		log.info(()->"Attach");
		this.sim = sim;
		runnable = new SimulatorThread(this,sim);
		Thread runningStateThread = new Thread(runnable);
		// NB: Starts in Paused state
		runningStateThread.start();
	}

	@Override
	public void detachSimulator(Simulator sim) {
		log.info(()->"Detached");
		// never used
		quitProc();
		this.sim = null;
	}

	@Override
	public void runProc() {
		log.info(()->"runProc()");
		runnable.resume();
	}

	@Override
	public void waitProc() {
		log.info(()->"waitProc()");
		sim.preProcess();
	}
	
	@Override
	public void resetProc() {
		log.info(()->"resetProc()");
		sim.postProcess();
	}

	@Override
	public void stepProc() {
		log.info(()->"stepProc()");

		runnable.resume();
		runnable.pause();
	}

	@Override
	public void finishProc() {
		log.info(()->"finishedProc() -no longer required??");
	}

	@Override
	public void pauseProc() {
		log.info(()->"pauseProc()");
		runnable.pause();
	}

	@Override
	public void quitProc() {
		log.info(()->"quitProc()");
		runnable.stop();
	}


	@Override
	public void ended(Simulator sim) {
		log.info(()->"ended");
		RVMessage message = new RVMessage(finalise.event().getMessageType(), null, this, this);
		callRendezvous(message);	
	}

}
