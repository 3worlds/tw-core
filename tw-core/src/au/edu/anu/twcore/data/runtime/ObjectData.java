package au.edu.anu.twcore.data.runtime;

import fr.cnrs.iees.twcore.constants.SimulatorStatus;

/**
 * @author Ian Davies
 *
 * @date 19 Sep 2019
 */
// Question: what's the use case for this class ? LabelValuePairData could do the same job (except
// for time)
public class ObjectData extends TimeData {
	// private DataLabel label = null;
	private Object value;

	public ObjectData(SimulatorStatus status, int senderId, int metaDataType) {
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
