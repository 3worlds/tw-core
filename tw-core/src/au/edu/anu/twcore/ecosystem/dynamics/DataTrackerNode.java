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
package au.edu.anu.twcore.ecosystem.dynamics;

import fr.cnrs.iees.omugi.graph.*;
import fr.cnrs.iees.omugi.graph.impl.TreeGraphNode;
import fr.cnrs.iees.omugi.identity.Identity;
import fr.cnrs.iees.omugi.properties.*;
import fr.cnrs.iees.omugi.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.rvgrid.rendezvous.GridNode;
import fr.cnrs.iees.twcore.constants.DataElementType;
import fr.cnrs.iees.twcore.constants.SamplingMode;
import fr.cnrs.iees.twcore.constants.StatisticalAggregatesSet;
import fr.cnrs.iees.omhtk.*;
import fr.cnrs.iees.omhtk.utils.*;

import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.get;

import java.util.*;
import java.util.logging.Logger;

import fr.cnrs.iees.omugi.collections.tables.*;
import au.edu.anu.omhtk.util.IntegerRange;
import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.data.*;
import au.edu.anu.twcore.data.runtime.*;
import au.edu.anu.twcore.ecosystem.ArenaType;
import au.edu.anu.twcore.ecosystem.runtime.DataTracker;
import au.edu.anu.twcore.ecosystem.runtime.containers.Described;
import au.edu.anu.twcore.ecosystem.runtime.system.*;
import au.edu.anu.twcore.ecosystem.runtime.tracking.*;
import au.edu.anu.twcore.ecosystem.structure.*;
import au.edu.anu.twcore.root.World;
import au.edu.anu.twcore.ui.TrackTableEdge;
import au.edu.anu.twcore.ui.runtime.DataReceiver;

/**
 * Class matching the
 * "ecosystem/dynamics/timeLine/timeModel/process/dataTracker" node label in the
 * 3Worlds configuration tree. Had many properties but needs refactoring.
 *
 * <p>Rules for {@code trackComponent} cross-links:</p>
 * <dl>
 * <dt>to  {@code component}:</dt>
 *  <dd>track this component and forget it if ephemeral. <br/>  {@code trackField} must be data from
 * this component's  {@code componentType}</dd>
 *  <dt>to  {@code group}:</dt>
 *  <dd> 
 *  <ul>
 *  <li>if  parent {@code ProcessNode} categories are all found in this group's {@code GroupType}:<br/>
 *  track this group variables</li>
 *  <li>otherwise:<br/>
 *  track  {@code sampleSize} components of this group according to sampling strategy</li>
 *  </ul>
 *  </dd>
 *  <dt>to  {@code GroupType}</dt>
 *  <dd>track {@code group}s of this  {@code GroupType} according to sampling strategy<br/>
 *  {@code trackField} must be data from this {@code GroupType}</dd>
 *  <dt>to system (arena):</dt>
 *  <dd>track data from the {@code ArenaComponent}; ignore sampling strategy as there is only one arena.
 *  {@code trackField} must be data from the {@code ArenaComponent}</dd>
 *  
 * </dl>
 *
 * @author Jacques Gignoux - 7 juin 2019
 *
 */
