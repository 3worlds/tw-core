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

import au.edu.anu.rscs.aot.archetype.Archetypes;
import au.edu.anu.omugi.collections.tables.*;
import au.edu.anu.qgraph.queries.QueryAdaptor;
import au.edu.anu.qgraph.queries.Queryable;
import au.edu.anu.twcore.TextTranslations;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.graph.Tree;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.SimpleTree;
import fr.cnrs.iees.graph.io.GraphImporter;

/**
 * A Query to be processed while in an archetype - use it to check whole
 * subtrees of an archetype conditional on the presence / value of some property
 * for example: When specifying EcologicalProcess in the 3w archetype, one has
 * to set a class property matching a proper class name in the code. Depending
 * on this class, a whole subtree of edges and nodes, and specific properties
 * must be checked. There is no way to simply express this conditionality in the
 * current archetype syntax. SO I developed this Query to do that: based on the
 * value of some property, it will load a sub-archetype and test the current
 * AotNode and all its attached sub-tree against this sub-archetype.
 * 
 * This is a bit cheating with the Query concept -- maybe a better
 * implementation is required within the Archetypes syntax. But maybe not. I
 * think it's important to keep archetypes simple.
 * 
 * @author gignoux - 22 nov. 2016
 *
 */
public class CheckSubArchetypeQuery extends QueryAdaptor{
	private String pKey;
	private String pValue;
	private String fileName;
	/**
	 * @param parameters parameters[0] = name of the property on which this
	 *                   archetype is conditioned parameters[1] = value of this
	 *                   property parameters[2] = name of the sub-archetype file to
	 *                   check the node subtree against.
	 */
	public CheckSubArchetypeQuery(StringTable parameters) {
		super();
		pKey = parameters.getWithFlatIndex(0);
		pValue = parameters.getWithFlatIndex(1);
		fileName = parameters.getWithFlatIndex(2);
	}

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		ReadOnlyDataHolder localItem =  (ReadOnlyDataHolder) input;
		TreeNode node = (TreeNode) input;
		Object givenpValue = localItem.properties().getPropertyValue(pKey);
		if (pValue.equals(givenpValue.toString())) {
			Tree<?> tree = (Tree<?>) GraphImporter.importGraph(fileName,getClass());
			Tree<TreeNode> treeToCheck = new SimpleTree<TreeNode>(node.factory());
			for (TreeNode tn:node.subTree())
				treeToCheck.addNode((TreeNode) tn);
			Archetypes checker = new Archetypes();
			// Check the 3worlds archetype is ok -- TODO hum lets see how this goes
			if (checker.isArchetype(tree)) {
				checker.check(treeToCheck,tree);
				if (!(checker.errorList()==null)){
					String[] msgs = TextTranslations.getCheckSubArchetypeQuery(fileName);
					actionMsg = msgs[0];
					errorMsg = msgs[1];
					result = checker.errorList();
				}
			}
			else
				if (tree.root()!=null)
					throw new IllegalArgumentException("Sub-archetype '"+tree.root().toShortString()+"' is not a valid archetype");
				else
					throw new IllegalArgumentException("Sub-archetype '"+tree.toShortString()+"' is not a valid archetype");

		}
		return this;
	}

}
