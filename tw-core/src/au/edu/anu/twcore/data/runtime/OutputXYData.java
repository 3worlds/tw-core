package au.edu.anu.twcore.data.runtime;

import fr.cnrs.iees.twcore.constants.SimulatorStatus;

/**
 * Data for 2D scatter plots (xy).
 * <p>Data for scatter plots are all converted to doubles for simplicity.</p>
 * <p>To be used with a plain Metadata message, with only two variables registered in the metadata.</p>
 *
 * @author J. Gignoux - 15 mai 2020
 *
 */
public class OutputXYData extends LabelledItemData {

	private double x;
	private double y;

	public OutputXYData(SimulatorStatus status, int senderId, int metaDataType) {
		super(status, senderId, metaDataType);
	}

	public void setX(double x) {
		this.x = x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public void setX(long x) {
		this.x = x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public void setX(short x) {
		this.x = x;
	}
	public void setX(byte x) {
		this.x = x;
	}
	public void setX(boolean x) {
		this.x = x?1.0:0.0;
	}

	public void setY(double y) {
		this.y = y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public void setY(long y) {
		this.y = y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public void setY(short y) {
		this.y = y;
	}
	public void setY(byte y) {
		this.y = y;
	}
	public void setY(boolean y) {
		this.y = y?1.0:0.0;
	}


	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		return sb.append(" x=").append(x).append(" y=").append(y).toString();
	}

}
