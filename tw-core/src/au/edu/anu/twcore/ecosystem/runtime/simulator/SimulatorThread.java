package au.edu.anu.twcore.ecosystem.runtime.simulator;

import au.edu.anu.twcore.experiment.runtime.Deployer;

/**
 * The thread in which a simulator is running
 * 
 * @author Jacques Gignoux - 30 août 2019 - copied from Shayne's Simulator$RunningStateThread
 *
 */
public class SimulatorThread implements Runnable {

	private boolean quit = false;
	private final Object lock = new Object();
	private Deployer dep = null;
	
	
	public SimulatorThread(Deployer dep) {
		super();
		this.dep = dep;
	}

	@Override
	public void run() {
		while (runContinue()) {
			dep.stepProc();
			Thread.yield();
		}
	}
	
	public void quit() {
		synchronized (lock) {
			quit = true;
		}
	}
	
	private boolean runContinue() {
		synchronized (lock) {
			return !quit;
		}
	}

}
