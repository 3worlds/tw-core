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
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import java.util.List;

import au.edu.anu.qgraph.queries.*;
import au.edu.anu.twcore.TextTranslations;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.CategorySet;
import au.edu.anu.twcore.ecosystem.structure.LifeCycleType;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.impl.TreeGraphNode;

/**
 * A Query to check that a categorized object's categories belong to a
 * categorySet found in its parent - designed for testing that recruit and
 * produce nodes only deal with the categorySet applicable to their parent
 * lifecycle
 *
 * @author Jacques Gignoux - 11 sept. 2019
 *
 */
// tested, works ok. 11/9/2019
// refactored and tested 9/2/2021
public class IsInLifeCycleCategorySetQuery extends QueryAdaptor {

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) { // input is produce or recruit node
		initInput(input);
		TreeGraphNode node = (TreeGraphNode) input;
		LifeCycleType lifecycleType = (LifeCycleType) node.getParent();
		CategorySet catset = (CategorySet) get(lifecycleType.edges(Direction.OUT), selectZeroOrOne(hasTheLabel(E_APPLIESTO.label())),
				endNode());
		// Too soon to know
		if (catset ==null)
			return this;
		List<Category> lifeCycleCats = (List<Category>) get(catset.getChildren(),
				selectZeroOrMany(hasTheLabel(N_CATEGORY.label())));
		// Too soon to know
		if (lifeCycleCats.isEmpty())
			return this;
		List<Category> toCats = (List<Category>) get(node.edges(Direction.OUT),
				selectZeroOrMany(hasTheLabel(E_TOCATEGORY.label())), edgeListEndNodes());
		List<Category> fromCats = (List<Category>) get(node.edges(Direction.OUT),
				selectZeroOrMany(hasTheLabel(E_FROMCATEGORY.label())), edgeListEndNodes());
		int nFromCats = 0;
		for (Category c : fromCats)
			if (lifeCycleCats.contains(c))
				nFromCats++;
		int nToCats = 0;
		for (Category c : toCats)
			if (lifeCycleCats.contains(c))
				nToCats++;
		
		if (nFromCats == 0) {
			String[] msgs = TextTranslations.getIsInLifeCycleCategorySetQuery1(nToCats, nFromCats, 
				E_TOCATEGORY.label(),E_FROMCATEGORY.label());
			actionMsg = msgs[0];
			errorMsg = msgs[1];
//			actionMsg = "What should I do?";
//			errorMsg = " Missing life cycle category for the 'from' link.";
			return this;
		} else if (nFromCats > 1) {
			String[] msgs = TextTranslations.getIsInLifeCycleCategorySetQuery2(nToCats, nFromCats, 
				E_TOCATEGORY.label(),E_FROMCATEGORY.label());
			actionMsg = msgs[0];
			errorMsg = msgs[1];
//			actionMsg = "What should I do?";
//			errorMsg = " Too many life cycle categories for the 'from' link.";
			return this;
		}
		if (nToCats == 0) {
			String[] msgs = TextTranslations.getIsInLifeCycleCategorySetQuery3(nToCats, nFromCats, 
				E_TOCATEGORY.label(),E_FROMCATEGORY.label());
			actionMsg = msgs[0];
			errorMsg = msgs[1];
//			actionMsg = "What should I do?";
//			errorMsg = " Missing life cycle category for the 'to' link.";
			return this;
		} else if (nToCats > 1) {
			String[] msgs = TextTranslations.getIsInLifeCycleCategorySetQuery4(nToCats, nFromCats, 
				E_TOCATEGORY.label(),E_FROMCATEGORY.label());
			actionMsg = msgs[0];
			errorMsg = msgs[1];
//			actionMsg = "What should I do?";
//			errorMsg = " Too many life cycle categories for the 'to' link.";
			return this;
		}
		return this;
	}

}
