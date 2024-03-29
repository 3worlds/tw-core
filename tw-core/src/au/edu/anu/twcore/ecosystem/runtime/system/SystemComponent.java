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

import java.util.Arrays;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.biology.SetInitialStateFunction;
import au.edu.anu.twcore.ecosystem.runtime.containers.Contained;
import au.edu.anu.twcore.ecosystem.runtime.space.Locatable;
import au.edu.anu.twcore.ecosystem.runtime.space.LocationData;
import au.edu.anu.twcore.ecosystem.runtime.space.ObserverDynamicSpace;
import fr.cnrs.iees.omugi.graph.GraphFactory;
import fr.cnrs.iees.omugi.graph.impl.ALDataNode;
import fr.cnrs.iees.omugi.identity.Identity;
import fr.cnrs.iees.omugi.properties.SimplePropertyList;

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
		implements CategorizedComponent, Contained<DescribedContainer<SystemComponent>>, Locatable {

	private Categorized<SystemComponent> categories = null;
	/** container */
	private ComponentContainer container = null;

	private LocationData constantLocation = null;
	private boolean dynamicLocation = false;
	// the complete id of this component, includin all its containers
	private String[] hierarchicalId = null;

	public SystemComponent(Identity id, SimplePropertyList props, GraphFactory factory) {
		super(id, props, factory);
		if ((constants()!=null) && (((LocationData)constants()).coordinates()!=null))
			constantLocation = (LocationData)constants(); // this never changes over life time
		else
			if ((currentState()!=null) && (((LocationData)currentState()).coordinates()!=null))
				dynamicLocation = true; // the exact record changes every time step
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

	@Override
	public ComponentFactory elementFactory() {
		return (ComponentFactory) categories;
	}


	@Override
	public SetInitialStateFunction initialiser() {
		return elementFactory().setinit;
	}

	@Override
	public SystemComponent clone() {
		SystemComponent result = elementFactory().newInstance();
		result.properties().setProperties(properties());
		result.setContainer(container());
		return result;
	}

	public SystemComponent cloneStructure() {
		SystemComponent result = elementFactory().newInstance();
		result.setContainer(container());
		return result;
	}


//	// TODO: These three methods could be optimized y storing the edges in a Map sorted by labels
//
//	@SuppressWarnings("unchecked")
//	public Iterable<SystemRelation> getRelations() {
//		return (Iterable<SystemRelation>) edges(Direction.OUT);
//	}
//
//	@SuppressWarnings("unchecked")
//	public Collection<SystemRelation> getRelations(String relationType) {
//		List<SystemRelation> list = (List<SystemRelation>) get(edges(Direction.OUT),
//			selectZeroOrMany(hasProperty(P_RELATIONTYPE.key(),relationType)));
//		return list;
//	}
//
//	@SuppressWarnings("unchecked")
//	public Collection<SystemComponent> getRelatives(String relationType) {
//		List<SystemComponent> list = (List<SystemComponent>) get(edges(Direction.OUT),
//			selectZeroOrMany(hasProperty(P_RELATIONTYPE.key(),relationType)),
//			edgeListEndNodes());
//		return list;
//	}

//	public SystemRelation relateTo(SystemComponent toComponent, String relationType) {
//		SystemRelation rel = (SystemRelation) connectTo(Direction.OUT,toComponent,
//			new SharedPropertyListImpl(SystemRelation.DEFAULT_PROPERTIES));
//		rel.properties().setProperty(P_RELATIONTYPE.key(),relationType);
//		return rel;
//	}

	@Override
	public TwData autoVar() {
		return ((SystemComponentPropertyListImpl)properties()).auto();
	}

	// Contained interface

	@Override
	public void setContainer(DescribedContainer<SystemComponent> container) {
		if (this.container==null) {
			this.container = (ComponentContainer) container;
			hierarchicalId = Arrays.copyOf(container.fullId(),container.fullId().length+1);
			hierarchicalId[hierarchicalId.length-1] = id();
		}
	}

	@Override
	public DescribedContainer<SystemComponent> container() {
		return container;
	}

	@Override
	public void detachFromContainer() {
		container = null;
	}

	@Override
	public String[] hierarchicalId() {
//		String[] s = Arrays.copyOf(container.fullId(),container.fullId().length+1);
//		s[s.length-1] = id();
//		return s;		
		return hierarchicalId;
	}

	@Override
	public boolean isPermanent() {
		return ((ComponentFactory) categories).isPermanent();
	}

	@Override
	public LocationData locationData() {
		if (constantLocation!=null)
			return constantLocation;
		else
			if (dynamicLocation)
				return (LocationData) currentState();
		return null;
	}

	@Override
	public LocationData nextLocationData() {
		if (dynamicLocation)
			return (LocationData) nextState();
		return null;
	}


	@Override
	public boolean mobile() {
		return (dynamicLocation);
	}
	
	public boolean isInSpace(ObserverDynamicSpace space) {
		return ((ComponentFactory) categories).spaces().contains(space);
	}

}
