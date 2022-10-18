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

import au.edu.anu.omugi.collections.tables.*;
import au.edu.anu.qgraph.queries.Queryable;
import fr.cnrs.iees.graph.*;

/**
 * Check that a node has at least one out edge of a given set of labels
 * 
 * @author gignoux
 *
 */
public class NodeAtLeastOneOutEdgeLabelOfQuery extends RequiredLabelQuery {
	
	public NodeAtLeastOneOutEdgeLabelOfQuery(String... lab) {
		super(lab);
	}

	public NodeAtLeastOneOutEdgeLabelOfQuery(StringTable el) {
		super(el);
	}

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		if (input instanceof Node) {
			int count = countLabels(((Node)input).edges(Direction.OUT));
			if (count==0) {
				actionMsg = "Add out-edge of one type among " + requiredLabels.toString() + "to node '"+input.toString()+"'.";
				errorMsg = "Expected at least one out-edge labelled " + requiredLabels.toString() +" for node '"
					+ input.toString() + "' but found none.";
			}
			else if (count<0) {
				errorMsg = "Count is negative - How did you manage to do that?";
				actionMsg = "You should get some pills, a lot of rest, and come back to work later.";
			}
		}
		return this;
	}

}
