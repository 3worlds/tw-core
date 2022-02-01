package au.edu.anu.twcore.archetype.tw;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.N_INITIALVALUES;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.ecosystem.dynamics.initial.InitialElement;
import au.edu.anu.twcore.ecosystem.structure.ComponentType;
import au.edu.anu.twcore.ecosystem.structure.GroupType;
import au.edu.anu.twcore.ecosystem.structure.LifeCycleType;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.graph.impl.TreeGraphNode;

/**
 * Check that data sources and initialValues nodes have the proper ids as required by the component
 * hierarchy
 * 
 * @author Jacques Gignoux - 16 déc. 2021
 *
 */
public class CheckFileIdentifiersQuery extends QueryAdaptor {

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {  // input is a component, group or lifeCycle node
		
// TODO: future version when data loading is fully refactored		
//	public Queryable submit(Object input) {  // input is a componentType, groupType or lifeCycleType node
		
		initInput(input);
		if (input instanceof InitialElement) {
// TODO: future version when data loading is fully refactored					
//		if (input instanceof ElementType) {
			
			
			TreeGraphDataNode inode = (TreeGraphDataNode) input;
			List<TreeGraphNode> initNodes = (List<TreeGraphNode>) get(inode.getChildren(),
				selectZeroOrMany(hasTheLabel(N_INITIALVALUES.label())));
			List<TreeGraphDataNode> dataSources = (List<TreeGraphDataNode>) get(inode,
				outEdges(), 
				selectZeroOrMany(hasTheLabel(E_LOADFROM.label())),
				edgeListEndNodes());
			// find which ids are required as per ElementType hierarchy
			boolean requireComponentId = false;
			boolean requireGroupId = false;
			boolean requireLifeCycleId = false;
			boolean isComponent = false;
			boolean isGroup = false;
			TreeGraphDataNode parent = (TreeGraphDataNode) inode.getParent();
			if (parent instanceof ComponentType) {
				isComponent = true;
				if (parent.getParent() instanceof GroupType) {
					requireComponentId = true;
					requireGroupId = true;
				}
				if (parent.getParent().getParent() instanceof LifeCycleType)
					requireLifeCycleId = true;
			}
			else if (parent instanceof GroupType) {
				isGroup = true;
				requireGroupId = true;
				if (parent.getParent() instanceof LifeCycleType)
					requireLifeCycleId = true;
			}
			else if (parent instanceof LifeCycleType)
				requireLifeCycleId = true;
			// node has an initialValues node to read its data from
			if (!initNodes.isEmpty()) {
				if (isComponent) {
					if (requireGroupId) {
						List<Edge> le = (List<Edge>) get(inode,outEdges(),
							selectZeroOrMany(hasTheLabel(E_INSTANCEOF.label())));
						if (le.isEmpty()) {
							errorMsg = "Missing '"+E_INSTANCEOF.label()+"' edge for '"+inode.id()+"'";
							actionMsg = "Add an '"+E_INSTANCEOF.label()
								+"' edge to a 'group' node to '"+inode.id()+"'";
						}
					}
				}
				else if (isGroup) {
					if (requireLifeCycleId) {
						List<Edge> le = (List<Edge>) get(inode,outEdges(),
							selectZeroOrMany(hasTheLabel(E_CYCLE.label())));
						if (le.isEmpty()) {
							errorMsg = "Missing '"+E_CYCLE.label()+"' edge for '"+inode.id()+"'";
							actionMsg = "Add an '"+E_CYCLE.label()
								+"' edge to a 'lifeCycle' node to '"+inode.id()+"'";
						}
					}
				}
			}
			// node has dataSources to read from
			else if (!dataSources.isEmpty()) {
				Map<TreeGraphDataNode,List<String>> missing = new HashMap<>();
				for (TreeGraphDataNode ds:dataSources) {
					missing.put(ds,new ArrayList<>());
					if (requireLifeCycleId && !ds.properties().hasProperty(P_DATASOURCE_IDLC.key()))
						missing.get(ds).add(P_DATASOURCE_IDLC.key());
					if (requireGroupId && !ds.properties().hasProperty(P_DATASOURCE_IDGROUP.key()))
						missing.get(ds).add(P_DATASOURCE_IDGROUP.key());
					if (requireComponentId && !ds.properties().hasProperty(P_DATASOURCE_IDCOMPONENT.key()))
						missing.get(ds).add(P_DATASOURCE_IDCOMPONENT.key());
				}
				String error = "";
				for (TreeGraphDataNode ds:missing.keySet())
					if (!missing.get(ds).isEmpty()) {
						for (String miss:missing.get(ds))
							error += miss+", ";
						error += "in dataSource '"+ds.id()+"'; ";
					}
				if (!error.isBlank()) {
					errorMsg = "Missing properties "+error;
					actionMsg = "Add properties "+error;
				}
			}
		}
		return this;
	}

}
