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
package au.edu.anu.twcore.archetype.tw.old;

import java.util.Arrays;
import java.util.List;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.old.queries.Query;
import au.edu.anu.rscs.aot.old.queries.base.OrQuery;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.Node;

import static au.edu.anu.rscs.aot.old.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.old.queries.base.SequenceQuery.get;

/**
 * Checks that an out edge has two labels (each coming from two separate sets). ie, if one
 * label of the first set is present, then one of the second must be here too
 *  
 * @author Jacques Gignoux - 14 Novembre 2019
 * 
 * 
 */
@Deprecated
public class OutEdgeXNorQuery extends Query {

	private String[] edgeLabel1 = null;
	private String[] edgeLabel2 = null;
	
	public OutEdgeXNorQuery(String label1, String label2) {
		super();
		edgeLabel1 = new String[1];
		edgeLabel2 = new String[1];
		edgeLabel1[0] = label1;
		edgeLabel2[0] = label2;
	}

	public OutEdgeXNorQuery(String label1, StringTable label2) {
		super();
		edgeLabel1 = new String[1];
		edgeLabel2 = new String[label2.size()];
		edgeLabel1[0] = label1;
		for (int i=0; i<edgeLabel2.length; i++)
			edgeLabel2[i] = label2.getWithFlatIndex(i);
	}
	
	public OutEdgeXNorQuery(StringTable label1, String label2) {
		this(label2,label1);
	}
	
	public OutEdgeXNorQuery(StringTable label1, StringTable label2) {
		super();
		edgeLabel1 = new String[label1.size()];
		edgeLabel2 = new String[label2.size()];
		for (int i=0; i<edgeLabel1.length; i++)
			edgeLabel1[i] = label1.getWithFlatIndex(i);
		for (int i=0; i<edgeLabel2.length; i++)
			edgeLabel2[i] = label2.getWithFlatIndex(i);
	}
		
	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) {  // input is a node
		defaultProcess(input);
		Node localItem = (Node) input;
		Query[] q = new Query[edgeLabel1.length];
		for (int i=0; i<edgeLabel1.length; i++)
			q[i] = hasTheLabel(edgeLabel1[i]);
		OrQuery orq1 = new OrQuery(q); 
		q = new Query[edgeLabel2.length];
		for (int i=0; i<edgeLabel2.length; i++)
			q[i] = hasTheLabel(edgeLabel2[i]);
		OrQuery orq2 = new OrQuery(q);
		List<Edge> el1 = (List<Edge>) get(localItem. 
			edges(Direction.OUT),
			selectZeroOrMany(orq1));
		List<Edge> el2 = (List<Edge>) get(localItem.
			edges(Direction.OUT),
			selectZeroOrMany(orq2));
		satisfied = !((el1.size()>0)^(el2.size()>0));
		return this;
	}

	public String toString() {
		return "[" + stateString() + "There must be at least one edge labelled from " 
			+ Arrays.toString(edgeLabel1) + " and one from "+ Arrays.toString(edgeLabel2) +".]";
	}

}