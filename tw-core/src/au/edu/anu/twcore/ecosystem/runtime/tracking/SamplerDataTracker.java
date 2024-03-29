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
package au.edu.anu.twcore.ecosystem.runtime.tracking;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import au.edu.anu.twcore.ecosystem.runtime.DataRecorder;
import au.edu.anu.twcore.ecosystem.runtime.Sampler;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import fr.cnrs.iees.twcore.constants.SamplingMode;

/**
 * <p>An ancestor for data trackers which get their data from a sample of objects (typically,
 * CategorizedComponent and its subclasses) taken from a population.</p>
 *
 * <p>Naming conventions for tracked data:</p>
 * <ul>
 * <li>if sample of size 1: <strong>systemName>groupName>componentId>variableName[index]</strong> </li>
 * <li>if sample of size >1 and no statistics required: same as above, with a different
 * <strong>componentId</strong> for each channel</li>
 * <li>if sample of size >1 and statistics: <strong>systemName>groupName>statistic>variableName[index]</strong>
 * (where statistic = mean, var, n, sum etc...) + in the data message, the list of the ids of the
 * components currently included in the sample (may be useful for the final rendering).</li>
 * <li>if tracking components in a model without groups:
 * <strong>systemName>componentId>variableName[index]</strong> or
 * <strong>systemName>statistic>variableName[index]</strong></li>
 * <li>if tracking groups: <strong>systemName>groupName>variableName[index]</strong> or
 * <strong>systemName>groupTypeName>statistic>variableName[index]</strong></li>
 * <li>if tracking system only: <strong>systemName>variableName[index]</strong></li>
 * </ul>
 *
 * @author Jacques Gignoux - 14 oct. 2020
 *
 * @param <C> the type of object to sample
 * @param <T> the type of data to send
 * @param <M> the type of metadata to send
 */
public abstract class SamplerDataTracker<C,T,M>
		extends AbstractDataTracker<T,M>
		implements Sampler<C>, DataRecorder {

	// sampling strategy
	protected int trackSampleSize = 0;
	private boolean trackAll = false;
	private SamplingMode trackMode;
	// sample
	protected Set<C> sample = new HashSet<>();
	private Set<C> initialSample = new HashSet<>();
	// population from which sample is drawn
	protected Collection<C> samplingPool = null;

	protected SamplerDataTracker(int messageType,
			int simulatorId,
			SamplingMode selection,
			int sampleSize,
			Collection<C> samplingPool,
			List<C> trackedComponents) {
		super(messageType, simulatorId);
		trackMode = selection;
		trackSampleSize = sampleSize;
		if (trackSampleSize==-1)
			trackAll = true;
		this.samplingPool = samplingPool;
		if (trackedComponents!=null) {
			if ((!trackAll)&&(trackedComponents.size()>trackSampleSize))
				for (int i=0; i<trackSampleSize; i++)
					initialSample.add(trackedComponents.get(i));
			else
				initialSample.addAll(trackedComponents);
			sample.addAll(initialSample);
		}
	}

	// use this to select new SystemComponents if some are missing
	// only needed if components are not permanent
	// TODO: handle missing sampling strategy !
	@Override
	public void updateSample() {
		if (samplingPool!=null) {
			if (trackAll) {
				sample.clear();
				sample.addAll(samplingPool);
			}
			else {
				// if we track system components, then sample only contains one group
				// first cleanup the tracked list from components which are gone from the
				// container list
				Iterator<C> isc = sample.iterator();
				while (isc.hasNext()) {
					if (!samplingPool.contains(isc.next()))
						isc.remove();
				}
				// only a selected subset is tracked
				if (sample.size() < trackSampleSize) {
					// then proceed to selection
					boolean goOn = true;
					switch (trackMode) {
					case FIRST:
						while (goOn) {
							Iterator<C> list = samplingPool.iterator();
							C next = list.next();
							while (sample.contains(next))
								next = list.next();
							if (next == null)
								goOn = false; // to stop at end of pool when trackSampleSize is too large
							else {
								sample.add(next);
								if (sample.size() == trackSampleSize)
									goOn = false;
							}
						}
						break;
					case RANDOM:
						// calls to random will crash if argument is zero
						goOn = true;
						LinkedList<C> ll = new LinkedList<>();
						for (C sc : samplingPool)
							ll.add(sc);
						while (goOn) {
							int i;
							C next = null;
							if (ll.size()>0)
								do {
									i = rng.nextInt(ll.size());
									next = ll.get(i);
									ll.remove(i); // if already in sample, no need to draw it again
								} while ((sample.contains(next))&&(ll.size()>0));
							if (ll.size()==0)
								goOn = false;
							if (next!=null)
								sample.add(next);
							if (sample.size() == trackSampleSize)
								goOn = false;
						}
						break;
					case LAST:
						goOn = true;
						while (goOn) {
							// reverse the list order
							LinkedList<C> l = new LinkedList<>();
							for (C sc : samplingPool)
								l.addFirst(sc);
							// as before
							Iterator<C> list = l.iterator();
							C next = list.next();
							while (sample.contains(next))
								next = list.next();
							if (next == null)
								goOn = false; // to stop at end of pool when trackSampleSize is too large
							else {
								sample.add(next);
								if (sample.size() == trackSampleSize)
									goOn = false;
							}
						}
						break;
					default:
						// do nothing if no sampling mode specified
						return;
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void preProcess() {
		super.preProcess();
		// replace initial items with runtime items in sample
		sample.clear();
		for (C s:initialSample) {
			if (s instanceof SystemComponent) {
				SystemComponent isc = (SystemComponent) s;
				if (isc.container().containsInitialItem(isc))
					for (SystemComponent sc:isc.container().items())
						if (isc==isc.container().initialForItem(sc.id())) {
						sample.add((C) sc);
				}
			}
			else
				sample.add(s);
		}
	}

	@Override
	public void removeFromSample(C wasTracked) {
		sample.remove(wasTracked);
	}

	@Override
	public void addToSample(C toTrack) {
		sample.add(toTrack);
	}

	@Override
	public boolean isTracked(C item) {
		return sample.contains(item);
	}

}
