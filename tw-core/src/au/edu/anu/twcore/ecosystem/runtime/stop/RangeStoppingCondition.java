package au.edu.anu.twcore.ecosystem.runtime.stop;

import au.edu.anu.twcore.ecosystem.dynamics.SimulatorNode;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.ens.biologie.generic.utils.Interval;

/**
 * 
 * @author Jacques Gignoux - 9 août 2019
 *
 */
public abstract class RangeStoppingCondition extends PropertyStoppingCondition {

	protected Interval range;
	
	public RangeStoppingCondition(SimulatorNode sim, 
			String stopVariable, 
			ReadOnlyPropertyList system,
			Interval range) {
		super(sim, stopVariable, system);
		this.range = range;
	}

}
