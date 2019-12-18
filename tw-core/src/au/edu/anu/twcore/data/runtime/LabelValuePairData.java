package au.edu.anu.twcore.data.runtime;

import fr.cnrs.iees.twcore.constants.SimulatorStatus;

/**
 * The class for sending just a value with a label. Value can be a number or a String
 * 
 * @author Jacques Gignoux - 10 sept. 2019
 *
 */
@Deprecated
public class LabelValuePairData extends OutputData {

	// the label as a hierarchical name
	private DataLabel label = null; 
	private Number value = null;
	private String svalue = null;
	
	public LabelValuePairData(SimulatorStatus status, 
			int senderId,
			int metaDataType) {
		super(status, senderId,metaDataType);
	}
		
	/**
	 * returns the numeric value - may be null if the value is not a number
	 * 
	 * @return
	 */
	public Number value() {
		return value;
	}
	
	/**
	 * sets a numeric value
	 * 
	 * @param n
	 */
	public void setValue(Number n) {
		value = n;
	}
	
	/**
	 * sets a string value
	 * 
	 * @param s
	 */
	public void setValue(String s) {
		svalue = s;
	}
	
	public void setLabel(String...labelParts) {
		label = new DataLabel(labelParts);
	}

	public void setLabel(DataLabel label) {
		this.label = label;
	}

	
	public DataLabel label() {
		return label;
	}
	
	/**
	 * returns the numeric value as a String, or the String value if the value is not numeric
	 * 
	 * @return
	 */
	public String asString() {
		if (value!=null)
			return value.toString();
		else
			return svalue;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(label.toString());
		sb.append('=');
		sb.append(asString());
		return sb.toString();
	}
	
}
