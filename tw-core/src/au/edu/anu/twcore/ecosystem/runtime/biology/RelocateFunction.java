package au.edu.anu.twcore.ecosystem.runtime.biology;

import au.edu.anu.twcore.ecosystem.runtime.space.Location;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import fr.cnrs.iees.uit.space.Box;

/**
 * Interface for user-defined ecological functions changing the location of a SystemComponent.
 * 
 * @author Jacques Gignoux - 30 janv. 2020
 *
 */
public abstract class RelocateFunction extends TwFunctionAdapter {

	/**
	 * 
	 * @param t 		current time
	 * @param dt		current time interval
	 * @param focal		system component to relocate
	 * @param ctLoc		current location of focal
	 * @return new location of focal
	 */
	public abstract double[] relocate(double t,
		double dt,
		SystemComponent focal,
		Location ctLocation,
		Box limits);
	
}
