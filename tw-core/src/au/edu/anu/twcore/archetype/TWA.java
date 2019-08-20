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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import au.edu.anu.rscs.aot.archetype.Archetypes;
import au.edu.anu.rscs.aot.archetype.CheckMessage;
import au.edu.anu.twcore.archetype.tw.CheckSubArchetypeQuery;
import fr.cnrs.iees.graph.Tree;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.io.GraphImporter;
import fr.cnrs.iees.identity.impl.PairIdentity;

/**
 * Global singleton instance of the 3Warchetype: thread safe and lazy load
 * 
 * @author Ian Davies
 *
 * @date 20 Jul 2019
 */
public class TWA {
	
	/** the 3Worlds archetype (= "The Instance")*/
	private static Tree<? extends TreeNode> threeWorldsArchetype;
	
	/** the root of the 3Worlds archetype */
	private static TreeNode twaRoot;
	
	/** if the 3Worlds archetype is valid, ie compies with the archetype format */
	private static boolean checked = false;
	
	/** a logger for getting information */
	private static Logger log = Logger.getLogger(TWA.class.getName());
	// Use this to adjust the logging level
	static { log.setLevel(Level.OFF); }

	/** all the sub-archetypes found in the 3Worlds archetype */
	private static Map<String, Tree<? extends TreeNode>> subGraphs = new HashMap<>();
	
	/** the archetype for archetypes, used to check the 3Worlds archetype and
	 * 3Worlds specifications against the 3Worlds archetype (has the "check(...)" methods */
	private static Archetypes rootArchetype = null;

	private TWA() {
	};

	/**
	 * 
	 * @return the 3Worlds archetype
	 */
	@SuppressWarnings("unchecked")
	public static synchronized Tree<? extends TreeNode> getInstance() {
		if (threeWorldsArchetype == null) {
			threeWorldsArchetype = (Tree<? extends TreeNode>) GraphImporter.importGraph("3wArchetype.ugt",
				CheckSubArchetypeQuery.class);
		}
		return threeWorldsArchetype;
	}

	/**
	 * 
	 * @return the root node of the 3Worlds archetype
	 */
	public static synchronized TreeNode getRoot() {
		if (twaRoot == null)
			twaRoot = TWA.getInstance().root();
		return twaRoot;
	}

	/**
	 * Makes sure the 3Worlds archetype is a valid archetype
	 * @return true if the 3Worlds archetype has no errors
	 */
	public static synchronized boolean validArchetype() {
		if (checked)
			return checked;
		if (rootArchetype==null)
			rootArchetype = new Archetypes();
		if (!rootArchetype.isArchetype(TWA.getInstance())) {
			log.severe("3WORLDS ARCHETYPE HAS ERRORS! (list follows)");
			for (CheckMessage cm : rootArchetype.errorList())
				log.severe(cm.toString() + "\n");
			checked = false;
		} else
			checked = true;
		return checked;
	}

	/**
	 * Accessor to the sub-archetypes listed in the main 3Worlds archetype
	 * @param key the name of the sub-archetype 
	 * @return the sub-archetype as a tree
	 */
	@SuppressWarnings("unchecked")
	public static synchronized Tree<? extends TreeNode> getSubArchetype(String key) {
		if (subGraphs.containsKey(key))
			return subGraphs.get(key);
		Tree<? extends TreeNode> tree = (Tree<? extends TreeNode>) GraphImporter.importGraph(key,
				CheckSubArchetypeQuery.class);
		subGraphs.put(key, tree);
		return tree;
	}

	/**
	 * Checks that a specification complies with the 3Worlds archetype
	 * @param graph the graph to check
	 * @return the list of errors, or null if no errors
	 */
	// JG: was initially in TwArchetypes, moved to here
	public static synchronized Iterable<CheckMessage> checkSpecifications(TreeGraph<?, ?> graph) {
		if (validArchetype())
			rootArchetype.check(graph, getInstance());
		return rootArchetype.errorList();
	}
	
//	// Static helper methods??
//	public static String getLabel(String id) {
//		return id.split(PairIdentity.LABEL_NAME_STR_SEPARATOR)[0];
//	}
//
//	public static String getName(String id) {
//		return id.split(PairIdentity.LABEL_NAME_STR_SEPARATOR)[1];
//	}
}
