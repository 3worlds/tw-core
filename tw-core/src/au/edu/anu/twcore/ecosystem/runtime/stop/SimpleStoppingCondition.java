package au.edu.anu.twcore.ecosystem.runtime.stop;

import au.edu.anu.twcore.ecosystem.dynamics.SimulatorNode;

/**
 * Old stuff refurbished in 2017.
 * A stopping condition based on time - will stop when a maximum value is reached
 * The max may be "infinite" (max long)
 * @author gignoux - 7 mars 2017
 *
 */
public class SimpleStoppingCondition extends AbstractStoppingCondition {
	
	private long endTime = Long.MAX_VALUE;
	
	public SimpleStoppingCondition(SimulatorNode sim, long endTime) {
		super(sim);
		this.endTime = endTime;
	}
	
	@Override
	public boolean stop() {
		if (sim.currentTime() >= endTime)
			return true;
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(time ≥ ")
			.append(endTime)
			.append(')');
		return sb.toString();
	}
	
}
