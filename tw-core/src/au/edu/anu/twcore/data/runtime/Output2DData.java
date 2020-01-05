package au.edu.anu.twcore.data.runtime;

import fr.cnrs.iees.twcore.constants.SimulatorStatus;

/**
 * Data for 2D maps of a single variable
 * 
 * @author Jacques Gignoux - 10 sept. 2019
 *
 */
public class Output2DData extends LabelledItemData {

	private DataLabel zlabel;
	private Number[][] map;

	public Output2DData(SimulatorStatus status, 
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
	
	public void setZLabel(DataLabel label) {
		zlabel = label;
	}

	public void setZLabel(String...labelParts) {
		zlabel = new DataLabel(labelParts);
	}

	
	public Number[][] map() {
		return map;
	}
	
	public DataLabel zLabel() {
		return zlabel;
	}

}