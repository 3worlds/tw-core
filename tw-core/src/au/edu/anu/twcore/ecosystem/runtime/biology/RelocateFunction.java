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
	 * recompute the location of a system component from its current location and, if
	 * required, of another component (eg its parent or a pedator, etc.).
	 * Attention: ctLocation, other and otherLocation can be null!
	 *
	 * @param t 		current time
	 * @param dt		current time interval
	 * @param focal		system component to relocate
	 * @param ctLocation	current location of focal
	 * @param limits	space bounding box - new location must be inside this box
	 * @return new location of focal
	 */
	public abstract double[] relocate(double t,
		double dt,
		SystemComponent focal,
		Location ctLocation,
		SystemComponent other,
		Location otherLocation,
		Box limits);

}
