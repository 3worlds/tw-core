package au.edu.anu.twcore.ecosystem.runtime.process;

import au.edu.anu.twcore.data.runtime.TwData;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;

/**
 * A class to pass context data between a Process and its functions
 * 
 * @author Jacques Gignoux - 19 sept. 2019
 *
 */
public class HierarchicalContext {

	public TwData ecosystemParameters = null;
	public TwData ecosystemVariables = null;
	public ReadOnlyPropertyList ecosystemPopulationData = null;
	public String ecosystemName = null;
	public TwData lifeCycleParameters = null;
	public TwData lifeCycleVariables = null;
	public ReadOnlyPropertyList lifeCyclePopulationData = null;
	public String lifeCycleName = null;
	public TwData groupParameters = null;
	public TwData groupVariables = null;
	public ReadOnlyPropertyList groupPopulationData = null;
	public String groupName = null;
	
	public HierarchicalContext() {
		super();
	}
	
	void clear() {
		ecosystemParameters = null;
		ecosystemVariables = null;
		ecosystemPopulationData = null;
		ecosystemName = null;
		lifeCycleParameters = null;
		lifeCycleVariables = null;
		lifeCyclePopulationData = null;
		lifeCycleName = null;
		groupParameters = null;
		groupVariables = null;
		groupPopulationData = null;
		groupName = null;
	}

}
