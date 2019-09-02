package au.edu.anu.twcore.ecosystem.runtime.stop;

import java.util.List;

import au.edu.anu.twcore.ecosystem.runtime.StoppingCondition;

/**
 * Combines a list of stopping conditions with the OR operator
 * @author gignoux - 7 mars 2017
 *
 */
public class MultipleOrStoppingCondition extends MultipleStoppingCondition {

	public MultipleOrStoppingCondition(List<StoppingCondition> conds) {
		super(conds);
	}

	@Override
	public boolean stop() {
		for (int i=0; i<conditions.length; i++)
			if (conditions[i].stop())
				return true;
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(conditions[0].toString());
		for (int i=1; i<conditions.length; i++)
			sb.append(" | ").append(conditions[i].toString());
		sb.append(')');
		return sb.toString();
	}

}
