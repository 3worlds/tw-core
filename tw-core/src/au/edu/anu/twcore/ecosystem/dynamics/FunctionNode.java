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

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.data.RngNode;
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.biology.ChangeCategoryDecisionFunction;
import au.edu.anu.twcore.ecosystem.runtime.biology.CreateOtherDecisionFunction;
import au.edu.anu.twcore.ecosystem.runtime.timer.EventQueueWriteable;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.Recruit;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import au.edu.anu.twcore.root.World;
import fr.cnrs.iees.OmugiClassLoader;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.omhtk.*;

import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static au.edu.anu.twcore.ecosystem.structure.RelationType.predefinedRelationTypes.*;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Class matching the "ecosystem/dynamics/timeLine/timeModel/process/function" node label in the
 * 3Worlds configuration tree. Has the user class name property or a way to generate this class
 *
 * @author Jacques Gignoux - 7 juin 2019
 *
 */
public class FunctionNode
		extends InitialisableNode
		implements LimitedEdition<TwFunction>, Sealable {

	private boolean sealed = false;
	private Map<Integer,TwFunction> functions = new HashMap<>();
	protected Constructor<? extends TwFunction> fConstructor = null;
	protected RngNode rngNode = null;
	private Collection<TimerNode> queues = null;

	public FunctionNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public FunctionNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		if (!sealed) {
			super.initialise();
			rngNode = (RngNode) get(edges(Direction.OUT),
				selectZeroOrOne(hasTheLabel(E_USERNG.label())),
				endNode());
			queues = (Collection<TimerNode>) get(edges(Direction.IN),
				selectZeroOrMany(hasTheLabel(E_FEDBY.label())),
				edgeListStartNodes());
			// this is once code has been generated and edited by the user
			String className = (String) properties().getPropertyValue(P_FUNCTIONCLASS.key());
			if (className!=null) {
				// instantiate the user code based function
				// we need a URL classLoader here: Class.forName("nameofclass", true, new URLClassLoader(urlarrayofextrajarsordirs));
				//https://community.oracle.com/thread/4011800
				ClassLoader classLoader = OmugiClassLoader.getJarClassLoader();
				Class<? extends TwFunction> functionClass;
				try {
					functionClass = (Class<? extends TwFunction>) Class.forName(className,true,classLoader);
					fConstructor = functionClass.getConstructor();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			sealed = true;
		}
	}

	@Override
	public int initRank() {
		return N_FUNCTION.initRank();
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
	protected TwFunction makeFunction(int index) {
		TwFunction result = null;
		try {
			result = fConstructor.newInstance();
			// attach a random number generator
			if (rngNode==null)
				result.defaultRng(index);
			else
				result.setRng(rngNode.getInstance(index));
			// attach event queues if feeding some of them
			for (TimerNode tn: queues)
				result.setEventQueue((EventQueueWriteable)tn.getInstance(index),tn.id());
			// add the consequences of the function, if any
			for (TreeNode n:getChildren())
				if (n instanceof FunctionNode) {
					FunctionNode csq = (FunctionNode) n;
					result.addConsequence(csq.getInstance(index));
			}
			if (result instanceof CreateOtherDecisionFunction) {
				if ((boolean)properties().getPropertyValue(P_RELATEPRODUCT.key())) {
					TreeNode rootNode = World.getRoot(this);
					RelationType rlt = (RelationType) get(rootNode.getChildren(),
						selectOne(hasTheLabel(N_PREDEFINED.label())),
						children(),
						selectOne(andQuery(
							hasTheLabel(N_RELATIONTYPE.label()),
							hasTheName(parentTo.key()))));
					((CreateOtherDecisionFunction)result).setRelateToOtherContainer(rlt.getInstance(index));
				}			
			}
			if (result instanceof ChangeCategoryDecisionFunction) {
				Collection<Recruit> recruits = (Collection<Recruit>) get(edges(Direction.IN),
					selectOneOrMany(hasTheLabel(E_EFFECTEDBY.label())),
					edgeListStartNodes());
				Collection<Category> toCats = new HashSet<>();
				for (Recruit rec:recruits) {
					Collection<Category> recCats = (Collection<Category>) get(rec.edges(Direction.OUT),
						selectOneOrMany(hasTheLabel(E_TOCATEGORY.label())),
						edgeListEndNodes());
					Collection<Category> lcCats = (Collection<Category>) get(rec.getParent(),
						outEdges(), // out edges of the LifeCycleType node
						selectOne(hasTheLabel(E_APPLIESTO.label())),
						endNode(),  // the CategorySet of the life cycle
						children()); // its children = the categories of the life cycle
					for (Category rc:recCats)
						if (lcCats.contains(rc))
							toCats.add(rc);
				}
				((ChangeCategoryDecisionFunction)result).setTransitions(toCats);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public TwFunction getInstance(int id) {
		if (!sealed)
			initialise();
		if (!functions.containsKey(id))
			functions.put(id, makeFunction(id));
		return functions.get(id);
	}

}
