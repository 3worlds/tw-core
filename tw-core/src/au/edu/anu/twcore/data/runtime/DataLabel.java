package au.edu.anu.twcore.data.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import au.edu.anu.rscs.aot.collections.tables.IndexString;
import au.edu.anu.twcore.exceptions.TwcoreException;

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
	private List<String> label = new LinkedList<String>(); 
	
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
		if ((i>0) && (i<label.size()))
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
	
	// helper for below (recursive)
	private static List<DataLabel> mergeLabels(List<DataLabel> list1, List<DataLabel>list2) {
		List<DataLabel> result = new ArrayList<>();
		if (list2!=null)
			for (DataLabel dl1:list1)
				for (DataLabel dl2:list2) {
					DataLabel dl = dl1.clone();
					dl.append(dl2.label);
					result.add(dl);
		}
		else 
			result = list1;
		return result;
	}
	
	// helper for below
	private static List<DataLabel> mergeLabels(List<List<DataLabel>> list) {
		List<DataLabel> result = list.get(0);
		for (int i=1; i<list.size(); i++)
			result = mergeLabels(result,list.get(i));
		return result;
	}
	
	/**
	 * Returns a list of DataLabel when the input contains expandable index specifications
	 * 
	 * @param multiLabel a datalabel with possible table indices in there
	 * @param dims the dimensions matching levels of the Label where dims are needed, in the same order
	 * @return
	 */
	// tested OK
	public static List<DataLabel> expandIndexes(DataLabel multiLabel,int[]...dims) {
		List<List<DataLabel>> result = new ArrayList<>();
		for (int i=0; i<multiLabel.size(); i++)
			result.add(new ArrayList<DataLabel>());
		int i=0;
		for (int k=0; k<multiLabel.label.size(); k++) {
			String s = multiLabel.label.get(k);
			int[][] index = null;
			if (s.contains("[")) {
				String ix = s.substring(s.indexOf("["));
				s = s.substring(0,s.indexOf("["));
				if (dims.length==i)
					throw new TwcoreException("Not enough dimensions passed to expandIndexes");
				index = IndexString.stringToIndex(ix,dims[i++]);
				for (int j=0; j<index.length; j++) {
					result.get(k).add(new DataLabel(s+Arrays.toString(index[j]).replace(" ","")));
				}
			}
			else {
				result.get(k).add(new DataLabel(s));
			}
		}
		return mergeLabels(result);
	}
	
	@Override
	public DataLabel clone() {
		return new DataLabel(label);
	}
	
}
