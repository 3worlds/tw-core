package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.twcore.ecosystem.structure.ComponentType;
import au.edu.anu.twcore.ecosystem.structure.GroupType;
import au.edu.anu.twcore.ecosystem.structure.LifeCycleType;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A (very specialised) query to check that an Element has idGroup or idLifeCycle properties
 * depending on its place in the config tree
 * 
 * @author Jacques Gignoux - 30 mai 2022
 *
 */
public abstract class CheckIdPropertiesQuery extends QueryAdaptor {

	protected List<String> propToAdd = new ArrayList<>();
	protected List<String> propToRemove = new ArrayList<>();
	
	protected void checkIdHierarchy(ComponentType componentNode, ReadOnlyPropertyList ropl) {
		if (componentNode.getParent() instanceof GroupType) {
			// target must have idGroup property
			if (!ropl.hasProperty(P_DATASOURCE_IDGROUP.key()))
				propToAdd.add(P_DATASOURCE_IDGROUP.key());
			// grandparent is a lifecycletype
			if (componentNode.getParent().getParent() instanceof LifeCycleType) {
				// edge must have idLifeCycle property
				if (!ropl.hasProperty(P_DATASOURCE_IDLC.key()))
					propToAdd.add(P_DATASOURCE_IDLC.key());
			}
			// no lifecycletype : there must be no idLC property
			else if (ropl.hasProperty(P_DATASOURCE_IDLC.key()))
				propToRemove.add(P_DATASOURCE_IDLC.key());
		} 
		// no grouptype : there must be no idgroup property nor idlifecycle property
		else {
			if (ropl.hasProperty(P_DATASOURCE_IDGROUP.key())) 
				propToRemove.add(P_DATASOURCE_IDGROUP.key());
			if (ropl.hasProperty(P_DATASOURCE_IDLC.key()))
				propToRemove.add(P_DATASOURCE_IDLC.key());
		}
	}
	
	protected void buildMessages(String element,String fullId) {
		// message building (Pheew!)
		if (propToAdd.isEmpty()) {
			// only remove properties
			if (!propToRemove.isEmpty()) {
				if (propToRemove.size()==1) {
					actionMsg = "Remove the '"+propToRemove.get(0)
						+"' property from "+element+" '"+fullId+"'";
					errorMsg = "The "+element+" '"+fullId
						+"' does not need a '"+propToRemove.get(0)
						+"' property to identify components";
				}
				else {
					actionMsg = "Remove the "+propToRemove.toString()+" properties from "
						+element+" '"+fullId+"'";
					errorMsg = "The "+element+" '"+fullId
						+" does not need "+propToRemove.toString()
						+" properties to identify components";
				}
			}
		}
		else {
			// only add properties
			if (propToRemove.isEmpty()) {
				if (propToAdd.size()==1) {
					actionMsg = "Add a '"+propToAdd.get(0)+"' property to "
						+element+" '"+fullId+"'";
					errorMsg = "The "+element+" '"+fullId
						+"' must have a '"+propToAdd.get(0)
						+"' property to identify components";
				}
				else {
					actionMsg = "Add "+propToAdd.toString()+" properties to "
						+element+" '"+fullId+"'";
					errorMsg = "The "+element+" '"+fullId
						+" must have "+propToAdd.toString()
						+" properties to identify components";
				}
			}
			// add and remove properties in this case ony one per list, cant be two
			else {
				actionMsg = "Add '"+propToAdd.get(0)
					+"' property to, and remove '"+propToRemove.get(0)
					+" property from "+element+" '"
					+fullId+"'";
				errorMsg = "The "+element+" '"+fullId
					+"' must have the '"+propToAdd.get(0)
					+"' property but does not need the '"+propToRemove.get(0)
					+"' property to identify components";
			}
		}
	}

}
