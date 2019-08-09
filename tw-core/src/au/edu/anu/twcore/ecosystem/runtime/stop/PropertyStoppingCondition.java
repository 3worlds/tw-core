package au.edu.anu.twcore.ecosystem.runtime.stop;

import au.edu.anu.twcore.ecosystem.dynamics.Simulator;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;

/**
 * An abstract ancestor for all stopping conditions that involve checking a property value,
 * ie conditions not based on time.
 * @author gignoux - 7 mars 2017
 *
 */
public abstract class PropertyStoppingCondition extends AbstractStoppingCondition {

	protected String pname = null;
	private ReadOnlyPropertyList plist = null;
	
	public PropertyStoppingCondition(Simulator sim, 
			String stopVariable, 
			ReadOnlyPropertyList system) {
		super(sim);
		pname = stopVariable;
		plist = system;
		if (plist==null)
			throw new TwcoreException("This stopping condition requires a non-null system to track");
	}

	protected double getVariable() {
		if (plist==null) {
			// TODO! find the property list in which to search !
			throw new TwcoreException("This stopping condition requires a non-null system to track");
		}
		return (double) plist.getPropertyValue(pname);
	}
	
}
