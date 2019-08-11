package au.edu.anu.twcore.ecosystem.runtime.stop;

import java.util.List;

import au.edu.anu.twcore.ecosystem.dynamics.Simulator;
import au.edu.anu.twcore.ecosystem.runtime.StoppingCondition;

/**
 * 
 * @author Jacques Gignoux - 9 août 2019
 *
 */
public abstract class MultipleStoppingCondition extends AbstractStoppingCondition {

	protected StoppingCondition[] conditions = null;

	public MultipleStoppingCondition(Simulator sim, List<StoppingCondition> conds) {
		super(sim);
		conditions = new StoppingCondition[conds.size()];
		int i=0;
		for (StoppingCondition sc:conds) {
			conditions[i] = sc;
			i++;
		}
	}

}
