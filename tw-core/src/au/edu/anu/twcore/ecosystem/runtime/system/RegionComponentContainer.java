package au.edu.anu.twcore.ecosystem.runtime.system;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.containers.IndexedContainer;
import fr.cnrs.iees.uit.indexing.BoundedRegionIndexingTree;

/**
 * 
 * @author Jacques Gignoux - 24 janv. 2020
 *
 */
public class RegionComponentContainer 
		extends ComponentContainer 
		implements IndexedContainer<SystemComponent> {
	
	private BoundedRegionIndexingTree<SystemComponent> indexer;

	public RegionComponentContainer(Categorized<SystemComponent> cats, String proposedId, ComponentContainer parent,
			TwData parameters, TwData variables) {
		super(cats, proposedId, parent, parameters, variables);
		// TODO Auto-generated constructor stub
	}

	@Override
	public SystemComponent getNearestItem(SystemComponent item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<SystemComponent> getItemsWithin(SystemComponent item, double distance) {
		// TODO Auto-generated method stub
		return null;
	}

}
