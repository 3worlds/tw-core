package au.edu.anu.twcore.ecosystem.runtime.simulator;

import java.util.logging.Logger;

/**
 * The class which runs a single simulation on a single parameter set
 * 
 * @author Jacques Gignoux - 29 août 2019
 *
 */
public class Simulator {

	private static Logger log = Logger.getLogger(Simulator.class.getName());
	
	// constructors

	public Simulator() {
//		super(new Transition(waiting.state(),initialise.event()),stateList());
		super();
	}
	
	// methods
	
	// run one simulation step
	public void step() {
		
	}
	
	// resets a simulation at its initial state
	public void resetSimulation() {
//		sendSimTimeMessage(null, lastTime);
	}

	// returns true if stopping condition is met
	public boolean stop() {
		return false;
	}
	
}
