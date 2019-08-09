package au.edu.anu.twcore.ecosystem.runtime.stop;

import org.apache.commons.math3.util.Precision;

import au.edu.anu.twcore.ecosystem.dynamics.Simulator;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;

/**
 * A Stopping condition that stops when a given property equals a particular value
 * @author gignoux - 7 mars 2017
 *
 */
public class ValueStoppingCondition extends PropertyStoppingCondition {

	/** relative tolerance to compare floating-point numbers */
	private static final double RELATIVE_EPSILON = 1E-20;
	private Double stopCriterion = 0.0;	
		
	public ValueStoppingCondition(Simulator sim, 
			String stopVariable, 
			ReadOnlyPropertyList system,
			double stopCrit) {
		super(sim,stopVariable,system);
		stopCriterion = stopCrit;
	}
	
	@Override
	public boolean stop() {
		if (Precision.equalsWithRelativeTolerance(getVariable(), stopCriterion, RELATIVE_EPSILON))
			return true;
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('(')
			.append(pname)
			.append(" = ")
			.append(stopCriterion)
			.append(" ± ")
			.append(RELATIVE_EPSILON)
			.append(')');
		return sb.toString();
	}

}
