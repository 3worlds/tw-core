package au.edu.anu.twcore.ecosystem.runtime.biology;

import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;

/**
 * @author Jacques Gignoux - 4/8/2014
 *
 * interface for user-defined ecological functions deciding to establish a relation
 * with another ComplexSystem.
 * result is a decision as a boolean.
 * 
 */
public abstract class RelateToDecisionFunction extends TwFunctionAdapter {

	public abstract boolean relate(double t,	
		double dt,	
		SystemComponent focal, 
		SystemComponent other);

}
