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

import au.edu.anu.qgraph.queries.*;
import fr.cnrs.iees.omugi.graph.Edge;
import fr.cnrs.iees.omugi.graph.impl.TreeGraphNode;

import static au.edu.anu.qgraph.queries.base.SequenceQuery.get;

import java.util.List;

import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;

/**
 * Check that a component, group, lifeCycle, or system node has either
 * <ul>
 * <li>a single initialValues node or</li>
 * <li>one or more loadFrom edges</li>
 * </ul>
 * and
 * <ul>
 * <li>a setInitialState method or</li>
 * <li>none of the above</li>
 * </ul> 
 * 
 * @author Jacques Gignoux - 16 déc. 2021
 *
 */
// TODO: move messages to TextTranslations
public class DataSourceConsistencyQuery extends QueryAdaptor {
	
	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) { 
		initInput(input);
		if (input instanceof TreeGraphNode) {
			TreeGraphNode localItem = (TreeGraphNode) input;
			List<TreeGraphNode> children = (List<TreeGraphNode>) get(localItem.getChildren(),
				selectZeroOrMany(hasTheLabel(N_INITIALVALUES.label())));
			List<Edge> outNodes = (List<Edge>) get(localItem,
				outEdges(), 
				selectZeroOrMany(hasTheLabel(E_LOADFROM.label())));
			if ((children.size()>0)&&(outNodes.size()>0)) {
				actionMsg = "Remove the '"+N_INITIALVALUES.label()+"' child node or all the '"+
					E_LOADFROM.label()+"' edges from node '"+localItem.classId()+":"+localItem.id()+"'";
				errorMsg = "A '"+localItem.classId()+"' node cannot have both an '"+
					N_INITIALVALUES.label()+"' node and '"+E_LOADFROM.label()+"' edges";
			}
		}
		return this;
	}

}
