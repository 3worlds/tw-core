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

import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;
import static au.edu.anu.rscs.aot.queries.CoreQueries.edgeListEndNodes;
import static au.edu.anu.rscs.aot.queries.CoreQueries.hasTheLabel;
import static au.edu.anu.rscs.aot.queries.CoreQueries.selectOneOrMany;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import au.edu.anu.twcore.DefaultStrings;
import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.Related;
import au.edu.anu.twcore.ecosystem.runtime.system.RelationContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;

/**
 * This is equivalent to the SystemFactory, but for SystemRelation
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class RelationType 
		extends InitialisableNode 
		implements LimitedEdition<RelationContainer>, 
			Related<SystemComponent>, Sealable, DefaultStrings {
	
	// predefined values for the type property of SystemRelation
	public enum predefinedRelationTypes {
		parentTo	(defaultPrefix+"parentTo"), 	// start if the parent of end
		returnsTo	(defaultPrefix+"returnsTo"),	// start changes state of end
		comprises	(defaultPrefix+"comprises"),	// start is made of end
		;
		private final String key;		
		private predefinedRelationTypes(String string) {
			this.key = string;
		}
		public String key() {
			return key;
		}
	}
	
	// a little class to record the from and to category lists
	private class cat implements Categorized<SystemComponent> {
		private SortedSet<Category> categories = new TreeSet<>();
		private String categoryId = null;
		private cat(Collection<Category>cats) {
			super();
			categories.addAll(cats);
			buildCategorySignature();
		}
		@Override
		public Set<Category> categories() {
			return categories;
		}
		@Override
		public String categoryId() {
			return categoryId;
		}
	}
	private boolean sealed = false;
	// from and to category lists
	private cat fromCat, toCat;
	
	private Map<Integer,RelationContainer> relconts = new HashMap<>();
	

	public RelationType(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public RelationType(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		if (!sealed) {
			super.initialise();
			Collection<Category> tocats = (Collection<Category>) get(edges(Direction.OUT),
				selectOneOrMany(hasTheLabel(E_TOCATEGORY.label())), 
				edgeListEndNodes());
			toCat = new cat(tocats);
			Collection<Category> fromcats = (Collection<Category>) get(edges(Direction.OUT),
				selectOneOrMany(hasTheLabel(E_FROMCATEGORY.label())), 
				edgeListEndNodes());
			fromCat = new cat(fromcats);
			sealed = true;
		}
	}

	@Override
	public int initRank() {
		return N_RELATIONTYPE.initRank();
	}

	@Override
	public RelationContainer getInstance(int id) {
		if (!sealed)
			initialise();
		if (!relconts.containsKey(id))
			relconts.put(id, new RelationContainer(this));
		return relconts.get(id);
	}
	
	@Override
	public Categorized<SystemComponent> from() {
		if (!sealed)
			initialise();
		return fromCat;
	}

	@Override
	public Categorized<SystemComponent> to() {
		if (!sealed)
			initialise();
		return toCat;
	}

	@Override
	public Sealable seal() {
		sealed = true;
		return this;
	}

	@Override
	public boolean isSealed() {
		return sealed;
	}

}
