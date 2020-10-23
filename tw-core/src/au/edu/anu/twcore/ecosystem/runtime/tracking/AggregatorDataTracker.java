package au.edu.anu.twcore.ecosystem.runtime.tracking;

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import au.edu.anu.twcore.data.runtime.DataLabel;
import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.Output0DMetadata;
import au.edu.anu.twcore.ecosystem.runtime.system.ArenaComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.HierarchicalComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SimplePropertyListImpl;
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
			Output0DMetadata.TSMETA };
	// metadata for numeric fields, ie min max units etc.
	protected ReadOnlyPropertyList fieldMetadata = null;
	// true if all tracked components are permanent, false if at least one is ephemeral
	private boolean permanentComponents = true;
	private StatisticalAggregatesSet statistics = null;
	// the part of the data channel label describing the sample
	private Map<StatisticalAggregates,DataLabel> itemChannels = new HashMap<>();
	// the part of the data channel label describing the variable/constant tracked
	private Map<CategorizedComponent,String> itemIdList = new HashMap<>();
	private DataLabel containerLabel = null;
	private Metadata singletonMD = null;
	protected int metadataType = -1;

	protected AggregatorDataTracker(int messageType, 
			int simulatorId, 
			SamplingMode selection, 
			int sampleSize,
			Collection<CategorizedComponent> samplingPool, 
			List<CategorizedComponent> trackedComponents, 
			StatisticalAggregatesSet statistics,
			Collection<String> track,
			ReadOnlyPropertyList fieldMetadata) {
		super(messageType, simulatorId, selection, sampleSize, samplingPool, trackedComponents);
		this.fieldMetadata = fieldMetadata;
		metaprops = new SimplePropertyListImpl(propertyKeys);
		metaprops.setProperty(P_DATATRACKER_SELECT.key(), selection);
		metaprops.setProperty(P_DATATRACKER_STATISTICS.key(), statistics);
//		metaprops.setProperty(P_DATATRACKER_TABLESTATS.key(), tableStatistics);
		metaprops.setProperty(P_DATATRACKER_SAMPLESIZE.key(), sampleSize);
		this.statistics = statistics;
		for (String s : track) {
			DataLabel l = (DataLabel) fieldMetadata.getPropertyValue(s + "." + P_FIELD_LABEL.key());
			aggregators.put(l,new Statistics());
		}
		if (!trackedComponents.isEmpty()) {
			for (CategorizedComponent cp: sample)
				if (!cp.isPermanent()) {
					permanentComponents = false;
					break;
			}
		}
		makeItemLabels();
		resetSampleIds();
	}
	
	// need a method to build a stat message from a std OUtput0D data message ?
	
	protected void resetStatistics() {
		for (Statistics stat:aggregators.values())
			stat.reset();
	}
	
	private void setContainerLabel() {		
		if (sample.size()>=1) {
			CategorizedComponent item =	sample.iterator().next();
			if (item  instanceof SystemComponent)
				containerLabel = new DataLabel(((SystemComponent)item).container().fullId());
			else if (item instanceof ArenaComponent)
				containerLabel = new DataLabel();
			else if (item instanceof HierarchicalComponent)
				containerLabel = new DataLabel(((HierarchicalComponent)item).content().parentContainer().fullId());
		}
	}
	
	private void resetSampleIds() {
		for (CategorizedComponent cc:sample)
			itemIdList.put(cc,cc.id());
	}
	
	// container.fullId() creates the String[] with system>lifecycle>group as
	// found in the hierarchy of containers. Doesnt include the groupType name
	private void makeItemLabels() {
		if (containerLabel==null)
			setContainerLabel();
		itemChannels.clear();
//		if (statistics==null) // no stats: one channel per tracked component
//			for (CategorizedComponent item:sample) {
//				DataLabel result = containerLabel.clone();
//				result.append(item.id());
//				itemChannels.add(result);			
//		}		
//		else  // stats: one channel per statistic required
			for (StatisticalAggregates stat:statistics.values()) {
				DataLabel result = containerLabel.clone();
				result.append(stat.toString());
				itemChannels.put(stat,result);
		}
	}
	
	public DataLabel itemName(StatisticalAggregates stat) {
		return itemChannels.get(stat);
	}
	
	@Override
	public void updateSample() {
		if (!permanentComponents) { 
			super.updateSample();
			makeItemLabels();
			resetSampleIds();
		}
	}
	
	public Collection<String> sampleIds() {
		return itemIdList.values();
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
		return Collections.unmodifiableSet(statistics.values());
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
