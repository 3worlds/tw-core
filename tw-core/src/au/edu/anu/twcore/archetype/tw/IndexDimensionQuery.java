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

import au.edu.anu.rscs.aot.collections.tables.Dimensioner;
import au.edu.anu.rscs.aot.collections.tables.IndexString;
import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.data.TableNode;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

/**
 * A Query to check that table index specifications in data trackers have compatible dimensions with
 * table dim
 * 
 * @author Jacques Gignoux - 7 nov. 2019
 *
 */
public class IndexDimensionQuery extends Query {
	
	private String ixs = null;
	private String nodeId = null;

	public IndexDimensionQuery() {
		super();
	}

	@Override
	public Query process(Object input) { // input is an edge with start=dataTracker and end=field or table
		defaultProcess(input);
		if (((ReadOnlyDataHolder)input).properties().hasProperty(P_TRACKEDGE_INDEX.key())) {
			StringTable index = (StringTable) ((ReadOnlyDataHolder)input).properties().getPropertyValue(P_TRACKEDGE_INDEX.key());
			TreeNode end = (TreeNode) ((Edge) input).endNode();
			TreeNode parent = end;
			int i=index.size()-1;
			boolean ok = true;
			while (parent!=null) {
				if (parent instanceof TableNode) {
					// get the dimensioners
					Dimensioner[] dims = ((TableNode)parent).dimensioners();
					int[] dd = new int[dims.length];
					for (int j=0; j<dd.length; j++)
						dd[j] = dims[j].getLength();
					// get the matching index string
					String ix = index.getWithFlatIndex(i--);
					// compare them
					ixs = ix;
					nodeId = parent.id();
					try {
						IndexString.stringToIndex(ix,dd);
						ok &= true;
					} catch (Exception e) {
						ok &= false;
						break;
					}
				}
				parent = parent.getParent();
			}
			satisfied = ok;
		}
		else
			satisfied=true; 
		return this;
	}
	
	public String toString() {
		return "[" + stateString() + "Index string '" + ixs 
			+ "' out of range for table '" + nodeId + "'.]";
	}

}
