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
package au.edu.anu.twcore.ecosystem;

import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.RuntimeGraphData;
import au.edu.anu.twcore.ecosystem.runtime.biology.SetInitialStateFunction;
import au.edu.anu.twcore.ecosystem.runtime.system.ArenaComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.ArenaFactory;
import au.edu.anu.twcore.ecosystem.runtime.tracking.GraphDataTracker;
import au.edu.anu.twcore.ecosystem.structure.newapi.ElementType;
import au.edu.anu.twcore.ui.runtime.DataReceiver;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.ExtendablePropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.rvgrid.rendezvous.GridNode;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.TwFunctionTypes.*;

import java.util.Collection;
import java.util.List;

/**
 * Replacement for ecosystem node - maps to system This class always makes one
 * factory for arenaComponents, and a unique arenaComponent with always be
 * generated because it has to be here in any model
 *
 * @author J. Gignoux - 23 avr. 2020
 *
 */
public class ArenaType extends ElementType<ArenaFactory, ArenaComponent> {

	private boolean makeContainer = true;
	private GraphDataTracker dataTracker;

	// default constructor
	public ArenaType(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public ArenaType(Identity id, GraphFactory gfactory) {
		super(id, gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		super.initialise();
		// if there are no ElementType descendant nodes in the model, means
		// this will be the unique instance of SystemComponent in the model
		// so no need for an associated container. Otherwise it's always here.
		Collection<TreeNode> nl = (Collection<TreeNode>) get(getChildren(),
				selectZeroOrOne(hasTheLabel(N_STRUCTURE.label())), children(), selectZeroOrMany(
						notQuery(orQuery(hasTheLabel(N_CATEGORY.label()), hasTheLabel(N_CATEGORYSET.label())))));
		if (nl == null || nl.isEmpty())
			makeContainer = false;
		seal();
	}

	@Override
	public int initRank() {
		return N_SYSTEM.initRank();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ArenaFactory makeTemplate(int id) {

		List<Edge> listeners = (List<Edge>) get(edges(Direction.IN),
				selectZeroOrMany(hasTheLabel(E_TRACKSYSTEM.label())));

		
		if (!listeners.isEmpty()) {
			// attach time metadata to data tracker - we will need a time from the simulator
			// for each update. The units will be the finest.
			ReadOnlyDataHolder timeLine = (ReadOnlyDataHolder) get(getChildren(),
					selectOne(hasTheLabel(N_DYNAMICS.label())), children(), selectOne(hasTheLabel(N_TIMELINE.label())));
			ExtendablePropertyList l = new ExtendablePropertyListImpl();
			l.addProperties(timeLine.properties());
			l.addProperties(properties());
			dataTracker = new GraphDataTracker(id, l);
		}

		if (setinit != null)
			return new ArenaFactory(categories, /* categoryId(), */
					autoVarTemplate, driverTemplate, decoratorTemplate, lifetimeConstantTemplate,
					(SetInitialStateFunction) setinit.getInstance(id), makeContainer, id(), dataTracker);
		else
			return new ArenaFactory(categories, /* categoryId(), */
					autoVarTemplate, driverTemplate, decoratorTemplate, lifetimeConstantTemplate, null, makeContainer,
					id(), dataTracker);
	}
	
	public  GraphDataTracker getDataTracker() {
		return dataTracker;
	}
	
	public void attachGraphWidget(DataReceiver<RuntimeGraphData,Metadata> widget) {
		if (dataTracker!=null) {
			dataTracker.addObserver(widget);
			dataTracker.sendMetadataTo((GridNode) widget, dataTracker.getInstance());
		}
	}

	/**
	 * The list of function types that are compatible with the Arena
	 */
	public static TwFunctionTypes[] compatibleFunctionTypes = {
		ChangeState,				// arena may change its drivers
		CreateOtherDecision,		// arena may create new items of its ComponentType
		SetInitialState,			// arena may set its constants at creation time
// THESE are not possible because relations are only between SystemComponents		
//		ChangeOtherCategoryDecision,// arena may change the category of a component
//		ChangeOtherState,			// arena change the state of a component
//		DeleteOtherDecision,		// arena may delete another component
//		ChangeRelationState,		// arena may change the state of a relation
//		MaintainRelationDecision,	// arena may maintain a relation
//		RelateToDecision,			// arena may relate to a new component (ALWAYS unindexed search)
//		SetOtherInitialState		// arena may set the initial state of another component ???
	};

}
