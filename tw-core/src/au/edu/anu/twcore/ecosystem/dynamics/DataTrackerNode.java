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
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.ExtendablePropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.twcore.constants.DataElementType;
import fr.cnrs.iees.twcore.constants.Grouping;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.edu.anu.rscs.aot.collections.tables.Dimensioner;
import au.edu.anu.rscs.aot.collections.tables.IndexString;
import au.edu.anu.rscs.aot.collections.tables.ObjectTable;
import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.util.IntegerRange;
import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.data.DimNode;
import au.edu.anu.twcore.data.Field;
import au.edu.anu.twcore.data.Record;
import au.edu.anu.twcore.data.TableNode;
import au.edu.anu.twcore.data.runtime.LabelValuePairData;
import au.edu.anu.twcore.data.runtime.MapData;
import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.TimeSeriesData;
import au.edu.anu.twcore.ecosystem.runtime.DataTracker;
import au.edu.anu.twcore.ecosystem.runtime.tracking.AbstractDataTracker;
import au.edu.anu.twcore.ecosystem.runtime.tracking.LabelValuePairTracker;
import au.edu.anu.twcore.ecosystem.runtime.tracking.MapTracker;
import au.edu.anu.twcore.ecosystem.runtime.tracking.TimeSeriesTracker;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import au.edu.anu.twcore.ui.runtime.DataReceiver;

/**
 * Class matching the "ecosystem/dynamics/timeLine/timeModel/process/dataTracker" node label in the 
 * 3Worlds configuration tree. Had many properties but needs refactoring.
 *  
 * @author Jacques Gignoux - 7 juin 2019
 *
 */
