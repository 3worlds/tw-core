package au.edu.anu.twcore.ecosystem.runtime;

/**
 * An interface for populations of components.
 * <p>3Worlds: component threeWorlds</p>
 * @author Jacques Gignoux - 15 oct. 2012 refactored 17 May 2017
 *
 */
public interface Population {
	
	/** population size */
	public int count();
	
	/** number of new individuals since last resetCounters() */
	public int nAdded();

	/** number of deleted individuals since last resetCounters() */
	public int nRemoved();
	
	/** reset mortality and natality counters to zero */
	public void resetCounters();
	
	
}
