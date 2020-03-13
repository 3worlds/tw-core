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

	public SpaceAdapter(Box box, double prec, String units, EdgeEffects ee, SpaceDataTracker dt) {
		super();
		limits = box;
// precision based on diagonal, but this is probably unexpected for users		
//		double boxdiag = Math.sqrt(limits.sideLength(0)*limits.sideLength(0)
//			+limits.sideLength(1)*limits.sideLength(1));
//		precision = Math.max(prec,minimalPrecision)*boxdiag;
//	precision based on shortest side of plot
		precision = Math.max(prec,minimalPrecision)*Math.min(limits.sideLength(0),limits.sideLength(1));
		this.units = units;
		correction = ee;
		dataTracker = dt;
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
	}
	
	
}
