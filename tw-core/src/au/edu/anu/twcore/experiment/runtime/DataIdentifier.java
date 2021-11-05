package au.edu.anu.twcore.experiment.runtime;

import java.util.Collection;

import au.edu.anu.twcore.data.runtime.DataLabel;
import au.edu.anu.twcore.exceptions.TwcoreException;

/**
 * A class to generate temporary identifier for data loaded from files. 
 * 
 * @author Jacques Gignoux - 5 nov. 2021
 *
 */
public class DataIdentifier extends DataLabel {
	
	private static int cindex = 0;
	private static int gindex = 1;
	private static int lcindex = 2;

	public DataIdentifier(String componentId, String groupId, String lifeCycleId) {
		super(componentId,groupId,lifeCycleId);
	}
	
	public DataIdentifier(String... labelParts) {
		super();
		if (labelParts.length==3)
			for (String lab : labelParts)
				label.add(lab);
		else
			throw new TwcoreException("A DataIdentifier must have three label parts");
	}

	public DataIdentifier(Collection<String> labelParts) {
		super();
		if (labelParts.size()==3)
			for (String lab : labelParts)
				label.add(lab);
		else
			throw new TwcoreException("A DataIdentifier must have three label parts");
	}

	public String componentId() {
		return label.get(cindex);
	}
	
	public void setComponentId(String id) {
		label.set(cindex,id);
	}
	
	public String groupId() {
		return label.get(gindex);
	}
	
	public String lifeCycleId() {
		return label.get(lcindex);
	}
	
	public boolean isEmpty() {
		if (((componentId()==null)||(componentId().isEmpty()))	&&
			((groupId()==null)	  ||(groupId().isEmpty()))		&&
			((lifeCycleId()==null)||(lifeCycleId().isEmpty()))	)
			return true;
		return false;
	}
	
}
