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
package au.edu.anu.twcore.archetype;

import java.util.logging.Level;
import java.util.logging.Logger;

import au.edu.anu.rscs.aot.archetype.Archetypes;
import au.edu.anu.rscs.aot.archetype.CheckMessage;
import au.edu.anu.twcore.archetype.tw.IsInValueSetQuery;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.graph.Tree;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.io.GraphImporter;
import fr.cnrs.iees.identity.impl.PairIdentity;

/**
 * @author daviesi
 *
 */
// tested ok with version 0.1.1 on 21/5/2019
public class TwArchetype {

	private Logger log = Logger.getLogger(TwArchetype.class.getName());
	// the 3Worlds archetype
	private Tree<? extends TreeNode> twArch = null;
	// the archetype for archetypes, with all the check methods
	private Archetypes rootArch = null;

	@SuppressWarnings("unchecked")
//	public TwArchetype(boolean checkArchetype) {
	public TwArchetype() {
		super();
		log.setLevel(Level.WARNING);
		// load 3worlds archetype
		twArch = (Tree<? extends TreeNode>) GraphImporter.importGraph("3wArchetype.ugt", IsInValueSetQuery.class);
		if (log.getLevel().equals(Level.INFO)) {
			String indent = "";
			printTree(twArch.root(), indent);
		}
		// checks compliance of 3Worlds archetype with the archetype for archetypes
		// I think this should only occur if a flag is set. Otherwise its a waste of
		// time!
		rootArch = new Archetypes();
		// if (checkArchetype)
		if (!rootArch.isArchetype(twArch)) {
			log.severe("3WORLDS ARCHETYPE HAS ERRORS! (list follows)");
			for (CheckMessage cm : rootArch.errorList())
				log.severe(cm.toString() + "\n");
		}
	}

	private void printTree(TreeNode parent, String indent) {
		System.out.println(indent + parent.classId() + ":" + parent.id());
		if (parent instanceof ReadOnlyDataHolder)
			for (String key : ((ReadOnlyDataHolder) parent).properties().getKeysAsSet())
				System.out.println(indent + "    " + "-(" + key + "="
						+ ((ReadOnlyDataHolder) parent).properties().getPropertyValue(key) + ")");
		for (TreeNode child : parent.getChildren())
			printTree(child, indent + "    ");
	}

	// I dont know if you need this method, but I need it to debug archetypes
	public Iterable<CheckMessage> checkSpecifications(TreeGraph<?, ?> graph) {
		rootArch.check(graph, twArch);
		return rootArch.errorList();
	}

	// Static helper methods??
	public static String getLabel(String id) {
		return id.split(PairIdentity.LABEL_NAME_STR_SEPARATOR)[0];
	}

	public static String getName(String id) {
		return id.split(PairIdentity.LABEL_NAME_STR_SEPARATOR)[1];
	}

}
