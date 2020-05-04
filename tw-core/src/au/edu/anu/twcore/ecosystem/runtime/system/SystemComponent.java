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
import java.util.List;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.containers.Contained;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.ALDataNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SharedPropertyListImpl;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_RELATIONTYPE;

/**
 * The main runtime object in 3worlds, representing "individuals" or "agents" or "system
 * components".
 * NB: not a TreeGraphNode ! this may be a problem in the future...
 *
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class SystemComponent
		extends ALDataNode
		implements CategorizedComponent, Contained<ComponentContainer> {

	private Categorized<SystemComponent> categories = null;
	/** container */
	private ComponentContainer container = null;

	public SystemComponent(Identity id, SimplePropertyList props, GraphFactory factory) {
		super(id, props, factory);
	}

	// used only once at init time
	@SuppressWarnings("unchecked")
	@Override
	public void setCategorized(Categorized<? extends CategorizedComponent> cat) {
		if (categories==null)
			categories = (Categorized<SystemComponent>) cat;
	}

	/**
	 *
	 * @return all the category information relevant to this component
	 */
	@Override
	public Categorized<SystemComponent> membership() {
		return categories;
	}

	public TwData parameters() {
		return container.parameters();
	}

	@Override
	public SystemComponent clone() {
		SystemComponent result = (SystemComponent) ((ComponentFactory)categories).newInstance();
		result.properties().setProperties(properties());
		result.setContainer(container());
		return result;
	}

	@SuppressWarnings("unchecked")
	public Iterable<SystemRelation> getRelations() {
		return (Iterable<SystemRelation>) edges(Direction.OUT);
	}

	@SuppressWarnings("unchecked")
	public Collection<SystemRelation> getRelations(String relationType) {
		List<SystemRelation> list = (List<SystemRelation>) get(edges(Direction.OUT),
			selectZeroOrMany(hasProperty(P_RELATIONTYPE.key(),relationType)));
		return list;
	}

	@SuppressWarnings("unchecked")
	public Collection<SystemComponent> getRelatives(String relationType) {
		List<SystemComponent> list = (List<SystemComponent>) get(edges(Direction.OUT),
			selectZeroOrMany(hasProperty(P_RELATIONTYPE.key(),relationType)),
			edgeListEndNodes());
		return list;
	}

	public SystemRelation relateTo(SystemComponent toComponent, String relationType) {
		SystemRelation rel = (SystemRelation) connectTo(Direction.OUT,toComponent,
			new SharedPropertyListImpl(SystemRelation.DEFAULT_PROPERTIES));
		rel.properties().setProperty(P_RELATIONTYPE.key(),relationType);
		return rel;
	}

	public TwData autoVar() {
		return ((SystemComponentPropertyListImpl)properties()).auto();
	}

	// Contained interface

	@Override
	public void setContainer(ComponentContainer container) {
		if (this.container==null)
			this.container = container;
	}

	@Override
	public ComponentContainer container() {
		return container;
	}

	@Override
	public void removeFromContainer() {
		container = null;
	}

}
