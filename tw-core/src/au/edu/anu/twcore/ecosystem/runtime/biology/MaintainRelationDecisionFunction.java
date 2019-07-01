package au.edu.anu.twcore.ecosystem.runtime.biology;

import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import fr.cnrs.iees.graph.Edge;

/**
 * @author Jacques Gignoux - 4/8/2014
 *
 * interface for user-defined ecological functions deciding to maintain an existing relation
 * with another ComplexSystem.
 * result is a decision as a boolean.
 * 
 */
public abstract class MaintainRelationDecisionFunction extends TwFunctionAdapter {

	/**
	 * @param t			current time
	 * @param dt		current time interval
	 * @param focal		system making the decision
	 * @param other		system involved in relation
	 * @param environment read-only systems to help for computations
	 * @return	true to maintain the relation, false to end it
	 */
	public abstract boolean maintainRelation(double t,	
		double dt,	
		Edge relation,
		SystemComponent start, 
		SystemComponent end);

}
