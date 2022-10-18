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

import java.util.*;

import au.edu.anu.omugi.collections.tables.StringTable;
import au.edu.anu.qgraph.queries.*;
import au.edu.anu.twcore.TextTranslations;
import fr.cnrs.iees.graph.DataHolder;
import fr.cnrs.iees.graph.Element;
import fr.cnrs.iees.graph.impl.TreeGraphNode;

/**
 * Check that a series of Edges or Nodes which have a common number property
 * have values that can be strictly ranked (no check that the values are evenly
 * spread, only that there are no two equal values).
 *
 * @author J. Gignoux - 19 nov. 2020
 *
 */
public class RankingPropertyQuery extends QueryAdaptor {
	private final String edgeLabel;
	private final String propName;

	/**
	 *
	 * @param args 2-dim StringTable [0] = edge label, [1] = rank property name
	 */
	public RankingPropertyQuery(StringTable args) {
		super();
		edgeLabel = args.getWithFlatIndex(0);
		propName = args.getWithFlatIndex(1);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The expected input is a {@linkplain TreeGraphNode}.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		TreeGraphNode node = (TreeGraphNode) input;
		Collection<DataHolder> elements = null;
		if (edgeLabel.equals(E_CHILD.label()))
			elements = (Collection<DataHolder>) node.getChildren();
		else
			elements = (Collection<DataHolder>) get(node.edges(), selectZeroOrMany(hasTheLabel(edgeLabel)));

		String numberList = "";
		SortedSet<Number> ranks = new TreeSet<>();
		if (elements != null) {
			for (DataHolder dh : elements)
				if (dh.properties().hasProperty(propName)) {
					Number rk = (Number) dh.properties().getPropertyValue(propName);
					ranks.add(rk);
					numberList += ", " + rk;
				}
			// if two ranks are equal, then the set should be smaller than the collection
			if (ranks.size() < elements.size()) {
				String elementList = "";
				for (DataHolder e:elements)
					elementList += ", " + ((Element)e).toShortString();
				elementList = elementList.replaceFirst(", ", "");
				numberList = numberList.replaceFirst(", ", "");
				if (edgeLabel.equals(E_CHILD.label())) {
					String[] msgs = TextTranslations.getRankingPropertyQuery1(propName,elementList,numberList,node.toShortString());
					actionMsg = msgs[0];
					errorMsg = msgs[1];

//					actionMsg = "Edit '" + propName + "' values for nodes [" + elementList
//							+ "] to unique values.";
//					errorMsg = "Expected '" + propName + "' values for children of '" + node.toShortString()
//							+ "' to be unique but found values [" + numberList + "].";
					return this;
				} else {
					String[] msgs = TextTranslations.getRankingPropertyQuery2(propName,elementList,numberList,edgeLabel);
					actionMsg = msgs[0];
					errorMsg = msgs[1];
//					actionMsg = "Edit '" + propName + "' values for elements [" + elementList
//							+ "] to unique values.";
//				
//					errorMsg = "Expected '" + propName + "' values of '" + edgeLabel + "' to be unique but found ["
//							+ numberList + "].";
					return this;
				}
			}
		}
		return this;
	}

}
