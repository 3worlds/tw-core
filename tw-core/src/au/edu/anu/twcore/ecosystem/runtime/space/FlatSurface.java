package au.edu.anu.twcore.ecosystem.runtime.space;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemRelation;
import au.edu.anu.twcore.ecosystem.runtime.tracking.SpaceDataTracker;
import fr.cnrs.iees.graph.Graph;
import fr.cnrs.iees.twcore.constants.EdgeEffects;
import fr.cnrs.iees.uit.indexing.BoundedRegionIndexingTree;
import fr.cnrs.iees.uit.space.Box;
import fr.cnrs.iees.uit.space.Point;
import fr.cnrs.iees.uit.space.Sphere;
import fr.cnrs.iees.uit.space.SphereImpl;
import fr.ens.biologie.generic.utils.Logging;

/**
 * A spatial representation of a rectangular flat surface.
 * 
 * @author Jacques Gignoux - 28 janv. 2020
 *
 */
// todo: toroidal correction
public class FlatSurface extends SpaceAdapter<SystemComponent> {
	
	private static Logger log = Logging.getLogger(FlatSurface.class);
	
	private static final int ndim = 2;

	private class flatSurfaceLocation implements Location {
		protected Point loc;
		protected Point locDeviation;
		protected flatSurfaceLocation(Located sc,double...xyloc) {
			super();
			double p = precision();
			double x = Math.floor(xyloc[0]/p)*p; // truncates location to nearest precision unit
			double y = Math.floor(xyloc[1]/p)*p; // truncates location to nearest precision unit
			loc = Point.newPoint(x,y);
			// replace truncated part by a random dev to make sure two positions are never exactly the same
			locDeviation = Point.newPoint(jitterRNG.nextDouble()*p,jitterRNG.nextDouble()*p);
			checkLocation(this);
//			if (!boundingBox().contains(loc))
//				throw new TwcoreException("New spatial coordinates for item "
//					+sc.toString()+" out of range "+boundingBox().toString());
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
	
	private final double xmin,xmax,ymin,ymax; // to save access time - redundant with boundingBox()

	public FlatSurface(double xmin, double xmax, double ymin, double ymax, 
			double prec, String units, EdgeEffects ee, SpaceDataTracker dt) {
		super(Box.boundingBox(Point.newPoint(xmin,ymin),Point.newPoint(xmax,ymax)),prec,units,ee,dt);
		indexer = new BoundedRegionIndexingTree<>(boundingBox());
		this.xmin = boundingBox().lowerBound(0);
		this.xmax = boundingBox().upperBound(0);
		this.ymin = boundingBox().lowerBound(1);
		this.ymax = boundingBox().upperBound(1);
	}
	
	private void checkLocation(flatSurfaceLocation location) {
		switch (edgeEffectCorrection()) {
			case bufferAndWrap:
				// TODO
			case bufferZone:
				// TODO
			case noCorrection:
				if (!boundingBox().contains(location.loc)) {
					log.warning("Proposed location "+location.loc+" out of range "+ boundingBox()+
						" - new location generated.");
					double x = Math.floor((xmin+rng().nextDouble()*(xmax-xmin))/precision())*precision(); 
					double y = Math.floor((ymin+rng().nextDouble()*(ymax-ymin))/precision())*precision();
					Point newloc = Point.newPoint(x,y);
					Point locD = Point.newPoint(jitterRNG.nextDouble()*precision(),jitterRNG.nextDouble()*precision());
					// CAUTION: theoretical possibility of an infinite loop here...
					while (!boundingBox().contains(Point.add(newloc,locD)))
						locD = Point.newPoint(jitterRNG.nextDouble()*precision(),jitterRNG.nextDouble()*precision());
					location.loc = newloc;
					location.locDeviation = locD;
				}
				break;
			case wrapAround1D:
				// TODO
			case wrapAround2D:
				// TODO
			case wrapAroundAllD:
				// TODO
				break;
		}
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
	public void locate(SystemComponent focal, double...location) {
		flatSurfaceLocation at = new flatSurfaceLocation(focal,location);
		locatedItems.put(focal,at);
		// new item is located in the quadtree in the square to the right and above its loc
		// by 1 precision unit
		indexer.insert(focal,Point.add(at.loc,at.locDeviation));
	}

	@Override
	public void locate(SystemComponent focal, Point location) {
		locate(focal,location.x(),location.y());		
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
		if (item==null)
			System.out.println("Null system component passed to getItemsWithin(...)");
		Location lok = locatedItems.get(item);
		Point p = lok.asPoint();
//		Sphere itemSphere = new SphereImpl(locatedItems.get(item).asPoint(),distance);
		Sphere itemSphere = new SphereImpl(p,distance);
		return indexer.getItemsWithin(itemSphere);
	}

	@Override
	public Point locationOf(SystemComponent focal) {
		if (locatedItems.get(focal)!=null)
			return locatedItems.get(focal).asPoint();
		else
			return null;
	}

	@Override
	public void unlocate(Collection<SystemComponent> items) {
		for (SystemComponent sc:items) {
			Point loc = locationOf(sc);
			if (loc!=null)
				indexer.remove(sc,loc);
		}
		locatedItems.keySet().removeAll(items);
	}

}
