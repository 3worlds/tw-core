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

import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.ExtendablePropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.rvgrid.rendezvous.GridNode;
import fr.cnrs.iees.twcore.constants.DataElementType;
import fr.cnrs.iees.twcore.constants.SamplingMode;
import fr.cnrs.iees.twcore.constants.StatisticalAggregatesSet;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;
import fr.ens.biologie.generic.utils.Interval;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.util.IntegerRange;
import au.edu.anu.twcore.DefaultStrings;
import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.data.FieldNode;
import au.edu.anu.twcore.data.Record;
import au.edu.anu.twcore.data.TableNode;
import au.edu.anu.twcore.data.runtime.DataLabel;
import au.edu.anu.twcore.data.runtime.IndexedDataLabel;
import au.edu.anu.twcore.data.runtime.Output2DData;
import au.edu.anu.twcore.data.runtime.OutputXYData;
import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.Output0DData;
import au.edu.anu.twcore.ecosystem.ArenaType;
import au.edu.anu.twcore.ecosystem.dynamics.initial.Component;
import au.edu.anu.twcore.ecosystem.runtime.DataTracker;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.HierarchicalComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.tracking.AbstractDataTracker;
import au.edu.anu.twcore.ecosystem.runtime.tracking.DataTracker2D;
import au.edu.anu.twcore.ecosystem.runtime.tracking.DataTrackerXY;
import au.edu.anu.twcore.ecosystem.runtime.tracking.DataTracker0D;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import au.edu.anu.twcore.ui.runtime.DataReceiver;

/**
 * Class matching the
 * "ecosystem/dynamics/timeLine/timeModel/process/dataTracker" node label in the
 * 3Worlds configuration tree. Had many properties but needs refactoring.
 *
 * @author Jacques Gignoux - 7 juin 2019
 *
 */
// NB: now (5/5/2020) population trackers are not needed anymore since populationdata are now in
// categorizedComponents

