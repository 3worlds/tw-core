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
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.N_RECORD;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import au.edu.anu.qgraph.queries.QueryAdaptor;
import au.edu.anu.qgraph.queries.Queryable;
import au.edu.anu.twcore.TextTranslations;
import au.edu.anu.twcore.data.Record;
import au.edu.anu.twcore.ecosystem.dynamics.ProcessNode;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;

public class CategoryConsistencyQuery extends QueryAdaptor {

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		ProcessNode proc = (ProcessNode) input;
		// see if these are categories OR relationType
		List<TreeGraphDataNode> ltgn = (List<TreeGraphDataNode>) get(proc.edges(Direction.OUT),
				selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())), edgeListEndNodes());
		// if empty we don't care here as OutNodeXorQuery will deal with that
		if (ltgn.isEmpty())
			return this;

		// get all process categories NB XOR Query
		Set<Category> catList = new HashSet<>();
		if (ltgn.get(0) instanceof Category) { // if cat there can be many
			catList.addAll((Collection<Category>) get(proc.edges(Direction.OUT),
					selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())), edgeListEndNodes()));
		} else if (ltgn.get(0) instanceof RelationType) {// if rel there will be only one
			RelationType rel = (RelationType) ltgn.get(0);
			catList.addAll((Collection<Category>) get(rel.edges(Direction.OUT),
					selectZeroOrMany(hasTheLabel(E_TOCATEGORY.label())), edgeListEndNodes()));
			catList.addAll((Collection<Category>) get(rel.edges(Direction.OUT),
					selectZeroOrMany(hasTheLabel(E_FROMCATEGORY.label())), edgeListEndNodes()));
		}

		// get the list of categories that have data
		List<Category> categoriesWithData = new LinkedList<>();
		for (Category c : catList) {
			List<Record> crecs = (List<Record>) get(c.edges(Direction.OUT), edgeListEndNodes(),
					selectZeroOrMany(hasTheLabel(N_RECORD.label())));
			if (!crecs.isEmpty())
				categoriesWithData.add(c);
		}
		// check that at least one ElementType belongs to one of the categories with
		// data
//		boolean result = true;
		for (Category c : categoriesWithData) {
			Collection<Edge> belongers = (Collection<Edge>) get(c.edges(Direction.IN),
					selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())));
			if (belongers.isEmpty()) {
				String[] msgs = TextTranslations.getCategoryConsistencyQuery(E_BELONGSTO.label(), c.toShortString(),
						proc.toShortString());
				actionMsg = msgs[0];
				errorMsg = msgs[1];
				return this;
			}
		}
//		if (!result)// TODO: not clear! What does this mean? process must applyTo a cateogry that
//					// belongsTo something
//			errorMsg = "A Category process '" + proc.id() + "' applies must have a 'belongsTo' edge to an ElementType";
		// Constraint: A Category process 'p1' applies must have a 'belongsTo' edge to
		// an ElementType
//processToRelationOrCategorySpec = check its working - this query should not appear until there is an apply
		return this;
	}

}
