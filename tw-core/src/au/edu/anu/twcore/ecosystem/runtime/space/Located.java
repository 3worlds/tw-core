package au.edu.anu.twcore.ecosystem.runtime.space;

import fr.cnrs.iees.identity.Identity;

/**
 * An interface for objects which have a location
 * 
 * @author Jacques Gignoux - 30 janv. 2020
 *
 */
public interface Located<I extends Identity, T extends Location> extends Identity {
	
	public T location();
	
	public void location(T location);
	
	public I item();

}
