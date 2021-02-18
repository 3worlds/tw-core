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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.Related;
import au.edu.anu.twcore.ecosystem.runtime.containers.DynamicContainer;
import au.edu.anu.twcore.ecosystem.runtime.space.Space;
import au.edu.anu.twcore.ecosystem.runtime.tracking.SingleDataTrackerHolder;
import au.edu.anu.twcore.ecosystem.runtime.tracking.SpaceDataTracker;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.identity.impl.ResettableLocalScope;
import fr.cnrs.iees.twcore.constants.LifespanType;
import fr.cnrs.iees.twcore.constants.SimulatorStatus;
import fr.ens.biologie.generic.Resettable;
import fr.ens.biologie.generic.utils.Duple;
import fr.ens.biologie.generic.utils.Logging;

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_RELATION_LIFESPAN;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_RELATION_DIRECTIONAL;

/**
 * Management of relations (ie delayed addition and removal). this is NOT a container, i.e. relations
 * are not stored here, they are stored by the SystemComponent edge lists.
 *
 * @author Jacques Gignoux - 16 janv. 2020
 *
 */
public class RelationContainer
		implements DynamicContainer<SystemRelation>,
			Resettable,
			Related<CategorizedComponent>,
			ObservableDynamicGraph<SystemComponent,SystemRelation> {

	private static Logger log = Logging.getLogger(RelationContainer.class);
	private Identity id = null;
	private scopes scope = new scopes();
	/** things which track changes in this container, eg spaces */
	private Set<DynamicGraphObserver<SystemComponent,SystemRelation>> observers = new HashSet<>();

	//
	private RelationType relationType = null;
	// the list of system component pairs to later relate
	private Set<Duple<CategorizedComponent,CategorizedComponent>> relationsToAdd = new HashSet<>();
	// the list of system relations to remove
	private Set<SystemRelation> relationsToRemove = new HashSet<>();
	private boolean changed = false;
	// set to true if relation instances are permanent
	private boolean permanent = false;
	// set to true if relation instances are directional
	private boolean directional = true;

	public RelationContainer(RelationType rel, int simulatorId) {
		super(); // since they are different local scopes it may work...
		scope = new scopes();
		scope.setSimId(simulatorId);
		if (scope.getContainerScope(simulatorId)==null)
			scope.setContainerScope(simulatorId, new ResettableLocalScope(containerScopeName+"-"+simulatorId));
		relationType = rel;
		id = scope().newId(true,rel.id()); // not the same scope, should work ?
		if (rel.properties().hasProperty(P_RELATION_LIFESPAN.key()))
			permanent =  rel.properties().getPropertyValue(P_RELATION_LIFESPAN.key()).equals(LifespanType.permanent);
		if (rel.properties().hasProperty(P_RELATION_DIRECTIONAL.key()))
			directional = (boolean) rel.properties().getPropertyValue(P_RELATION_DIRECTIONAL.key());
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

	@SafeVarargs
	@Override
	public final void effectChanges(Collection<SystemRelation>...changedLists) {
		// delete all old relations
		for (SystemRelation sr:relationsToRemove) {
			// if a relation is in both deletion and addition sets, remove it from
			// sets and do nothing (this is possible with ephemeral relations)
			Duple<CategorizedComponent,CategorizedComponent> ends =
				new Duple<CategorizedComponent,CategorizedComponent>
					((CategorizedComponent)sr.startNode(),(CategorizedComponent)sr.endNode());
			if (relationsToAdd.contains(ends))
				relationsToAdd.remove(ends);
			else {
				// Do NOT use sr.disconnect() --> ConcurrentModificationException
				log.info(()->"Removing relation "+sr.toShortString());
				sr.startNode().disconnectFrom(Direction.OUT,sr.endNode());
				sr.detachFromContainer();
			}
		}
		for (DynamicGraphObserver<SystemComponent,SystemRelation> o:observers)
			o.onEdgesRemoved(relationsToRemove);
		relationsToRemove.clear();
		// establish all new relations
		for (Duple<CategorizedComponent,CategorizedComponent> item : relationsToAdd) {
			SystemRelation sr = item.getFirst().relateTo(item.getSecond(),relationType.id());
			sr.setContainer(this);
			sr.setRelated(relationType);
			log.info(()->"Creating relation "+sr.toShortString());
			// if autodelete is true, then tag all the new relations to be deleted next time step
//			if (relationType.autoDelete())
//				relationsToRemove.add(sr);
			for (DynamicGraphObserver<SystemComponent,SystemRelation> o:observers)
				o.onEdgeAdded(sr);
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

	public boolean isDirectional() {
		return directional;
	}

	public boolean autoDelete() {
		return relationType.autoDelete();
	}

	public void sendDataForAutoDeletedRelations(Space<SystemComponent> sp,
			long time,
			SimulatorStatus status) {
		if (sp instanceof SingleDataTrackerHolder) {
			SpaceDataTracker dts = (SpaceDataTracker) ((SingleDataTrackerHolder<?>) sp).dataTracker();
			if (!relationsToRemove.isEmpty()) {
				dts.recordTime(status, time);
				for (SystemRelation sr:relationsToRemove) {
					SystemComponent sn = (SystemComponent)sr.startNode();
					SystemComponent en = (SystemComponent) sr.endNode();
					// BUG HERE due to container sometimes set to null
					// DIRTY FIX:
//					if ((sn.container()!=null)&&(en.container()!=null)) // maybe completely wrong
					dts.deleteLine(sn.container().itemId(sn.id()),
						en.container().itemId(en.id()),
						sr.type());
				}
				dts.closeTimeStep();
			}
		}
	}

	@Override
	public ResettableLocalScope scope() {
		return scope.getContainerScope(scope.getSimId());
	}

	// NB relationType has the same id as RelationContainer - different scopes.
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("container for ");
		sb.append(permanent ? "permanent" : "ephemeral");
		sb.append(" relation ")
			.append(relationType.id());
		sb.append("[to create = { ");
		for (Duple<CategorizedComponent,CategorizedComponent> item : relationsToAdd)
			sb.append(item.toString()).append(' ');
		sb.append("}; to delete = { ");
		for (SystemRelation item : relationsToRemove)
			sb.append(item.toString()).append(' ');
		sb.append("}]");
		return sb.toString();
	}

	// ObservableDynamicGraph

	@Override
	public void addObserver(DynamicGraphObserver<SystemComponent, SystemRelation> listener) {
		observers.add(listener);
	}

	@Override
	public void removeObserver(DynamicGraphObserver<SystemComponent, SystemRelation> listener) {
		observers.remove(listener);
	}

	@Override
	public Collection<DynamicGraphObserver<SystemComponent, SystemRelation>> observers() {
		return Collections.unmodifiableCollection(observers);
	}

}
