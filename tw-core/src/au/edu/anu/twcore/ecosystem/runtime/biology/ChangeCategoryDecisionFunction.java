package au.edu.anu.twcore.ecosystem.runtime.biology;

import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Jacques Gignoux - 4/8/2014
 *
 * interface for user-defined ecological functions deciding to recruit a ComplexSystem
 * to a new set of categories.
 * result is a decision index (based on dsl, will chose which transformation to apply to focal)
 * 
 */
public abstract class ChangeCategoryDecisionFunction extends TwFunctionAdapter {

	private List<ChangeOtherStateFunction> consequences = new LinkedList<ChangeOtherStateFunction>();

	// this to be overriden by user code
	// must return a 'stage name', ie a system name
	// or null if no category change.
	public abstract String changeCategory(double t, double dt,	SystemComponent focal);

	public void addConsequence(TwFunction function) {
		consequences.add((ChangeOtherStateFunction) function);
	}
	
	public List<ChangeOtherStateFunction> getConsequences() {
		return consequences;
	}

}
