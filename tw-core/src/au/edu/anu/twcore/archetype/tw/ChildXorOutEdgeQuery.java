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
import au.edu.anu.qgraph.queries.QueryAdaptor;
import au.edu.anu.qgraph.queries.Queryable;
import au.edu.anu.twcore.TextTranslations;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.impl.TreeGraphNode;

import static au.edu.anu.qgraph.queries.base.SequenceQuery.get;

import java.util.List;

import static au.edu.anu.qgraph.queries.CoreQueries.*;

/**
 * Check that either child nodes of a given label or out-edges of another label
 * are present, but not both.
 * 
 * @author Jacques Gignoux - 16 déc. 2021
 *
 */
public class ChildXorOutEdgeQuery extends QueryAdaptor {

	private final String childLabel;
	private final String outEdgeLabel;

	public ChildXorOutEdgeQuery(StringTable args) {
		childLabel = args.getWithFlatIndex(0);
		outEdgeLabel = args.getWithFlatIndex(1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		if (input instanceof TreeGraphNode) {
			TreeGraphNode localItem = (TreeGraphNode) input;
			List<TreeGraphNode> children = (List<TreeGraphNode>) get(localItem.getChildren(),
					selectZeroOrMany(hasTheLabel(childLabel)));
			boolean childPresent = !children.isEmpty();
			List<Edge> outNodes = (List<Edge>) get(localItem, outEdges(), selectZeroOrMany(hasTheLabel(outEdgeLabel)));
			boolean outNodePresent = !outNodes.isEmpty();
			if (!(childPresent ^ outNodePresent)) {
				String[] msgs = TextTranslations.getChildXorOutEdgeQuery(childLabel, outEdgeLabel);
				actionMsg = msgs[0];
				errorMsg = msgs[1];
			}
		}
		return this;
	}

}
