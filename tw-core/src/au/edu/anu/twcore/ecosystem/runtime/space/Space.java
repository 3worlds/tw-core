package au.edu.anu.twcore.ecosystem.runtime.space;

import au.edu.anu.twcore.ecosystem.runtime.containers.IndexedContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import fr.cnrs.iees.uit.space.Box;

/**
 * 
 * @author Jacques Gignoux - 28 janv. 2020
 *
 */
public interface Space extends IndexedContainer<SystemComponent> {

	public Box boundingBox();
	
}