public class DataTrackerNode extends InitialisableNode
		implements LimitedEdition<DataTracker<?, ? extends Metadata>>, Sealable {

	private static Logger log = Logging.getLogger(DataTrackerNode.class);
	
	// a class to collect metadata on fields, ie min, max, precision, units etc.
	private class TrackMeta {
//		IndexedDataLabel label = null;
		DataLabel label = null;
		Class<?> trackType = null;
		Interval rrange = null;
		IntegerRange irange = null;
		Double prec = null;
		String units = null;
		List<int[]> index = new ArrayList<>();
	}

	private Map<Integer, DataTracker<?, Metadata>> dataTrackers = new HashMap<>();
	private boolean sealed = false;
	private SamplingMode selection = null;
	private int sampleSize = 0;
	private StatisticalAggregatesSet stats = null;
	private StatisticalAggregatesSet tstats = null;
//	private boolean viewOthers = false;
	private Object dataTrackerClass;
	private ExtendablePropertyList fieldMetadata = new ExtendablePropertyListImpl();
	// a map of all table dimensions
	private Map<String, int[]> tableDims = new HashMap<>();
	private Map<String, TrackMeta> expandedTrackList = new HashMap<>();
	// target objects of tracking: groups or systemComponents
	private List<Edge> trackedComponents = new ArrayList<>();
//	private List<Category> processCategories = null;
	private DataLabel fullTableLabel = null;

	public DataTrackerNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public DataTrackerNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	private int[] getTableDims(TableNode tab) {
		int[] dims = new int[tab.dimensioners().length];
		for (int i = 0; i < dims.length; i++)
			dims[i] = tab.dimensioners()[i].getLength();
		return dims;
	}

	// search a table for data types - assumes trackVar is the table name WITHOUT
	// index
	// (ie 'myTable', not 'myTable[0][12]')
	// cross-recursive with next method
	private TrackMeta findTrackMetadata(TableNode tab, String trackVar) {
		tableDims.put(tab.id(), getTableDims(tab));
		TrackMeta result = null;
		String tv = trackVar;

		// leaf table, ie with primitive elements
		// CAUTION: returns the type of the table elements, NOT the table type
		// (eg Boolean, not BooleanTable)
		if (tab.properties().hasProperty(P_DATAELEMENTTYPE.key())) {
			if (tab.id().equals(tv)) {
				result = new TrackMeta();
				DataElementType det = (DataElementType) tab.properties().getPropertyValue(P_DATAELEMENTTYPE.key());
				if (tab.properties().hasProperty(P_TABLE_UNITS.key()))
					result.units = (String) tab.properties().getPropertyValue(P_TABLE_UNITS.key());
				if (tab.properties().hasProperty(P_TABLE_INTERVAL.key()))
					result.rrange = (Interval) tab.properties().getPropertyValue(P_TABLE_INTERVAL.key());
				if (tab.properties().hasProperty(P_TABLE_RANGE.key()))
					result.irange = (IntegerRange) tab.properties().getPropertyValue(P_TABLE_RANGE.key());
				if (tab.properties().hasProperty(P_TABLE_PREC.key()))
					result.prec = (Double) tab.properties().getPropertyValue(P_TABLE_PREC.key());
				try {
					result.trackType = Class.forName(det.className());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		// table of records - must have exactly one child which is a record
		else
			for (TreeNode nn : tab.getChildren()) {
				result = findTrackMetadata((Record) nn, tv);
				if (result != null)
					break;
			}
		return result;
	}

	// cross-recursive with previous
	private TrackMeta findTrackMetadata(Record rec, String trackVar) {
		TrackMeta result = null;
		for (TreeNode n : rec.getChildren()) {
			if (n instanceof FieldNode) {
				if (n.id().equals(trackVar)) {
					FieldNode f = (FieldNode) n;
					result = new TrackMeta();
					if (f.properties().hasProperty(P_FIELD_UNITS.key()))
						result.units = (String) f.properties().getPropertyValue(P_FIELD_UNITS.key());
					if (f.properties().hasProperty(P_FIELD_RANGE.key()))
						result.irange = (IntegerRange) f.properties().getPropertyValue(P_FIELD_RANGE.key());
					if (f.properties().hasProperty(P_FIELD_INTERVAL.key()))
						result.rrange = (Interval) f.properties().getPropertyValue(P_FIELD_INTERVAL.key());
					if (f.properties().hasProperty(P_FIELD_PREC.key()))
						result.prec = (Double) f.properties().getPropertyValue(P_FIELD_PREC.key());
					DataElementType det = (DataElementType) f.properties().getPropertyValue(P_FIELD_TYPE.key());
					try {
						result.trackType = Class.forName(det.className());
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			} else if (n instanceof TableNode)
				result = findTrackMetadata((TableNode) n, trackVar);
			if (result != null)
				break;
		}
		return result;
	}

	// recursive
	@SuppressWarnings("unchecked")
	private TrackMeta findTrackMetadata(Category cat, String trackVar) {
		TrackMeta result = null;
		List<Record> lr = (List<Record>) get(cat.edges(Direction.OUT),
			selectZeroOrMany(orQuery(hasTheLabel(E_DRIVERS.label()),
				hasTheLabel(E_DECORATORS.label()),
				hasTheLabel(E_AUTOVAR.label()))),
			edgeListEndNodes());
		if (lr != null)
			for (Record r : lr) { // this list may contain at most 2 items
				result = findTrackMetadata((Record) r, trackVar);
				if (result != null)
					break;
			}
		return result;
	}

	private String stripVarName(DataLabel lab) {
		String trackName = lab.getEnd();
		if (trackName.contains("["))
			trackName = trackName.substring(0, trackName.indexOf('['));
		trackName = trackName.replaceAll("\\|", "");
		return trackName;
	}

	private DataLabel getFullVarName(TreeNode var, StringTable index) {
		DataLabel result = new DataLabel();
		Deque<String> l = new LinkedList<>();
		TreeNode parent = var;
		int i = 0;
		if (index != null)
			i = index.size() - 1;
		while (parent != null) {
			if (parent instanceof FieldNode)
				l.add(parent.id());
			else if (parent instanceof TableNode) {
				if (index != null) {
					if (index.getWithFlatIndex(i) != null) {
						l.add(parent.id() + index.getWithFlatIndex(i));
					}else
						l.add(parent.id());
					i--;
				}
				else
					l.add(parent.id());
			}
			parent = parent.getParent();
		}
		Iterator<String> it = l.descendingIterator();
		while (it.hasNext())
			result.append(it.next());
		return result;
	}
	
	/**
	 * Returns true if the datalabel passed as argument as a leaf index indicating a full table.
	 * 
	 * @param unexpanded
	 * @return
	 */
	private boolean trackFullLeafTable(DataLabel unexpanded,TableNode table) {
//		String s1 = (String) properties().getPropertyValue(P_DATATRACKER_SUBCLASS.key());
//		String s2 = DataTracker0D.class.getName();
//		return !s2.equals(s1);
		String end = unexpanded.getEnd();
		if (end.contains("["))
			end = end.substring(end.indexOf('['));
		else
			end = "";
		int[] dimensions = getTableDims(table);
		IntegerRange[] res = IndexString.stringIndexRanges(end, dimensions);
		for (int i=0; i<res.length; i++)
			if ((res[i].getFirst()!=0)||(res[i].getLast()!=dimensions[i]-1))
				return false;
		return true;
	}

	@SuppressWarnings("unchecked")
	private void setupComponentTracker() {
		// list of categories or relations the process applies to
		List<Node> ln = (List<Node>) get(getParent().edges(Direction.OUT),
			selectOneOrMany(hasTheLabel(E_APPLIESTO.label())),
			edgeListEndNodes());
		
		// variables to track
		if (!ln.isEmpty())
			// relation variables
			if (ln.get(0) instanceof RelationType) {
				// TODO: implement code for relation data trackers
				// when there are data in relations (not yet)
				log.warning("DataTrackers cannot track relation data so far. Request to track the '"
					+ln.get(0).id()+"' RelationType ignored.");
			}
			// category variables
			else {
				List<Edge> le = (List<Edge>) get(edges(Direction.OUT),
					selectOneOrMany(orQuery(hasTheLabel(E_TRACKFIELD.label()),hasTheLabel(E_TRACKTABLE.label()))));
				SortedMap<String, TrackMeta> fm = new TreeMap<>();
				for (Node n : ln) {
					// 1 - get all the info where indexing and full label is not needed (tables)
					for (Edge e : le) {
						String trackName = e.endNode().id();
						if (!fm.containsKey(trackName)) {
							TrackMeta tt = findTrackMetadata((Category) n, trackName);
							if (tt != null)
								fm.put(trackName, tt);
							else
								; // throw Exception ? this should never happen normally...
						}
					}
				}
				// 2 - expand indexes and develop full labels and store above information
				for (Edge e : le) {
					DataLabel unexpanded = getFullVarName((TreeNode) e.endNode(), (StringTable) ((ReadOnlyDataHolder) e)
						.properties().getPropertyValue(P_TRACKEDGE_INDEX.key()));
					String dtc = (String) properties().getPropertyValue(P_DATATRACKER_SUBCLASS.key());
					// this only for the leaf table of dataTracker2Ds 
					if ((e instanceof TrackTableEdge)&&
						(DataTracker2D.class.getName().equals(dtc))&&
						(trackFullLeafTable(unexpanded,(TableNode)e.endNode()))) {
							String last = unexpanded.getEnd();
							if (last.contains("["))
								last = last.substring(0,last.indexOf('['));
							fullTableLabel = unexpanded;
							fullTableLabel.stripEnd();
							fullTableLabel.append(last);
							TrackMeta tmm = fm.get(fullTableLabel.toString());
							expandedTrackList.put(fullTableLabel.toString(), tmm);
					}
					else {
						List<IndexedDataLabel> labels = IndexedDataLabel.expandIndexes(unexpanded, tableDims);
						// now there is one label for each index combination
						for (IndexedDataLabel l : labels) {
							String trackName = stripVarName(l);
							TrackMeta tm = new TrackMeta();
							tm.label = l; // the index is in the label, now!
							TrackMeta tmm = fm.get(trackName);
							tm.irange = tmm.irange;
							tm.rrange = tmm.rrange;
							tm.prec = tmm.prec;
							tm.trackType = tmm.trackType;
							tm.units = tmm.units;
							// this contains in the proper order all the indexes needed to read the data
							for (int j = 0; j < l.size(); j++)
								if (l.getIndex(j) != null)
									tm.index.add(l.getIndex(j));
							expandedTrackList.put(l.toString(), tm);
						}
					}
				}
				// 3 store results into fieldMetadata
				for (String trackName : expandedTrackList.keySet()) {
					TrackMeta tm = expandedTrackList.get(trackName);
					setFieldMetadata(tm, trackName);
				}
			}
		
		// Objects to track
		trackedComponents.addAll((List<Edge>)get(edges(Direction.OUT),
			selectZeroOrMany(orQuery(hasTheLabel(E_SAMPLECOMPONENT.label()),hasTheLabel(E_SAMPLEGROUP.label()),
									hasTheLabel(E_SAMPLELIFECYCLE.label()),hasTheLabel(E_SAMPLEARENA.label())) ) ));
	}

	private void setFieldMetadata(TrackMeta tm, String trackName) {
		trackName += ".";
		if (!fieldMetadata.hasProperty(trackName + P_FIELD_TYPE.key()))
			fieldMetadata.addProperty(trackName + P_FIELD_TYPE.key(), tm.trackType);
		if (!fieldMetadata.hasProperty(trackName + P_FIELD_LABEL.key()))
			fieldMetadata.addProperty(trackName + P_FIELD_LABEL.key(), tm.label);
		if (tm.units != null)
			if (!fieldMetadata.hasProperty(trackName + P_FIELD_UNITS.key()))
				fieldMetadata.addProperty(trackName + P_FIELD_UNITS.key(), tm.units);
		if (tm.irange != null)
			if (!fieldMetadata.hasProperty(trackName + P_FIELD_RANGE.key()))
				fieldMetadata.addProperty(trackName + P_FIELD_RANGE.key(), tm.irange);
		if (tm.rrange != null)
			if (!fieldMetadata.hasProperty(trackName + P_FIELD_INTERVAL.key()))
				fieldMetadata.addProperty(trackName + P_FIELD_INTERVAL.key(), tm.rrange);
		if (tm.prec != null)
			if (!fieldMetadata.hasProperty(trackName + P_FIELD_PREC.key()))
				fieldMetadata.addProperty(trackName + P_FIELD_PREC.key(), tm.prec);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		if (!sealed) {
			super.initialise();
			// record process categories (= trackable data)
			// NB only category processes can have a data tracker. Hence:
//			processCategories = (List<Category>) get(getParent().edges(Direction.OUT),
//				selectOneOrMany(hasTheLabel(E_APPLIESTO.label())),
//				edgeListEndNodes());
			// required properties
			if (properties().hasProperty(P_DATATRACKER_SAMPLESIZE.key())) {
				String s = (String) properties().getPropertyValue(P_DATATRACKER_SAMPLESIZE.key());
				if (s.isBlank())
					s = "ALL";
				if (s.equals("ALL"))
					sampleSize = -1;
				else
					sampleSize = Integer.valueOf(s);
			} 
			else
				sampleSize = -1;
			// optional properties
			if (properties().hasProperty(P_DATATRACKER_SELECT.key()))
				selection = (SamplingMode) properties().getPropertyValue(P_DATATRACKER_SELECT.key());
			if (properties().hasProperty(P_DATATRACKER_STATISTICS.key()))
				stats = (StatisticalAggregatesSet) properties().getPropertyValue(P_DATATRACKER_STATISTICS.key());
			if (properties().hasProperty(P_DATATRACKER_TABLESTATS.key()))
				tstats = (StatisticalAggregatesSet) properties().getPropertyValue(P_DATATRACKER_TABLESTATS.key());
			List<Edge> ll = (List<Edge>) get(edges(Direction.OUT),
				selectZeroOrMany(orQuery(hasTheLabel(E_TRACKFIELD.label()),
				hasTheLabel(E_TRACKTABLE.label()))));
			if (!ll.isEmpty())
				setupComponentTracker();
			// the type of tracker
			dataTrackerClass = properties().getPropertyValue(P_DATATRACKER_SUBCLASS.key());
			sealed = true;
		}
	}

	@Override
	public int initRank() {
		return N_DATATRACKER.initRank();
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

	// CAUTION: This method assumes the Arena containers and components have been initialised
	// BEFORE the data trackers.
	// which is TRUE! (debugging 27/5/2022) when here, arena has all the containers properly setup
	@SuppressWarnings({ "unchecked" })
	private DataTracker<?, ?> makeDataTracker(int index) {
		AbstractDataTracker<?, ?> result = null;
		List<CategorizedComponent> ls = new ArrayList<>();
		DescribedContainer<? extends CategorizedComponent> samplingPool = null;
		boolean permanent = true;
		// get the arena to search for containers.
//		TreeNode root = World.getRoot(this);
//		ArenaType anode = (ArenaType) get(root,children(),selectOne(hasTheLabel(N_SYSTEM.label())));
		ArenaType anode = (ArenaType)World.getSystemRoot(this);
		ArenaComponent arena = anode.getInstance(index).getInstance();
		// NB trackedComponents now contains a list of trackComponent Edges to ElementType objects.
		for (Edge etrack:trackedComponents) {
			TreeGraphNode etype = (TreeGraphNode)etrack.endNode();
			// tracking the arena - easy
			if (etype instanceof ArenaType)
				ls.add((CategorizedComponent)((ArenaType)etype).getInstance(index).getInstance());
			// tracking components - means a sample of them
			else if (etype instanceof ComponentType) {
				if (etype.getParent() instanceof Structure) {
					// no group, no lifecycle - container = arena
					ls.addAll(arena.content().getInitialItems());
					samplingPool = arena.content();
					permanent = ((ComponentType)etype).isPermanent();
				}
				else if (etype.getParent() instanceof GroupType) {
					String theGroup = (String) ((ReadOnlyDataHolder)etrack).properties()
						.getPropertyValue(P_DATASOURCE_IDGROUP.key());
					if (etype.getParent().getParent() instanceof LifeCycleType) {
						String theLC = (String) ((ReadOnlyDataHolder)etrack).properties()
							.getPropertyValue(P_DATASOURCE_IDLC.key());
						CategorizedContainer<SystemComponent> theLCCont = arena.content().findContainer(theLC);
						samplingPool = (DescribedContainer<? extends CategorizedComponent>) theLCCont.findContainer(theGroup);
					}
					else
						samplingPool = (DescribedContainer<? extends CategorizedComponent>) 
							arena.content().findContainer(theGroup);
					ls.addAll(samplingPool.getInitialItems()); // ??? check this
				}
			}
			// tracking a group - means permanent GroupComponents
			else if (etype instanceof GroupType) {
				StringTable theGroups = (StringTable) ((ReadOnlyDataHolder)etrack).properties()
					.getPropertyValue(P_DATASOURCE_IDGROUP.key()); // always present
				if (etype.getParent() instanceof Structure)
					// no lifecycle - container = arena
					for (int i=0; i<theGroups.size(); i++) {
						CategorizedContainer<SystemComponent> theCont = arena.content().findContainer(theGroups.getWithFlatIndex(i));
						if (theCont!=null)
							ls.add(((Described<HierarchicalComponent>)theCont).descriptors());
					}
				else if (etype.getParent() instanceof LifeCycleType) {
					// container is a lifeCycle.
					for (CategorizedContainer<SystemComponent> cont:arena.content().subContainers()) {
						for (int i=0; i<theGroups.size(); i++) {
							CategorizedContainer<SystemComponent> theCont = cont.findContainer(theGroups.getWithFlatIndex(i));
							if (theCont!=null)
								ls.add(((Described<HierarchicalComponent>)theCont).descriptors());
						}
					}
				}
			}
			// tracking a life cycle - means permanent LifeCycleComponents
			else if (etype instanceof LifeCycleType) {
				StringTable theLCs = (StringTable) ((ReadOnlyDataHolder)etrack).properties()
					.getPropertyValue(P_DATASOURCE_IDLC.key()); // always present
				for (int i=0; i<theLCs.size(); i++) {
					CategorizedContainer<SystemComponent> theCont = arena.content().findContainer(theLCs.getWithFlatIndex(i));
					if (theCont!=null)
						ls.add(((Described<HierarchicalComponent>)theCont).descriptors());
				}
			}
		}
		if (dataTrackerClass.equals(DataTracker0D.class.getName())) {
			if (samplingPool!=null)
				result = new DataTracker0D(index,stats, tstats, selection, sampleSize,
					((DescribedContainer<CategorizedComponent>) samplingPool).itemListPointerUseWithCaution(),
					samplingPool.fullId(),permanent,
					ls,expandedTrackList.keySet(),fieldMetadata);
			else
				result = new DataTracker0D(index,stats, tstats, selection, sampleSize,
					null, null, permanent, ls, expandedTrackList.keySet(),fieldMetadata);
		}
		else if (dataTrackerClass.equals(DataTracker2D.class.getName())) {
			// JG 19/5/2022 - THIS iS WRONG!
			// trackfield/tracktable should be replaced by trackVar trackMap, ie edge type depends on 
			// data tracker type.
			// because in the general case a table may be within a record and reciproc.
			// so if 0D tracker : trackvar and search for indices etc, leaf track value is a single field or table cell
			// if 2D tracker same, but leaf track value is (1) a 2D table or (2) a field in a 2D table
			// That's the ideal case.
			// Temp. fix: impose that 2D datatracker can only track a plain 2D table with no fields
			// --- flawed code hereafter ---
			// This happens only if trackTable was selected
			// trackTable has multiplicity 0..1 after dynamics.utg
			// to there must be only one item in the tableDims map.
			// and since this is a 2D map it must have exactly two dimensions
			int[] ix = tableDims.get(fullTableLabel.toString());
			// the line below creates indexing miss-matches from time to time.
			//int[] ix = tableDims.values().iterator().next();
//			System.out.println("-----------");
//			for (Map.Entry<String,int[]> e:tableDims.entrySet()){
//				System.out.print(e.getKey()+"= ");
//				for (int i=0;i<e.getValue().length;i++)
//					System.out.print(e.getValue()[i]+",");
//				System.out.println();		
//			}
//			System.out.println("pre-construct: "+fullTableLabel+" "+ix[0]+","+ix[1]);	

			result = new DataTracker2D(index, selection, sampleSize, null,ls, 
				expandedTrackList.keySet(), fieldMetadata, ix[0], ix[1], fullTableLabel);
		}
		else if (dataTrackerClass.equals(DataTrackerXY.class.getName())) {
			if (samplingPool!=null)
				result = new DataTrackerXY(index,selection,
					((DescribedContainer<CategorizedComponent>) samplingPool).itemListPointerUseWithCaution(), 
					ls,expandedTrackList.keySet(),fieldMetadata);
			else
				result = new DataTrackerXY(index,selection, null,ls,expandedTrackList.keySet(),
					fieldMetadata);
		}
		return result;
	}

	// CAUTION: this method assumes that the widgets have been instantiated AFTER
	// the DataTrackers
	/**
	 * attach time series widgets to these trackers
	 *
	 * @param widget
	 */
	@SuppressWarnings("unchecked")
	public void attachWidget(DataReceiver<?,Metadata> widget) {
		for (DataTracker<?, Metadata> dt : dataTrackers.values()) {
			if (dt instanceof DataTracker0D)
				((DataTracker0D) dt).addObserver((DataReceiver<Output0DData, Metadata>) widget);
			else if (dt instanceof DataTrackerXY)
				((DataTrackerXY) dt).addObserver((DataReceiver<OutputXYData, Metadata>) widget);
			else if (dt instanceof DataTracker2D)
				((DataTracker2D) dt).addObserver((DataReceiver<Output2DData, Metadata>) widget);
			dt.sendMetadataTo((GridNode)widget,dt.getInstance());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public DataTracker<?, Metadata> getInstance(int id) {
		if (!sealed)
			initialise();
		if (!dataTrackers.containsKey(id))
			dataTrackers.put(id, (DataTracker<?, Metadata>) makeDataTracker(id));
		return dataTrackers.get(id);
	}

}
