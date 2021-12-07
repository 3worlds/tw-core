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
import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.experiment.DataSource;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;

/**
 * Check that a data source has a group identifier column if components refer to a group
 * 
 * @author Jacques Gignoux - 7 dec. 2021
 *
 */
// TODO: move messages to TextTranslations.
public class CheckFileGroupQuery extends QueryAdaptor {
	
	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {  // input is a DataSource node
		initInput(input);
		if (input instanceof DataSource) {
			TreeGraphDataNode ds = (TreeGraphDataNode) input;
			// does this data source have a groupId property?
			boolean hasGroupId = ds.properties().hasProperty(P_DATASOURCE_IDGROUP.key());
			// get component groups
			List<TreeGraphDataNode> components = (List<TreeGraphDataNode>) get(ds,inEdges(),
				selectZeroOrMany(hasTheLabel(E_LOADFROM.label())),
				edgeListStartNodes());
			for (TreeGraphDataNode comp:components) {
				TreeGraphDataNode group = (TreeGraphDataNode) get(comp,outEdges(),
					selectZeroOrOne(hasTheLabel(E_INSTANCEOF.label())),
					endNode());
				// being member of a group and having no groupId prop in data source is an error
				if ((group!=null)&&(!hasGroupId)) {
					errorMsg = "data source '"+ds.id()+"' lacks a group identifier column ('"
						+P_DATASOURCE_IDGROUP.key()+"' property)";
					actionMsg = "set the optional property '"+P_DATASOURCE_IDGROUP.key()+"'"+
						" in data source '"+ds.id()+"'";
				}
			}
			// todo: if no component, but a group is present (with 'groupOf' relation)
		}
		return this;
	}

}
