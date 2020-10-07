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

import java.util.HashSet;
import java.util.Set;

import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.Related;
import au.edu.anu.twcore.ecosystem.runtime.containers.DynamicContainer;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.identity.impl.ResettableLocalScope;
import fr.cnrs.iees.twcore.constants.LifespanType;
import fr.ens.biologie.generic.Resettable;
import fr.ens.biologie.generic.utils.Duple;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_RELATION_LIFESPAN;

/**
 * Management of relations (ie delayed addition and removal). this is NOT a container, i.e. relations
 * are not stored here, they are stored by the SystemComponent edge lists.
 *
 * @author Jacques Gignoux - 16 janv. 2020
 *
 */
public class RelationContainer
		implements DynamicContainer<SystemRelation>, Resettable, Related<CategorizedComponent>  {

	private Identity id = null;
	//
	private RelationType relationType = null;
	// the list of system component pairs to later relate
	private Set<Duple<CategorizedComponent,CategorizedComponent>> relationsToAdd = new HashSet<>();
	// the list of system relations to remove
	private Set<SystemRelation> relationsToRemove = new HashSet<>();
	private boolean changed = false;
	private boolean permanent = false;

	public RelationContainer(RelationType rel) {
		super(); // since they are different local scopes it may work...
		relationType = rel;
		id = scope().newId(true,rel.id()); // not the same scope, should work ?
		if (rel.properties().hasProperty(P_RELATION_LIFESPAN.key()))
			permanent =  rel.properties().getPropertyValue(P_RELATION_LIFESPAN.key()).equals(LifespanType.permanent);
	}

	@Override
	public void postProcess() {
		relationsToAdd.clear();
		relationsToRemove.clear();
		((ResettableLocalScope)scope()).postProcess();
	}

	@Override
	public void addItem(SystemRelation item) {
		throw new TwcoreException("Relations cannot be directly added to relation container "
			+ "- must be a pair of Components");
	}

	// use this instead of the previous
	public void addItem(CategorizedComponent from, CategorizedComponent to) {
		relationsToAdd.add(new Duple<>(from,to));
	}

	@Override
	public void removeItem(SystemRelation relation) {
		relationsToRemove.add(relation);
	}

	@Override
	public void effectChanges() {
		// delete all old relations
		for (SystemRelation sr:relationsToRemove) {
			sr.startNode().disconnectFrom(Direction.OUT,sr.endNode()); // Do NOT use sr.disconnect() --> ConcurrentModificationException
			sr.detachFromContainer();
		}
		relationsToRemove.clear();
		// establish all new relations
		for (Duple<CategorizedComponent,CategorizedComponent> item : relationsToAdd) {
			SystemRelation sr = item.getFirst().relateTo(item.getSecond(),relationType.id());
			sr.setContainer(this);
			sr.setRelated(relationType);
		}
		relationsToAdd.clear();
		changed = false;
	}

	@Override
	public Categorized<CategorizedComponent> from() {
		return relationType.from();
	}

	@Override
	public Categorized<CategorizedComponent> to() {
		return relationType.to();
	}

	public RelationType type() {
		return relationType;
	}

	@Override
	public String id() {
		return id.id();
	}

	@Override
	public boolean changed() {
		return changed;
	}

	@Override
	public void change() {
		changed = true;
	}

	public boolean isPermanent() {
		return permanent;
	}



}
