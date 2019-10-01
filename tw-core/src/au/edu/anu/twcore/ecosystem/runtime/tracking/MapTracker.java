package au.edu.anu.twcore.ecosystem.runtime.tracking;

import au.edu.anu.twcore.data.runtime.MapData;
import au.edu.anu.twcore.data.runtime.Metadata;

/**
 * A data tracker for map data
 * 
 * @author Jacques Gignoux - 1 oct. 2019
 *
 */
public class MapTracker extends AbstractDataTracker<MapData, Metadata> {

	public MapTracker() {
		super(DataMessageTypes.MAP);
	}

}
