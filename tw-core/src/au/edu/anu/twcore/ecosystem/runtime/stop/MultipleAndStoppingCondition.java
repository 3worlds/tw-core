package au.edu.anu.twcore.ecosystem.runtime.stop;

import java.util.List;

import au.edu.anu.twcore.ecosystem.dynamics.Simulator;
import au.edu.anu.twcore.ecosystem.runtime.StoppingCondition;

/**
 * Combines a list of stopping conditions with the AND operator
 * @author gignoux - 7 mars 2017
 *
 */
public class MultipleAndStoppingCondition extends MultipleStoppingCondition {

	public MultipleAndStoppingCondition(Simulator sim, List<StoppingCondition> conds) {
		super(sim, conds);
	}

	@Override
	public boolean stop() {
		for (int i=0; i<conditions.length; i++)
			if (!conditions[i].stop())
				return false;
		return true;
	}

}
