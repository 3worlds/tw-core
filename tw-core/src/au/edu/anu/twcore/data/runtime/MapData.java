package au.edu.anu.twcore.data.runtime;

import fr.cnrs.iees.twcore.constants.SimulatorStatus;

/**
 * Data for 2D maps of a single variable
 * 
 * @author Jacques Gignoux - 10 sept. 2019
 *
 */
public class MapData extends TimeData {

	private DataLabel zlabel;
	private Number[][] map;

	public MapData(SimulatorStatus status, 
			int senderId, 
			int metadataType,
			int nx,
			int ny) {
		super(status, senderId, metadataType);
		map = new Number[nx][ny];
	}
	
	public void addValue(int ix, int iy, Number value) {
		if ((ix>=0) && (ix<map.length) && (iy>=0) && (iy<map[0].length)) 
			map[ix][iy] = value;
	}
	
	public void setLabel(DataLabel label) {
		zlabel = label;
	}

	public void setLabel(String...labelParts) {
		zlabel = new DataLabel(labelParts);
	}

	
	public Number[][] map() {
		return map;
	}
	
	public DataLabel label() {
		return zlabel;
	}

}
