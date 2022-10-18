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

import java.util.*;

import com.google.common.collect.Sets;

import au.edu.anu.qgraph.queries.*;
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
public class LifeCycleCategoryConsistencyQuery extends QueryAdaptor{

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
					actionMsg = "Provide exactly "+lccats.size()+" GroupTypes to LifeCycleType '"
						+ lct.id()+ "' to match all the categories of CategorySet '"+lccatset.id()+"'";
					errorMsg = "A LifeCycleType must define a GroupType for each of the categories of its CategorySet";
					return this;
				}
				// 2nd condition: each GroupType ComponentTypes must match one of the categories
				Collection<GroupType> testSet = new LinkedList<>();
				Map<GroupType,Set<Category>> catsPerGroup = new HashMap<>();
				for (GroupType gt:gts) {
					catsPerGroup.put(gt,new HashSet<>());
					Collection<ComponentType> cts = (Collection<ComponentType>) get(gt,
						children(),
						selectZeroOrMany(hasTheLabel(N_COMPONENTTYPE.label())));
					// 3rd condition there must be at least one component type per grouptype
					if (cts.size()<1) {
						errorMsg = "GroupType '"+gt.id()+"' must have at least one child ComponentType matching one of "
							+"its LifeCycleType 'appliesTo' categories";
						actionMsg = "Make at least one ComponentType child of GroupType '"+gt.id()+"', belonging to one of the "
							+"'appliesTo' categories of LifeCycleType '"+lct.id()+"'";
						return this;
					}
					for (ComponentType ct:cts) {
						Collection<Category> ctcats = (Collection<Category>) get(ct.edges(Direction.OUT),
							selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())),
							edgeListEndNodes());
						// 6th condition: each ComponentType must have one category of the lifecycle
						Set<Category> a = new HashSet<>();
						a.addAll(ctcats);
						Set<Category> b = new HashSet<>();
						b.addAll(lccats);
						if (Sets.intersection(a,b).isEmpty()) {
							errorMsg = "ComponentType '"+ct.id()+"' child of GroupType '"+gt.id()+"' must belong to one category of LifeCycleType '"
								+lct.id()+"' 'appliesTo' CategorySet";
							actionMsg = "Add membership to one category of LifeCycleType '"
								+lct.id()+"' 'appliesTo' CategorySet to ComponentType '"+ct.id()+"'";
							return this;
						}
						for (Category cat:lccats) {
							if (ctcats.contains(cat)) {
								testSet.add(gt);
								catsPerGroup.get(gt).add(cat);
							}							
						}
					}
				}
//				// 4th condition: all grouptypes must contain component types of a different category of the life cycle set
//				if (lccats.size()!=testSet.size()) {
//					actionMsg = "Make sure that each GroupType defined under LifeCycleType '"+lct.id()
//						+"' has ComponentTypes which categories match each of those the 'appliesTo' CategorySet.";
//					errorMsg = "All categories of LifeCycleType '"+lct.id()+"' category set must be present in its GroupType's ComponentTypes";
//					return this;
//				}
				// 5th condition: all componentTypes of a group type must have the same life cycle category
				for (GroupType gt:catsPerGroup.keySet())
					if (catsPerGroup.get(gt).size()>1) {
						errorMsg = "ComponentTypes children of GroupType '"+gt.id()+"' must all belong to a same category of LifeCycleType '"
							+lct.id()+"' 'appliesTo' CategorySet";
						actionMsg = "Make sure that ComponentTypes children of GroupType '"+gt.id()+"' all belong to a same category of LifeCycleType '"
							+lct.id()+"' 'appliesTo' CategorySet";
						return this;
					}
			}
		}
		return this;
	}

}
