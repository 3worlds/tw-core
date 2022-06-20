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

import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;

/**
 * checks that the lifecycle referred to by a group is compatible with its
 * groupType.
 *
 * @author Jacques Gignoux - 10 janv. 2020 refactored 11 dec. 2020
 *
 */

public class LifeCycleSubGroupsQuery extends QueryAdaptor {

	@Override
	public Queryable submit(Object input) {
		initInput(input);
//		Group localItem = (Group) input;
//		LifeCycle lc = (LifeCycle) get(localItem.edges(Direction.OUT), selectZeroOrOne(hasTheLabel(E_CYCLE.label())),
//				endNode());
//		if (lc == null)
//			return this;
//		else {
//			LifeCycleType lct = (LifeCycleType) lc.getParent();
//			GroupType gt = (GroupType) localItem.getParent();
//			if (gt.getParent() != null) {
//				if (!gt.getParent().equals(lct)) {//??????
//					String[] msgs = TextTranslations.getLifeCycleSubGroupsQuery();
//					actionMsg = msgs[0];
//					errorMsg = msgs[1];
////					actionMsg = "Reconfigure GroupType to have at least one child group belonging to each category of it's categorySet.";
////					errorMsg = "A life cycle group must have at least one child group "
////							+ "belonging to each category of it's categorySet.";
////					errorMsg = "Expected life cycle group to have at least one child group to belong to each category of it's categorySet but found none.";
//					return this;
//				}
//			}
//		}
////		LifeCycleType lc = (LifeCycleType) get(localItem.edges(Direction.OUT),
//			selectZeroOrOne(hasTheLabel(E_CYCLE.label())),
//			endNode());
//		if (lc==null)
//			satisfied = true;
//		else {
//			// categories of the life cycle
//			Collection<Category> lccats = (Collection<Category>) get(lc.edges(Direction.OUT),
//				selectOne(hasTheLabel(E_APPLIESTO.label())),
//				endNode(),
//				children());
//			int ncats = lccats.size();
//			Set<Category> foundcats = new HashSet<>();
//			// categories of the input node's children, which should be only groups
//			for (TreeNode n:localItem.getChildren()) {
//				if (n instanceof Group) {
//					Group g = (Group) n;
//					Collection<Category> sgcats = (Collection<Category>) get(g.edges(Direction.OUT),
//						selectOne(hasTheLabel(E_GROUPOF.label())),
//						endNode(),
//						outEdges(),
//						selectOneOrMany(hasTheLabel(E_BELONGSTO.label())),
//						edgeListEndNodes());
//					// checks that at least one group category is found in the life cycle's categories
//					for (Category cg:sgcats) {
//						if (lccats.contains(cg))
//							foundcats.add(cg);
//					}
//				}
//			}
//			satisfied = (foundcats.size()==ncats);
//		}
		return this;
	}

}
