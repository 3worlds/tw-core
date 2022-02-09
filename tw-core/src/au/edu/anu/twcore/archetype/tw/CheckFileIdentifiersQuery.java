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
import au.edu.anu.twcore.ecosystem.dynamics.initial.InitialValues;
import au.edu.anu.twcore.ecosystem.structure.ComponentType;
import au.edu.anu.twcore.ecosystem.structure.ElementType;
import au.edu.anu.twcore.ecosystem.structure.GroupType;
import au.edu.anu.twcore.ecosystem.structure.LifeCycleType;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
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
	public Queryable submit(Object input) {  // input is a componentType, groupType or lifeCycleType node
		initInput(input);
		if (input instanceof ElementType<?,?>) {
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
//			boolean isComponent = false;
//			boolean isGroup = false;
			TreeGraphDataNode parent = (TreeGraphDataNode) inode.getParent();
			if (parent instanceof ComponentType) {
//				isComponent = true;
				if (parent.getParent() instanceof GroupType) {
					requireComponentId = true;
					requireGroupId = true;
				}
				if (parent.getParent().getParent() instanceof LifeCycleType)
					requireLifeCycleId = true;
			}
			else if (parent instanceof GroupType) {
//				isGroup = true;
				requireGroupId = true;
				if (parent.getParent() instanceof LifeCycleType)
					requireLifeCycleId = true;
			}
			else if (parent instanceof LifeCycleType)
				requireLifeCycleId = true;
			// list of 
			Map<TreeGraphNode,List<String>> missing = new HashMap<>();
			// node has initialValues node to read its data from
			for (TreeGraphNode initNode:initNodes) {
				missing.put(initNode,new ArrayList<>());
				if (requireGroupId) {
					if (initNode instanceof ReadOnlyDataHolder)
						if (((ReadOnlyDataHolder) initNode).properties().hasProperty(P_DATASOURCE_IDGROUP.key()))
							break;
					missing.get(initNode).add(P_DATASOURCE_IDGROUP.key());
//					errorMsg = "Missing property '"+P_DATASOURCE_IDGROUP.key()
//						+"' in node '"+N_INITIALVALUES.label()+":"+initNode.id()+"'";;
//					actionMsg = "Add property '"+P_DATASOURCE_IDGROUP.key()
//						+"' to node '"+N_INITIALVALUES.label()+":"+initNode.id()+"'";
				}
				else if (requireLifeCycleId) {
					if (initNode instanceof ReadOnlyDataHolder)
						if (((ReadOnlyDataHolder) initNode).properties().hasProperty(P_DATASOURCE_IDLC.key()))
							break;
					missing.get(initNode).add(P_DATASOURCE_IDLC.key());
//					errorMsg = "Missing property '"+P_DATASOURCE_IDLC.key()
//						+"' in node '"+N_INITIALVALUES.label()+":"+initNode.id()+"'";;
//					actionMsg = "Add property '"+P_DATASOURCE_IDLC.key()
//						+"' to node '"+N_INITIALVALUES.label()+":"+initNode.id()+"'";
				}
			}
			// node has dataSources to read from
			if (!dataSources.isEmpty()) {
				for (TreeGraphDataNode ds:dataSources) {
					missing.put(ds,new ArrayList<>());
					if (requireLifeCycleId && !ds.properties().hasProperty(P_DATASOURCE_IDLC.key()))
						missing.get(ds).add(P_DATASOURCE_IDLC.key());
					if (requireGroupId && !ds.properties().hasProperty(P_DATASOURCE_IDGROUP.key()))
						missing.get(ds).add(P_DATASOURCE_IDGROUP.key());
					if (requireComponentId && !ds.properties().hasProperty(P_DATASOURCE_IDCOMPONENT.key()))
						missing.get(ds).add(P_DATASOURCE_IDCOMPONENT.key());
				}
			}
			// formatting error message
			String error = "";
			for (TreeGraphNode ds:missing.keySet())
				if (!missing.get(ds).isEmpty()) {
					for (String miss:missing.get(ds))
						error += miss+", ";
					if (ds instanceof InitialValues)
						error += "in initialValues '"+ds.id()+"'; ";
					else
						error += "in dataSource '"+ds.id()+"'; ";
				}
			if (!error.isBlank()) {
				errorMsg = "Missing properties "+error;
				actionMsg = "Add properties "+error;
			}
		}
		return this;
	}

}
