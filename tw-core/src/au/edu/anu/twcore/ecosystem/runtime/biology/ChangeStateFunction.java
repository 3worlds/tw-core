package au.edu.anu.twcore.ecosystem.runtime.biology;

import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;

/**
 * @author Jacques Gignoux - 4/8/2014
 *
 * interface for user-defined ecological functions changing the state of a ComplexSystem
 * this function is meant to read data in focal.currentState() and compute new data into focal.nextState()
 */
public abstract class ChangeStateFunction extends TwFunctionAdapter {
	
	/**
	 * @param t			current time
	 * @param dt		current time interval
	 * @param focal		system to modify
	 * @param environment	read-only systems to help for computations
	 */
	public abstract void changeState(double t,	
		double dt,	
		SystemComponent focal);

}
