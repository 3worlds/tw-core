package au.edu.anu.twcore.ecosystem.runtime;

import au.edu.anu.twcore.ecosystem.runtime.space.Located;
import au.edu.anu.twcore.ecosystem.runtime.space.Space;

/**
 * An interface for objects that can have a space attached to them
 * @author Jacques Gignoux - 10 févr. 2020
 *
 */
public interface Spatialized<T extends Located> {
	
	public Space<T> space();

}
