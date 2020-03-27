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
package au.edu.anu.twcore.ecosystem.dynamics.initial;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.edu.anu.rscs.aot.collections.tables.DoubleTable;
import au.edu.anu.twcore.DefaultStrings;
import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.dynamics.Initialiser;
import au.edu.anu.twcore.ecosystem.dynamics.LocationEdge;
import au.edu.anu.twcore.ecosystem.runtime.containers.CategorizedContainer;
import au.edu.anu.twcore.ecosystem.runtime.space.DynamicSpace;
import au.edu.anu.twcore.ecosystem.runtime.space.LocatedSystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.space.Location;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.structure.ComponentType;
import au.edu.anu.twcore.ecosystem.structure.SpaceNode;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;

/**
 *
 * @author Jacques Gignoux - 2 juil. 2019
 *
 */
public class Component
		extends InitialisableNode
		implements Sealable, LimitedEdition<SystemComponent>, DefaultStrings {

	private boolean sealed = false;
//	private TwData variables = null;
	private ComponentType componentFactory = null;
	// This is FLAWED: assumes only ONE component per simulator ???, no, its fine, different components
	// have different Component nodes
	private Map<Integer,SystemComponent> individuals = new HashMap<>();

	private Map<SpaceNode,double[]> coordinates = new HashMap<>();

	// default constructor
	public Component(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public Component(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		super.initialise();
		sealed = false;
		componentFactory = (ComponentType) get(edges(Direction.OUT),
			selectOne(hasTheLabel(E_INSTANCEOF.label())),
			endNode());
		List<LocationEdge> spaces = (List<LocationEdge>) get(edges(Direction.OUT),
			selectZeroOrMany(hasTheLabel(E_LOCATION.label())));
		for (LocationEdge spe:spaces) {
			SpaceNode space = (SpaceNode) spe.endNode();
			DoubleTable tab = (DoubleTable) spe.properties().getPropertyValue(P_SPACE_COORDINATES.key());
			double[] coord = new double[tab.size()];
			for (int i=0; i<coord.length; i++)
				coord[i] = tab.getWithFlatIndex(i);
			coordinates.put(space,coord);
		}
		sealed = true;
	}

	@Override
	public int initRank() {
		return N_COMPONENT.initRank();
	}

//	public TwData getVariables() {
//		if (sealed)
//			return variables;
//		else
//			throw new TwcoreException("attempt to access uninitialised data");
//	}

	@Override
	public Sealable seal() {
		sealed = true;
		return this;
	}

	@Override
	public boolean isSealed() {
		return sealed;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SystemComponent getInstance(int id) {
		if (!sealed)
			initialise();
		if (!individuals.containsKey(id)) {
			// instantiate component
			SystemComponent sc = componentFactory.getInstance(id).newInstance();
			// fill component with initial values
			for (TreeNode tn:getChildren())
				if (tn instanceof VariableValues) {
					// this copies all variables contained in Drivers but ignores automatc variables
					((VariableValues)tn).fill(sc.currentState());
					// this copies automatic variables, if any
					((VariableValues)tn).fill(sc.autoVar());
				}
			// including spatial coordinates
			for (SpaceNode spn:coordinates.keySet()) {
				DynamicSpace<SystemComponent,LocatedSystemComponent> sp = spn.getInstance(id);
				Location loc = sp.makeLocation(coordinates.get(spn));
				LocatedSystemComponent lsc = new LocatedSystemComponent(sc,loc);
				sp.addInitialItem(lsc);
			}
			// insert component into container
			LimitedEdition<ComponentContainer> p = (LimitedEdition<ComponentContainer>) getParent();
			// first case: no groups are specified, the component is required to be stored directly
			// at the ecosystem level.
			// In order not to break the logic of containers, which must only contain items of the
			// same category signature, we must create a new group for every new category signature
			// and put the new SC into it. The ecosystem container cannot store any SC directly because
			// it's got no categories.
			if (p instanceof InitialState) {
				ComponentContainer ecoCont = p.getInstance(id);
				ComponentContainer theCont = null;
				for (CategorizedContainer<SystemComponent> cont:ecoCont.subContainers()) {
					if (cont.categoryInfo().categories().equals(sc.membership().categories())) {
						theCont = (ComponentContainer) cont;
						break;
					}
				}
				if (theCont==null) {
					String groupName = defaultPrefix + "group" + nameSeparator + sc.membership().categoryId();
					// if there were parameters attached to the SC, attach them to its group
					// CAUTION: this is only possible if there is just ONE permanent component
					ParameterValues pv = null;
					for (TreeNode tn:getChildren())
						if (tn instanceof ParameterValues) {
							pv = (ParameterValues) tn;
							break;
					}
					if (pv==null)
						theCont = new ComponentContainer(sc.membership(),groupName,ecoCont,null,null);
					else {
						theCont = new ComponentContainer(sc.membership(),groupName,ecoCont,
							componentFactory.newParameterSet(),null);
						pv.fill(theCont.parameters());
						// compute secondary parameters if initialiser present
						Initialiser.computeSecondaryParameters(this,theCont,id);
					}
					theCont.addInitialItem(sc);
					sc.setContainer((ComponentContainer) theCont);
				}
			}
			// second case: the container has categories, then they must match those of the component
			else if	(sc.membership().categories().equals(p.getInstance(id).categoryInfo().categories())) {
				ComponentContainer c = p.getInstance(id);
				c.addInitialItem(sc);
				sc.setContainer(c);
			}
			individuals.put(id,sc);
		}
		return individuals.get(id);
	}

}
