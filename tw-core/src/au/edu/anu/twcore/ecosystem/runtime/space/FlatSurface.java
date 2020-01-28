package au.edu.anu.twcore.ecosystem.runtime.space;

import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import fr.cnrs.iees.uit.indexing.BoundedRegionIndexingTree;
import fr.cnrs.iees.uit.space.Box;
import fr.cnrs.iees.uit.space.Point;
import fr.cnrs.iees.uit.space.Sphere;
import fr.cnrs.iees.uit.space.SphereImpl;

/**
 * 
 * @author Jacques Gignoux - 28 janv. 2020
 *
 */
public class FlatSurface implements Space {
	
	private BoundedRegionIndexingTree<SystemComponent> indexer;
	private Box limits;
	private String pointPropertyName;

	public FlatSurface(double xmin, double xmax, double ymin, double ymax, String ppn) {
		super();
		limits = Box.boundingBox(Point.newPoint(xmin,ymin),Point.newPoint(xmax,ymax));
		indexer = new BoundedRegionIndexingTree<>(limits);
		pointPropertyName = ppn;
	}
	
	private Point coordinates(SystemComponent sc) {
		// TODO: really compute coordinates of system component here
		// Question: should we implement points as possible property types,
		// or should we convert some properties ?
		// NB: first would be more efficient.
		return (Point) sc.currentState().getPropertyValue(pointPropertyName);
	}

	@Override
	public Box boundingBox() {
		return limits;
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

	@Override
	public void index(SystemComponent component) {
		indexer.insert(component,coordinates(component));
	}

	@Override
	public void unIndex(SystemComponent item) {
		indexer.remove(item,coordinates(item));
	}

}
