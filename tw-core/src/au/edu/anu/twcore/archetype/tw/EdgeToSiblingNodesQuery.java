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
import static au.edu.anu.qgraph.queries.base.SequenceQuery.*;

import java.util.List;

import au.edu.anu.qgraph.queries.*;
import au.edu.anu.twcore.TextTranslations;
import fr.cnrs.iees.omugi.graph.impl.TreeGraphNode;

/**
 * A Query to check that edges to/from a particular node having the same label
 * point to nodes which are all children of the same node (ie siblings).
 *
 * @author J. Gignoux - 18 nov. 2020
 *
 */

public class EdgeToSiblingNodesQuery extends QueryAdaptor{
	private String label;

	/**
	 * Constructor for use in archetype <strong>.ugt</strong> files.
	 * @param label the label (as returned by {@linkplain fr.cnrs.iees.omugi.graph.Edge#classId})
	 * of the edges that are to be tested
	 */
	public EdgeToSiblingNodesQuery(String label) {
		super();
		this.label = label;
	}

	/**
	 * {@inheritDoc}
	 *
	 *  <p>The expected input is a {@linkplain TreeGraphNode} with IN or OUT
	 *  edges having the <em>label</em> passed to the constructor.</p>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		TreeGraphNode node = (TreeGraphNode) input;
		List<TreeGraphNode> fields = (List<TreeGraphNode>) get(node.edges(),
			selectZeroOrMany(hasTheLabel(label)),
			edgeListOtherNodes(node));
		
		if (fields.size() >= 1) {
			/**
			 * Parent may be null but thats ok. If all parents are null (i.e. during MM
			 * editing) then this query can't make a decision so returning satisfied is ok
			 * until the parent links are re-established.
			 */
			TreeGraphNode theParent = (TreeGraphNode) fields.get(0).getParent();
			for (TreeGraphNode f : fields)
				if ((f.getParent()!=null) && (f.getParent()!=theParent)) {
					String[] msgs = TextTranslations.getEdgeToSiblingNodesQuery(label);
					actionMsg = msgs[0];
					errorMsg = msgs[1];
//					errorMsg = label + " edges must refer to sibling nodes, i.e. nodes with the same parent.";
				}
		}
		return this;
	}

}
