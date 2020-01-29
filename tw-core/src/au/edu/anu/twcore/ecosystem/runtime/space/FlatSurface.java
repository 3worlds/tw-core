package au.edu.anu.twcore.ecosystem.runtime.space;

import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemRelation;
import fr.cnrs.iees.graph.Graph;
import fr.cnrs.iees.uit.indexing.BoundedRegionIndexingTree;
import fr.cnrs.iees.uit.space.Box;
import fr.cnrs.iees.uit.space.Point;
import fr.cnrs.iees.uit.space.Sphere;
import fr.cnrs.iees.uit.space.SphereImpl;

/**
 * A spatial representation of a rectangular flat surface.
 * 
 * @author Jacques Gignoux - 28 janv. 2020
 *
 */
public class FlatSurface implements Space {
	
	private BoundedRegionIndexingTree<SystemComponent> indexer;
	private Box limits;
	private String xname;
	private String yname;

	// TODO: rewrite with
	// (1) intervals instead of xmin xmax etc
	// (2) prec and units as fields here
	// (3) properties per dimension
	
	public FlatSurface(double xmin, double xmax, double ymin, double ymax, String xname, String yname) {
		super();
		limits = Box.boundingBox(Point.newPoint(xmin,ymin),Point.newPoint(xmax,ymax));
		indexer = new BoundedRegionIndexingTree<>(limits);
		this.xname = xname;
		this.yname = yname;
	}
	
	private Point coordinates(SystemComponent sc) {
		return Point.newPoint((double)sc.currentState().getPropertyValue(xname),
			(double)sc.currentState().getPropertyValue(yname));
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

	@Override
	public int ndim() {
		return 2;
	}

	@Override
	public Graph<SystemComponent, SystemRelation> asGraph() {
		// TODO Auto-generated method stub
		return null;
	}

}
