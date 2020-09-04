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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.SpaceData;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.tracking.SpaceDataTracker;
import au.edu.anu.twcore.rngFactory.RngFactory;
import au.edu.anu.twcore.ui.runtime.DataReceiver;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.identity.impl.ResettableLocalScope;
import fr.cnrs.iees.twcore.constants.BorderType;
import fr.cnrs.iees.twcore.constants.RngAlgType;
import fr.cnrs.iees.twcore.constants.RngResetType;
import fr.cnrs.iees.twcore.constants.RngSeedSourceType;
import fr.cnrs.iees.uit.space.Box;
import fr.cnrs.iees.uit.space.Point;
import fr.ens.biologie.generic.utils.Logging;

/**
 * The base class for all space implementations in 3Worlds.
 *
 * @author Jacques Gignoux - 30 janv. 2020
 *
 */
public abstract class SpaceAdapter
		implements DynamicSpace<SystemComponent,LocatedSystemComponent> {
	
	private static Logger log = Logging.getLogger(SpaceAdapter.class);
	private static final String jitterRNGName = "SpaceJitterRNG";

	private Identity id = null;
	/**
	 * Space grain -
	 * 	the minimal relative precision of locations is 1E-5
	 * 	ie points apart from less than this relative distance are considered to have the same location
	 * 	it is relative to space bounding box diagonal */
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
	/** type of edge-effect correction */
//	private EdgeEffects correction; // deprecated to below
	/** observation window */
	private Box obsWindow;
	/** border behaviour per dimension */
	private BorderType[] upperBorderTypes;
	private BorderType[] lowerBorderTypes;
	
	private TwShape shape;
	
	/** absolute location of this space in the SpaceOrganiser -
	 * must be a box with dim = the greatest number of dims of any space */
//	private Box absoluteLimits;
	 /** A RNG available to descendants to create jitter around locations if needed */
	protected Random jitterRNG = null;
	/** list of SystemComponents to insert later */
	private List<LocatedSystemComponent> toInsert = new LinkedList<>();
	/** list of SystemComponents to delete later */
	private List<SystemComponent> toDelete = new LinkedList<>();
	/** list of initial SystemComponents */
	private Set<LocatedSystemComponent> initialComponents = new HashSet<>();
	/** mapping of cloned item to their initial components */
	private Map<String, LocatedSystemComponent> itemsToInitials = new HashMap<>();
	private boolean changed = false;


	public SpaceAdapter(Box box, 
			double prec, 
			String units, 
			BorderType[][] borderBehaviours, 
			SpaceDataTracker dt, 
			String proposedId) {
		super();
		if (RngFactory.find(jitterRNGName)==null)
			RngFactory.newInstance(jitterRNGName, 0, RngResetType.never,RngSeedSourceType.secure,RngAlgType.Pcg32);
		jitterRNG = RngFactory.find(jitterRNGName).getRandom();
		limits = box;
		obsWindow = limits; // default value
		upperBorderTypes = borderBehaviours[1];
		lowerBorderTypes = borderBehaviours[0];		
		//	precision based on shortest side of plot - NB I think that's a mistake - cf below
//		precision = Math.max(prec,minimalPrecision)*Math.min(limits.sideLength(0),limits.sideLength(1));
		// absolute precision, i.e. in units of measurement.
		precision = Math.max(prec,minimalPrecision);
		this.units = units;
		dataTracker = dt;
		DynamicSpace.super.preProcess(); // to set the scope if not set
		id = scope().newId(true,proposedId);
	}

	// Space<T>

	@Override
	public final Box boundingBox() {
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

//	@Override
//	public final EdgeEffects edgeEffectCorrection() {
//		return correction;
//	}

	@Override
	public Location locate(SystemComponent focal, Point location) {
		return locate(focal,location.x(),location.y()); // FLAW here! what about 3D spaces ?
	}

	@Override
	public Location locate(SystemComponent focal, Location location) {
		return locate(focal,location.asPoint());
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
	public final void addItem(LocatedSystemComponent item) {
		// CAUTION: what happens if the system is to be deleted in containers after relocation?
		toInsert.add(item);
	}

	@Override
	public final void removeItem(LocatedSystemComponent item) {
		toDelete.add(item.item());
	}

	// This is called after all graph changes (structure and state)
	@Override
	public final void effectChanges() {
		for (SystemComponent sc:toDelete)
			unlocate(sc);
		toDelete.clear();
		// CAUTION: what happens if the system is to be deleted in containers after relocation?
		for (LocatedSystemComponent lsc:toInsert)
			locate(lsc.item(),lsc.location());
		toInsert.clear();
		changed = false;
	}

	// ResettableContainer

	@Override
	public final void setInitialItems(LocatedSystemComponent... items) {
		for (LocatedSystemComponent lsc:items)
			initialComponents.add(lsc);
	}

	@Override
	public final void setInitialItems(Collection<LocatedSystemComponent> items) {
		initialComponents.addAll(items);
	}

	@Override
	public final void setInitialItems(Iterable<LocatedSystemComponent> items) {
		for (LocatedSystemComponent lsc:items)
			initialComponents.add(lsc);
	}

	@Override
	public final void addInitialItem(LocatedSystemComponent item) {
		initialComponents.add(item);
	}

	@Override
	public final Set<LocatedSystemComponent> getInitialItems() {
		return initialComponents;
	}

	@Override
	public final boolean containsInitialItem(LocatedSystemComponent item) {
		return initialComponents.contains(item);
	}

	@Override
	public final LocatedSystemComponent initialForItem(String id) {
		return itemsToInitials.get(id);
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
		itemsToInitials.clear();
		((ResettableLocalScope)scope()).postProcess();
	}

	@Override
	public final String id() {
		return id.id();
	}

	@Override
	public final boolean changed() {
		return changed;
	}

	@Override
	public final void change() {
		changed = true;
	}
	
	// Recursive - veeery tricky! 
	private double minDist(int depth, int ndim, double[] focal, double[] other) {
		double dist=Double.MAX_VALUE;
		if ((upperBorderTypes[depth]==BorderType.wrap)&&
			(lowerBorderTypes[depth]==BorderType.wrap)) {
			double[] alt = other.clone();
			if (depth==ndim-1)
				dist = Math.min(dist,DynamicSpace.super.squaredEuclidianDistance(focal,alt));
			else
				dist = Math.min(dist,minDist(depth+1,ndim,focal,alt));
			alt[depth] = other[depth]-limits.sideLength(depth);
			if (depth==ndim-1)
				dist = Math.min(dist,DynamicSpace.super.squaredEuclidianDistance(focal,alt));
			else
				dist = Math.min(dist,minDist(depth+1,ndim,focal,alt));
			alt[depth] = other[depth]+limits.sideLength(depth);
			if (depth==ndim-1)
				dist = Math.min(dist,DynamicSpace.super.squaredEuclidianDistance(focal,alt));
			else
				dist = Math.min(dist,minDist(depth+1,ndim,focal,alt));
		}
		else
			if (depth==ndim-1)
				dist = Math.min(dist,DynamicSpace.super.squaredEuclidianDistance(focal,other));
			else
				dist = Math.min(dist,minDist(depth+1,ndim,focal,other));
		return dist;
	}
	
	@Override
	public double squaredEuclidianDistance(double[] focal, double[] other) {
 		return minDist(0,ndim(),focal,other);
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
					break;
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
					break;
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
	public final Box ObservationWindow() {
		return obsWindow;
	}

	@Override
	public final TwShape asShape() {
		return shape;
	}

}
