package au.edu.anu.twcore.archetype.tw;

import static au.edu.anu.rscs.aot.queries.CoreQueries.edgeListStartNodes;
import static au.edu.anu.rscs.aot.queries.CoreQueries.hasTheLabel;
import static au.edu.anu.rscs.aot.queries.CoreQueries.inEdges;
import static au.edu.anu.rscs.aot.queries.CoreQueries.selectZeroOrMany;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_LOADFROM;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.ecosystem.dynamics.initial.Group;
import au.edu.anu.twcore.ecosystem.dynamics.initial.LifeCycle;
import au.edu.anu.twcore.ecosystem.structure.ComponentType;
import au.edu.anu.twcore.ecosystem.structure.GroupType;
import au.edu.anu.twcore.ecosystem.structure.LifeCycleType;
import au.edu.anu.twcore.experiment.DataSource;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;

/**
 * Check that a data source to load a group has a groupId column OR at least contains the
 * group name in some column.
 * 
 * @author Jacques Gignoux - 13 déc. 2021
 *
 */
@Deprecated
public class CheckFileIdQuery extends QueryAdaptor {
	
	private void lackGroupId(String dsid) {
		errorMsg = "data source '"+dsid+"' lacks a group identifier column ('"
			+P_DATASOURCE_IDGROUP.key()+"' property)";
		actionMsg = "set the optional property '"+P_DATASOURCE_IDGROUP.key()+"'"+
			" in data source '"+dsid+"'";
	}

	private void lackComponentId(String dsid) {
		errorMsg = "data source '"+dsid+"' lacks a component identifier column ('"
			+P_DATASOURCE_IDCOMPONENT.key()+"' property)";
		actionMsg = "set the optional property '"+P_DATASOURCE_IDCOMPONENT.key()+"'"+
			" in data source '"+dsid+"'";
	}
	
	private void lackLifeCycleId(String dsid) {
		errorMsg = "data source '"+dsid+"' lacks a life cycle identifier column ('"
			+P_DATASOURCE_IDLC.key()+"' property)";
		actionMsg = "set the optional property '"+P_DATASOURCE_IDLC.key()+"'"+
			" in data source '"+dsid+"'";
	}

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) { // input is a DataSource node
		initInput(input);
		if (input instanceof DataSource) {
			DataSource ds = (DataSource) input;
			// get objects to load (either component, group, lifecycle, componentType, groupType or lifeCycleType)
			List<TreeGraphDataNode> objectsToLoad = (List<TreeGraphDataNode>) get(ds,inEdges(),
				selectZeroOrMany(hasTheLabel(E_LOADFROM.label())),
				edgeListStartNodes());
			// get a id--- property if any
			boolean hasGroupId = ds.properties().hasProperty(P_DATASOURCE_IDGROUP.key());
			boolean hasLCId = ds.properties().hasProperty(P_DATASOURCE_IDLC.key());
			boolean hasComponentId = ds.properties().hasProperty(P_DATASOURCE_IDCOMPONENT.key());
			Map<Class<?>,List<TreeGraphDataNode>> ctls = new HashMap<>();
			// set of all types that load from this source - most of the time, size==1
			for (TreeGraphDataNode otl:objectsToLoad) {
				List<TreeGraphDataNode> l = ctls.get(otl.getClass());
				if (l==null) 
					l = new LinkedList<>();
				l.add(otl);
				ctls.put(otl.getClass(),l);
			}
			for (Class<?> ctl:ctls.keySet())	{
				// if the object to load is a GroupType, then a idGroup property must be present
				if (ctl.equals(GroupType.class) && !hasGroupId)
					lackGroupId(ds.id());
				// if the object to load is a Group, then a idGroup property must be present
				// only if there is more than one group referring to this data source
				else if (ctl.equals(Group.class) && !hasGroupId) {
					if (ctls.get(ctl).size()>1)
						lackGroupId(ds.id());
					else {
						// OK: we should be able to load the file even if a groupId col. is not present
						//ie we should be able to find the one matching the groupname
						// NB this is to tricky: the column wont be read because its heading wont
						// match an existing file/table name. So we impose to have a groupId in all cases
						lackGroupId(ds.id());
					}
					// NB: it may also need a LCId !!

				}
				// if the object to load is a ComponentType, then a idComponent property must be present
				else if (ctl.equals(ComponentType.class) && !hasComponentId) {
					lackComponentId(ds.id());
					// NB: it may also need a groupId !!
					
					// NB: it may also need a LCId !!
				}
				else if (ctl.equals(LifeCycleType.class) && !hasLCId) {
					lackLifeCycleId(ds.id());
					
					// NB: it may also need a groupId !!
					
				}
				else if (ctl.equals(LifeCycle.class) && !hasLCId) {
					// ??
					
				}
				
			}
		}
		return this;
	}

}
