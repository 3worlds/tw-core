package au.edu.anu.twcore.data.runtime;

import fr.cnrs.iees.twcore.constants.SimulatorStatus;

/**
 * Data for time series.
 * <p>
 * Data for time series are written in arrays to messages, as doubles, longs or strings. The ranking of
 * labels to indices in the arrays is passed in this metadata message, together with all other metadata
 * (eg precision, min, max, units).
 * </p>
 * <p>
 * The sender has to know in which order the values should be sent - usually it will generate a
 * TimeSeriesMetadata record which maps the names to table indices </p>
 * 
 * @author Jacques Gignoux - 10 sept. 2019
 *
 */
public class TimeSeriesData extends TimeData {
	
//	private Map<DataLabel, Number> values = new HashMap<>();
	
	private TimeSeriesMetadata meta;
	// this table is used to send byte,int,short,long and boolean values
	private long intValues[];
	// this table is used to send float and double values
	private double doubleValues[];
	// this table is used to send String values
	private String stringValues[];

	public TimeSeriesData(SimulatorStatus status, 
			int senderId, 
			int metadataType, 
			TimeSeriesMetadata metadata) {
		super(status, senderId, metadataType);
		this.meta = metadata;
		intValues = new long[meta.nInt()];
		doubleValues = new double[meta.nDouble()];
		stringValues = new String[meta.nString()];
	}
	
	public void setValues(long... values) {
		for (int i=0; i<values.length; i++)
			intValues[i] = values[i];
	}
	
	public void setValues(double... values) {
		for (int i=0; i<values.length; i++)
			doubleValues[i] = values[i];
	}
	
	public void setValues(String... values) {
		for (int i=0; i<values.length; i++)
			stringValues[i] = values[i];
	}
	
	public void setValue(DataLabel label, double value) {
		doubleValues[meta.indexOf(label)] = value;
	}

	public void setValue(DataLabel label, float value) {
		doubleValues[meta.indexOf(label)] = value;
	}

	public void setValue(DataLabel label, int value) {
		intValues[meta.indexOf(label)] = value;
	}

	public void setValue(DataLabel label, long value) {
		intValues[meta.indexOf(label)] = value;
	}

	public void setValue(DataLabel label, byte value) {
		intValues[meta.indexOf(label)] = value;
	}

	public void setValue(DataLabel label, short value) {
		intValues[meta.indexOf(label)] = value;
	}

	public void setValue(DataLabel label, boolean value) {
		intValues[meta.indexOf(label)] = value?1:0;
	}

	public void setValue(DataLabel label, String value) {
		stringValues[meta.indexOf(label)] = value;
	}

	// the order of these list exactly matches the order of intNames, doubleNames and stringNames
	// in TimeSeriesMetadata
	public long[] getIntValues() {
		return intValues;
	}

	public double[] getDoubleValues() {
		return doubleValues;
	}
	
	public String[] getStringValues() {
		return stringValues;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		int i=0;
		for (DataLabel name:meta.intNames())
			sb.append(' ').append(name.toString()).append('=').append(intValues[i++]);
		i=0;
		for (DataLabel name:meta.doubleNames())
			sb.append(' ').append(name.toString()).append('=').append(doubleValues[i++]);
		i=0;
		for (DataLabel name:meta.stringNames())
			sb.append(' ').append(name.toString()).append('=').append(stringValues[i++]);
		return sb.toString();
	}
	

//	public void addValue(Number value, DataLabel label) {
//		values.put(label, value);
//	}
//
//	public void addValue(Number value, String... labelParts) {
//		DataLabel label = new DataLabel(labelParts);
//		values.put(label, value);
//	}
//
//	public Map<DataLabel, Number> values() {
//		return values;
//	}

}
