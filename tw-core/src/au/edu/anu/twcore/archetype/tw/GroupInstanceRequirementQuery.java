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
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import java.util.Collection;
import java.util.LinkedList;

import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.TextTranslations;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.CategorySet;
import au.edu.anu.twcore.ecosystem.structure.ComponentType;
import au.edu.anu.twcore.ecosystem.structure.GroupType;
import au.edu.anu.twcore.ecosystem.structure.LifeCycleType;
import fr.cnrs.iees.graph.Direction;

/**
 * Checks that a lifeCycleType has exactly one GroupType matching each of its appliesTo CategorySet
 * (repeat ten times and then ask).
 *
 * @author J. Gignoux - 22 déc. 2020
 *
 */
public class GroupInstanceRequirementQuery extends QueryAdaptor{

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) { // input is a lifeCycleType
		initInput(input);
		if (input instanceof LifeCycleType) {
			LifeCycleType lct = (LifeCycleType) input;
			// get all the group type declared under this lifecycletype
			Collection<GroupType> gts = (Collection<GroupType>) get(lct.getChildren(),
				selectZeroOrMany(hasTheLabel(N_GROUPTYPE.label())));
			// get the category set defining the stages of the life cycle
			// NB possible flaw: if the graph has >1 category set at this point
			// but MM should guarantee this will never happen
			CategorySet lccatset = (CategorySet) get(lct.edges(Direction.OUT),
				selectZeroOrOne(hasTheLabel(E_APPLIESTO.label())),
				endNode());
			if (lccatset!=null) {
				Collection<Category> lccats = (Collection<Category>) lccatset.getChildren();
				// 1st condition: number of group types = number of stage categories
				if (lccats.size()!=gts.size()) {
					actionMsg = "Please provide exactly "+lccats.size()+" GroupTypes to LifeCycleType '"
						+ lct.id()+ "' to match all the categories of CategorySet '"+lccatset.id()+"'";
					errorMsg = "A LifeCycleType must define a GroupType for each of the categories of its CategorySet";
					return this;
				}
				// 2nd condition: each GroupType ComponentType must match one of the categories
				Collection<GroupType> testSet = new LinkedList<>();
				for (Category cat:lccats) {
					for (GroupType gt:gts) {
						Collection<ComponentType> cts = (Collection<ComponentType>) get(gt,
							children(),
							selectZeroOrMany(hasTheLabel(N_COMPONENTTYPE.label())));
						for (ComponentType ct:cts) {
							Collection<Category> ctcats = (Collection<Category>) get(ct.edges(Direction.OUT),
								selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())),
								edgeListEndNodes());
							if (ctcats.contains(cat))
								testSet.add(gt);
						}
					}
				}
				if (lccats.size()!=testSet.size()) {
					String[] msgs = TextTranslations.getGroupInstanceRequirementQuery();
					actionMsg = msgs[0];
					errorMsg = msgs[1];
				}
			}
		}
		return this;
	}

}
