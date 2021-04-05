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
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_CONSTANTS;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_COORDMAPPING;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_DRIVERS;

import java.util.List;

import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.TextTranslations;
import au.edu.anu.twcore.ecosystem.structure.SpaceNode;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.impl.TreeGraphNode;

/**
 * A Query to make sure that fields used as space coordinates are either in a
 * driver or in a constant record
 *
 * @author J. Gignoux - 20 nov. 2020
 *
 */
public class SpaceRecordTypeQuery extends QueryAdaptor {

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		SpaceNode space = (SpaceNode) input;
		List<TreeGraphNode> fields = (List<TreeGraphNode>) get(space.edges(Direction.OUT),
				selectZeroOrMany(hasTheLabel(E_COORDMAPPING.label())), edgeListEndNodes());

		if (!fields.isEmpty()) {
			TreeGraphNode rec = (TreeGraphNode) fields.get(0).getParent();
			List<Edge> ln = (List<Edge>) get(rec.edges(Direction.IN),
					selectZeroOrMany(orQuery(hasTheLabel(E_DRIVERS.label()), hasTheLabel(E_CONSTANTS.label()))));
			if (ln.isEmpty()) {
				String fieldNames = "";
				for (TreeGraphNode n:fields)
					fieldNames+=","+n.toShortString();
				fieldNames = fieldNames.replaceFirst("'", "");
				String[] msgs = TextTranslations.getSpaceRecordTypeQuery(fieldNames);
				actionMsg = msgs[0];
				errorMsg = msgs[1];
//				actionMsg = "Set coordinate field(s) to belong to a record that is used as either a driver or contant.";
//				errorMsg = "coordinate fields ["+fieldNames+"] must belong to a record used as drivers or constants but found none.";
			}
		}
		return this;
	}

}
