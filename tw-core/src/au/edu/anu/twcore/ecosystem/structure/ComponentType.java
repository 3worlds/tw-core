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

import java.util.*;

import static au.edu.anu.qgraph.queries.base.SequenceQuery.*;
import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.dynamics.ProcessNode;
import au.edu.anu.twcore.ecosystem.runtime.biology.SetInitialStateFunction;
import au.edu.anu.twcore.ecosystem.runtime.space.ObserverDynamicSpace;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentFactory;
import fr.cnrs.iees.omugi.graph.*;
import fr.cnrs.iees.omugi.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.omugi.identity.Identity;
import fr.cnrs.iees.omugi.properties.SimplePropertyList;
import fr.cnrs.iees.omugi.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

/**
 * Specification node for SystemComponents
 *
 * @author J. Gignoux - 23 avr. 2020
 *
 */
public class ComponentType extends ElementType<ComponentFactory, SystemComponent> {

	private Set<SpaceNode> spaces = new HashSet<>();

	public ComponentType(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public ComponentType(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	// This to handle spaces as ComponentFactory is the only one linked to spaces
	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		super.initialise();
		// get all categories it belongs to
		List<Category> lc = (List<Category>) get(edges(Direction.OUT),
			selectOneOrMany(hasTheLabel(E_BELONGSTO.label())),
			edgeListEndNodes(),
			selectOneOrMany(hasTheLabel(N_CATEGORY.label())));
		// get all the processes affecting these categories
		Set<ProcessNode> lp = new HashSet<>();
		for (Category c:lc) {
			// case of ComponentProcesses
			lp.addAll((Collection<? extends ProcessNode>) get(c.edges(Direction.IN),
				selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())),
				edgeListStartNodes(),
				selectZeroOrMany(hasTheLabel(N_PROCESS.label()))));
			// case of RelationProcesses // TODO: check this code !
			List<TreeNode> lrt = (List<TreeNode>) get(c.edges(Direction.IN),
				selectZeroOrMany(hasTheLabel(E_TOCATEGORY.label())),
				edgeListStartNodes(),
				selectZeroOrMany(hasTheLabel(N_RELATIONTYPE.label())));
			for (Node n:lrt)
				lp.addAll((Collection<? extends ProcessNode>) get(n.edges(Direction.IN),
					selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())),
					edgeListStartNodes()));
			lrt = (List<TreeNode>) get(c.edges(Direction.IN),
				selectZeroOrMany(hasTheLabel(E_FROMCATEGORY.label())),
				edgeListStartNodes(),
				selectZeroOrMany(hasTheLabel(N_RELATIONTYPE.label())));
			for (Node n:lrt)
				lp.addAll((Collection<? extends ProcessNode>) get(n.edges(Direction.IN),
					selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())),
					edgeListStartNodes()));
		}
		// get all the spaces attached to the ProcessNodes
//		// FLAW: one must check if coordinate fields are present in ComponentType categories, rather
//		for (ProcessNode pn:lp) {
//			SpaceNode sn = (SpaceNode) get(pn.edges(Direction.OUT),
//				selectZeroOrOne(hasTheLabel(E_SPACE.label())),
//				endNode());
//			if (sn!=null)
//				spaces.add(sn);
//		}
		// search for spaces this type of component may be inserted in
		for (Category c:lc) {
			// look for all fields in categories
			Collection<TreeGraphDataNode> records = (Collection<TreeGraphDataNode>) get(c.edges(Direction.OUT),
				edgeListEndNodes(),
				selectZeroOrMany(hasTheLabel(N_RECORD.label())));
			for (TreeGraphDataNode rec:records) 
				for (TreeNode field:rec.getChildren()){
				// look for fields which are referenced by a space
				Collection<SpaceNode> sp = (Collection<SpaceNode>) get(field.edges(Direction.IN),
					edgeListStartNodes(),
					selectZeroOrMany(hasTheLabel(N_SPACE.label())));
				// record the spaces
				spaces.addAll(sp);
			}
		}
		
	}

	@Override
	protected ComponentFactory makeTemplate(int id) {
		List<ObserverDynamicSpace> sps = new LinkedList<>();
		for (SpaceNode sn:spaces)
			sps.add(sn.getInstance(id));
		if (setinit!=null)
			return new ComponentFactory(categories,sps,
				autoVarTemplate,driverTemplate,decoratorTemplate,lifetimeConstantTemplate,
				(SetInitialStateFunction)setinit.getInstance(id),isPermanent,id);
		else
			return new ComponentFactory(categories,sps,
				autoVarTemplate,driverTemplate,decoratorTemplate,lifetimeConstantTemplate,null,isPermanent,id);
	}

	@Override
	public int initRank() {
		return N_COMPONENTTYPE.initRank();
	}

	/**
	 * The list of function types that are compatible with a ComponentType (all of them)
	 */
	public static TwFunctionTypes[] compatibleFunctionTypes = TwFunctionTypes.values();

}
