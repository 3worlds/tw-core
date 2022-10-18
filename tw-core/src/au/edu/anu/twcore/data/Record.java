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
package au.edu.anu.twcore.data;

import fr.cnrs.iees.graph.DataHolder;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.ALDataEdge;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.get;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import au.edu.anu.twcore.InitialisableNode;

/**
 * Class matching the "record" node label in the 3Worlds configuration tree.
 * 
 * @author Jacques Gignoux - 31 mai 2019
 *
 */
public class Record extends InitialisableNode {

	/**
	 * Default constructor
	 * 
	 * @param id       Unique identity of this node.
	 * @param props    Property list for this node.
	 * @param gfactory The graph construction factory
	 */
	public Record(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	/**
	 * Donstructor with no properties
	 * 
	 * @param id       Unique identity of this node.
	 * @param gfactory The graph construction factory
	 */
	public Record(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_RECORD.initRank();
	}

	/**
	 * Returns a list of all leaf nodes (Fields or Tables) that are children of the
	 * given record.
	 * 
	 * @param rootRecord The root record of the sub-tree.
	 * @return List of leaf nodes.
	 */
	public static List<TreeGraphDataNode> getLeaves(TreeGraphDataNode rootRecord) {
		List<TreeGraphDataNode> result = new ArrayList<>();
		getLeaves(result, rootRecord);
		return result;
	}

	private static void getLeaves(List<TreeGraphDataNode> result, TreeNode record) {
		for (TreeNode child : record.getChildren()) {
			if (child.classId().equals(N_FIELD.label()))
				result.add((TreeGraphDataNode) child);
			else if (child.classId().equals(N_TABLE.label())) {
				DataHolder table = (DataHolder) child;
				// cannot depend on child record being present
				if (table.properties().hasProperty(P_DATAELEMENTTYPE.key())) {
					result.add((TreeGraphDataNode) child);
				} else// There can be only one child but this is easiest.
					for (TreeNode tableRecord : child.getChildren())
						getLeaves(result, tableRecord);
			}
		}

	}

	/**
	 * Total the dimensions of a record or table in a sub-tree definition.
	 * 
	 * @param parent Starting node of the child search.
	 * @return dimension index|size pair.
	 */
	@SuppressWarnings("unchecked")
	public static int[][] collectDims(TreeNode parent) {
		List<int[]> dimList = new ArrayList<>();
		while (parent != null) {
			if (parent instanceof TableNode) {
				// Have to do this the hard way - not by instantiation
				// Get the edges and sort by rank
				List<ALDataEdge> edges = (List<ALDataEdge>) get(parent.edges(Direction.OUT),
						selectZeroOrMany(hasTheLabel(E_SIZEDBY.label())));
				edges.sort(new Comparator<ALDataEdge>() {

					@Override
					public int compare(ALDataEdge e1, ALDataEdge e2) {
						Integer r1 = (Integer) e1.properties().getPropertyValue(P_DIMENSIONER_RANK.key());
						Integer r2 = (Integer) e2.properties().getPropertyValue(P_DIMENSIONER_RANK.key());
						return r1.compareTo(r2);
					}
				});
				int[] dd = new int[edges.size()];
				dimList.add(dd);
				int idx = 0;
				for (ALDataEdge edge : edges) {
					TreeGraphDataNode dimNode = (TreeGraphDataNode) edge.endNode();
					int size = (Integer) dimNode.properties().getPropertyValue(P_DIMENSIONER_SIZE.key());
					dd[idx++] = size;
				}
			}
			parent = parent.getParent();
		}

		int[][] result = new int[dimList.size()][];
		// reverse the order
		for (int i = dimList.size() - 1; i >= 0; i--) {
			int[] dd = dimList.get(i);
			result[result.length - i - 1] = dd;
		}
		return result;
	}

}
