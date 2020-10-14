package au.edu.anu.twcore.ecosystem.runtime.tracking;

import au.edu.anu.twcore.ecosystem.runtime.DataRecorder;
import au.edu.anu.twcore.ecosystem.runtime.Sampler;

public abstract class SamplerDataTracker<C,T,M>
		extends AbstractDataTracker<T,M>
		implements Sampler<C>, DataRecorder {

	protected SamplerDataTracker(int messageType, int simulatorId) {
		super(messageType, simulatorId);
		// TODO Auto-generated constructor stub
	}

}
