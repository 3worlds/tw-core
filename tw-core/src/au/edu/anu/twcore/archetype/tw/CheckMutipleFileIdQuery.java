/**************************************************************************
 *  TW-CORE - 3Worlds Core classes and methods                            *
 *                                                                        *
 *  Copyright 2018: Shayne Flint, Jacques Gignoux & Ian D. Davies         *
 *       shayne.flint@anu.edu.au                                          * 
 *       jacques.gignoux@upmc.fr                                          *
 *       ian.davies@anu.edu.au                                            * 
 *                                                                        *
 *  TW-CORE is a library of the principle components required by 3W       *
 *                                                                        *
 **************************************************************************                                       
 *  This file is part of TW-CORE (3Worlds Core).                          *
 *                                                                        *
 *  TW-CORE is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  TW-CORE is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *                         
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with TW-CORE.                                                   *
 *  If not, see <https://www.gnu.org/licenses/gpl.html>                   *
 *                                                                        *
 **************************************************************************/
package au.edu.anu.twcore.archetype.tw;

import java.util.List;
import au.edu.anu.qgraph.queries.QueryAdaptor;
import au.edu.anu.qgraph.queries.Queryable;
import au.edu.anu.twcore.ecosystem.structure.ComponentType;
import au.edu.anu.twcore.ecosystem.structure.GroupType;
import au.edu.anu.twcore.ecosystem.structure.LifeCycleType;
import au.edu.anu.twcore.experiment.DataSource;
import fr.cnrs.iees.omugi.graph.impl.TreeGraphDataNode;
import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;

/**
 * Check that a data source has a identifier column if the objects to be loaded come 
 * from >1 dataSource
 * 
 * @author Jacques Gignoux - 7 dec. 2021
 *
 */
// TODO: move messages to TextTranslations.
public class CheckMutipleFileIdQuery extends QueryAdaptor {
	
	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {  // input is a DataSource node
		initInput(input);
		if (input instanceof DataSource) {
			TreeGraphDataNode ds = (TreeGraphDataNode) input;
			// get objects to load (either component, group, lifecycle, componentType, groupType or lifeCycleType)
			List<TreeGraphDataNode> objectsToLoad = (List<TreeGraphDataNode>) get(ds,inEdges(),
				selectZeroOrMany(hasTheLabel(E_LOADFROM.label())),
				edgeListStartNodes());
			for (TreeGraphDataNode otl:objectsToLoad) {
				List<TreeGraphDataNode> sources = (List<TreeGraphDataNode>) get(otl,outEdges(),
					selectZeroOrMany(hasTheLabel(E_LOADFROM.label())),
					edgeListEndNodes());
				// if the component loads from multiple sources, then ds must have a componentId column
				if (sources.size()>1) {
					if (otl instanceof ComponentType) {
						if (!ds.properties().hasProperty(P_DATASOURCE_IDCOMPONENT.key())) {
							errorMsg = "data source '"+ds.id()+"' lacks a component identifier column ('"
								+P_DATASOURCE_IDCOMPONENT.key()+"' property)";
							actionMsg = "set the optional property '"+P_DATASOURCE_IDCOMPONENT.key()+"'"+
								" in data source '"+ds.id()+"'";
						}
					}
					else if (otl instanceof GroupType) {
						if (!ds.properties().hasProperty(P_DATASOURCE_IDGROUP.key())) {
							errorMsg = "data source '"+ds.id()+"' lacks a group identifier column ('"
								+P_DATASOURCE_IDGROUP.key()+"' property)";
							actionMsg = "set the optional property '"+P_DATASOURCE_IDGROUP.key()+"'"+
								" in data source '"+ds.id()+"'";
						}
					}
					else if (otl instanceof LifeCycleType) {
						if (!ds.properties().hasProperty(P_DATASOURCE_IDLC.key())) {
							errorMsg = "data source '"+ds.id()+"' lacks a life cycle identifier column ('"
								+P_DATASOURCE_IDLC.key()+"' property)";
							actionMsg = "set the optional property '"+P_DATASOURCE_IDLC.key()+"'"+
								" in data source '"+ds.id()+"'";
						}
					}
				}
			}
		}
		return this;
	}

}
