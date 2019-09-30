package au.edu.anu.twcore.data.runtime;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * <p>A class storing time series metadata to initialise time series widgets.
 * It is meant to be passed as the "timeSeriesMetadata" property in a Metadata message property list.
 * </p>
 * <p>
 * Data for time series are written in arrays to messages, as doubles, longs or strings. The ranking of
 * labels to indices in the arrays is passed in this metadata message, together with all other metadata
 * (eg precision, min, max, units).
 * </p>
 * 
 * 
 * @author Jacques Gignoux - 30 sept. 2019
 *
 */
public class TimeSeriesMetadata {
	
	public static String TSMETA = "timeSeriesMetadata";
	
	private SortedMap<DataLabel,Integer> nameIndex = new TreeMap<DataLabel,Integer>();
	private SortedSet<DataLabel> intNames = new TreeSet<DataLabel>();
	private SortedSet<DataLabel> doubleNames = new TreeSet<DataLabel>();
	private SortedSet<DataLabel> stringNames = new TreeSet<DataLabel>(); 

	public TimeSeriesMetadata() {
		// TODO Auto-generated constructor stub
	}
	
	// create inner order of variables
	public void addIntVariable(DataLabel key) {
		int nextIx = intNames.size();
		intNames.add(key);
		nameIndex.put(key, nextIx);
	}
	
	public void addDoubleVariable(DataLabel key) {
		int nextIx = doubleNames.size();
		doubleNames.add(key);
		nameIndex.put(key, nextIx);
	}

	public void addStringVariable(DataLabel key) {
		int nextIx = stringNames.size();
		stringNames.add(key);
		nameIndex.put(key, nextIx);
	}
	
	// looping helpers
	public Iterable<DataLabel> intNames() {
		return intNames;
	}

	public Iterable<DataLabel> doubleNames() {
		return doubleNames;
	}
	
	public Iterable<DataLabel> stringNames() {
		return stringNames;
	}
	
	public int indexOf(DataLabel key) {
		return nameIndex.get(key);
	}

	public int nInt() {
		return intNames.size();
	}

	public int nDouble() {
		return doubleNames.size();
	}

	public int nString() {
		return stringNames.size();
	}

}
