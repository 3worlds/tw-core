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

import static fr.cnrs.iees.twcore.constants.TwFunctionTypes.ChangeState;
import static fr.cnrs.iees.twcore.constants.TwFunctionTypes.SetInitialState;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import au.edu.anu.twcore.ecosystem.ArenaType;
import au.edu.anu.twcore.ecosystem.dynamics.FunctionNode;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.biology.ChangeCategoryDecisionFunction;
import au.edu.anu.twcore.ecosystem.runtime.biology.CreateOtherDecisionFunction;
import au.edu.anu.twcore.ecosystem.runtime.biology.SetInitialStateFunction;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.LifeCycleComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.LifeCycleFactory;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.Graph;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.Graphable;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.ALGraph;
import fr.cnrs.iees.graph.impl.ALGraphFactory;
import fr.cnrs.iees.graph.impl.ALNode;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ReadOnlyPropertyListImpl;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;
import fr.cnrs.iees.twcore.generators.odd.TwConfigurationAnalyser;
import fr.ens.biologie.generic.utils.Duple;

/**
 *
 * @author J. Gignoux - 8 déc. 2020
 *
 */
public class LifeCycleType 
		extends ElementType<LifeCycleFactory,LifeCycleComponent> 
		implements Graphable<ALNode,ALEdge> {

	// maps of to- and from-categories matching recruit and produce nodes identified
	// by their function node (1..1 relation between produce/recruit and function through
	// 'effectedBy' edge)
	private Map<FunctionNode,Duple<String,String>>
		produceNodes = new HashMap<>(),
		recruitNodes = new HashMap<>();
	private SortedSet<Category> stageCategories = new TreeSet<>();
	
	// a graph of this life cycle - for display or any other use
	private Graph<? extends Node,? extends Edge> lifeCycleGraph = null;

	public LifeCycleType(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public LifeCycleType(Identity id, GraphFactory gfactory) {
		super(id, gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		super.initialise();
		// collect the list of categories acted on by this life cycle type
		Collection<Category> lccats = (Collection<Category>) get(edges(Direction.OUT),
			selectOne(hasTheLabel(E_APPLIESTO.label())),
			endNode(), // this is the categoryset
			children(),
			selectOneOrMany(hasTheLabel(N_CATEGORY.label())));
		stageCategories.addAll(lccats);
		// collect produce nodes information for factory
		Collection<Category> lcat = null;
		Collection<TreeGraphNode> lprod = (Collection<TreeGraphNode>) get(getChildren(),
			selectZeroOrMany(hasTheLabel(N_PRODUCE.label())));
		for (TreeGraphNode prod:lprod) {
			SortedSet<Category> fromProduceCat = new TreeSet<>();
			lcat = (Collection<Category>) get(prod.edges(Direction.OUT),
				selectOneOrMany(hasTheLabel(E_FROMCATEGORY.label())),
				edgeListEndNodes());
			fromProduceCat.addAll(lcat);
			SortedSet<Category> toProduceCat = new TreeSet<>();
			lcat = (Collection<Category>) get(prod.edges(Direction.OUT),
				selectOneOrMany(hasTheLabel(E_TOCATEGORY.label())),
				edgeListEndNodes());
			toProduceCat.addAll(lcat);
			FunctionNode fnode = (FunctionNode) get(prod.edges(Direction.OUT),
				selectOne(hasTheLabel(E_EFFECTEDBY.label())),
				endNode());
			produceNodes.put(fnode,new Duple<>(Categorized.signature(fromProduceCat),
				Categorized.signature(toProduceCat)));
		}
		// collect recruit nodes information for factory
		Collection<TreeGraphNode> lrec = (Collection<TreeGraphNode>) get(getChildren(),
			selectZeroOrMany(hasTheLabel(N_RECRUIT.label())));
		for (TreeGraphNode rec:lrec) {
			SortedSet<Category> fromRecruitCat = new TreeSet<>();
			lcat = (Collection<Category>) get(rec.edges(Direction.OUT),
				selectOneOrMany(hasTheLabel(E_FROMCATEGORY.label())),
				edgeListEndNodes());
			fromRecruitCat.addAll(lcat);
			SortedSet<Category> toRecruitCat = new TreeSet<>();
			lcat = (Collection<Category>) get(rec.edges(Direction.OUT),
				selectOneOrMany(hasTheLabel(E_TOCATEGORY.label())),
				edgeListEndNodes());
			toRecruitCat.addAll(lcat);
			FunctionNode fnode = (FunctionNode) get(rec.edges(Direction.OUT),
				selectOne(hasTheLabel(E_EFFECTEDBY.label())),
				endNode());
			recruitNodes.put(fnode,new Duple<>(Categorized.signature(fromRecruitCat),
				Categorized.signature(toRecruitCat)));
		}
		// get a human-readable version of the life cycle as a graph
		lifeCycleGraph = TwConfigurationAnalyser.getLifeCycleGraph(this);
//		System.out.println(lifeCycleGraph.toDetailedString());
	}

	@Override
	public int initRank() {
		return N_LIFECYCLETYPE.initRank();
	}
	
	/**
	 * 
	 * @return a graph representing this life cycle
	 */
	public Graph<? extends Node,? extends Edge> graph() {
		if (sealed)
			return lifeCycleGraph;
		else
			throw new IllegalStateException("attempt to access uninitialised data");
	}


	@Override
	protected LifeCycleFactory makeTemplate(int id) {
		ArenaType system = (ArenaType) get(this,parent(isClass(ArenaType.class)));
		ComponentContainer superContainer = (ComponentContainer) system.getInstance(id).getInstance().content();
		Map<CreateOtherDecisionFunction,Duple<String,String>>
			prMap = new HashMap<>();
		for (FunctionNode f:produceNodes.keySet())
			prMap.put((CreateOtherDecisionFunction)f.getInstance(id),produceNodes.get(f));
		Map<ChangeCategoryDecisionFunction,Duple<String,String>>
			rcMap = new HashMap<>();
		for (FunctionNode f:recruitNodes.keySet())
			rcMap.put((ChangeCategoryDecisionFunction)f.getInstance(id), recruitNodes.get(f));
		if (setinit!=null) {
			return new LifeCycleFactory(categories,
				autoVarTemplate,driverTemplate,decoratorTemplate,lifetimeConstantTemplate,
				(SetInitialStateFunction)setinit.getInstance(id),id(),superContainer,
				prMap,rcMap,id,stageCategories);
		}
		else {
			return new LifeCycleFactory(categories,
				autoVarTemplate,driverTemplate,decoratorTemplate,lifetimeConstantTemplate,
				null,id(),superContainer,
				prMap,rcMap,id,stageCategories);
		}
	}

	/**
	 * The list of function types that are compatible with a LifeCycleType
	 */
	public static TwFunctionTypes[] compatibleFunctionTypes = {
		ChangeState,				// a life cycle may change its drivers
		SetInitialState,			// a life cycle may set its constants at creation time
// THESE are not possible because relations are only between SystemComponents
//		CreateOtherDecision,		// a group may create new items of its ComponentType
//		ChangeOtherCategoryDecision,// a group may change the category of a component
//		ChangeOtherState,			// a group may change the state of a component
//		DeleteOtherDecision,		// a group may delete another component
//		ChangeRelationState,		// a group may change the state of a relation
//		MaintainRelationDecision,	// a group may maintain a relation
//		RelateToDecision,			// a group may relate to a new component (ALWAYS unindexed search)
//		SetOtherInitialState		// a group may set the initial state of another component ???
	};

	@Override
	public Graph<ALNode,ALEdge> asGraph() {
		ALGraphFactory factory = new ALGraphFactory("LifeCycle");
		Graph<ALNode,ALEdge> graph = new ALGraph<ALNode,ALEdge>(factory);
		for (Category c:stageCategories)
			factory.makeNode(c.id());
		List<String> keys = new LinkedList<>();
		keys.add("function");
		for (FunctionNode fn:produceNodes.keySet()) {
			ALNode start = graph.findNode(produceNodes.get(fn).getFirst());
			ALNode end = graph.findNode(produceNodes.get(fn).getSecond());
			List<Object> values = new LinkedList<>();
			values.add(fn.id());			
			ReadOnlyPropertyList ropl = new ReadOnlyPropertyListImpl(keys,values);
			factory.makeEdge(start, end, "produce", ropl);
		}
		for (FunctionNode fn:recruitNodes.keySet()) {
			ALNode start = graph.findNode(recruitNodes.get(fn).getFirst());
			ALNode end = graph.findNode(recruitNodes.get(fn).getSecond());
			List<Object> values = new LinkedList<>();
			values.add(fn.id());			
			ReadOnlyPropertyList ropl = new ReadOnlyPropertyListImpl(keys,values);
			factory.makeEdge(start, end, "recruit", ropl);
		}
		return graph;
	}

}
