package au.edu.anu.twcore.experiment.runtime;

import au.edu.anu.twcore.ecosystem.runtime.simulator.Simulator;
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
			sim.resetSimulation();
	}

	@Override
	public void stepProc() {
		if (sim!=null)
			if (sim.stop()) {
				// this sends a message to itself to switch to the finished state
				RVMessage message = new RVMessage(finalise.event().getMessageType(),null,this,this);
				callRendezvous(message);
			}
			else {
				sim.step();
				if (sim.isFinished()) {
					RVMessage message = new RVMessage(finalise.event().getMessageType(),null,this,this);
					callRendezvous(message);
				}
			}
	}

	@Override
	public void finishProc() {
		if (runnable != null)
			runnable.stop();
		// send status message to listeners
		// isnt this already done by the state machine ? yes it is
	}

	@Override
	public void pauseProc() {
		if (runnable != null)
			runnable.pause();
	}

	@Override
	public void quitProc() {
		// DO NOTHING!
	}

	
}
