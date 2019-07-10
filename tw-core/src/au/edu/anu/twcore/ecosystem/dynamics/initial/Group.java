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

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.dynamics.LifeCycle;
import au.edu.anu.twcore.ecosystem.runtime.Parameterised;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemFactory;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.Sealable;

import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;

/**
 * A class matching the "ecosystem/dynamics/initialState/group" node of the 3w configuration
 * 
 * @author Jacques Gignoux - 2 juil. 2019
 *
 */
public class Group extends InitialisableNode implements Sealable, Parameterised {

	private boolean sealed = false;
	private TwData parameters = null;
	private SystemContainer container = null;
	
	// default constructor
	public Group(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public Group(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
		sealed = false;
		Node n = (Node) get(edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(E_GROUPOF.label())),
			endNode());
		if (n!=null) {
			SystemFactory sf = (SystemFactory) n;
			container = sf.makeContainer(id());
			parameters = container.parameters();
		}
		n = (Node) get(edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(E_CYCLE.label())),
			endNode());
		if (n!=null) {
			LifeCycle lc = (LifeCycle) n;
			container = lc.makeContainer(id());
			parameters = container.parameters();
		}
		sealed = true;
	}
	
	@Override
	public TwData getParameters() {
		if (sealed)
			return parameters;
		else
			throw new TwcoreException("attempt to access uninitialised data");
	}

	@Override
	public int initRank() {
		return N_GROUP.initRank();
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

	@Override
	public SystemContainer container() {
		if (sealed)
			return container;
		else
			throw new TwcoreException("attempt to access uninitialised data");
	}

}
