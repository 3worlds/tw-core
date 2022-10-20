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

import java.util.*;
import fr.cnrs.iees.omugi.collections.tables.DoubleTable;
import fr.cnrs.iees.uit.space.Dimensioned;
import fr.cnrs.iees.uit.space.Point;
import fr.cnrs.iees.omhtk.Sealable;

/**
 * The class that knows how spaces are positioned relative to each other (equivalent for Spaces of the
 * TimeLine for Timers).
 *
 * @author J. Gignoux - 10 juil. 2020
 *
 */
public class SpaceOrganiser implements Sealable, Dimensioned {

	/** all spaces in a simulation, sorted by name (id) */
	private Map<String,ObserverDynamicSpace> spaces = new HashMap<>();
	private boolean sealed = false;
	/** The fixed points used to position spaces relative to each other */
	/** All spaces must also keep the coordinates of these points in their own coordinate system */
	private Point[] fixedPoints = null;

	// TODO: overlaps between spaces, intersections, projections, topolgical assemblage...
	// store geometric transformations from one space to the next
	// NB: if DynamicSpace is a Node, then a graph of spaces can be build and used here.

	/**
	 * Use this constructor to initialise a SpaceOrganiser with a list of fixed points. Spaces must have
	 * matching values in their own coordinate systems. This is only for models with multiple spaces
	 *
	 * @param points
	 */
	public SpaceOrganiser(DoubleTable points) {
		super();
		fixedPoints = new Point[points.ndim()];
		for (int dim=0; dim<points.ndim(); dim++) { // ndim must ==2
			double[] d = new double[points.size(dim)];
			for (int i=0; i<d.length;i++)
				d[i] = points.getByInt(dim,i);
			fixedPoints[dim] = Point.newPoint(d);
		}
	}

	/**
	 * Use this constructor to initialise a SpaceOrganiser with a single Space. In this case, no need
	 * for fixed points - the space bounding box points are used as fixed points.
	 * @param space
	 */
	public SpaceOrganiser(ObserverDynamicSpace space) {
		super();
		this.spaces.put(space.id(),space);
		// only one space present: fxed points = bounding box
		fixedPoints = new Point[2];
		fixedPoints[0] = space.boundingBox().lowerBounds();
		fixedPoints[1] = space.boundingBox().upperBounds();
		seal();
	}

	/**
	 * Use this in conjunction with first constructor to add spaces once the fixed points have been set.
	 * @param sp
	 */
	public void addSpace(ObserverDynamicSpace sp) {
		if (!sealed) {
			spaces.put(sp.id(),sp);
			//build geometric transformation from fixedPoints
		}
	}

	@Override
	public Sealable seal() {
		sealed = true;
		return this;
	}

	@Override
	public boolean isSealed() {
		return sealed;
	}

//	/**
//	 * Finds a SystemComponent in the named space, returns null if not found.
//	 *
//	 * @param item
//	 * @param inSpace
//	 * @return
//	 */
//	public Location whereIs(SystemComponent item, String inSpace) {
//		DynamicSpace<SystemComponent> sp = spaces.get(inSpace);
//		if (sp!=null)
//			return sp.locationOf(item);
//		return null;
//	}

	public ObserverDynamicSpace space(String name) {
		return spaces.get(name);
	}

	@Override
	public int dim() {
		return fixedPoints[0].dim();
	}

	public Point fixedPoint(int i) {
		return fixedPoints[i];
	}

	public int nPoints() {
		return fixedPoints.length;
	}

	public Collection<ObserverDynamicSpace> spaces() {
		return Collections.unmodifiableCollection(spaces.values());
	}

}
