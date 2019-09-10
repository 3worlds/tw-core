package au.edu.anu.twcore.data.runtime;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * A class for hierarchical names
 * 
 * @author Jacques Gignoux - 10 sept. 2019
 *
 */
public class DataLabel {

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
	
	public String getEnd() {
		return label.get(label.size()-1);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<label.size(); i++) {
			sb.append(label.get(i));
			if (i<label.size()-1)
				sb.append('>');
		}
		return sb.toString();
	}

}
