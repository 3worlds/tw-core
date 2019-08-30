package au.edu.anu.twcore.experiment.runtime;

import au.edu.anu.twcore.ecosystem.runtime.simulator.Simulator;
import au.edu.anu.twcore.ecosystem.runtime.simulator.SimulatorThread;
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
		Thread runningStateThread = new Thread(runnable);
		runningStateThread.start();
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
				// switch to finished state ? how ?
				sendMessage(finalise.event().getMessageType(), null);
			}
			else
				sim.step();
	}

	@Override
	public void finishProc() {
		if (runnable != null)
			runnable.quit();
		// send status message to listeners
		// isnt this already done by the state machine ?
	}

	@Override
	public void pauseProc() {
		if (runnable != null)
			runnable.quit();
	}

	@Override
	public void quitProc() {
		// DO NOTHING!
	}

	
}
