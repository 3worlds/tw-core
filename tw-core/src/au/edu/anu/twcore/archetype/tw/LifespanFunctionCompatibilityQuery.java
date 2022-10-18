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
package au.edu.anu.twcore.archetype.tw;

import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import java.util.LinkedList;
import java.util.List;

import au.edu.anu.qgraph.queries.*;
import au.edu.anu.twcore.TextTranslations;
import au.edu.anu.twcore.ecosystem.dynamics.*;
import au.edu.anu.twcore.ecosystem.structure.*;
import au.edu.anu.twcore.root.World;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

/**
 * <p>
 * A class to check that processes applying to permanent objects do not have
 * functions only compatible with ephemeral objects. These are:
 * </p>
 * <dl>
 * <dt>DeleteDecision</dt>
 * <dd>Only ephemeral components can be deleted</dd>
 * <dt>ChangeCategoryDecision</dt>
 * <dd>Only ephemeral components can change categories</dd>
 * <dt>CreateOtherDecision</dt>
 * <dd>Only ephemeral components can be created (but the creator may be
 * permanent)</dd>
 * </dl>
 * 
 *
 * @author J. Gignoux - 13 nov. 2020
 *
 */
public class LifespanFunctionCompatibilityQuery extends QueryAdaptor {

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		ProcessNode pn = (ProcessNode) input;
		// get the *ephemeral* category
		TreeNode root = World.getRoot(pn);
		Category ceph = (Category) get(root.subTree(),
				selectZeroOrOne(andQuery(hasTheLabel(N_CATEGORY.label()), hasTheName(Category.ephemeral))));
		if (ceph != null) {
			// get all function types of this ProcessNode
			for (FunctionNode fn : (List<FunctionNode>) get(pn.getChildren(),
					selectZeroOrMany(hasTheLabel(N_FUNCTION.label()))))
				if (fn.properties().hasProperty(P_FUNCTIONTYPE.key())) {
					List<Category> procCats = new LinkedList<>();
					// case 1: deleteDecision: componentTypes to which this process may
					// apply must all be of category *ephemeral* even if the process categories
					// do not contain *ephemeral*
					if (TwFunctionTypes.DeleteDecision.equals(fn.properties().getPropertyValue(P_FUNCTIONTYPE.key()))) {
						procCats = (List<Category>) get(pn.edges(Direction.OUT),
								selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())), edgeListEndNodes());
						checkComponentTypes(procCats, ceph, root, pn, fn);
					}
					if (!satisfied())
						return this;
					// case 2: createotherDecision: componentTypes to create (according to
					// lifecycle)
					// must all be of category *ephemeral*
					if (TwFunctionTypes.CreateOtherDecision
							.equals(fn.properties().getPropertyValue(P_FUNCTIONTYPE.key()))) {
						// check for life cycle spec
						List<TreeNode> prods = (List<TreeNode>) get(fn.edges(Direction.IN),
								selectZeroOrMany(hasTheLabel(E_EFFECTEDBY.label())), edgeListStartNodes(),
								selectZeroOrMany(hasTheLabel(N_PRODUCE.label())));
						// no life cycle:
						if (prods.isEmpty())
							procCats = (List<Category>) get(pn.edges(Direction.OUT),
									selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())), edgeListEndNodes());
						// life cycle: get the product categories
						else if (prods.size() == 1)
							procCats = (List<Category>) get(prods.get(0).edges(Direction.OUT),
									selectZeroOrMany(hasTheLabel(E_TOCATEGORY.label())), edgeListEndNodes());
						checkComponentTypes(procCats, ceph, root, pn, fn);
						if (!satisfied())
							return this;
					}
					// case3: both componentType to recruit from and to recruit to must
					// belong to category *ephemeral* even if the process doesnt specify so
					if (TwFunctionTypes.ChangeCategoryDecision
							.equals(fn.properties().getPropertyValue(P_FUNCTIONTYPE.key()))) {
						// check that recruiting componentTypes are ephemeral
						procCats = (List<Category>) get(pn.edges(Direction.OUT),
								selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())), edgeListEndNodes());
						// String msg = message;
						checkComponentTypes(procCats, ceph, root, pn, fn);
						if (!satisfied())
							return this;
						// check that recruited componentTypes are ephemeral
						List<TreeNode> recs = (List<TreeNode>) get(fn.edges(Direction.IN),
								selectZeroOrMany(hasTheLabel(E_EFFECTEDBY.label())), edgeListStartNodes(),
								selectZeroOrMany(hasTheLabel(N_RECRUIT.label())));
						if (recs.size() == 1)
							procCats = (List<Category>) get(recs.get(0).edges(Direction.OUT),
									selectZeroOrMany(hasTheLabel(E_TOCATEGORY.label())), edgeListEndNodes());
						checkComponentTypes(procCats, ceph, root, pn, fn);
						if (!satisfied())
							return this;
					}
				}
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	private void checkComponentTypes(List<Category> procCats, Category ceph, TreeNode root, ProcessNode pn,
			FunctionNode fn) {
		// if ephemeral category is present in process categories, everything is ok
		if (!procCats.contains(ceph)) {
			// otherwise must search componentTypes
			List<ComponentType> lct = (List<ComponentType>) get(root.subTree(),
					selectZeroOrMany(hasTheLabel(N_COMPONENTTYPE.label())));
			for (ComponentType ct : lct) {
				// get componentType categories
				List<Category> lcct = (List<Category>) get(ct.edges(Direction.OUT),
						selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())), edgeListEndNodes());
				// if componentType categories that may use this process
				if (lcct.containsAll(procCats))
					// check the componentType is ephemeral
					if (!lcct.contains(ceph)) {
						String[] msgs = TextTranslations.getLifespanFunctionCompatibilityQuery(ct.toShortString(),
								fn.toShortString(), pn.toShortString());
						actionMsg = msgs[0];
						errorMsg = msgs[1];
//						errorMsg = "Expected '" + ct.toShortString() + "' to belong to Category:*ephemeral*.";
//						actionMsg = "Reconfigure. '" + ct.toShortString() + "' is not ephemeral but is processed by '"
//								+ fn.toShortString() + "' of '" + pn.toShortString()
//								+ "' that only works on ephemeral ComponentTypes.";
					}
			}
		}
	}

}
