package au.edu.anu.twcore.ecosystem.runtime.tracking;

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.edu.anu.rscs.aot.collections.tables.Dimensioner;
import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.twcore.data.runtime.DataLabel;
import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.Output0DMetadata;
import au.edu.anu.twcore.ecosystem.runtime.system.ArenaComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ui.runtime.DataReceiver;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SimplePropertyListImpl;
import fr.cnrs.iees.rvgrid.rendezvous.GridNode;
import fr.cnrs.iees.twcore.constants.SamplingMode;
import fr.cnrs.iees.twcore.constants.StatisticalAggregates;
import fr.cnrs.iees.twcore.constants.StatisticalAggregatesSet;
import fr.ens.biologie.generic.utils.Statistics;

/**
 * The ancestor of data trackers managing data aggregated with Statistics on a Sample
 * @author Jacques Gignoux - 21 oct. 2020
 *
 */
public abstract class AggregatorDataTracker<T>
		extends SamplerDataTracker<CategorizedComponent, T, Metadata> {
	
	// statistical aggregators - one per variable
	private Map<DataLabel,Statistics> aggregators = new HashMap<>();
	protected SimplePropertyList metaprops;
	private static String[] propertyKeys = { P_DATATRACKER_SELECT.key(), 
			P_DATATRACKER_STATISTICS.key(),
			P_DATATRACKER_TABLESTATS.key(), 
			P_DATATRACKER_TRACK.key(), 
			P_DATATRACKER_SAMPLESIZE.key(),
			"sample",
			Output0DMetadata.TSMETA };
	// metadata for numeric fields, ie min max units etc.
	protected ReadOnlyPropertyList fieldMetadata = null;
	// true if all tracked components are permanent, false if at least one is ephemeral
	private boolean permanentComponents = true;
	private StatisticalAggregatesSet statistics = null;
	// the part of the data channel label describing the sample
	private Map<StatisticalAggregates,DataLabel> statChannels = new HashMap<>();
	// the part of the data channel label describing the variable/constant tracked
	private Map<CategorizedComponent,DataLabel> itemChannels = new HashMap<>();
	private DataLabel containerLabel = null;
	private Metadata singletonMD = null;
	protected int metadataType = -1;

	protected AggregatorDataTracker(int messageType, 
			int simulatorId, 
			SamplingMode selection, 
			int sampleSize,
			Collection<CategorizedComponent> samplingPool, 
			String[] samplingPoolIds,
			boolean samplingPoolPermanent,
			List<CategorizedComponent> trackedComponents, 
			StatisticalAggregatesSet statistics,
			Collection<String> track,
			ReadOnlyPropertyList fieldMetadata) {
		super(messageType, simulatorId, selection, sampleSize, 
				samplingPool, trackedComponents);
		this.fieldMetadata = fieldMetadata;
		metaprops = new SimplePropertyListImpl(propertyKeys);
		metaprops.setProperty(P_DATATRACKER_SELECT.key(), selection);
		metaprops.setProperty(P_DATATRACKER_STATISTICS.key(), statistics);
//TODO:		metaprops.setProperty(P_DATATRACKER_TABLESTATS.key(), tableStatistics);
		metaprops.setProperty(P_DATATRACKER_SAMPLESIZE.key(), sampleSize);
		this.statistics = statistics;
		for (String s : track) {
			DataLabel l = (DataLabel) fieldMetadata.getPropertyValue(s + "." + P_FIELD_LABEL.key());
			aggregators.put(l,new Statistics());
		}
		if (samplingPool!=null) {
			permanentComponents = samplingPoolPermanent;
			containerLabel = new DataLabel(samplingPoolIds);
		}
		else
			if (!trackedComponents.isEmpty()) {
				for (CategorizedComponent cp: sample) {
					if (!cp.isPermanent())
						permanentComponents = false;
						break;
				}
				if (sample.size()>=1) {
					CategorizedComponent item =	sample.iterator().next();
					containerLabel = new DataLabel(item.hierarchicalId());
					if (!(item instanceof ArenaComponent))
						containerLabel.stripEnd();
				}
		}
		// container label
		makeItemLabels();
		if (permanentComponents) {
			StringTable itemIds = new StringTable(new Dimensioner(itemChannels.size()));
			int i=0;
			for (DataLabel dl:itemChannels.values())
				itemIds.setWithFlatIndex(dl.toString(),i++);
			metaprops.setProperty("sample",itemIds);
		}
	}
	
	protected void resetStatistics() {
		for (Statistics stat:aggregators.values())
			stat.reset();
	}
	
	private void resetSampleIds() {
		itemChannels.clear();
		for (CategorizedComponent cc:sample) {
			DataLabel dl = new DataLabel(cc.hierarchicalId());
			itemChannels.put(cc,dl);
		}
	}
	
	// container.fullId() creates the String[] with system>lifecycle>group as
	// found in the hierarchy of containers. Doesnt include the groupType name
	private void makeItemLabels() {
		statChannels.clear();
		if (statistics==null) // no stats: one channel per tracked component
			resetSampleIds();
		else  // stats: one channel per statistic required
			for (StatisticalAggregates stat:statistics.values()) {
				DataLabel result = containerLabel.clone();
				result.append(stat.toString());
				statChannels.put(stat,result);
		}
	}
	
	public DataLabel itemName(StatisticalAggregates stat) {
		return statChannels.get(stat);
	}
	
	@Override
	public void updateSample() {
		if (!permanentComponents) { 
			super.updateSample();
			makeItemLabels();
			resetSampleIds();
		}
	}
	
	public Collection<DataLabel> sampleIds() {
		return itemChannels.values();
	}

	// There may be a time bottleneck here
	// maybe all initial items should be removed from sample in updateSample if their counterparts
	// are available ?
	@Override
	public boolean isTracked(CategorizedComponent sc) {
		boolean result = false;
		result = sample.contains(sc);
		if ((!result)&&(sc instanceof SystemComponent)) {
			CategorizedComponent isc = ((SystemComponent)sc).container().initialForItem(sc.id());
			if (isc != null)
				result = sample.contains(isc);
		}
		return result;
	}

	@Override
	public Metadata getInstance() {
		if (singletonMD == null) {
			singletonMD = new Metadata(senderId, metaprops);
			metadataType = singletonMD.type();
			if (fieldMetadata != null)
				singletonMD.addProperties(fieldMetadata);
		}
		return singletonMD;
	}
	
	protected Collection<DataLabel> variableChannels() {
		return Collections.unmodifiableCollection(aggregators.keySet());
	}
	
	protected void aggregateData(double value, DataLabel channel) {
		if (statistics!=null)
			aggregators.get(channel).add(value);
	}
	protected void aggregateData(long value, DataLabel channel) {
		if (statistics!=null)
			aggregators.get(channel).add(value);
	}
	protected void aggregateData(String value, DataLabel channel) {
		if (statistics!=null)
			aggregators.get(channel).add(value);
	}
	
	
	@Override
	public void preProcess() {
		super.preProcess();
		if (permanentComponents) {
			resetSampleIds();
			StringTable itemIds = new StringTable(new Dimensioner(itemChannels.size()));
			int i=0;
			for (DataLabel dl:itemChannels.values())
				itemIds.setWithFlatIndex(dl.toString(),i++);
			metaprops.setProperty("sample",itemIds);
		}
//		// I HATE THIS!
//		// 1) it sends another Metadata message overriding the initial one
//		// 2) it trashes singletonMD by making it inconsistent with the data actually sent.
//		// But what to do? at widget instantiation time, the runtime systemComponents have
//		// not yet been instantiated, hence their ids cannot be sent as metadata
//		// one possible solution to this very bad design would be, for permanent system 
//		// components, to move the initial item in the runtime items and place a copy of
//		// it as the initial system....
//		/// hey.... that's not so bad, actually....
//		for (DataReceiver<T, Metadata> w:observers()) {
//			singletonMD = new Metadata(senderId, metaprops);
//			metadataType = singletonMD.type();
//			if (fieldMetadata != null)
//				singletonMD.addProperties(fieldMetadata);
//			sendMetadataTo((GridNode)w, singletonMD);
//		}
	}

	// assuming all aggregators have the same number of observation, which makes sense because
	// there is only one sample here
	// of course it may be true before the last variable is updated, so be careful
	protected int nAggregated() {
		if (statistics!=null) {
			if (aggregators.size()>0)
				return aggregators.values().iterator().next().n();
			else
				return 0;
		}
		else
			return 1;
	}
	
	public boolean isAggregating() {
		return statistics!=null;
	}
	
	public Collection<StatisticalAggregates> statisticsRequired() {
		if (statistics!=null)
			return Collections.unmodifiableSet(statistics.values());
		else
			return null;
	}

	protected double aggregatedValue(DataLabel channel, StatisticalAggregates stat) {
		Statistics s = aggregators.get(channel);
		switch (stat) {
		case CV:
			return s.average()/Math.sqrt(s.variance());
		case MEAN:
			return s.average();
		case N:
			return s.n(); // this is an int actually
		case SE:
			return Math.sqrt(s.variance());
		case SUM:
			return s.sum(); // concatenate for  string
		case VAR:
			return s.variance();
//		case MIN:
//			return s.min();
//		case MAX:
//			return s.max();
		default:
			return Double.NaN;
		}
	}
}
