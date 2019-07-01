package au.edu.anu.twcore.ecosystem.runtime.biology;

import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import fr.cnrs.iees.graph.Edge;

/**
 * @author Jacques Gignoux - 10/3/2017
 *
 * interface for user-defined ecological functions changing the state of a relation between
 * two SystemComponents
 */
public abstract class ChangeRelationStateFunction extends TwFunctionAdapter {

	/**
	 * @param t			current time
	 * @param dt		current time interval
	 * @param focal		system making the decision
	 * @param other		system to modify
	 * @param environment	read-only systems to help for computations
	 */
	public abstract void changeRelationState(double t,	
		double dt,	
		SystemComponent focal,
		SystemComponent other,
		Edge relation);

}
