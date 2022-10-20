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
import java.util.logging.Logger;

import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.SpaceData;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemRelation;
import au.edu.anu.twcore.ecosystem.runtime.tracking.SpaceDataTracker;
import au.edu.anu.twcore.rngFactory.RngFactory;
import au.edu.anu.twcore.ui.runtime.DataReceiver;
import fr.cnrs.iees.omugi.identity.Identity;
import fr.cnrs.iees.omugi.identity.impl.ResettableLocalScope;
import fr.cnrs.iees.twcore.constants.BorderType;
import fr.cnrs.iees.twcore.constants.RngAlgType;
import fr.cnrs.iees.twcore.constants.RngResetType;
import fr.cnrs.iees.twcore.constants.RngSeedSourceType;
import fr.cnrs.iees.uit.space.Box;
import static fr.cnrs.iees.uit.space.Distance.*;
import fr.cnrs.iees.uit.space.Point;
import fr.cnrs.iees.omhtk.utils.Logging;

/**
 * The base class for all space implementations in 3Worlds.
 *
 * @author Jacques Gignoux - 30 janv. 2020
 *
 */
public abstract class SpaceAdapter
		implements ObserverDynamicSpace {

	private static Logger log = Logging.getLogger(SpaceAdapter.class);
	private static final String jitterRNGName = "SpaceJitterRNG";
	private scopes scope = new scopes();

	private Identity id = null;
	/**
	 * Space grain -
	 * 	the minimal absolute precision of locations is 1E-5
	 * 	ie points apart from less than this relative distance are considered to have the same location.
	 * 	It is absolute, ie does not depend on distance units */
	private static final double minimalPrecision = 0.00001;
	/** random number generator attached to this Space, if any */
	private Random rng = null;
	/** data tracker attached to this space, if any */
	private SpaceDataTracker dataTracker = null;
	/** Space bounding box (rectangle)*/
	private Box limits;
	/** absolute precision */
	private double precision;
	/** Space measurement units */
	private String units;
	/** observation window */
	private Box obsWindow;
	/** border behaviour per dimension */
	protected BorderType[] upperBorderTypes;
	protected BorderType[] lowerBorderTypes;

	private TwShape shape;

	/** absolute location of this space in the SpaceOrganiser -
	 * must be a box with dim = the greatest number of dims of any space */
//	private Box absoluteLimits;
	 /** A RNG available to descendants to create jitter around locations if needed */
	protected Random jitterRNG = null;
	/** list of SystemComponents to insert later */
	private Set<SystemComponent> toInsert = new HashSet<>();
	/** list of SystemComponents to delete later */
	private Set<SystemComponent> toDelete = new HashSet<>();
	/** list of SystemComponents to move later */
	private Set<SystemComponent> toMove = new HashSet<>();
	private boolean changed = false;
	private Set<SystemComponent> outOfSpace = new HashSet<>();


	public SpaceAdapter(Box box,
			double prec,
			String units,
			BorderType[][] borderBehaviours,
			Box obsWindow,
			double guardWidth,
			SpaceDataTracker dt,
			String proposedId,
			int simulatorId) {
		super();
		scope = new scopes();
		scope.setSimId(simulatorId);
		if (scope.getContainerScope(simulatorId)==null)
			scope.setContainerScope(simulatorId, new ResettableLocalScope(containerScopeName+"-"+simulatorId));
		if (RngFactory.find(jitterRNGName)==null)
			RngFactory.newInstance(jitterRNGName, 0, RngResetType.NEVER,RngSeedSourceType.RANDOM,RngAlgType.PCG32);
		jitterRNG = RngFactory.find(jitterRNGName).getRandom();
		limits = box;
		if (obsWindow==null)
			if (guardWidth>0.0)
				this.obsWindow = Box.boundingBox(
					Point.add(limits.lowerBounds(),guardWidth),
					Point.add(limits.upperBounds(),-guardWidth));
			else
				this.obsWindow = Box.boundingBox(limits.lowerBounds(),limits.upperBounds());
		else
			this.obsWindow = Box.boundingBox(obsWindow.lowerBounds(),obsWindow.upperBounds());
		upperBorderTypes = borderBehaviours[1];
		lowerBorderTypes = borderBehaviours[0];
		// absolute precision, i.e. in units of measurement.
		precision = Math.max(prec,minimalPrecision);
		this.units = units;
		dataTracker = dt;
		ObserverDynamicSpace.super.preProcess(); // to set the scope if not set
		id = scope().newId(true,proposedId);
	}

	// Space<T>

	@Override
	public ResettableLocalScope scope() {
		return scope.getContainerScope(scope.getSimId());
	}

	@Override
	public Box boundingBox() {
		return limits;
	}

	@Override
	public final double precision() {
		return precision;
	}

	@Override
	public final String units() {
		return units;
	}

	// RngHolder

	@Override
	public final Random rng() {
		return rng;
	}

	@Override
	public final void setRng(Random arng) {
		if (rng==null)
			rng = arng;
	}

	// SingleDataTrackerHolder<Metadata>

	@Override
	public final SpaceDataTracker dataTracker() {
		return dataTracker;
	}

	@Override
	public final Metadata metadata() {
		return dataTracker.getInstance();
	}

	// Local methods

	// CAUTION: this method assumes that the widgets have been instantiated AFTER
	// the DataTrackers
	/**
	 * 	attach space display widget to this data tracker
	 * @param widget
	 */
	public final void addObserver(DataReceiver<SpaceData,Metadata> widget)  {
		dataTracker.addObserver(widget);
	}

	// Object

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName())
			.append(" limits = ")
			.append(limits.toString())
			.append(" grain = ")
			.append(precision);
		return sb.toString();
	}

	// DynamicContainer<T>

	@Override
	public final void addItem(SystemComponent item) {
		// CAUTION: what happens if the system is to be deleted in containers after relocation?
		toInsert.add(item);
	}

	@Override
	public final void removeItem(SystemComponent item) {
		toDelete.add(item);
	}

	// DynamicSpace

	@Override
	public void moveItem(SystemComponent item) {
		toMove.add(item);
	}

	// DynamicContainer

	@SafeVarargs
	@Override
	// Remember that this is called AFTER state update, so that currentState() now contains the new locations
	public final void effectChanges(Collection<SystemComponent>... changedLists) {
		for (SystemComponent sc:toDelete)
			unlocate(sc);
		toDelete.clear();
		for (SystemComponent lsc:toInsert)
			locate(lsc);
		toInsert.clear();
		for (SystemComponent sc:toMove)
			relocate(sc);
		toMove.clear();
		changed = false;
	}

	// Resettable

	@Override
	public void preProcess() {
		// DO NOTHING! cloning initial items is the business of ComponentContainers !
	}

	@Override
	public void postProcess() {
		clear();
		toDelete.clear();
		toInsert.clear();
		((ResettableLocalScope)scope()).postProcess();
	}

	@Override
	public final String id() {
		return id.id();
	}

	// class needed by the minDist(...) method below
	private class distLoc {
		double dist;
		Point loc;
		distLoc(double dist, Point loc) {
			this.dist = dist;
			this.loc = loc;
		}
		void setMin(double d, Point otherLoc) {
			if (dist>d) {
				dist = d;
				loc = otherLoc;
			}
		}
		void setMin(distLoc other) {
			if (dist>other.dist) {
				dist = other.dist;
				loc = other.loc; // no cloning - is it right ?
			}
		}
	}
	// Recursive - veeery tricky!
	// returns both the minimal distance and the corresponding (corrected) location
	private distLoc minDist(int depth, int ndim, Point focal, Point other) {
		distLoc result = new distLoc(Double.MAX_VALUE,other);
		if ((upperBorderTypes[depth]==BorderType.wrap)&&
			(lowerBorderTypes[depth]==BorderType.wrap)) {
			Point alt = other.clone();
			if (depth==ndim-1)
				result.setMin(squaredEuclidianDistance(focal,alt),alt);
			else
				result.setMin(minDist(depth+1,ndim,focal,alt));
			alt = Point.add(other,-limits.sideLength(depth),depth);
			if (depth==ndim-1)
				result.setMin(squaredEuclidianDistance(focal,alt),alt);
			else
				result.setMin(minDist(depth+1,ndim,focal,alt));

			alt = Point.add(other,+limits.sideLength(depth),depth);
			if (depth==ndim-1)
				result.setMin(squaredEuclidianDistance(focal,alt),alt);
			else
				result.setMin(minDist(depth+1,ndim,focal,alt));
		}
		else
			if (depth==ndim-1)
				result.setMin(squaredEuclidianDistance(focal,other),other);
			else
				result.setMin(minDist(depth+1,ndim,focal,other));
		return result;
	}

	// returns the (corrected) location of other matching the shortest distance between other and focal
	@Override
	public Point fixOtherLocation(Point focal, Point other) {
		return minDist(0,ndim(),focal,other).loc;
	}

	@Override
	public double[] fixLocation(double[] location) {
		if (location.length!=ndim()) {
			log.warning("Wrong number of dimensions: default location generated");
			location = defaultLocation();
		}
		double[] newLoc = location.clone();
		// if point is inside limit, skip all corrections
		while (!limits.contains(Point.newPoint(newLoc))) {
			for (int dim=0; dim<ndim(); dim++) {
				double upper = limits.upperBound(dim);
				switch (upperBorderTypes[dim]) {
				case infinite: // always ok
					return newLoc;
				case oblivion: // point owner must be destroyed
					if (newLoc[dim]>upper)
						return null;
					break;
				case reflection: // point bounces back inside (may fall beyond the other side, though)
					if (newLoc[dim]>upper)
						newLoc[dim] = 2*upper-newLoc[dim];
					break;
				case sticky: // point sticks on border
					newLoc[dim] = Math.min(newLoc[dim],upper);
					break;
				case wrap: // points enters on the other side
					while (newLoc[dim]>upper)
						newLoc[dim] -= limits.sideLength(dim);
					break;
				default:
					break;
				}
				double lower = limits.lowerBound(dim);
				switch (lowerBorderTypes[dim]) {
				case infinite: // always ok
					return newLoc;
				case oblivion: // point owner must be destroyed
					if (newLoc[dim]<lower)
						return null;
					break;
				case reflection: // point bounces back inside (may fall beyond the other side, though)
					if (newLoc[dim]<lower)
						newLoc[dim] = 2*lower-newLoc[dim];
					break;
				case sticky: // point sticks on border
					newLoc[dim] = Math.max(newLoc[dim],lower);
					break;
				case wrap: // points enters on the other side
					while (newLoc[dim]<lower)
						newLoc[dim] += limits.sideLength(dim);
					break;
				default:
					break;
				}
			}
		}
		return newLoc;
	}

	@Override
	public final Box observationWindow() {
		return obsWindow;
	}

	@Override
	public double[] randomLocation() {
		double[] result = new double[limits.dim()];
		for (int i=0; i<result.length; i++)
			result[i] = limits.lowerBound(i) + rng.nextDouble()*limits.sideLength(i);
		return result;
	}

	@Override
	public final TwShape asShape() {
		return shape;
	}
	
	// DynamicGraphObserver

	@Override
	public void onEdgeAdded(SystemRelation sr) {
		// we only care about relations if we want to draw them
		if (dataTracker!=null)
			dataTracker.createLine(((SystemComponent)sr.startNode()).hierarchicalId(),
				((SystemComponent)sr.endNode()).hierarchicalId(),sr.type());
	}

	@Override
	public void onEdgeRemoved(SystemRelation sr) {
		// we only care about relations if we want to draw them
		if (dataTracker!=null)
			dataTracker.deleteLine(((SystemComponent)sr.startNode()).hierarchicalId(),
				((SystemComponent)sr.endNode()).hierarchicalId(),sr.type());
	}
	
	/**
	 * Apply edge-effect corrections to raw component spatial coordinates
	 * @param sc
	 * @return fixed coordinates OR null if component jumped out of space
	 */
	private double[] checkCoordinates(SystemComponent sc) {
		double[] oldLoc, newLoc;
		// get the coordinates - depends if the component is mobile
		if (sc.mobile())
			oldLoc = sc.nextLocationData().coordinates(); 	// always non null
		else
			oldLoc = sc.locationData().coordinates(); 		// always non null
		// apply edge effect correction
		newLoc = fixLocation(oldLoc);					// may be null
		// a null new location means the component jumped out of space
		if (newLoc!=null) { 
			// replace its coordinate with the new ones
			if (sc.mobile())
				sc.nextLocationData().setCoordinates(newLoc);
			else
				sc.locationData().setCoordinates(newLoc);
			// add component into the new component list for delayed insertion
		}
		return newLoc;
	}

	@Override
	public final void onNodeAdded(SystemComponent sc) {
		double[] loc = checkCoordinates(sc);
		if (loc!=null) {
			addItem(sc);
			if (dataTracker!=null)
				dataTracker.createPoint(loc,sc.hierarchicalId());
		}
		else
			outOfSpace.add(sc);
	}

	@Override
	public void onNodeChanged(SystemComponent sc) {
		double[] loc = checkCoordinates(sc);
		if (loc!=null) {
			moveItem(sc);
			if (dataTracker!=null)
				dataTracker.movePoint(loc,sc.hierarchicalId());
		}
		else {
			removeItem(sc);
			if (dataTracker!=null)
				dataTracker.deletePoint(sc.hierarchicalId());
			outOfSpace.add(sc);
		}
	}

	@Override
	public final void onNodeRemoved(SystemComponent sc) {
		// Assumes the SC has been properly disconnected before this call
		removeItem(sc);
		if (dataTracker!=null)
			dataTracker().deletePoint(sc.hierarchicalId());
	}

	@Override
	public Collection<SystemComponent> outOfSpaceItems() {
		return Collections.unmodifiableCollection(outOfSpace);
	}
	
	@Override
	public void removeOutOfSpaceItems() {
		toDelete.removeAll(outOfSpace);
		toInsert.removeAll(outOfSpace);
		toMove.removeAll(outOfSpace);
		outOfSpace.clear();
	}

	@Override
	public boolean structureChanged() {
		return changed;
	}

	@Override
	public SpaceAdapter changeStructure() {
		changed = true;
		return this;
	}

}
