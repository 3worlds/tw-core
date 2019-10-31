package au.edu.anu.twcore.data.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import au.edu.anu.rscs.aot.collections.tables.IndexString;

/**
 * A specialized data label class for items that may contain a SIMPLE index String
 * (ie not one using the index syntax of StringIndex)
 * 
 * @author Jacques Gignoux - 30 oct. 2019
 *
 */
public class IndexedDataLabel extends DataLabel {

	private List<int[]> index = new ArrayList<int[]>();
	
	public IndexedDataLabel() {
		super();
	}

	public IndexedDataLabel(DataLabel lab) {
		super();
		append(lab.label);
		resetIndexes();
	}
	
	public IndexedDataLabel(String... labelParts) {
		super(labelParts);
		resetIndexes();
	}

	public IndexedDataLabel(Collection<String> labelParts) {
		super(labelParts);
		resetIndexes();
	}
	
	private void resetIndexes() {
		index.clear();
		for (String s: label) {
			if (s.contains("[")) {
				String ixs = s.substring(s.indexOf("[")); 
				ixs = ixs.substring(1,ixs.length()-1);
				String[] ss = ixs.split(",",-1);
				int[] ix = new int[ss.length];
				for (int i=0; i<ix.length; i++)
					ix[i] = Integer.valueOf(ss[i]);
				index.add(ix);
			}
			else
				index.add(null);
		}
	}

	public int[] getIndex(int i) {
		if ((i>=0) && (i<index.size()))
			return index.get(i);
		return null;
	}
	
	@Override
	public void append(Collection<String> labelParts) {
		super.append(labelParts);
		resetIndexes();
	}

	@Override
	public void append(String... labelParts) {
		super.append(labelParts);
		resetIndexes();
	}

	@Override
	public IndexedDataLabel clone() {
		return new IndexedDataLabel(label);
	}
	
	public static IndexedDataLabel valueOf(String text) {
		return new IndexedDataLabel(text.split(HIERARCHY_DOWN));
	}

	// helper for below (recursive)
	private static List<IndexedDataLabel> mergeLabels(List<IndexedDataLabel> list1, List<IndexedDataLabel>list2) {
		List<IndexedDataLabel> result = new ArrayList<>();
		if (list2!=null)
			for (IndexedDataLabel dl1:list1)
				for (IndexedDataLabel dl2:list2) {
					IndexedDataLabel dl = dl1.clone();
					dl.append(dl2.label);
					result.add(dl);
		}
		else 
			result = list1;
		return result;
	}
	
	// helper for below
	private static List<IndexedDataLabel> mergeLabels(List<List<IndexedDataLabel>> list) {
		List<IndexedDataLabel> result = list.get(0);
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
	public static List<IndexedDataLabel> expandIndexes(DataLabel multiLabel,Map<String,int[]> dims) {
		List<List<IndexedDataLabel>> result = new ArrayList<>();
		for (int i=0; i<multiLabel.size(); i++)
			result.add(new ArrayList<IndexedDataLabel>());
		for (int k=0; k<multiLabel.label.size(); k++) {
			String s = multiLabel.label.get(k);
			int[][] index = null;
			String ix = "";
			if (s.contains("[")) {
				ix = s.substring(s.indexOf("["));
				s = s.substring(0,s.indexOf("["));
			}
			if (dims!=null) {
				if (dims.containsKey(s)) { // means this variable is a table
					index = IndexString.stringToIndex(ix,dims.get(s));
					for (int j=0; j<index.length; j++) {
						result.get(k).add(new IndexedDataLabel(s+Arrays.toString(index[j]).replace(" ","")));
					}
				}
				else
					result.get(k).add(new IndexedDataLabel(s));
			}
			else
				result.get(k).add(new IndexedDataLabel(s));
		}
		return mergeLabels(result);
	}

}
