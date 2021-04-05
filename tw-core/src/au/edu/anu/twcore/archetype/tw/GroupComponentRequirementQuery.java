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
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import java.util.Collection;

import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.TextTranslations;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.Node;

/**
 * Checks that a group either has in 'instanceOf' edges or one 'groupOf' edge
 *
 * @author J. Gignoux - 22 déc. 2020
 *
 */
public class GroupComponentRequirementQuery extends QueryAdaptor {

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		Node group = (Node) input;
		Edge groupof = (Edge) get(group.edges(Direction.OUT), selectZeroOrOne(hasTheLabel(E_GROUPOF.label())));
		Collection<Edge> instofs = (Collection<Edge>) get(group.edges(Direction.IN),
				selectZeroOrMany(hasTheLabel(E_INSTANCEOF.label())));
		if (instofs.isEmpty())// if no component is instance of this group
			if (groupof == null) {// Group must have a groupOf link to a ComponentType
				// that's what it says
				String[] msgs = TextTranslations.getGroupComponentRequirementQuery(group.toShortString(),
						E_INSTANCEOF.label(), N_COMPONENTTYPE.label(), E_GROUPOF.label(), N_GROUPTYPE.label());
				actionMsg = msgs[0];
				errorMsg = msgs[1];
//				actionMsg = "Make '" + group + "' an '" + E_INSTANCEOF.label() + "' some '" + N_COMPONENTTYPE.label()
//						+ ":' OR make it a  '" + E_GROUPOF.label() + "' of some '" + N_GROUPTYPE.label() + "'.";
//				errorMsg = "Expected inEdge '" + E_INSTANCEOF.label() + "' from some '" + N_COMPONENTTYPE.label()
//						+ ":' OR outEdge '" + E_GROUPOF.label() + "' to some '" + N_GROUPTYPE.label()
//						+ "' but found neither case.";
			}
//		!XOR both true OR both false : here we have false/true so need groupof!=null? but easier to read above BUT what is wanted?
//		if (!((groupof == null) ^ (instofs.isEmpty()))) {
//			errorMsg = "If no Component is instance of Group, Group must have a groupOf link to a ComponentType.";
//		}
		return this;
	}

}
