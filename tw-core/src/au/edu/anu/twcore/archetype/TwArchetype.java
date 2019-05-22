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

import au.edu.anu.rscs.aot.archetype.Archetypes;
import au.edu.anu.rscs.aot.archetype.CheckMessage;
import au.edu.anu.twcore.archetype.tw.IsInValueSetQuery;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.graph.Tree;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.io.GraphImporter;

/**
 * @author daviesi
 *
 */
// tested ok with version 0.1.1 on 21/5/2019
public class TwArchetype {

	@SuppressWarnings("unchecked")
	public TwArchetype() {
		super();
//		String filename = System.getProperty("user.dir") //
//				+ File.separator + "src" + File.separator
//				+ "au.edu.anu.twcore.archetype.tw".replace('.', File.separatorChar) + File.separator
//				+ "3wArchetype.ugt";
//		File file = new File(filename);
//		FileImporter importer = new FileImporter(file);
//		Tree<? extends TreeNode> specs = (Tree<? extends TreeNode>) importer.getGraph();
		Tree<? extends TreeNode> specs = (Tree<? extends TreeNode>) 
			GraphImporter.importGraph("3wArchetype.ugt",IsInValueSetQuery.class);
		
		String indent = "";

		printTree(specs.root(), indent);
		
		Archetypes rootArch = new Archetypes();
		if (!rootArch.isArchetype(specs))
			for (CheckMessage cm: rootArch.errorList())
				System.out.println(cm.toString());
	}

	private void printTree(TreeNode parent, String indent) {
		System.out.println(indent + parent.classId() + ":" + parent.id());
		if (parent instanceof ReadOnlyDataHolder)
			for (String key:((ReadOnlyDataHolder) parent).properties().getKeysAsSet())
			System.out.println(indent+"    "+"-("+key+"="+
				((ReadOnlyDataHolder)parent).properties().getPropertyValue(key)+")");
		for (TreeNode child : parent.getChildren())
			printTree(child, indent + "    ");

	}

}
