package au.edu.anu.twcore.ecosystem.runtime.biology;

import java.util.LinkedList;
import java.util.List;

import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;

/**
 * @author Jacques Gignoux - 4/8/2014
 *
 * interface for user-defined ecological functions deciding to recruit a ComplexSystem
 * to a new set of categories.
 * result is a decision index (based on dsl, will chose which transformation to apply to focal)
 * 
 */
public abstract class ChangeOtherCategoryDecisionFunction extends TwFunctionAdapter {


	private List<ChangeOtherStateFunction> consequences = new LinkedList<ChangeOtherStateFunction>();

	public abstract String changeCategory(double t,	
		double dt,	
		SystemComponent focal, 
		SystemComponent other);
	
	public void addConsequence(TwFunction function) {
		consequences.add((ChangeOtherStateFunction) function);
	}
	
	public List<ChangeOtherStateFunction> getConsequences() {
		return consequences;
	}

}
