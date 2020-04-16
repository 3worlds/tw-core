package au.edu.anu.twcore.ecosystem.runtime.containers;

import au.edu.anu.twcore.ecosystem.runtime.system.HierarchicalComponent;

/**
 * Interface for container which have a hierarchical view counterpart (ie are represented by a
 * SystemComponent).
 *
 * @author J. Gignoux - 16 avr. 2020
 *
 */
public interface ContainerHierarchicalView {

	public HierarchicalComponent hierarchicalView();

}
