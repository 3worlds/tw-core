package au.edu.anu.twcore.ecosystem.runtime.system;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.containers.IndexedContainer;
import fr.cnrs.iees.uit.indexing.BoundedRegionIndexingTree;
import fr.cnrs.iees.uit.space.Box;
import fr.cnrs.iees.uit.space.Point;
import fr.cnrs.iees.uit.space.Sphere;
import fr.cnrs.iees.uit.space.SphereImpl;

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
		// build indexer - need variables, only doubles or floats
		Box limits = Box.boundingBox(Point.newPoint(0,0,0,0),Point.newPoint(10,10,8,12));
		indexer = new BoundedRegionIndexingTree<>(limits);
		for (SystemComponent sc:allItems())
			indexer.insert(sc,coordinates(sc));
	}

	@Override
	public SystemComponent getNearestItem(SystemComponent item) {
		return indexer.getNearestItem(coordinates(item));
	}

	@Override
	public Iterable<SystemComponent> getItemsWithin(SystemComponent item, double distance) {
		Sphere itemSphere = new SphereImpl(coordinates(item),distance);
		return indexer.getItemsWithin(itemSphere);
	}
	
	private Point coordinates(SystemComponent sc) {
		// TODO: really compute coordinates of system component here
		return Point.newPoint(0);
	}

	@Override
	public void effectChanges() {
		for (String id : itemsToRemove) {
			SystemComponent sc = items.get(id);
			indexer.remove(sc,coordinates(sc));
		}
		for (SystemComponent sc: itemsToAdd)
			indexer.insert(sc,coordinates(sc));
		super.effectChanges();
	}

}
