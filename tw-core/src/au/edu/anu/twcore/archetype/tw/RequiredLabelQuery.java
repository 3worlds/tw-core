package au.edu.anu.twcore.archetype.tw;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import fr.cnrs.iees.graph.Specialized;

/**
 * Check that only one string out of many is found in some list
 * 
 * @author gignoux 14/2/2022
 *
 */
public abstract class RequiredLabelQuery extends QueryAdaptor {

	List<String> requiredLabels = new ArrayList<>();
	
	public RequiredLabelQuery(StringTable el) {
		super();
		for (int i=0; i<el.size(); i++)
			requiredLabels.add(el.getWithFlatIndex(i));
	}
	
	public RequiredLabelQuery(String... lab) {
		super();
		for (int i=0; i<lab.length; i++)
			requiredLabels.add(lab[i]);
	}
	
	final int countLabels(Collection<? extends Specialized> labelled) {
		int count=0;
		for (Specialized s:labelled)
			if (requiredLabels.contains(s.classId()))
				count++;
		return count;
	}

}
