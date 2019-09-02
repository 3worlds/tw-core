package au.edu.anu.twcore.ecosystem.runtime.stop;

import java.util.List;

import au.edu.anu.twcore.ecosystem.runtime.StoppingCondition;

/**
 * Combines a list of stopping conditions with the AND operator
 * @author gignoux - 7 mars 2017
 *
 */
public class MultipleAndStoppingCondition extends MultipleStoppingCondition {

	public MultipleAndStoppingCondition(List<StoppingCondition> conds) {
		super(conds);
	}

	@Override
	public boolean stop() {
		for (int i=0; i<conditions.length; i++)
			if (!conditions[i].stop())
				return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(conditions[0].toString());
		for (int i=1; i<conditions.length; i++)
			sb.append(" & ").append(conditions[i].toString());
		sb.append(')');
		return sb.toString();
	}

}
