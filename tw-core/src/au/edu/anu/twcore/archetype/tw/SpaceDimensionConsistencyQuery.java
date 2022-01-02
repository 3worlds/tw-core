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

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_COORDMAPPING;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_SPACETYPE;

import java.util.List;

import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.TextTranslations;
import au.edu.anu.twcore.ecosystem.structure.SpaceNode;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.twcore.constants.SpaceType;

/**
 * Check that the number of edge coordinates of a space exactly matches its
 * dimension.
 *
 * @author J. Gignoux - 18 nov. 2020
 *
 */
public class SpaceDimensionConsistencyQuery extends QueryAdaptor {

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		SpaceNode spn = (SpaceNode) input;
		SpaceType spt = (SpaceType) spn.properties().getPropertyValue(P_SPACETYPE.key());
		int dimension = spt.dimensions();
		List<Edge> coordEdges = (List<Edge>) get(spn.edges(Direction.OUT),
				selectZeroOrMany(hasTheLabel(E_COORDMAPPING.label())));
		// The edge multiplicity is 1..* so if the list is empty another query will flag
		// this. Best not to have 2 queries for what seems like the same (or similar)
		// problem.
		if (coordEdges.isEmpty())
			return this;
		if (coordEdges.size() != dimension) {
			int dif = dimension - coordEdges.size();
			String[] msgs = TextTranslations.getSpaceDimensionConsistencyQuery(dif, dimension, E_COORDMAPPING.label());
			actionMsg = msgs[0];
			errorMsg = msgs[1];
//			if (dif > 0)
//				actionMsg = "Add " + dif + " '" + E_COORDMAPPING.label() + ":' edges.";
//			else// can't happen unless editing by hand.
//				actionMsg = "Remove " + dif + " '" + E_COORDMAPPING.label() + ":' edges.";
//
//			errorMsg = "must have " + dimension + " " + E_COORDMAPPING.label() + " edges.";
		}
		;
		return this;
	}

}
