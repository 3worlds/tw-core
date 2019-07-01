package au.edu.anu.twcore.ecosystem.runtime.biology;

import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;

/**
 * @author Jacques Gignoux - 4/8/2014
 *
 * interface for user-defined ecological functions changing the state of a ComplexSystem
 * based on the state of another one.
 * This occurs either as (A=focal, B=other)
 * system A recruiting to B, use this to carry over values from A to B
 * system A dying and transferring values to B
 * system A parent to newborn B
 * system A disturbing system B
 * in all cases A is read-only.
 */
public abstract class ChangeOtherStateFunction extends TwFunctionAdapter {

	/**
	 * @param t			current time
	 * @param dt		current time interval
	 * @param focal		system making the decision
	 * @param other		system to modify
	 * @param environment	read-only systems to help for computations
	 */
	public abstract void changeOtherState(double t,	
		double dt,	
		SystemComponent focal,
		SystemComponent other);

}
