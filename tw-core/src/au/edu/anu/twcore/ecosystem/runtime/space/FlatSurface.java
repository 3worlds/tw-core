/**************************************************************************
 *  TW-CORE - 3Worlds Core classes and methods                            *
 *                                                                        *
 *  Copyright 2018: Shayne Flint, Jacques Gignoux & Ian D. Davies         *
 *       shayne.flint@anu.edu.au                                          *
 *       jacques.gignoux@upmc.fr                                          *
 *       ian.davies@anu.edu.au                                            *
 *                                                                        *
 *  TW-CORE is a library of the principle components required by 3W       *
 *                                                                        *
 **************************************************************************
 *  This file is part of TW-CORE (3Worlds Core).                          *
 *                                                                        *
 *  TW-CORE is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  TW-CORE is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with TW-CORE.                                                   *
 *  If not, see <https://www.gnu.org/licenses/gpl.html>                   *
 *                                                                        *
 **************************************************************************/
package au.edu.anu.twcore.ecosystem.runtime.space;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemRelation;
import au.edu.anu.twcore.ecosystem.runtime.tracking.SpaceDataTracker;
import fr.cnrs.iees.graph.Graph;
import fr.cnrs.iees.twcore.constants.BorderType;
import fr.cnrs.iees.twcore.constants.SpaceType;
import fr.cnrs.iees.uit.indexing.BoundedRegionIndexingTree;
import fr.cnrs.iees.uit.indexing.ExpandingRegionIndexingTree;
import fr.cnrs.iees.uit.indexing.RegionIndexingTree;
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
public class FlatSurface extends SpaceAdapter {

	private static final int ndim = SpaceType.continuousFlatSurface.dimensions();

	private class flatSurfaceLocation implements Location {
		protected Point loc;
		protected Point locDeviation;
		protected flatSurfaceLocation(double...xyloc) {
			super();
			double p = precision();
			double x = Math.floor(xyloc[0]/p)*p; // truncates location to nearest precision unit
			double y = Math.floor(xyloc[1]/p)*p; // truncates location to nearest precision unit
			loc = Point.newPoint(x,y);
			// replace truncated part by a random dev to make sure two positions are never exactly the same
			locDeviation = Point.newPoint(jitterRNG.nextDouble()*p,jitterRNG.nextDouble()*p);
//			checkLocation(this);
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

	private RegionIndexingTree<SystemComponent> indexer;

	public FlatSurface(double xmin, double xmax, double ymin, double ymax,
			double prec, String units, BorderType[][] bt, Box guard, double guardWidth,
			SpaceDataTracker dt,String proposedId) {
		super(Box.boundingBox(Point.newPoint(xmin,ymin),Point.newPoint(xmax,ymax)),prec,units,
			bt,guard,guardWidth,dt,proposedId);
		if ((upperBorderTypes[0]==BorderType.infinite)||
			(upperBorderTypes[1]==BorderType.infinite)||
			(lowerBorderTypes[0]==BorderType.infinite)||
			(lowerBorderTypes[1]==BorderType.infinite)) {
			indexer = new ExpandingRegionIndexingTree<>(boundingBox());
		}
		else
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
	public Location locate(SystemComponent focal, double...location) {
		flatSurfaceLocation at = new flatSurfaceLocation(location);
		locatedItems.put(focal,at);
		// new item is located in the quadtree in the square to the right and above its loc
		// by 1 precision unit
		indexer.insert(focal,Point.add(at.loc,at.locDeviation));
		return at;
	}

	@Override
	public void unlocate(SystemComponent focal) {
		indexer.remove(focal);
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
	public Location locationOf(SystemComponent focal) {
		return locatedItems.get(focal);
	}

	@Override
	public void unlocate(Collection<SystemComponent> items) {
		for (SystemComponent sc:items) {
			Point loc = locationOf(sc).asPoint();
			if (loc!=null)
				indexer.remove(sc);
		}
		locatedItems.keySet().removeAll(items);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append(" n = ")
			.append(locatedItems.size());
		return sb.toString();
	}

	@Override
	public void clear() {
		indexer = new BoundedRegionIndexingTree<>(boundingBox());
		locatedItems.clear();
	}

	@Override
	public Location makeLocation(double... x) {
		return new flatSurfaceLocation(x);
	}

	@Override
	public Location makeLocation(Point point) {
		double[] d = new double[point.dim()];
		for (int i=0; i< d.length; i++)
			d[i] = point.coordinate(i);
		return new flatSurfaceLocation(d);
	}

	@Override
	public boolean equalLocation(Location reference, double[] candidate) {
		if (reference.asPoint().dim()==candidate.length)
			if (reference instanceof flatSurfaceLocation) {
				flatSurfaceLocation refloc = (flatSurfaceLocation) reference;
				for (int i=0; i<refloc.loc.dim(); i++)
					if (Math.abs(refloc.loc.coordinate(i)-candidate[i])>precision())
						return false;
				return true;
		}
		return false;
	}

	@Override
	public Box boundingBox() {
		if (indexer instanceof ExpandingRegionIndexingTree) {
			Box reg = super.boundingBox();
			double xmin = reg.lowerBound(0);
			double xmax = reg.upperBound(0);
			double ymin = reg.lowerBound(1);
			double ymax = reg.upperBound(1);
			if (lowerBorderTypes[0]==BorderType.infinite)
				if (indexer.region()!=null)
					xmin = indexer.region().lowerBound(0);
			if (upperBorderTypes[0]==BorderType.infinite)
				if (indexer.region()!=null)
					xmax = indexer.region().upperBound(0);
			if (lowerBorderTypes[1]==BorderType.infinite)
				if (indexer.region()!=null)
					ymin = indexer.region().lowerBound(1);
			if (upperBorderTypes[1]==BorderType.infinite)
				if (indexer.region()!=null)
					ymax = indexer.region().upperBound(1);
			reg = Box.boundingBox(Point.newPoint(xmin,ymin), Point.newPoint(xmax,ymax));
			return reg;
		}
		else
			return super.boundingBox();
	}

}
