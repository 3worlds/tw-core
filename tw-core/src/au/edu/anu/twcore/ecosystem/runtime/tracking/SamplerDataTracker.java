package au.edu.anu.twcore.ecosystem.runtime.tracking;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import au.edu.anu.twcore.ecosystem.runtime.DataRecorder;
import au.edu.anu.twcore.ecosystem.runtime.Sampler;
import fr.cnrs.iees.twcore.constants.SamplingMode;

/**
 * An ancestor for data trackers which get their data from a sample of objects (typicallty,
 * CategorizedComponent and its subclasses) taken from a population.
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
	private int trackSampleSize = 0;
	private boolean trackAll = false;
	private SamplingMode trackMode;
	// sample 
	protected Set<C> sample = new HashSet<>();
	// population from which sample is drawn
	private Collection<C> samplingPool = null;
	
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
		if (trackedComponents!=null)
			sample.addAll(trackedComponents);
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
					}
				}
			}
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
