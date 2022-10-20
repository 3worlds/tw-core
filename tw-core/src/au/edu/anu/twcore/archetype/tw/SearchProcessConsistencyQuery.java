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

import java.util.Collection;
import java.util.HashSet;

import au.edu.anu.qgraph.queries.*;
import au.edu.anu.twcore.TextTranslations;
import au.edu.anu.twcore.ecosystem.dynamics.*;
import au.edu.anu.twcore.ecosystem.structure.*;
import au.edu.anu.twcore.ecosystem.structure.SpaceNode;
import fr.cnrs.iees.omugi.graph.*;
import fr.cnrs.iees.omugi.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

/**
 * Check that if a SearchProcess refers to a Space, then the ComponentTypes
 * processed by it will implement the proper coordinates
 * 
 * @author Jacques Gignoux - 26 févr. 2021
 *
 */
public class SearchProcessConsistencyQuery extends QueryAdaptor {

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		ProcessNode proc = (ProcessNode) input;

		boolean checkProcess = false;
		Collection<FunctionNode> funx = (Collection<FunctionNode>) get(proc.getChildren(),
				selectZeroOrMany(hasTheLabel(N_FUNCTION.label())));
		// look if the process is a SearchProcess (= has a relateToDecision function as
		// a child)
		for (FunctionNode func : funx)
			if (func.properties().hasProperty(P_FUNCTIONTYPE.key()))
				if (func.properties().getPropertyValue(P_FUNCTIONTYPE.key()).equals(TwFunctionTypes.RelateToDecision))
					checkProcess = true;
		if (checkProcess) {
			// get the space of this ProcessNode
			SpaceNode space = (SpaceNode) get(proc.edges(Direction.OUT), selectZeroOrOne(hasTheLabel(E_SPACE.label())),
					endNode());
			if (space != null) {
				// get the relation type of this SearchProcess
				Collection<RelationType> relt = (Collection<RelationType>) get(proc.edges(Direction.OUT),
						selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())), edgeListEndNodes());
				if (relt.size() > 0) {
					RelationType rt = relt.iterator().next();
					// get the toCategories of the relation type
					Collection<Category> tocs = (Collection<Category>) get(rt.edges(Direction.OUT),
							selectZeroOrMany(hasTheLabel(E_TOCATEGORY.label())), edgeListEndNodes());
					// get the from categories of the relation type
					Collection<Category> fromcs = (Collection<Category>) get(rt.edges(Direction.OUT),
							selectZeroOrMany(hasTheLabel(E_FROMCATEGORY.label())), edgeListEndNodes());
					Collection<SpaceNode> toSpaces = new HashSet<>();
					Collection<SpaceNode> fromSpaces = new HashSet<>();
					for (Category c : tocs) {
						// look for all fields in categories
						Collection<TreeGraphDataNode> records = (Collection<TreeGraphDataNode>) get(
								c.edges(Direction.OUT), edgeListEndNodes(),
								selectZeroOrMany(hasTheLabel(N_RECORD.label())));
						for (TreeGraphDataNode rec : records)
							for (TreeNode field : rec.getChildren()) {
								// look for fields which are referenced by a space
								Collection<SpaceNode> sp = (Collection<SpaceNode>) get(field.edges(Direction.IN),
										edgeListStartNodes(), selectZeroOrMany(hasTheLabel(N_SPACE.label())));
								// record the to-spaces
								toSpaces.addAll(sp);
							}
					}
					for (Category c : fromcs) {
						// look for all fields in categories
						Collection<TreeGraphDataNode> records = (Collection<TreeGraphDataNode>) get(
								c.edges(Direction.OUT), edgeListEndNodes(),
								selectZeroOrMany(hasTheLabel(N_RECORD.label())));
						for (TreeGraphDataNode rec : records)
							for (TreeNode field : rec.getChildren()) {
								// look for fields which are referenced by a space
								Collection<SpaceNode> sp = (Collection<SpaceNode>) get(field.edges(Direction.IN),
										edgeListStartNodes(), selectZeroOrMany(hasTheLabel(N_SPACE.label())));
								// record the from-spaces
								fromSpaces.addAll(sp);
							}
					}
					if (!(toSpaces.contains(space)) & (fromSpaces.contains(space))) {
						// TODO: Untested - IDD
						String list = "";
						for (SpaceNode s : toSpaces)
							list += "," + s.toShortString();
						list = list.replaceFirst(",", "");
						String[] msgs = TextTranslations.getSearchProcessConsistencyQuery(proc.toShortString(),
								space.toShortString(), list);
						actionMsg = msgs[0];
						errorMsg = msgs[1];

//						actionMsg = "Reconfigure graph so that all componentTypes processed by '" + proc.toShortString()
//								+ "' have valid coordinates for '" + space.toShortString() + "'.";
//						errorMsg = "Expected all componentTypes processed by '" + proc.toShortString()
//								+ "' to have valid coordinates for '" + space.toShortString()
//								+ "' but found associations with [" + list + "].";
						return this;
					}
				}
			}
		}
		return this;
	}

}
