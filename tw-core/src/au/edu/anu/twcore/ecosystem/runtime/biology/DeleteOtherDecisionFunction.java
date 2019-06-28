package au.edu.anu.twcore.ecosystem.runtime.biology;

import java.util.LinkedList;
import java.util.List;

import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.structure.system.SystemComponent;

/**
 * @author Jacques Gignoux - 4/8/2014
 *
 * interface for user-defined ecological functions deciding to delete another ComplexSystem.
 * result is a decision as a boolean.
 * 
 */
public abstract class DeleteOtherDecisionFunction extends TwFunctionAdapter {

	private List<ChangeOtherStateFunction> consequences = 
			new LinkedList<ChangeOtherStateFunction>();
	 
	public abstract boolean delete(double t,	
		double dt,	
		SystemComponent focal, 
		SystemComponent other);
	
	public final void addConsequence(TwFunction function) {
		consequences.add((ChangeOtherStateFunction) function);
	}
	
	public List<ChangeOtherStateFunction> getConsequences() {
		return consequences;
	}

}
