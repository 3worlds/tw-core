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

import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.SpaceData;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.tracking.SpaceDataTracker;
import au.edu.anu.twcore.rngFactory.RngFactory;
import au.edu.anu.twcore.ui.runtime.DataReceiver;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.identity.impl.ResettableLocalScope;
import fr.cnrs.iees.twcore.constants.EdgeEffects;
import fr.cnrs.iees.twcore.constants.RngAlgType;
import fr.cnrs.iees.twcore.constants.RngResetType;
import fr.cnrs.iees.twcore.constants.RngSeedSourceType;
import fr.cnrs.iees.uit.space.Box;
import fr.cnrs.iees.uit.space.Point;

/**
 * The base class for all space implementations in 3Worlds.
 *
 * @author Jacques Gignoux - 30 janv. 2020
 *
 */
public abstract class SpaceAdapter
		implements DynamicSpace<SystemComponent,LocatedSystemComponent> {

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
	private EdgeEffects correction;
	 /** A RNG available to descendants to create jitter around locations if needed */
	protected Random jitterRNG = RngFactory.newInstance("SpaceJitterRNG", 0, RngResetType.never,
			RngSeedSourceType.secure,RngAlgType.Pcg32).getRandom();
	/** list of SystemComponents to insert later */
	private List<LocatedSystemComponent> toInsert = new LinkedList<>();
	/** list of SystemComponents to delete later */
	private List<LocatedSystemComponent> toDelete = new LinkedList<>();
	/** list of initial SystemComponents */
	private Set<LocatedSystemComponent> initialComponents = new HashSet<>();
	/** mapping of cloned item to their initial components */
	private Map<String, LocatedSystemComponent> itemsToInitials = new HashMap<>();

	private boolean changed = false;

	public SpaceAdapter(Box box, double prec, String units, EdgeEffects ee, SpaceDataTracker dt, String proposedId) {
		super();
		limits = box;
		//	precision based on shortest side of plot - NB I think that's a mistake - cf below
//		precision = Math.max(prec,minimalPrecision)*Math.min(limits.sideLength(0),limits.sideLength(1));
		// absolute precision, i.e. in units of measurement.
		precision = Math.max(prec,minimalPrecision);
		this.units = units;
		correction = ee;
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

	@Override
	public final EdgeEffects edgeEffectCorrection() {
		return correction;
	}

	@Override
	public Location locate(SystemComponent focal, Point location) {
		return locate(focal,location.x(),location.y());
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
	public final void attachSimpleSpaceWidget(DataReceiver<SpaceData,Metadata> widget)  {
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
		toInsert.add(item);
	}

	@Override
	public final void removeItem(LocatedSystemComponent item) {
		toDelete.add(item);
	}

	@Override
	public final void effectChanges() {
		for (LocatedSystemComponent lsc:toDelete)
			unlocate(lsc.item());
		toDelete.clear();
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
	public String id() {
		return id.id();
	}

	@Override
	public boolean changed() {
		return changed;
	}

	@Override
	public void change() {
		changed = true;
	}

}
