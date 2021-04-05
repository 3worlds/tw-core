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
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.N_CATEGORY;

import java.util.LinkedList;
import java.util.List;

import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.TextTranslations;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.CategorySet;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.TreeNode;
/**
 * A Query to check that a ComponentType only belongs to one category of a given
 * category set (categories within a set are assumed exclusive)
 * 
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class ExclusiveCategoryQuery extends QueryAdaptor{

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		Node localItem = (Node) input;
		Iterable<Category> cats = getLocalCategories(localItem);

		if (!cats.iterator().hasNext())
			return this;
		

		List<CategorySet> csl = new LinkedList<>();
		for (Category c : cats) {
			CategorySet cs = (CategorySet) c.getParent();
			if (!csl.contains(cs))
				csl.add(cs);
			else {
				String[] msgs = TextTranslations.getExclusiveCategoryQuery(localItem.toShortString(),cs.toShortString());
				actionMsg = msgs[0];
				errorMsg = msgs[1];
//				errorMsg = "'"+localItem.toShortString()+"'cannot belong to two categories of the same set. Two categories of set '"
//						+ cs.toShortString() + "' were found.";
				return this;
			}
		}
		return this;

	}
	public static boolean propose(TreeNode startNode, TreeNode proposedEndNode) {

		Iterable<Category> cats = getLocalCategories(startNode);

		if (!cats.iterator().hasNext()) {
			// log.info(endNode.id()+": OK - no other categories in use.");
			return true;
		}
		Node proposedCatSet = proposedEndNode.getParent();
		if (proposedCatSet == null) {// Tested: never happens - the editor filters this out.
//			log.info(endNode.id()+": no parent category set for comparison.");
			return true;
		}

		for (Category c : cats) {
			CategorySet cs = (CategorySet) c.getParent();
			// might be null
			if (cs != null) {
				if (cs.id().equals(proposedCatSet.id())) {
					// log.info(endNode.id()+": Failed - category has the same parent.");
					return false;
				}
			}
		}
		// log.info(endNode.id()+": OK for edge proposal");
		return true;

	}
	@SuppressWarnings("unchecked")
	private static Iterable<Category> getLocalCategories(Node localItem) {
		return (Iterable<Category>) get(localItem.edges(Direction.OUT), edgeListEndNodes(),
				selectZeroOrMany(hasTheLabel(N_CATEGORY.label())));
	}

}
