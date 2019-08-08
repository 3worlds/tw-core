package au.edu.anu.twcore.ecosystem.runtime.stop;

import au.edu.anu.twcore.ecosystem.dynamics.Simulator;
import au.edu.anu.twcore.ecosystem.runtime.StoppingCondition;

/**
 * Old stuff - refactored by:
 * @author gignoux - 7 mars 2017
 *
 */

public abstract class AbstractStoppingCondition implements StoppingCondition {

	protected Simulator sim = null;
	
	public AbstractStoppingCondition(Simulator sim) {
		super();
		this.sim = sim;
	}

}
