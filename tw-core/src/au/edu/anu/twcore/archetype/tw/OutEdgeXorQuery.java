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

import java.util.Arrays;
import java.util.List;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.rscs.aot.queries.base.OrQuery;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.Node;
import fr.ens.biologie.generic.utils.Duple;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;

/**
 * Checks that an out edge has either of two (sets of) labels.
 * 
 * @author Jacques Gignoux - 14 Novembre 2019
 * 
 * 
 */
public class OutEdgeXorQuery extends Query {

	private String[] edgeLabel1 = null;
	private String[] edgeLabel2 = null;

	public OutEdgeXorQuery(String label1, String label2) {
		super();
		edgeLabel1 = new String[1];
		edgeLabel2 = new String[1];
		edgeLabel1[0] = label1;
		edgeLabel2[0] = label2;
	}

	public OutEdgeXorQuery(String label1, StringTable label2) {
		super();
		edgeLabel1 = new String[1];
		edgeLabel2 = new String[label2.size()];
		edgeLabel1[0] = label1;
		for (int i = 0; i < edgeLabel2.length; i++)
			edgeLabel2[i] = label2.getWithFlatIndex(i);
	}

	public OutEdgeXorQuery(StringTable label1, String label2) {
		this(label2, label1);
	}

	public OutEdgeXorQuery(StringTable label1, StringTable label2) {
		super();
		edgeLabel1 = new String[label1.size()];
		edgeLabel2 = new String[label2.size()];
		for (int i = 0; i < edgeLabel1.length; i++)
			edgeLabel1[i] = label1.getWithFlatIndex(i);
		for (int i = 0; i < edgeLabel2.length; i++)
			edgeLabel2[i] = label2.getWithFlatIndex(i);
	}

	@Override
	public Query process(Object input) { // input is a node
		defaultProcess(input);
		Node localItem = (Node) input;
		Duple<List<Edge>,List<Edge>> lstEdges = getEdgeLists(localItem,edgeLabel1,edgeLabel2);

//		Query[] q = new Query[edgeLabel1.length];
//		for (int i = 0; i < edgeLabel1.length; i++)
//			q[i] = hasTheLabel(edgeLabel1[i]);
//		OrQuery orq1 = new OrQuery(q);
//		q = new Query[edgeLabel2.length];
//		for (int i = 0; i < edgeLabel2.length; i++)
//			q[i] = hasTheLabel(edgeLabel2[i]);
//		OrQuery orq2 = new OrQuery(q);
//		List<Edge> el1 = (List<Edge>) get(localItem.edges(Direction.OUT), selectZeroOrMany(orq1));
//		List<Edge> el2 = (List<Edge>) get(localItem.edges(Direction.OUT), selectZeroOrMany(orq2));
//		satisfied = (el1.size() > 0) ^ (el2.size() > 0);
		satisfied = (lstEdges.getFirst().size() > 0) ^ (lstEdges.getSecond().size() > 0);
		return this;
	}

	public String toString() {
		return "[" + stateString() + "There must be at least one edge labelled either " + Arrays.toString(edgeLabel1)
				+ " or " + Arrays.toString(edgeLabel2) + ".]";
	}

	@SuppressWarnings("unchecked")
	private static Duple<List<Edge>, List<Edge>> getEdgeLists(Node input, String[] edgeLabel1, String[] edgeLabel2) {
		Node localItem = (Node) input;
		Query[] q = new Query[edgeLabel1.length];
		for (int i = 0; i < edgeLabel1.length; i++)
			q[i] = hasTheLabel(edgeLabel1[i]);
		OrQuery orq1 = new OrQuery(q);
		q = new Query[edgeLabel2.length];
		for (int i = 0; i < edgeLabel2.length; i++)
			q[i] = hasTheLabel(edgeLabel2[i]);
		OrQuery orq2 = new OrQuery(q);
		List<Edge> el1 = (List<Edge>) get(localItem.edges(Direction.OUT), selectZeroOrMany(orq1));
		List<Edge> el2 = (List<Edge>) get(localItem.edges(Direction.OUT), selectZeroOrMany(orq2));
		return new Duple<List<Edge>, List<Edge>>(el1, el2);
	}

	public static boolean propose(Node localItem, String[] edgeLabel1, String[] edgeLabel2, String proposedEdgeLabel) {
		Duple<List<Edge>,List<Edge>> lstEdges = getEdgeLists(localItem,edgeLabel1,edgeLabel2);
		String choice;
		if (!lstEdges.getFirst().isEmpty())
			choice = lstEdges.getFirst().get(0).classId();
		else
			choice = lstEdges.getSecond().get(0).classId();
		return choice.equals(proposedEdgeLabel);
	}

}
