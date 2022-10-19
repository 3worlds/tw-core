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

import java.util.List;

import au.edu.anu.omugi.collections.tables.*;
import au.edu.anu.qgraph.queries.*;
import au.edu.anu.qgraph.queries.base.OrQuery;
import au.edu.anu.twcore.TextTranslations;
import fr.cnrs.iees.graph.*;
import fr.cnrs.iees.omhtk.utils.Duple;

/**
 * Checks that an out edge has two labels (each coming from two separate sets).
 * ie, if one label of the first set is present, then one of the second must be
 * here too
 * 
 * NB Modified to agree with original msg. It's no longer a pure NXOR but also
 * handles case where there are no out-edges.-IDD
 * 
 * @author Jacques Gignoux - 14 Novembre 2019
 * 
 * 
 */
public class OutEdgeXNorQuery extends QueryAdaptor {
	private final String[] edgeLabel1;
	private final String[] edgeLabel2;

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
		for (int i = 0; i < edgeLabel2.length; i++)
			edgeLabel2[i] = label2.getWithFlatIndex(i);
	}

	public OutEdgeXNorQuery(StringTable label1, String label2) {
		this(label2, label1);
	}

	public OutEdgeXNorQuery(StringTable label1, StringTable label2) {
		super();
		edgeLabel1 = new String[label1.size()];
		edgeLabel2 = new String[label2.size()];
		for (int i = 0; i < edgeLabel1.length; i++)
			edgeLabel1[i] = label1.getWithFlatIndex(i);
		for (int i = 0; i < edgeLabel2.length; i++)
			edgeLabel2[i] = label2.getWithFlatIndex(i);
	}

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		Node localItem = (Node) input;
		Duple<List<Edge>, List<Edge>> edgeList = getEdgeLists(localItem, edgeLabel1, edgeLabel2);
//		Queryable[] q = new Queryable[edgeLabel1.length];
//		for (int i = 0; i < edgeLabel1.length; i++)
//			q[i] = hasTheLabel(edgeLabel1[i]);
//		OrQuery orq1 = new OrQuery(q);
//		q = new Queryable[edgeLabel2.length];
//		for (int i = 0; i < edgeLabel2.length; i++)
//			q[i] = hasTheLabel(edgeLabel2[i]);
//		OrQuery orq2 = new OrQuery(q);
//		List<Edge> el1 = (List<Edge>) get(localItem.edges(Direction.OUT), selectZeroOrMany(orq1));
//		List<Edge> el2 = (List<Edge>) get(localItem.edges(Direction.OUT), selectZeroOrMany(orq2));
		if (edgeList.getFirst().isEmpty() && edgeList.getSecond().isEmpty()) {
			String[] msgs = TextTranslations.getOutEdgeXNorQuery1(edgeLabel1, edgeLabel2);
			actionMsg = msgs[0];
			errorMsg = msgs[1];
		} else if ((edgeList.getFirst().size() > 0) ^ (edgeList.getSecond().size() > 0)) {
			String[] msgs;
			if (edgeList.getFirst().isEmpty())
				msgs = TextTranslations.getOutEdgeXNorQuery2(edgeLabel1, edgeLabel2);
			else
				msgs = TextTranslations.getOutEdgeXNorQuery2(edgeLabel2, edgeLabel1);
			actionMsg = msgs[0];
			errorMsg = msgs[1];

//		 According to the message below we must have at least 2 edges, one from each edgeLabel set. Therefore its not really an NXOR

//			errorMsg = "There must be at least one edge labelled from " + Arrays.toString(edgeLabel1) + " and one from "
//					+ Arrays.toString(edgeLabel2) + ".]";
//			actionMsg = "Add one of " + Arrays.toString(edgeLabel1) + " edges and one of "
//					+ Arrays.toString(edgeLabel2) + " edges.";
		}
		return this;
	}
	// we don't need a proposal because you can always add one of these somewhere.
	// It's up to the query to decide if eventually, the query is satisfied

//	public static boolean propose(Node localItem, String[] edgeLabel1, String[] edgeLabel2, String proposedEdgeLabel) {
//		Duple<List<Edge>, List<Edge>> edgeList = getEdgeLists(localItem, edgeLabel1, edgeLabel2);
//		/**
//		 * The proposed edge id must be:
//		 * 
//		 * 1) in either one of the edge lists
//		 */
//		return false;
//	}

	@SuppressWarnings("unchecked")
	private static Duple<List<Edge>, List<Edge>> getEdgeLists(Node localItem, String[] edgeLabel1,
			String[] edgeLabel2) {
		Queryable[] q = new Queryable[edgeLabel1.length];
		for (int i = 0; i < edgeLabel1.length; i++)
			q[i] = hasTheLabel(edgeLabel1[i]);
		OrQuery orq1 = new OrQuery(q);
		q = new Queryable[edgeLabel2.length];
		for (int i = 0; i < edgeLabel2.length; i++)
			q[i] = hasTheLabel(edgeLabel2[i]);
		OrQuery orq2 = new OrQuery(q);
		List<Edge> el1 = (List<Edge>) get(localItem.edges(Direction.OUT), selectZeroOrMany(orq1));
		List<Edge> el2 = (List<Edge>) get(localItem.edges(Direction.OUT), selectZeroOrMany(orq2));

		return new Duple<List<Edge>, List<Edge>>(el1, el2);
	}
}
