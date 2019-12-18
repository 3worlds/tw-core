package au.edu.anu.twcore.data.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

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
public class Output0DMetadata {
	
	public static String TSMETA = "timeSeriesMetadata";
	
	private SortedMap<DataLabel,Integer> nameIndex = new TreeMap<DataLabel,Integer>();
	private List<DataLabel> intNames = new ArrayList<DataLabel>();
	private List<DataLabel> doubleNames = new ArrayList<DataLabel>();
	private List<DataLabel> stringNames = new ArrayList<DataLabel>(); 

	public Output0DMetadata() {
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
