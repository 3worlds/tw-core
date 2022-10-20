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
package au.edu.anu.twcore.ecosystem.runtime.system;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.biology.CreateOtherDecisionFunction;
import au.edu.anu.twcore.ecosystem.structure.Category;
import fr.cnrs.iees.omugi.graph.GraphFactory;
import fr.cnrs.iees.omugi.identity.Identity;
import fr.cnrs.iees.omugi.properties.SimplePropertyList;

/**
 *
 * @author J. Gignoux - 8 déc. 2020
 *
 */
public class LifeCycleComponent extends HierarchicalComponent {

	// the list of groups linked by this life cycle.
	// key is a category signature
	private Map<String,GroupComponent> groups = new HashMap<>();

	public LifeCycleComponent(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	@Override
	public LifeCycleFactory elementFactory() {
		return (LifeCycleFactory) membership();
	}

	public String name() {
		return content().id();
	}

	// to be called only once just after construction
	// The reverse mapping of groups by categories is only possible because all groups of a
	// life cycle MUST be of different categories
	protected void addGroup(GroupComponent group) {
		// this looks incredeby slow - at least in the debugger
		SetView<Category> set = Sets.intersection(elementFactory().stageCategories(),
			group.content().itemCategorized().categories());
		SortedSet<Category> sset = new TreeSet<>();
		sset.addAll(set);
		String s = Categorized.signature(sset);
		groups.put(s,group);
	}


	public GroupComponent produceGroup (CreateOtherDecisionFunction function) {
		String toCat = elementFactory().toCategories(function);
		for (String gc:groups.keySet())
			if (toCat.contains(gc))
				return groups.get(gc);
		return null;
	}

	public GroupComponent recruitGroup(String toCat) {
		for (String gc:groups.keySet())
			if (toCat.contains(gc))
				return groups.get(gc);
		return null;
	}


}