public class DataTrackerNode 
		extends InitialisableNode 
		implements LimitedEdition<DataTracker<?,?>>, Sealable {
	
	// a class to collect metadata on fields, ie min, max, precision, units etc.
	private class TrackMeta {
		Class<?> trackType = null;
		Interval rrange = null;
		IntegerRange irange = null;
		Double prec = null;
		String units = null;
		int[][] indexes = null;
	}

	private Map<Integer, DataTracker<?,?>> dataTrackers = new HashMap<>();
	private boolean sealed = false;
	private SamplingMode selection = null;
	private Grouping grouping = null;
	private StatisticalAggregatesSet stats = null;
	private StatisticalAggregatesSet tstats = null;
	private boolean viewOthers = false;
	private Object dataTrackerClass;
	private StringTable track = null;
	private ObjectTable<Class<?>> trackTypes = null;
	private ExtendablePropertyList fieldMetadata = new ExtendablePropertyListImpl();

	public DataTrackerNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public DataTrackerNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}
	
	// search a table for data types - assumes trackVar is the table name WITHOUT index
	// (ie 'myTable', not 'myTable[0][12]') 
	// cross-recursive with next method
	@SuppressWarnings("unchecked")
	private TrackMeta findTrackMetadata(TableNode tab, String trackVar) {
		TrackMeta result = null;
		String tv = trackVar;
		String is = "";
		if (trackVar.contains("[")) {
			tv = trackVar.substring(0,trackVar.indexOf('['));
			is = trackVar.substring(trackVar.indexOf('['));
		}
		// leaf table, ie with primitive elements
		// CAUTION: returns the type of the table elements, NOT the table type
		// (eg Boolean, not BooleanTable)
		if (tab.properties().hasProperty(P_DATAELEMENTTYPE.key())) {
			if (tab.id().equals(tv)) {
				result = new TrackMeta();
				List<DimNode> dimns = (List<DimNode>) get(tab.edges(Direction.OUT),
					selectOneOrMany(hasTheLabel(E_SIZEDBY.label())),
					edgeListEndNodes());
				int [] dims = new int[dimns.size()];
				int j=0;
				for (DimNode dim:dimns)
					dims[j++] = (int) dim.properties().getPropertyValue(P_DIMENSIONER_SIZE.key());
				result.indexes = IndexString.stringToIndex(is,dims);
				DataElementType det = (DataElementType) tab.properties().getPropertyValue(P_DATAELEMENTTYPE.key());
				if (tab.properties().hasProperty(P_TABLE_UNITS.key()))
					result.units = (String) tab.properties().getPropertyValue(P_TABLE_UNITS.key());
				if (tab.properties().hasProperty(P_TABLE_RANGE.key()))
					switch(det) {
					case Double: case Float:
						result.rrange = (Interval) tab.properties().getPropertyValue(P_TABLE_RANGE.key());
						break;
					case Integer: case Long: case Short: case Byte:
						result.irange = (IntegerRange) tab.properties().getPropertyValue(P_TABLE_RANGE.key());
						break;
					default:
						break;
					}
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
		else for (TreeNode nn:tab.getChildren()) {
			result = findTrackMetadata((Record)nn,tv);
			if (result!=null)
				break;
		}
		return result;
	}
	
	// cross-recursive with previous
	private TrackMeta findTrackMetadata(Record rec, String trackVar) {
		TrackMeta result = null;
		for (TreeNode n:rec.getChildren()) {
			if (n instanceof Field) {
				if (n.id().equals(trackVar)) {
					Field f = (Field) n;
					result = new TrackMeta();
					DataElementType det = (DataElementType)f.properties().getPropertyValue(P_FIELD_TYPE.key());
					if (f.properties().hasProperty(P_FIELD_UNITS.key()))
						result.units = (String) f.properties().getPropertyValue(P_FIELD_UNITS.key());
					if (f.properties().hasProperty(P_FIELD_RANGE.key()))
						switch(det) {
						case Double: case Float:
							result.rrange = (Interval) f.properties().getPropertyValue(P_FIELD_RANGE.key());
							break;
						case Integer: case Long: case Short: case Byte:
							result.irange = (IntegerRange) f.properties().getPropertyValue(P_FIELD_RANGE.key());
							break;
						default:
							break;
						}
					if (f.properties().hasProperty(P_FIELD_PREC.key())) 
						result.prec = (Double) f.properties().getPropertyValue(P_FIELD_PREC.key());
					try {
						result.trackType = Class.forName(det.className());
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
			else if (n instanceof TableNode)
				result = findTrackMetadata((TableNode)n,trackVar);
			if (result!=null)
				break;
		}
		return result;
	}

	// recursive
	@SuppressWarnings("unchecked")
	private TrackMeta findTrackMetadata(Category cat, String trackVar) {
		TrackMeta result = null;
		List<Record> lr = (List<Record>) get(cat.edges(Direction.OUT),
			selectZeroOrMany(orQuery(hasTheLabel(E_DRIVERS.label()),hasTheLabel(E_DECORATORS.label()))),
			edgeListEndNodes());
		if (lr!=null) 
			for (Record r:lr) { // this list may contain at most 2 items
				result = findTrackMetadata((Record)r,trackVar);
				if (result!=null)
					break;
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		if (!sealed) {
			super.initialise();
			// optional properties - if absent take default value
			if (properties().hasProperty(P_DATATRACKER_SELECT.key()))
				selection= (SamplingMode) properties().getPropertyValue(P_DATATRACKER_SELECT.key());
			else
				selection = SamplingMode.defaultValue();
			if (properties().hasProperty(P_DATATRACKER_GROUPBY.key()))
				grouping = (Grouping) properties().getPropertyValue(P_DATATRACKER_GROUPBY.key());
			else
				grouping = Grouping.defaultValue();
			if (properties().hasProperty(P_DATATRACKER_STATISTICS.key()))
				stats = (StatisticalAggregatesSet) properties().getPropertyValue(P_DATATRACKER_STATISTICS.key());
			else
				stats = StatisticalAggregatesSet.defaultValue();
			if (properties().hasProperty(P_DATATRACKER_TABLESTATS.key()))
				tstats = (StatisticalAggregatesSet) properties().getPropertyValue(P_DATATRACKER_TABLESTATS.key());
			else
				tstats = StatisticalAggregatesSet.defaultValue();
			if (properties().hasProperty(P_DATATRACKER_VIEWOTHERS.key()))
				viewOthers = (boolean) properties().getPropertyValue(P_DATATRACKER_VIEWOTHERS.key());
			// the only required properties.
			track = (StringTable) properties().getPropertyValue(P_DATATRACKER_TRACK.key());
			// extract the property types + metadata from the graph
			trackTypes = new ObjectTable<>(new Dimensioner(track.size()));
			List<Node> ln = (List<Node>) get(getParent().edges(Direction.OUT),
				selectOneOrMany(hasTheLabel(E_APPLIESTO.label())),
				edgeListEndNodes());
			for (Node n:ln) {
				if (n instanceof RelationType) {
					// TODO: implement code for relation data trackers
				}
				else if (n instanceof Category)
					for (int i=0; i<track.size(); i++) {
						TrackMeta tt = findTrackMetadata((Category)n,track.getWithFlatIndex(i));
						if (tt!=null) {
							trackTypes.setWithFlatIndex(tt.trackType,i);
							String trackName = track.getWithFlatIndex(i);
							if (trackName.contains("["))
								trackName = trackName.substring(0,trackName.indexOf('['));
							trackName += ".";
							if (tt.units!=null)
								if (!fieldMetadata.hasProperty(trackName+P_FIELD_UNITS.key()))
									fieldMetadata.addProperty(trackName+P_FIELD_UNITS.key(),tt.units);
							if (tt.irange!=null)
								if (!fieldMetadata.hasProperty(trackName+P_FIELD_RANGE.key()))
									fieldMetadata.addProperty(trackName+P_FIELD_RANGE.key(),tt.irange);
							if (tt.rrange!=null)
								if (!fieldMetadata.hasProperty(trackName+P_FIELD_RANGE.key()))
									fieldMetadata.addProperty(trackName+P_FIELD_RANGE.key(),tt.rrange);
							if (tt.prec!=null)
								if (!fieldMetadata.hasProperty(trackName+P_FIELD_UNITS.key()))
									fieldMetadata.addProperty(trackName+P_FIELD_PREC.key(),tt.prec);
							if (tt.indexes!=null) {
								if (fieldMetadata.hasProperty(trackName+P_TABLE_INDEX.key())) {
									// means an index has already been set before, so they must be merged
									int[][] prevIndex = (int[][]) fieldMetadata.getPropertyValue(trackName+P_TABLE_INDEX.key());
									int[][] newIndex = Arrays.copyOf(prevIndex, prevIndex.length + tt.indexes.length);
									for (int j=0; j<tt.indexes.length; j++)
										newIndex[j+prevIndex.length] = tt.indexes[j];
									fieldMetadata.addProperty(trackName+P_TABLE_INDEX.key(),newIndex);
								}
								else
									fieldMetadata.addProperty(trackName+P_TABLE_INDEX.key(),tt.indexes);
							}
						}
						else ; // throw Exception ? this should never happen normally...
				}
			}
			
			// end code to change
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
	
	private DataTracker<?,?> makeDataTracker(int index) {
		AbstractDataTracker<?,?> result = null;
		if (dataTrackerClass.equals(TimeSeriesTracker.class.getName())) {	
			result = new TimeSeriesTracker(grouping,stats,tstats,selection,viewOthers,track,trackTypes,fieldMetadata);
		}		
		else if (dataTrackerClass.equals(MapTracker.class.getName())) {	
			result = new MapTracker();
		}		
		else if (dataTrackerClass.equals(LabelValuePairTracker.class.getName())) {	
			result = new LabelValuePairTracker();
		}
		if (result!=null)
			result.setSender(index);
		return result;
	}

	// CAUTION: this method assumes that the widgets have been instantiated AFTER
	// the DataTrackers
	/**
	 * attach time series widgets to these trackers
	 * @param widget
	 */
	public void attachTimeSeriesWidget(DataReceiver<TimeSeriesData,Metadata> widget) {
		for (DataTracker<?,?> dt:dataTrackers.values())
			if (dt instanceof TimeSeriesTracker)
				((TimeSeriesTracker)dt).addObserver(widget);
	}

	public void attachMapWidget(DataReceiver<MapData,Metadata> widget) {
		for (DataTracker<?,?> dt:dataTrackers.values())
			if (dt instanceof MapTracker)
				((MapTracker)dt).addObserver(widget);
	}

	public void attachLabelValuePairWidget(DataReceiver<LabelValuePairData,Metadata> widget) {
		for (DataTracker<?,?> dt:dataTrackers.values())
			if (dt instanceof LabelValuePairTracker)
				((LabelValuePairTracker)dt).addObserver(widget);
	}

	
	@Override
	public DataTracker<?, ?> getInstance(int id) {
		if (!sealed)
			initialise();
		if (!dataTrackers.containsKey(id))
			dataTrackers.put(id,makeDataTracker(id));
		return dataTrackers.get(id);
	}

}
