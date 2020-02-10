package au.edu.anu.twcore.ecosystem.runtime.space;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemRelation;
import au.edu.anu.twcore.exceptions.TwcoreException;
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
// todo: toroidal correction
public class FlatSurface extends SpaceAdapter<SystemComponent> {
	
	private static final int ndim = 2;

	private class flatSurfaceLocation implements Location {
		protected Point loc;
		protected Point locDeviation;
		protected flatSurfaceLocation(Located sc,double[] xyloc) {
			super();
			double p = precision();
			double x = Math.floor(xyloc[0]/p)*p; // truncates location to nearest precision unit
			double y = Math.floor(xyloc[1]/p)*p; // truncates location to nearest precision unit
			loc = Point.newPoint(x,y);
			// replace truncated part by a random dev to make sure two positions are never exactly the same
			locDeviation = Point.newPoint(jitterRNG.nextDouble()*p,jitterRNG.nextDouble()*p);
			if (!boundingBox().contains(loc))
				throw new TwcoreException("New spatial coordinates for item "
					+sc.toString()+" out of range "+boundingBox().toString());
		}
		@Override
		public Point asPoint() {
			return loc;
		}
		@Override
		public String toString() {
			return loc.toString();
		}
	}
	
	private Map<SystemComponent,Location> locatedItems = new HashMap<>();
	
	private BoundedRegionIndexingTree<SystemComponent> indexer;

	public FlatSurface(double xmin, double xmax, double ymin, double ymax, double prec, String units) {
		super(Box.boundingBox(Point.newPoint(xmin,ymin),Point.newPoint(xmax,ymax)),prec,units);
		indexer = new BoundedRegionIndexingTree<>(boundingBox());
	}
	
	@Override
	public int ndim() {
		return ndim;
	}

	@Override
	public Graph<SystemComponent, SystemRelation> asGraph() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void locate(SystemComponent focal, double[] location) {
		flatSurfaceLocation at = new flatSurfaceLocation(focal,location);
		locatedItems.put(focal,at);
		// new item is located in the quadtree in the square to the right and above its loc
		// by 1 precision unit
		indexer.insert(focal,Point.add(at.loc,at.locDeviation));
	}

	@Override
	public void unlocate(SystemComponent focal) {
		indexer.remove(focal,locatedItems.get(focal).asPoint());
		locatedItems.remove(focal);		
	}

	@Override
	public Iterable<SystemComponent> getNearestItems(SystemComponent item) {
		flatSurfaceLocation at = (flatSurfaceLocation) locatedItems.get(item);
		// get the closest SystemComponent
		SystemComponent closest = indexer.getNearestItem(locatedItems.get(item).asPoint());
		List<SystemComponent> result = new ArrayList<>();
		// closest might be within <precision> distance of item
		// in this case we must search the square box of side <precision> for all
		// other items because they are considered at the same location
		Box jitterBox = Box.boundingBox(at.loc,Point.add(at.loc,precision()));
		if (jitterBox.contains(locatedItems.get(closest).asPoint())) // maybe wrong here - this is not exact location
			for (SystemComponent sc:indexer.getItemsWithin(jitterBox))
				result.add(sc);
		// if nothing else was found in the box, or if closest was further away
		// then it's the only result to return
		if (result.isEmpty())
			result.add(closest);
		return result;
	}

	@Override
	public Iterable<SystemComponent> getItemsWithin(SystemComponent item, double distance) {
		// BUG HERE: item MUST be in locateditems list, otherwise null pointer exception
		// This has to be done at model initialisation
		Sphere itemSphere = new SphereImpl(locatedItems.get(item).asPoint(),distance);
		return indexer.getItemsWithin(itemSphere);
	}

}
