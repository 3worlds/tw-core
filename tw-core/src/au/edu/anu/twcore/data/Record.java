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
package au.edu.anu.twcore.data;

import fr.cnrs.iees.graph.DataHolder;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import au.edu.anu.twcore.InitialisableNode;

/**
 * 
 * @author Jacques Gignoux - 31 mai 2019
 *
 */
public class Record extends InitialisableNode {

	// default constructor
	public Record(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public Record(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_RECORD.initRank();
	}

	public static List<TreeGraphDataNode> getLeaves(TreeGraphDataNode rootRecord) {
		List<TreeGraphDataNode> result = new ArrayList<>();
		getLeaves(result,rootRecord);
		return result;
	}

	private static void getLeaves(List<TreeGraphDataNode> result, TreeNode record) {
		for (TreeNode child:record.getChildren()) {
			if (child.classId().equals(N_FIELD.label()))
				result.add((TreeGraphDataNode) child);
			else if (child.classId().equals(N_TABLE.label())) {
				DataHolder table = (DataHolder) child;
				// cannot depend on child record being present
				if (table.properties().hasProperty(P_DATAELEMENTTYPE.key())) {
					result.add((TreeGraphDataNode) child);
				} else// There can be only one child but this is easiest.
					for (TreeNode tableRecord:child.getChildren())
						getLeaves(result,tableRecord);
			}
		}
		

		
	}

	

}
