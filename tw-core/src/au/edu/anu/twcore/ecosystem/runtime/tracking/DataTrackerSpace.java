package au.edu.anu.twcore.ecosystem.runtime.tracking;

import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.SpaceData;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.twcore.constants.SimulatorStatus;

/**
 * A data tracker for spatial data of SystemComponents (no edges at the moment).
 * 
 * The metadata are the space properties, namely: type (SpaceType), edgeEffects (EdgeEffects),
 * precision (double), units (String), plus the descendant-specific properties:
 * 
 * for FlatSurface: x-limits 'Interval) and y-limits (Interval)
 * for SquareGrid: cellSize(double), x-nCells (int), y-nCells (int) (optional, if absent = x-nCells)
 * 
 * This DataTracker is not instantiated by a DataTrackerNode, but by the SpaceNode it points to.
 * 
 * @author Jacques Gignoux - 14 févr. 2020
 *
 */
public class DataTrackerSpace extends AbstractDataTracker<SpaceData, Metadata> {

	private long currentTime = Long.MIN_VALUE;
	private Metadata metadata = null;
	
	public DataTrackerSpace(int simId, ReadOnlyPropertyList meta) {
		super(DataMessageTypes.SPACE);
		metadata = new Metadata(simId,meta);
		setSender(simId);
	}

	public void recordTime(long time) {
		currentTime = time;
	}

	public void recordItem(SimulatorStatus status, double[] coord, String... labels) {
		SpaceData msg = new SpaceData(status, senderId, metadata.type());
		msg.setTime(currentTime);
		msg.setItemLabel(labels);
		msg.newLocation(coord);
		sendData(msg);
	}
	
	public void removeItem(SimulatorStatus status, String... labels) {
		SpaceData msg = new SpaceData(status, senderId, metadata.type());
		msg.setTime(currentTime);
		msg.deleteLocation(labels);
		sendData(msg);
	}
	
	@Override
	public Metadata getInstance() {
		return metadata;
	}

}