public class DataTrackerNode extends InitialisableNode
		implements LimitedEdition<DataTracker<?, ? extends Metadata>>, Sealable, DefaultStrings {

	// a class to collect metadata on fields, ie min, max, precision, units etc.
	private class TrackMeta {
		IndexedDataLabel label = null;
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
//	private Grouping grouping = null;
	private StatisticalAggregatesSet stats = null;
	private StatisticalAggregatesSet tstats = null;
//	private boolean viewOthers = false;
	private Object dataTrackerClass;
//	private StringTable track = null;
	private ExtendablePropertyList fieldMetadata = new ExtendablePropertyListImpl();
	// a map of all table dimensions
	private Map<String, int[]> tableDims = new HashMap<>();
	private Map<String, TrackMeta> expandedTrackList = new HashMap<>();
	// target objects of tracking: groups or systemComponents
//	private List<LimitedEdition<ComponentContainer>> trackedGroups = new ArrayList<>();
//	private List<Component> trackedComponents = new ArrayList<>();
	private List<TreeGraphNode> trackedComponents = new ArrayList<>();
//	private boolean groupTracker;
//	private InitialState ecosystemContainer = null;

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
				if (index != null)
					l.add(parent.id() + index.getWithFlatIndex(i--));
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

	@SuppressWarnings("unchecked")
	private void setupComponentTracker() {
//		groupTracker = false;
		// list of categories or relations the process applies to
		List<Node> ln = (List<Node>) get(getParent().edges(Direction.OUT),
			selectOneOrMany(hasTheLabel(E_APPLIESTO.label())),
			edgeListEndNodes());
		// get all the variables to track
		if (!ln.isEmpty())
			// relation variables
			if (ln.get(0) instanceof RelationType) {
				// TODO: implement code for relation data trackers
				// NO: we dont trak anything from a relation process
				// unless maybe one day there are relation data to track
			}
			// category variables
			else {
				List<Edge> le = (List<Edge>) get(edges(Direction.OUT),
					selectOneOrMany(orQuery(hasTheLabel(E_TRACKFIELD.label()),hasTheLabel(E_TRACKTABLE.label()))));
				SortedMap<String, TrackMeta> fm = new TreeMap<>();
				for (Node n : ln) {
//					if (n instanceof Category) {
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
				// 3 store results into fieldMetadata
				for (String trackName : expandedTrackList.keySet()) {
					TrackMeta tm = expandedTrackList.get(trackName);
					setFieldMetadata(tm, trackName);
				}
			}
//		for (Node n : ln) {
//			if (n instanceof RelationType) {
//				// TODO: implement code for relation data trackers
//				// NO: we dont trak anything from a relation process
//				// unless maybe one day there are relation data tro track
//			} else ;
//		}
		// get the initial components to track
		// NB: this list either contains initial components OR points to a single group
		// to be tracked
		// CAUTION: this is adding the INITIAL components, not the RUNTIME ones
		List<Edge> ll = (List<Edge>) get(edges(Direction.OUT),
			selectZeroOrMany(hasTheLabel(E_TRACKCOMPONENT.label())));
		for (Edge e : ll) {
			if (e.endNode() instanceof ArenaType) {
				ArenaType ar = (ArenaType) ll.get(0).endNode();
				trackedComponents.add(ar);
			}
			// this is all dead now!
//			if (ll.get(0).endNode() instanceof Component) {
//				trackedComponents.add((Component) ll.get(0).endNode());
//				LimitedEdition<ComponentContainer> tgroup =
//					(LimitedEdition<ComponentContainer>) trackedComponents.get(0).getParent();
//				if (tgroup instanceof InitialState)
//					ecosystemContainer = (InitialState) tgroup;
//				else
//					trackedGroups.add(tgroup);
//			} else
//				trackedGroups.add((LimitedEdition<ComponentContainer>) ll.get(0).endNode());
			// end dead code
			else if (e.endNode() instanceof Component)
				trackedComponents.add((Component) e.endNode());
//			trackedGroups.add((LimitedEdition<ComponentContainer>) trackedComponents.get(0).getParent());
		}
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

//	@SuppressWarnings("unchecked")
//	private void setupPopulationTracker() {
//		groupTracker = true;
//		List<Edge> trackedEdges = (List<Edge>) get(edges(Direction.OUT),
//				selectZeroOrMany(hasTheLabel(E_TRACKPOP.label())));
//		for (Edge e : trackedEdges) {
//			if (e instanceof ReadOnlyDataHolder) {
//				ReadOnlyDataHolder rodh = (ReadOnlyDataHolder) e;
//				if (rodh.properties().hasProperty(P_TRACKPOP_VAR.key())) {
//					for (PopulationVariables pv : ((PopulationVariablesSet) rodh.properties()
//							.getPropertyValue(P_TRACKPOP_VAR.key())).values()) {
//						TrackMeta tm = new TrackMeta();
//						tm.label = new IndexedDataLabel(pv.shortName());
//						tm.irange = IntegerRange.valueOf(pv.range());
//						try {
//							tm.trackType = Class.forName(pv.type());
//						} catch (ClassNotFoundException e1) {
//							e1.printStackTrace();
//						}
//						tm.units = pv.units();
//						setFieldMetadata(tm, pv.shortName());
//						expandedTrackList.put(tm.label.toString(), tm);
//					}
//				}
//			}
//			LimitedEdition<ComponentContainer> group = (LimitedEdition<ComponentContainer>) e.endNode();
//			trackedGroups.add(group);
//		}
//	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		if (!sealed) {
			super.initialise();
			// optional properties - if absent take default value
			if (properties().hasProperty(P_DATATRACKER_SELECT.key()))
				selection = (SamplingMode) properties().getPropertyValue(P_DATATRACKER_SELECT.key());
			else
				selection = SamplingMode.defaultValue();
			if (properties().hasProperty(P_DATATRACKER_SAMPLESIZE.key())) {
				String s = (String) properties().getPropertyValue(P_DATATRACKER_SAMPLESIZE.key());
				if (s.isBlank())
					s = "ALL";
				if (s.equals("ALL"))
					sampleSize = -1;
				else
					sampleSize = Integer.valueOf(s);
			} else
				sampleSize = -1;
			if (properties().hasProperty(P_DATATRACKER_STATISTICS.key()))
				stats = (StatisticalAggregatesSet) properties().getPropertyValue(P_DATATRACKER_STATISTICS.key());
			else
				stats = StatisticalAggregatesSet.defaultValue();
			if (properties().hasProperty(P_DATATRACKER_TABLESTATS.key()))
				tstats = (StatisticalAggregatesSet) properties().getPropertyValue(P_DATATRACKER_TABLESTATS.key());
			else
				tstats = StatisticalAggregatesSet.defaultValue();
			List<Edge> ll = (List<Edge>) get(edges(Direction.OUT),
				selectZeroOrMany(orQuery(hasTheLabel(E_TRACKFIELD.label()),
				hasTheLabel(E_TRACKTABLE.label()))));
			if (!ll.isEmpty())
				setupComponentTracker();
			// population tracker - now deprecated
//			ll = (List<Edge>) get(edges(Direction.OUT),
//				selectZeroOrMany(hasTheLabel(E_TRACKPOP.label())));
//			if (!ll.isEmpty())
//				setupPopulationTracker();
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

	@SuppressWarnings("unchecked")
	private DataTracker<?, ?> makeDataTracker(int index) {
		AbstractDataTracker<?, ?> result = null;
		List<CategorizedComponent> ls = new ArrayList<>();
		CategorizedContainer<? extends CategorizedComponent> trackedContainer = null;
		for (TreeGraphNode etype:trackedComponents) {
			if (etype instanceof ArenaType) {
				trackedContainer = null;
				ls.add((CategorizedComponent)((ArenaType)etype).getInstance(index).getInstance());
			}
			else if (etype instanceof Component) {
				List<? extends CategorizedComponent<?>> cp = ((Component)etype).getInstance(index);
				if (!cp.isEmpty()) {
//					System.out.println(cp instanceof SystemComponent);
//					System.out.println(cp.get(0) instanceof SystemComponent);
					ls.addAll(cp);
//					if (cp.get(0) instanceof SystemComponent)
//						trackedContainer = ((SystemComponent) cp).container();
					if (cp.get(0) instanceof SystemComponent)
						trackedContainer = ((SystemComponent) cp.get(0)).container();
					else if (cp.get(0) instanceof HierarchicalComponent)
						// TODO: check this one
						trackedContainer = ((HierarchicalComponent) cp.get(0)).content().parentContainer();
//					trackedContainer = ((HierarchicalComponent) cp).content().parentContainer();
				}
			}
		}
		if (dataTrackerClass.equals(DataTracker0D.class.getName()))
			result = new DataTracker0D(index,stats, tstats, selection, sampleSize,
				(CategorizedContainer<CategorizedComponent>) trackedContainer, ls,
				expandedTrackList.keySet(),fieldMetadata);
		else if (dataTrackerClass.equals(DataTracker2D.class.getName())) {
			result = new DataTracker2D(index);
		}
		else if (dataTrackerClass.equals(DataTrackerXY.class.getName()))
			result = new DataTrackerXY(index,selection,
				(CategorizedContainer<CategorizedComponent>) trackedContainer, ls,
				expandedTrackList.keySet(),fieldMetadata);
		// TODO: remove senderId and put it in constructor
//		if (result != null)
//			result.setSender(index);
		return result;
	}
// old code
//	private DataTracker<?, ?> makeDataTracker(int index) {
//		AbstractDataTracker<?, ?> result = null;
//		if (dataTrackerClass.equals(DataTracker0D.class.getName())) {
//			List<ComponentContainer> lsc = new ArrayList<ComponentContainer>();
//			if (ecosystemContainer != null) {
//				// assuming only 1 component is tracked!
//				SystemComponent sc = trackedComponents.get(0).getInstance(index);
//				String gname = defaultPrefix + "group" + nameSeparator + sc.membership().categoryId();
//				lsc.add((ComponentContainer) ecosystemContainer.getInstance(index).subContainer(gname));
//			} else
//				for (LimitedEdition<ComponentContainer> group : trackedGroups)
//					lsc.add(group.getInstance(index));
//			List<SystemComponent> ls = new ArrayList<SystemComponent>();
//			for (Component c : trackedComponents)
//				ls.add(c.getInstance(index));
//			result = new DataTracker0D(stats, tstats, selection, sampleSize, lsc, ls, expandedTrackList.keySet(),
//					fieldMetadata, groupTracker);
//		} else if (dataTrackerClass.equals(DataTracker2D.class.getName())) {
//			result = new DataTracker2D();
//		}
//		if (result != null)
//			result.setSender(index);
//		return result;
//	}


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

//	public void attachTimeSeriesWidget(DataReceiver<Output0DData, Metadata> widget) {
//		for (DataTracker<?, Metadata> dt : dataTrackers.values())
//			if (dt instanceof DataTracker0D) {
//				((DataTracker0D) dt).addObserver(widget);
//				dt.sendMetadataTo((GridNode) widget, (Metadata) dt.getInstance());
//			}
//	}
//
//	public void attachMapWidget(DataReceiver<Output2DData, Metadata> widget) {
//		for (DataTracker<?, Metadata> dt : dataTrackers.values())
//			if (dt instanceof DataTracker2D) {
//				((DataTracker2D) dt).addObserver(widget);
//				dt.sendMetadataTo((GridNode) widget, (Metadata) dt.getInstance());
//			}
//	}
//
//	public void attachXYPlotWidget(DataReceiver<OutputXYData, Metadata> widget) {
//		for (DataTracker<?, Metadata> dt : dataTrackers.values())
//			if (dt instanceof DataTrackerXY) {
//				((DataTrackerXY) dt).addObserver(widget);
//				dt.sendMetadataTo((GridNode) widget, (Metadata) dt.getInstance());
//			}
//	}

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
