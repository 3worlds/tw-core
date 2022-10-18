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
package au.edu.anu.twcore.ecosystem.dynamics;

import au.edu.anu.rscs.aot.collections.DynamicList;
import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.ecosystem.ArenaType;
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.TwProcess;
import au.edu.anu.twcore.ecosystem.runtime.process.AbstractProcess;
import au.edu.anu.twcore.ecosystem.runtime.process.ComponentProcess;
import au.edu.anu.twcore.ecosystem.runtime.process.RelationProcess;
import au.edu.anu.twcore.ecosystem.runtime.process.SearchProcess;
import au.edu.anu.twcore.ecosystem.runtime.space.DynamicSpace;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.tracking.SamplerDataTracker;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import au.edu.anu.twcore.ecosystem.structure.SpaceNode;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static au.edu.anu.qgraph.queries.base.SequenceQuery.*;
import static au.edu.anu.qgraph.queries.CoreQueries.*;

/**
 * Class matching the "ecosystem/dynamics/timeLine/timeModel/Process" node label in the
 * 3Worlds configuration tree. Has no properties.
 *
 * @author Jacques Gignoux - 10 mars 2017
 *
 */
public class ProcessNode
		extends InitialisableNode
		implements LimitedEdition<TwProcess>, Sealable {

	private boolean sealed = false;
	private ArenaType ecosystem = null;

	private Collection<Category> categories = null;
	private RelationType relation = null;

	private Map<Integer,TwProcess> processes = new HashMap<>();
	private List<FunctionNode> functions = null;

	private SpaceNode spaceNode = null;
	private double searchRadius = 0.0;
	private int searchNeighbours = 0;

	// default constructor
	public ProcessNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public ProcessNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		if (!sealed) {
			super.initialise();
			sealed = false;			// timeModel  timeLine    dynamics    ecosystem
//			ecosystem = (Ecosystem) getParent().getParent().getParent().getParent();
			ecosystem = (ArenaType) getParent().getParent().getParent().getParent();
//			timeModel = (TimeModel)getParent();
			// 1 - setting up simulation code execution
			DynamicList<? extends Node> applies = (DynamicList<? extends Node>) get(edges(Direction.OUT),
				selectOneOrMany(hasTheLabel(E_APPLIESTO.label())),
				edgeListEndNodes());
			Node first = applies.getFirst();
			// process applies to a set of categories
			if (first.classId().equals(N_CATEGORY.label()))
				categories = (Collection<Category>) applies;
			else if (first.classId().equals(N_RELATIONTYPE.label()))
				relation = (RelationType) first;
			functions = (List<FunctionNode>) get(getChildren(),
				selectZeroOrMany(hasTheLabel(N_FUNCTION.label())));
			// space
			getSpace();
			sealed = true;
		}
	}

	private void getSpace() {
		ProcessSpaceEdge pse = (ProcessSpaceEdge) get(edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(E_SPACE.label())));
		if (pse!=null) {
			spaceNode = (SpaceNode) pse.endNode();
//			if (spaceNode.properties().hasProperty(P_SPACE_SEARCHRADIUS.key()))
//				searchRadius = (double) pse.properties().getPropertyValue(P_SPACE_SEARCHRADIUS.key());
//			else if (spaceNode.properties().hasProperty(P_SPACE_SEARCHNEIGHBOURS.key()))
//				searchNeighbours = (int) pse.properties().getPropertyValue(P_SPACE_SEARCHNEIGHBOURS.key());
			if (pse.properties().hasProperty(P_SPACE_SEARCHRADIUS.key()))
				searchRadius = (double) pse.properties().getPropertyValue(P_SPACE_SEARCHRADIUS.key());
			else if (pse.properties().hasProperty(P_SPACE_SEARCHNEIGHBOURS.key()))
				searchNeighbours = (int) pse.properties().getPropertyValue(P_SPACE_SEARCHNEIGHBOURS.key());

		}
	}

	@Override
	public int initRank() {
		return N_PROCESS.initRank();
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

	@SuppressWarnings("unchecked")
	private TwProcess makeProcess(int index) {
		AbstractProcess result = null;
		TimerNode tm = (TimerNode) getParent();
		DynamicSpace<SystemComponent> sp = null;
		if (spaceNode!=null)
			sp = spaceNode.getInstance(index);
		if (categories!=null)
			result = new ComponentProcess(ecosystem.getInstance(index).getInstance(),
				categories,tm.getInstance(index),sp,searchRadius,searchNeighbours);
		else if (relation!=null) {
			if ((functions.size()==1) &&
				(functions.get(0).properties().getPropertyValue(P_FUNCTIONTYPE.key())
					.equals(TwFunctionTypes.RelateToDecision)))
				result = new SearchProcess(ecosystem.getInstance(index).getInstance(),
					relation.getInstance(index),tm.getInstance(index),sp,searchRadius,searchNeighbours);
			else
				result = new RelationProcess(ecosystem.getInstance(index).getInstance(),
					relation.getInstance(index),tm.getInstance(index),sp,searchRadius,searchNeighbours);
		}
		for (FunctionNode func:functions) {
			TwFunction funk = func.getInstance(index);
			result.addFunction(funk);
			funk.setProcess(result);
		}
		List<DataTrackerNode> ldt = (List<DataTrackerNode>) get(getChildren(),
			selectZeroOrMany(hasTheLabel(N_DATATRACKER.label())));
		for (DataTrackerNode dt:ldt) {
			result.addDataTracker((SamplerDataTracker<CategorizedComponent,?,Metadata>)
				dt.getInstance(index));
		}
		result.seal();
		return result;
	}

	@Override
	public TwProcess getInstance(int id) {
		if (!sealed)
			initialise();
		if (!processes.containsKey(id))
			processes.put(id, makeProcess(id));
		return processes.get(id);
	}

	public boolean hasSpace() {
		if (!sealed)
			getSpace();
		return (spaceNode!=null);
	}

}
