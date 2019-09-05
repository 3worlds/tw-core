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
import au.edu.anu.twcore.ecosystem.Ecosystem;
import au.edu.anu.twcore.ecosystem.runtime.TwProcess;
import au.edu.anu.twcore.ecosystem.runtime.process.AbstractProcess;
import au.edu.anu.twcore.ecosystem.runtime.process.ComponentProcess;
import au.edu.anu.twcore.ecosystem.runtime.process.RelationProcess;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.Sealable;
import fr.ens.biologie.generic.Singleton;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import java.util.Collection;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;

/**
 * Class matching the "ecosystem/dynamics/timeLine/timeModel/Process" node label in the 
 * 3Worlds configuration tree. Has no properties. 
 * 
 * @author Jacques Gignoux - 10 mars 2017
 *
 */
public class ProcessNode 
		extends InitialisableNode 
		implements Singleton<TwProcess>, Sealable {
	
	private boolean sealed = false;
	private TimeModel timeModel = null;
	private AbstractProcess process = null;

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
			sealed = false;					// timeModel  timeLine    dynamics    ecosystem
			Ecosystem ecosystem = (Ecosystem) getParent().getParent().getParent().getParent();
			timeModel = (TimeModel)getParent();
			// 1 - setting up simulation code execution 
			DynamicList<? extends Node> applies = (DynamicList<? extends Node>) get(edges(Direction.OUT),
				selectOneOrMany(hasTheLabel("appliesTo")),
				edgeListEndNodes());
			Node first = applies.getFirst();
			// process applies to a set of categories
			if (first.classId().equals(N_CATEGORY.label())) {
				process = new ComponentProcess(ecosystem,(Collection<Category>)applies);
				DynamicList<FunctionNode> functions = (DynamicList<FunctionNode>) get(getChildren(),
					selectZeroOrMany(hasTheLabel("function")));
				for (FunctionNode func:functions)
					process.addFunction(func.getInstance());
			}
			// process applies to a single relation (a relation links two sets of categories so no
			// need for multiple relations)
			else if (first.classId().equals(N_RELATIONTYPE.label())) {
				DynamicList<FunctionNode> functions = (DynamicList<FunctionNode>) get(getChildren(),
					selectZeroOrMany(hasTheLabel("function")));
				for (FunctionNode func:functions ){
					if (process == null) {
	// TODO: fix this					
	//					if (RelateToDecisionFunction.class.isAssignableFrom(function.getClass())) 
	//						process = new IndexedSearchProcess(world,(RelationType)first,null,null);
	//					else
							process = new RelationProcess(ecosystem,(RelationType)first);
					}
					process.addFunction(func.getInstance());
				}
			}
			sealed = true;
		}
	}
	
	@Override
	public int initRank() {
		return N_PROCESS.initRank();
	}

	@Override
	public TwProcess getInstance() {
		if (!sealed)
			initialise();
		return process;
	}

	public final void execute(long t, long dt) {		
		process.execute(timeModel.userTime(t), timeModel.userTime(dt));
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

}
