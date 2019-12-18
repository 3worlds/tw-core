package au.edu.anu.twcore.ecosystem.runtime.tracking;

import au.edu.anu.twcore.data.runtime.Output2DData;
import au.edu.anu.twcore.data.runtime.Metadata;

/**
 * A data tracker for map data
 * 
 * @author Jacques Gignoux - 1 oct. 2019
 *
 */
public class DataTrackerTracker2D extends AbstractDataTracker<Output2DData, Metadata> {

	public DataTrackerTracker2D() {
		super(DataMessageTypes.MAP);
	}

}
