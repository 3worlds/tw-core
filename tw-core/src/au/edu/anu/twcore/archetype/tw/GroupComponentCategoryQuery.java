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

import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;

import java.util.LinkedList;
import java.util.List;
import au.edu.anu.qgraph.queries.*;
import au.edu.anu.twcore.ecosystem.structure.ComponentType;
import au.edu.anu.twcore.ecosystem.structure.GroupType;
import fr.cnrs.iees.graph.Node;

/**
 * Checks that a GroupType can instantiate its groups with proper ComponentType categories
 *
 * @author J. Gignoux - 22 déc. 2020
 *
 */
public class GroupComponentCategoryQuery extends QueryAdaptor{

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) { // input is a groupType
		initInput(input);
		if (input instanceof GroupType) {
			List<ComponentType> missing = new LinkedList<>();
			GroupType gt = (GroupType) input;
			List<ComponentType> cts = (List<ComponentType>) get(gt,
				children(),
				selectZeroOrMany(hasTheLabel(N_COMPONENTTYPE.label())));
			if (cts.size()>1) {
				// in this case each componentType must have a initValues refering to a groupId of the groupType
				for (ComponentType ct:cts) {
					List<Node> ivs = new LinkedList<>();
					ivs.addAll((List<Node>) get(ct, 
						children(),
						selectZeroOrMany(hasTheLabel(N_INITIALVALUES.label()))) );
					ivs.addAll((List<Node>)get(ct,
						outEdges(),
						selectZeroOrMany(hasTheLabel(E_LOADFROM.label())),
						edgeListEndNodes()) );
					if (ivs.isEmpty()) 
						missing.add(ct);
				}
				if (!missing.isEmpty())	{
					errorMsg="GroupType '"+gt.id()+"' has >1 ComponentType, it must be able to tell "
						+"which of its group contains which of its ComponentTypes. Please check the consistency of your initial data.";
					actionMsg = "Add a InitialValues node or a loafFrom edge to ComponentType(s) ";
					for (ComponentType ct:missing)
						actionMsg += "'"+ct.id()+"', ";
					actionMsg += "with a 'groupId' property matching one group of GroupType '"+gt.id()+"'";
				}
			}
			// if cts.size==1, no problem with initialisation
			// in theory, cts.size==0 should be a problem because group container content is unknown.
		}
		return this;
	}

}
