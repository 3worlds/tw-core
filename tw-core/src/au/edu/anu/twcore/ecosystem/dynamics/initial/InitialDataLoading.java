package au.edu.anu.twcore.ecosystem.dynamics.initial;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_LOADFROM;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_COMPONENT_NINST;
import java.util.List;
import java.util.Map;

import au.edu.anu.twcore.ecosystem.structure.ComponentType;
import au.edu.anu.twcore.ecosystem.structure.GroupType;
import au.edu.anu.twcore.ecosystem.structure.LifeCycleType;
import au.edu.anu.twcore.experiment.DataSource;
import au.edu.anu.twcore.experiment.runtime.DataIdentifier;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.properties.ExtendablePropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;

/**
 * Load initial data in a node based on the presence of datasource nodes or initialValue nodes
 * 
 * @author gignoux 14/1/2022
 *
 */
public class InitialDataLoading {
	
	private InitialDataLoading() {}
	
	/**
	 * read data from data sources attached to node argument and load them into the loadedData argument
	 * 
	 * @param node
	 * @param loadedData
	 */
	@SuppressWarnings("unchecked")
	public static void loadFromDataSources(TreeGraphDataNode node,
			Map<DataIdentifier, SimplePropertyList> loadedData) {
		List<DataSource> sources = (List<DataSource>) get(node.edges(Direction.OUT),
			selectZeroOrMany(hasTheLabel(E_LOADFROM.label())),
			edgeListEndNodes());
		for (DataSource source:sources)
			source.getInstance().load(loadedData);		
	}
	
	/**
	 * read data from child nodes of type InitialValues and load them into the loadedData argument
	 * 
	 * @param node
	 * @param loadedData
	 */
	public static void loadFromConfigTree(TreeGraphDataNode node,
			Map<DataIdentifier, SimplePropertyList> loadedData) {
		for (TreeNode tn:node.getChildren()) {
			if (tn instanceof InitialValues) {
				InitialValues dataNode = (InitialValues)tn;
				if (dataNode instanceof ReadOnlyDataHolder) {
					if (dataNode.properties().hasProperty(P_COMPONENT_NINST.key())) {
						int nInst = (int)dataNode.properties().getPropertyValue(P_COMPONENT_NINST.key());
						if (nInst==1 ) {
							DataIdentifier id = dataNode.fullId();
							ExtendablePropertyList props = new ExtendablePropertyListImpl();
							props.addProperties(dataNode.readOnlyProperties());
							loadedData.put(id,props);
						}
						else
							for (int i=0; i<nInst; i++) {
								DataIdentifier id = dataNode.fullId();
								ExtendablePropertyList props = new ExtendablePropertyListImpl();
								if (node instanceof ComponentType)
									id.setComponentId(id.componentId()+i);
								else if (node instanceof GroupType)
									id = new DataIdentifier(id.componentId(),id.groupId()+i,id.lifeCycleId());
								else if (node instanceof LifeCycleType)
									id = new DataIdentifier(id.componentId(),id.groupId(),id.lifeCycleId()+i);
								props.addProperties(dataNode.readOnlyProperties());
								loadedData.put(id,props);							
						}
					}
				}
				
			}
		}
		
	}

}
