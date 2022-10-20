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
package au.edu.anu.twcore.ecosystem.structure;

import fr.cnrs.iees.omugi.graph.*;
import fr.cnrs.iees.omugi.identity.Identity;
import fr.cnrs.iees.omugi.properties.SimplePropertyList;
import fr.cnrs.iees.omugi.properties.impl.ExtendablePropertyListImpl;
import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.data.Record;

/**
 * Class matching the "category" node label in the 3Worlds configuration tree.
 * Has no properties.
 * Categories are singleton, ie only one category of a given type can exist. This is normally
 * guaranteed by the fact that category name = category id, and since ids are unique
 * no duplication is possible.
 *
 * @author Jacques Gignoux - 29 mai 2019
 *
 */
public class Category extends InitialisableNode implements Comparable<Category> {

	// default categories present in ALL models
	// hierarchy
	public final static String arena 		= "*arena*";
	public final static String lifeCycle 	= "*life cycle*";
	public final static String group 		= "*group*";
	public final static String component 	= "*component*";
	public final static String relation 	= "*relation*";
	// lifespan
	public final static String permanent 	= "*permanent*";
	public final static String ephemeral 	= "*ephemeral*";
	// Composition: assemblage vs individual
	public final static String assemblage 	= "*assemblage*";
	public final static String atomic 	= "*atomic*";

	// default constructor
	public Category(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public Category(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_CATEGORY.initRank();
	}

	public String name() {
		return id();
	}

	public CategorySet categorySet() {
		return (CategorySet) getParent();
	}

	@Override
	public int compareTo(Category other) {
		return this.id().compareTo(other.id());
	}
	
	// SLOW! only for init time
	public Collection<String> fields(String datagroup) {
		List<String> list = new LinkedList<>();
		Record rec = (Record) get(edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(datagroup)),
			endNode());
		if (rec!=null)
			for (TreeNode tn:rec.getChildren())
				list.add(tn.id());
		return Collections.unmodifiableCollection(list);
	}

}
