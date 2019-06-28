package au.edu.anu.twcore.ecosystem.runtime.biology;

import java.util.LinkedList;
import java.util.List;

import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.structure.system.SystemComponent;

/**
 * @author Jacques Gignoux - 4/8/2014
 *
 * interface for user-defined ecological functions deciding to delete a system.
 * result is a decision as a boolean.
 * 
 */
public abstract class DeleteDecisionFunction extends TwFunctionAdapter {
	
	private List<ChangeOtherStateFunction> consequences = new LinkedList<ChangeOtherStateFunction>();
	
	/**
	 * @param t			current time
	 * @param dt		current time interval
	 * @param focal		system to delete
	 * @param environment read-only systems to help for computations
	 * @return	true to delete focal, false to keep it
	 */
	public abstract boolean delete(double t,	
		double dt,	
		SystemComponent focal);

	public void addConsequence(TwFunction function) {
		consequences.add((ChangeOtherStateFunction) function);
	}
	
	public List<ChangeOtherStateFunction> getConsequences() {
		return consequences;
	}


}
