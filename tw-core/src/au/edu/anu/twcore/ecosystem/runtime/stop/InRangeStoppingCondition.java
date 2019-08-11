package au.edu.anu.twcore.ecosystem.runtime.stop;

import au.edu.anu.twcore.ecosystem.dynamics.Simulator;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.ens.biologie.generic.utils.Interval;

/**
 * A stopping condition that stops when a variable falls within its range
 * @author gignoux - 7 mars 2017
 *
 */
public class InRangeStoppingCondition extends RangeStoppingCondition {

	public InRangeStoppingCondition(Simulator sim, 
			String stopVariable, 
			ReadOnlyPropertyList system,
			Interval range) {
		super(sim, stopVariable, system, range);
	}

	@Override
	public boolean stop() {
		return range.contains(getVariable());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('(')
			.append(pname)
			.append(" \u2208 ")
			.append(range.toString())
			.append(')');
		return sb.toString();
	}

}
