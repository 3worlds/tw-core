package au.edu.anu.twcore.ecosystem.runtime.biology;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import fr.cnrs.iees.uit.space.Box;

/**
 *
 * @author J. Gignoux - 8 avr. 2020
 *
 */
public abstract class SetInitialStateFunction extends TwFunctionAdapter {

	/**
	 * sets the initial state of a newly created SystemComponent.
	 * Notice that some parameters may be null when calling the method (as denoted by 'if any').
	 *
	 * @param t	current time
	 * @param dt current time step
	 * @param limits boundary of the space set in the enclosing Process, if any
	 * @param ecosystemPar ecosystem parameters, if any
	 * @param ecosystemPop ecosystem population data
	 * @param lifeCyclePar life cycle parameters, if any
	 * @param lifeCyclePop life cycle population data, if any
	 * @param groupPar focal group parameters, if any
	 * @param groupPop focal group population data
	 *
	 * @param focalLtc focal lifetime constants at creation time, if any
	 * @param focalDrv focal driver variables at creation time, if any
	 * @param focalLoc focal location at creation time, if any
	 */
	public abstract void setInitialState(
		double t,
		double dt,
		Box limits,
		TwData ecosystemPar,
		ComponentContainer ecosystemPop,
		TwData lifeCyclePar,
		ComponentContainer lifeCyclePop,
		TwData groupPar,
		ComponentContainer groupPop,
		// read-write returned value
		TwData focalLtc,
		TwData focalDrv,
		double[] focalLoc
	);

}
