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
import fr.cnrs.iees.twcore.constants.LifespanType;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import au.edu.anu.twcore.DefaultStrings;
import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.dynamics.FunctionNode;
import au.edu.anu.twcore.ecosystem.dynamics.ProcessNode;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.Related;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.RelationContainer;

/**
 * This is equivalent to the SystemFactory, but for SystemRelation
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class RelationType
		extends InitialisableNode
		implements LimitedEdition<RelationContainer>,
			Related<CategorizedComponent>, Sealable {

	// INNER CLASSES
	// predefined values for the type property of SystemRelation
	public enum predefinedRelationTypes {
		parentTo	("*parentTo*"), 	// start if the parent of end
		returnsTo	("*returnsTo*"),	// start changes state of end
//		comprises	("*comprises*"),	// start is made of end
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
	private class cat implements Categorized<CategorizedComponent> {
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

	// FIELDS
	private boolean sealed = false;
	// from and to category lists
	private cat fromCat, toCat;
	private Map<Integer,RelationContainer> relconts = new HashMap<>();
	// if true, means ephemeral relations must be deleted at the end of time step
	private boolean autoDelete = false;

	// CONSTRUCTORS
	public RelationType(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public RelationType(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	// METHODS

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
			// ephemeral relations must be cleared at each time step if they have no maintainRelationFunction
			if (properties().hasProperty(P_RELATION_LIFESPAN.key()))
				if (properties().getPropertyValue(P_RELATION_LIFESPAN.key()).equals(LifespanType.ephemeral)) {
					List<ProcessNode> procs = (List<ProcessNode>) get(edges(Direction.IN),
						selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())),
						edgeListStartNodes());
					// when there is no MaintainRelationFunction in processes pointing to me,
					// ephemeral relations must be cleared at each time step
					autoDelete = true;
					for (ProcessNode proc:procs) {
						List<FunctionNode> fnl = (List<FunctionNode>) get(proc,children(),
							selectZeroOrMany(hasProperty(P_FUNCTIONTYPE.key(),TwFunctionTypes.MaintainRelationDecision)));
						if (!fnl.isEmpty())
							autoDelete = false;
					}
			}
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
			relconts.put(id, new RelationContainer(this,id));
		return relconts.get(id);
	}

	@Override
	public Categorized<CategorizedComponent> from() {
		if (!sealed)
			initialise();
		return fromCat;
	}

	@Override
	public Categorized<CategorizedComponent> to() {
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

	public boolean autoDelete() {
		return autoDelete;
	}

}
