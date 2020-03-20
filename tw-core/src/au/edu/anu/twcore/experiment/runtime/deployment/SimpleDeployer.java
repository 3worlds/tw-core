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
import au.edu.anu.twcore.experiment.runtime.Deployer;
import fr.cnrs.iees.rvgrid.rendezvous.RVMessage;

import static au.edu.anu.twcore.ecosystem.runtime.simulator.SimulatorEvents.*;

/**
 * A simple deployer running a single simulator in a single thread
 * (copied from Shayne' Simulator)
 *
 * @author Jacques Gignoux - 30 août 2019
 *
 */
public class SimpleDeployer extends Deployer {

	private Simulator sim = null;
	private SimulatorThread runnable = null;
	private boolean threadUp = false;

	public SimpleDeployer() {
		super();
		runnable = new SimulatorThread(this);
	}

	@Override
	public void attachSimulator(Simulator sim) {
		this.sim = sim;
	}

	@Override
	public void detachSimulator(Simulator sim) {
		this.sim = null;
	}

	@Override
	public void runProc() {
		if (!threadUp) {
			Thread runningStateThread = new Thread(runnable);
			runningStateThread.start();
			threadUp = true;
		}
		else
			runnable.resume();
	}

	@Override
	public void waitProc() {
		if (sim!=null)
			sim.preProcess();
	}

	@Override
	public void stepProc() {
		if (!threadUp) {
			Thread runningStateThread = new Thread(runnable);
			runningStateThread.start();
			threadUp = true;
			runnable.pause();
		}
		else
			if (runnable != null) {
				runnable.resume();
				runnable.pause();
			}
	}

	@Override
	public void finishProc() {
		if (runnable != null)
			runnable.pause();
	}

	@Override
	public void pauseProc() {
		if (runnable != null)
			runnable.pause();
	}

	@Override
	public void quitProc() {
		// open dialog box so that user can check everything is ok before quitting
		if (runnable != null)
			runnable.stop();
	}

	@Override
	public void resetProc() {
		if (sim!=null)
			sim.postProcess();
	}

	@Override
	public void stepSimulators() {
		// was previously in StepProc()
		if (sim!=null) {
//			if (sim.stop()) {
//				// this sends a message to itself to switch to the finished state
//				RVMessage message = new RVMessage(finalise.event().getMessageType(),null,this,this);
//				callRendezvous(message);
//			}
//			else {
//				sim.step();
//				if (sim.isFinished()) {
//					RVMessage message = new RVMessage(finalise.event().getMessageType(),null,this,this);
//					callRendezvous(message);
//				}
//			}
			if (sim.stop()) {
				// this sends a message to itself to switch to the finished state
				RVMessage message = new RVMessage(finalise.event().getMessageType(),null,this,this);
				callRendezvous(message);
			}
			if (sim.isFinished()) {
//				RVMessage message = new RVMessage(finalise.event().getMessageType(),null,this,this);
//				callRendezvous(message);
			}
			else
				sim.step();
		}
	}


}
