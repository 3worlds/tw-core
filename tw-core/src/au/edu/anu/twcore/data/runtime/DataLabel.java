package au.edu.anu.twcore.data.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static fr.ens.biologie.generic.SaveableAsText.*;

/**
 * A class for hierarchical names
 * 
 * @author Jacques Gignoux - 10 sept. 2019
 *
 */
public class DataLabel implements Comparable<DataLabel>, Cloneable {

	private static final int HIERARCHYix = 3;
	public static final String HIERARCHY_DOWN = 
		Character.toString(BLOCK_DELIMITERS[HIERARCHYix][BLOCK_CLOSE]);
	public static final String HIERARCHY_UP = 
		Character.toString(BLOCK_DELIMITERS[HIERARCHYix][BLOCK_OPEN]);
	protected List<String> label = new ArrayList<String>(); 
	
	public DataLabel() {
		super();
	}
	
	public DataLabel(String... labelParts) {
		super();
		for (String lab:labelParts)
			label.add(lab);
	}
	
	public DataLabel(Collection<String> labelParts) {
		super();
		for (String lab:labelParts)
			label.add(lab);
	}
	
	public void append(Collection<String> labelParts) {
		label.addAll(labelParts);
	}
	
	public void append(String... labelParts) {
		for (String lab:labelParts)
			label.add(lab);
	}
	
	public int size() {
		return label.size();
	}
	
	public String get(int i) {
		if ((i>=0) && (i<label.size()))
			return label.get(i);
		return null;
	}
	
	public void stripEnd() {
		label.remove(label.size()-1);
	}
		
	public String getEnd() {
		return label.get(label.size()-1);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<label.size(); i++) {
			sb.append(label.get(i));
			if (i<label.size()-1)
				sb.append(HIERARCHY_DOWN);
		}
		return sb.toString();
	}

	// slow ?
	@Override
	public int compareTo(DataLabel o) {		
		return toString().compareTo(o.toString());
	}

	/**
	 * Returns a data label formed from a 'hierarchical description' String 
	 * 
	 * @param text the hierarchical String, i.e. containing '>' separating String items
	 * @return the matching DataLabel
	 */
	public static DataLabel valueOf(String text) {
		return new DataLabel(text.split(HIERARCHY_DOWN));
	}
	
	@Override
	public DataLabel clone() {
		return new DataLabel(label);
	}
	
}
