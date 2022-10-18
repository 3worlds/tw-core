package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.qgraph.queries.Queryable;
import au.edu.anu.twcore.ecosystem.dynamics.initial.InitialValues;
import au.edu.anu.twcore.experiment.DataSource;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.get;

/**
 * A query to check that SampleXXX edges to data trackers have correct values for idGroup and idLifeCycle
 * 
 * @author Jacques Gignoux - 27 mai 2022
 *
 */
// TODO: one problem left : doesnt check the consistency of idGroup w.r. idLifeCycle (ie a
// group could belong to another life cycle and this woudlnt be detected)
public class ValidElementTrackNameQuery extends CheckDataFileQuery {
	
	public ValidElementTrackNameQuery() {
		super();
	}

	/**
	 * return a list of valid group or life cycle identifiers from a list of InitialValues nodes or 
	 * data sources
	 */
	private Set<String> validIds(List<InitialValues> initialValueNodes, 
			List<DataSource> dataSources,
			String pKey,
			boolean sampleComponent) {
		Set<String> validNames = new HashSet<>();
		// get all initial values ids
		if (sampleComponent)
			for (InitialValues ivn:initialValueNodes)
				validNames.add((String) ivn.properties().getPropertyValue(pKey));
		else
			for (InitialValues ivn:initialValueNodes)
				validNames.add(ivn.id());
		// get all ids found in dataSources
		for (DataSource ds:dataSources) {
			String[][] rawData = loadFile(ds);
			if (rawData!=null) {
				int col=-1; // column containing group or lc id values
				if (rawData[0]!=null) { // header line
					// search for idGroup or idLifeCycle property
					if (ds.properties().hasProperty(pKey)) {
						String s = (String) ds.properties().getPropertyValue(pKey);
						for (int j=0; j<rawData[0].length; j++)
							if (s.equals(rawData[0][j])) {
								col=j;
								break;
						}
					}
				}
				if (col>=0)
					for (int i=1; i<rawData.length; i++) {
						validNames.add(rawData[i][col]);
				}
			}
		}
		return validNames;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) { // an Edge
		initInput(input);
		if ((input instanceof Edge)&&(input instanceof ReadOnlyDataHolder)) {
			ReadOnlyPropertyList ropl = ((ReadOnlyDataHolder)input).properties();
			boolean ok = false;
			List<InitialValues> ivns = null;
			List<DataSource> dss = null;
			Set<String> validNames = null;
			// groups
			if (E_SAMPLEGROUP.type().isAssignableFrom(input.getClass())) {
				// get group names requested by data tracker
				StringTable prop = (StringTable) ropl.getPropertyValue(P_DATASOURCE_IDGROUP.key());
				// get group names from initialValues and dataSource nodes
				ivns = (List<InitialValues>) get(((Edge)input).endNode(),
					children(),
					selectZeroOrMany(hasTheLabel(N_INITIALVALUES.label())));
				dss = (List<DataSource>) get(((Edge)input).endNode(),
					outEdges(),
					edgeListEndNodes(),
					selectZeroOrMany(hasTheLabel(N_DATASOURCE.label())));
				validNames = validIds(ivns,dss,P_DATASOURCE_IDGROUP.key(),false);
				// check requested group name matches one found in the model data
				for (int i=0; i<prop.size(); i++)
					if (validNames.contains(prop.getWithFlatIndex(i))) {
						ok=true;
						break;
				}
				if (!ok) {
					errorMsg = "Cross-link '"+E_SAMPLEGROUP.label()
						+":"+((Identity)input).id()
						+"' property '"+P_DATASOURCE_IDGROUP.key()
						+"' must contain a group name from "+validNames.toString();
					actionMsg = "Provide at least one group name from "+validNames.toString() 
						+" for data sampling in cross-link '"
						+((Identity)input).id()+"', property '"+P_DATASOURCE_IDGROUP.key();					
				}
			}		
			// life cycles
			else if (E_SAMPLELIFECYCLE.type().isAssignableFrom(input.getClass())) {
				// get life cycle names requested by data tracker
				StringTable prop = (StringTable) ropl.getPropertyValue(P_DATASOURCE_IDLC.key());
				// get life cycle names from initialValues and dataSource nodes
				ivns = (List<InitialValues>) get(((Edge)input).endNode(),
					children(),
					selectZeroOrMany(hasTheLabel(N_INITIALVALUES.label())));
				dss = (List<DataSource>) get(((Edge)input).endNode(),
					outEdges(),
					edgeListEndNodes(),
					selectZeroOrMany(hasTheLabel(N_DATASOURCE.label())));
				validNames = validIds(ivns,dss,P_DATASOURCE_IDLC.key(),false);
				// check requested life cycle name matches one found in the model data
				for (int i=0; i<prop.size(); i++)
					if (validNames.contains(prop.getWithFlatIndex(i))) {
						ok=true;
						break;
				}				
				if (!ok) {
					errorMsg = "Cross-link '"+E_SAMPLELIFECYCLE.label()
						+":"+((Identity)input).id()
						+"' property '"+P_DATASOURCE_IDLC.key()
						+"' must contain a life cycle name from "+validNames.toString();
					actionMsg = "Provide at least one life cycle name from "+validNames.toString()
						+" for data sampling in cross-link '"
						+((Identity)input).id()+"', property '"+P_DATASOURCE_IDLC.key();					
				}
			}
			// components
			else if (E_SAMPLECOMPONENT.type().isAssignableFrom(input.getClass())) {
				String gp = null;
				String lc = null;
				boolean okgrp = false;
				boolean oklc = false;
				// get initialValues and dataSource nodes for this componentType
				ivns = (List<InitialValues>) get(((Edge)input).endNode(),
					children(),
					selectZeroOrMany(hasTheLabel(N_INITIALVALUES.label())));
				dss = (List<DataSource>) get(((Edge)input).endNode(),
					outEdges(),
					edgeListEndNodes(),
					selectZeroOrMany(hasTheLabel(N_DATASOURCE.label())));
				Set<String> validGrpNames = null; // for message writing
				// if group name requested by data tracker,
				// check requested group name matches one found in the model data
				if (ropl.hasProperty(P_DATASOURCE_IDGROUP.key())) {
					gp = (String) ropl.getPropertyValue(P_DATASOURCE_IDGROUP.key());
					validNames = validIds(ivns,dss,P_DATASOURCE_IDGROUP.key(),true);
					if (validNames.contains(gp))
						okgrp=true;
					else
						validGrpNames = validNames;
				}
				else
					okgrp=true;
				// if life cycle name requested by data tracker,
				// check requested life cycle name matches one found in the model data
				if (ropl.hasProperty(P_DATASOURCE_IDLC.key())) {
					lc = (String) ropl.getPropertyValue(P_DATASOURCE_IDLC.key());
					// bad coding: double loading of data files! may be very slow
					validNames = validIds(ivns,dss,P_DATASOURCE_IDLC.key(),true);
					if (validNames.contains(lc))
						oklc=true;
				}
				else
					oklc = true;
				// generate messages from check results
				if (okgrp & !oklc) {
					errorMsg = "Cross-link '"+E_SAMPLECOMPONENT.label()+":"+((Identity)input).id()
						+"' property '"+P_DATASOURCE_IDLC.key()
						+"' must match a life cycle name from "+validNames.toString();
					actionMsg = "Provide a life cycle name from "+validNames.toString()
						+" for data sampling in cross-link '"
						+E_SAMPLECOMPONENT.label()+":"+((Identity)input).id()
						+"', property '"+P_DATASOURCE_IDLC.key()+"'";					
				}
				else if (!okgrp & !oklc) {
					errorMsg = "Cross-link '"+E_SAMPLECOMPONENT.label()+":"+((Identity)input).id()
						+"' properties '"+P_DATASOURCE_IDGROUP.key()
						+"' and '"+P_DATASOURCE_IDLC.key()
						+"' must match an existing group name from "+validGrpNames.toString()
						+ " and an existing life cycle name from "+validNames.toString();
					actionMsg = "Provide a group name from "+validGrpNames.toString()
						+ " and a life cycle name from "+validNames.toString()
						+" for data sampling in cross-link '"
						+E_SAMPLECOMPONENT.label()+":"+((Identity)input).id()
						+"', properties '"+P_DATASOURCE_IDGROUP.key()
						+"' and '"+P_DATASOURCE_IDLC.key()+"'";					
				}
				else if (!okgrp & oklc) {
					errorMsg = "Cross-link '"+E_SAMPLECOMPONENT.label()+":"+((Identity)input).id()
						+"' property '"+P_DATASOURCE_IDGROUP.key()
						+"' must match a group name from "+validNames.toString();
					actionMsg = "Provide a group name from "+validNames.toString()
						+" for data sampling in cross-link '"
						+E_SAMPLECOMPONENT.label()+":"+((Identity)input).id()
						+"', property '"+P_DATASOURCE_IDGROUP.key()+"'";					
				}
			}
		}
		return this;
	}

}
