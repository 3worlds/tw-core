package au.edu.anu.twcore.ecosystem.runtime.tracking;

import au.edu.anu.twcore.data.runtime.LabelValuePairData;
import au.edu.anu.twcore.data.runtime.Metadata;

/**
 * A data tracker for simple (label,value) pair data
 * 
 * @author Jacques Gignoux - 1 oct. 2019
 *
 */
@Deprecated
public class LabelValuePairTracker extends AbstractDataTracker<LabelValuePairData, Metadata> {

	public LabelValuePairTracker() {
		super(DataMessageTypes.VALUE_PAIR);
	}

}
