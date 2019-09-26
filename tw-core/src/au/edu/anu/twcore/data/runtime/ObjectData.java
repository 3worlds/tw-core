package au.edu.anu.twcore.data.runtime;

import fr.cnrs.iees.twcore.constants.DataTrackerStatus;

/**
 * @author Ian Davies
 *
 * @date 19 Sep 2019
 */
public class ObjectData extends TimeData {
	// private DataLabel label = null;
	private Object value;

	public ObjectData(DataTrackerStatus status, int senderId, int metaDataType) {
		super(status, senderId, metaDataType);
	}

	/**
	 * returns the object
	 * 
	 * @return
	 */
	public Object value() {
		return value;
	}

	public String text() {
		return value.toString();
	}

	/**
	 * sets object
	 * 
	 * @param o
	 */
	public void setValue(Object o) {
		value = o;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("time=");
		sb.append(time());
		sb.append("; Object = ");
		sb.append(text());
		return sb.toString();
	}

}