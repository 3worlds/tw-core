package au.edu.anu.twcore.ecosystem.runtime;

/**
 * The runtime counterpart of TimeModel 
 * 
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public interface Timer {

	/**
	 * The next time step according to this time model.
	 * 
	 * @param time
	 *            the current time
	 * @return the next time step This has to be overriden in descendant time models
	 */
	public abstract long dt(long time);

	/**
	 * Advances time for this time model, ie replaces lastTime with newTime in
	 * RegularTS and pops event from queue in irregularTS (Ian)
	 * 
	 * @param newTime
	 */
	public abstract void advanceTime(long newTime);

}
