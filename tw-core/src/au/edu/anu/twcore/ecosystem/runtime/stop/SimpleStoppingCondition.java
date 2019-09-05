package au.edu.anu.twcore.ecosystem.runtime.stop;

import au.edu.anu.twcore.ecosystem.runtime.StoppingCondition;

/**
 * Old stuff refurbished in 2017.
 * A stopping condition based on time - will stop when a maximum value is reached
 * The max may be "infinite" (max long)
 * @author gignoux - 7 mars 2017
 *
 */
public class SimpleStoppingCondition extends AbstractStoppingCondition {
	
	private long endTime = Long.MAX_VALUE;
	
	public static StoppingCondition defaultStoppingCondition() {
		return new SimpleStoppingCondition();
	}
	
	/** Constructor for the default stopping conditions, ie no stopping before Long.MaxVALUE */
	private SimpleStoppingCondition() {
		super();
	}
	
	public SimpleStoppingCondition(long endTime) {
		super();
		this.endTime = endTime;
	}
	
	@Override
	public boolean stop() {
		if (simulator().currentTime() > endTime)
			return true;
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(time > ")
			.append(endTime)
			.append(')');
		return sb.toString();
	}
	
}
