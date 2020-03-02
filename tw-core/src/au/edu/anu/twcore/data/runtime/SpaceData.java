package au.edu.anu.twcore.data.runtime;

import fr.cnrs.iees.twcore.constants.SimulatorStatus;
import fr.cnrs.iees.uit.space.Point;
import fr.ens.biologie.generic.utils.Duple;

/**
 * This class is used to send spatial information to widgets. It contains the coordinates of
 * a single item (usually a SystemComponent) and its DataLabel.
 * NB may also contain a line.
 * 
 * It is meant to be used as a message to sent everytime a new item is located / unlocated by the space
 * (for economy: only changes are sent)
 * 
 *  
 * @author Jacques Gignoux - 14 févr. 2020
 *
 */
public class SpaceData extends LabelledItemData {
	
	private static final boolean delete = false;
	private static final boolean create = true;
	
	// the coordinates of the labelled item
	private double[] coordinates = null;
	// a boolean telling if the item should be removed or added from/to the widget
	private boolean action;
	
	private Duple<double[],double[]> line = null;

	public SpaceData(SimulatorStatus status, int senderId, int metaDataType) {
		super(status, senderId, metaDataType);
	}
	
	// points (ie, SystemComponents)

	public void newLocation(Point loc) {
		action = create;
		coordinates = new double[loc.dim()];
		for (int i=0; i<loc.dim(); i++)
			coordinates[i] = loc.coordinate(i);
	}
	
	public void newLocation(double...coord) {
		action = create;
		coordinates = coord.clone();
	}

	public void deleteLocation(String... labels) {
		action = delete;
		setItemLabel(labels);
	}
	
	public double[] coordinates() {
		return coordinates;
	}
	
	public boolean isPoint() {
		return (coordinates!=null);
	}
	
	// for both SystemComponents and SystemRelations
	
	public void delete(DataLabel labels) {
		action = delete;
		setItemLabel(labels);
	}
	
	// lines (ie, SystemRelations) - actually just a pair of points
	
	public void newLine(Point start, Point end) {
		action = create;
		double[] s = new double[start.dim()];
		double[] e = new double[end.dim()];
		for (int i=0; i<start.dim(); i++) {
			s[i] = start.coordinate(i);
			e[i] = end.coordinate(i);
		}
		line = new Duple<double[],double[]>(s,e);
	}

	public void newLine(double[] start, double[] end) {
		action = create;
		line = new Duple<double[],double[]>(start.clone(),end.clone());
	}
	
	public Duple<double[],double[]> line() {
		return line;
	}
	
	// what to do with the data
	
	public boolean create() {
		return action;
	}
	
	public boolean delete() {
		return !action;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("SpaceData: ");
		sb.append("Sender ").append(sender()).append("; ");
		if (action==create)
			sb.append("new item ")
				.append(itemLabel())
				.append(" @ ")
				.append(Point.newPoint(coordinates));
		else if (action==delete)
			sb.append("delete item ")
			.append(itemLabel());
		return sb.toString();
	}
	
}