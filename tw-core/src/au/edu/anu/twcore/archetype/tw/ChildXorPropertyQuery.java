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

import java.util.List;

import au.edu.anu.omugi.collections.tables.*;
import au.edu.anu.qgraph.queries.QueryAdaptor;
import au.edu.anu.qgraph.queries.Queryable;
import au.edu.anu.twcore.TextTranslations;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.graph.TreeNode;

/**
 * @author Jacques Gignoux - 31/5/2019
 * 
 *         Constraint: some nodes must have ONE of either a property or n child
 *         nodes of a given label
 */

public class ChildXorPropertyQuery extends QueryAdaptor {
	private String nodeLabel;
	private String propertyName;

	public ChildXorPropertyQuery(StringTable args) {
		nodeLabel = args.getWithFlatIndex(0);
		propertyName = args.getWithFlatIndex(1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		TreeNode localItem = (TreeNode) input;
		boolean propertyPresent = false;
		if (localItem instanceof ReadOnlyDataHolder)
			propertyPresent = (((ReadOnlyDataHolder) localItem).properties().hasProperty(propertyName));
		List<Node> ln = (List<Node>) get(localItem, children(), selectZeroOrMany(hasTheLabel(nodeLabel)));
		boolean edgePresent = (!ln.isEmpty());
		boolean ok = (propertyPresent ^ edgePresent);
		if (!ok) {
			String[] msgs = TextTranslations.getChildXorPropertyQuery(propertyName,nodeLabel);
			actionMsg = msgs[0];
			errorMsg = msgs[1];
		}
		return this;
	}

}
