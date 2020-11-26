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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemRelation;
import au.edu.anu.twcore.ecosystem.runtime.tracking.SpaceDataTracker;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.graph.Graph;
import fr.cnrs.iees.twcore.constants.BorderType;
import fr.cnrs.iees.twcore.constants.SpaceType;
import fr.cnrs.iees.uit.indexing.ExpandingLimitedPrecisionIndexingTree;
import fr.cnrs.iees.uit.indexing.LimitedPrecisionIndexingTree;
import fr.cnrs.iees.uit.space.Box;
import fr.cnrs.iees.uit.space.Point;
import fr.cnrs.iees.uit.space.Sphere;

/**
 * A spatial representation of a rectangular flat surface.
 * Locations are known to a given precision that must be passed as an argument to the constructor.
 * Internally, coordinates are stored as longs computed from real (double) coordinates truncated to
 * precision.
 *
 * @author Jacques Gignoux - 28 janv. 2020
 *
 */
public class FlatSurface extends SpaceAdapter {

	private static final int ndim = SpaceType.continuousFlatSurface.dimensions();

	// Locations for this space - just a 2D Point
	private class flatSurfaceLocation implements Location {
		protected Point loc;
		protected flatSurfaceLocation(double...xyloc) {
			super();
			loc = Point.newPoint(xyloc);
		}
		protected flatSurfaceLocation(Point p) {
			super();
			loc = p;
		}
		@Override
		public Point asPoint() {
			return loc;
		}
		@Override
		public boolean equals(Object obj) {
			throw new TwcoreException("equals() disabled for locations. use Space.equalLocation() to compare locations.");
		}
	}

	private Map<SystemComponent,Point> locatedItems = new HashMap<>();

	private LimitedPrecisionIndexingTree<SystemComponent> indexer;

	public FlatSurface(double xmin, double xmax, double ymin, double ymax,
			double prec, String units, BorderType[][] bt, Box guard, double guardWidth,
			SpaceDataTracker dt,String proposedId) {
		super(Box.boundingBox(Point.newPoint(xmin,ymin),Point.newPoint(xmax,ymax)),prec,units,
			bt,guard,guardWidth,dt,proposedId);
		if ((upperBorderTypes[0]==BorderType.infinite)||
			(upperBorderTypes[1]==BorderType.infinite)||
			(lowerBorderTypes[0]==BorderType.infinite)||
			(lowerBorderTypes[1]==BorderType.infinite)) {
			indexer = new ExpandingLimitedPrecisionIndexingTree<>(boundingBox(),prec);
		}
		else
			indexer = new LimitedPrecisionIndexingTree<>(boundingBox(),prec);
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
	public Location locate(SystemComponent focal) {
		Point at = Point.newPoint(focal.locationData().coordinates());
		locatedItems.put(focal,at);
		indexer.insert(focal,at);
		return makeLocation(at);
	}

	@Override
	public void unlocate(SystemComponent focal) {
		indexer.remove(focal);
		locatedItems.remove(focal);
	}

	@Override
	public Iterable<SystemComponent> getNearestItems(SystemComponent item) {
		return indexer.getNearestItems(locatedItems.get(item));
	}

	@Override
	public Iterable<SystemComponent> getItemsWithin(SystemComponent item, double distance) {
		if (item==null)
			System.out.println("Null system component passed to getItemsWithin(...)");
		Point p = locatedItems.get(item);
		Sphere itemSphere = Sphere.newSphere(p,distance);
		return indexer.getItemsWithin(itemSphere);
	}

	@Override
	public Location locationOf(SystemComponent focal) {
		return makeLocation(locatedItems.get(focal));
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
		indexer.clear();
		locatedItems.clear();
	}

	@Override
	public Location makeLocation(double... x) {
		return new flatSurfaceLocation(x);
	}

	@Override
	public Location makeLocation(Point point) {
		return new flatSurfaceLocation(point);
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

	// NOTICE that now the full limit of the underlying space indexer is not reachable
	// only the required limits set at creation are accessible.
	// This because the indexer limits are a square of size 2^n > real limits

	@Override
	public Box boundingBox() {
//		if (indexer instanceof ExpandingLimitedPrecisionIndexingTree) {
//			Box reg = super.boundingBox();
//			double xmin = reg.lowerBound(0);
//			double xmax = reg.upperBound(0);
//			double ymin = reg.lowerBound(1);
//			double ymax = reg.upperBound(1);
//			if (lowerBorderTypes[0]==BorderType.infinite)
//				if (indexer.region()!=null)
//					xmin = indexer.root().lowerBounds.coordinate(0);
//			if (upperBorderTypes[0]==BorderType.infinite)
//				if (indexer.region()!=null)
//					xmax = indexer.region().upperBound(0);
//			if (lowerBorderTypes[1]==BorderType.infinite)
//				if (indexer.region()!=null)
//					ymin = indexer.region().lowerBound(1);
//			if (upperBorderTypes[1]==BorderType.infinite)
//				if (indexer.region()!=null)
//					ymax = indexer.region().upperBound(1);
//			reg = Box.boundingBox(Point.newPoint(xmin,ymin), Point.newPoint(xmax,ymax));
//			return reg;
//		}
//		else
			return super.boundingBox();
	}

	@Override
	public void relocate(SystemComponent item) {
		if (item.mobile()) {
			Point newLoc = Point.newPoint(item.nextLocationData().coordinates());
			indexer.remove(item);
			indexer.insert(item, newLoc);
			locatedItems.put(item,newLoc);
		}
	}

}
