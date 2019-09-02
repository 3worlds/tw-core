package au.edu.anu.twcore.ecosystem.runtime.stop;

import java.util.List;

import au.edu.anu.twcore.ecosystem.runtime.StoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.simulator.Simulator;

/**
 * 
 * @author Jacques Gignoux - 9 août 2019
 *
 */
public abstract class MultipleStoppingCondition extends AbstractStoppingCondition {

	protected StoppingCondition[] conditions = null;

	public MultipleStoppingCondition(List<StoppingCondition> conds) {
		super();
		conditions = new StoppingCondition[conds.size()];
		int i=0;
		for (StoppingCondition sc:conds) {
			conditions[i] = sc;
			i++;
		}
	}
	
	// recursion to attach the simulator to all component conditions
	@Override
	public void attachSimulator(Simulator sim) {
		for (StoppingCondition stop:conditions)
			stop.attachSimulator(sim);
		super.attachSimulator(sim);
	}


}
