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

import au.edu.anu.omugi.collections.tables.*;
import au.edu.anu.qgraph.queries.*;
import au.edu.anu.twcore.TextTranslations;
import fr.cnrs.iees.graph.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.get;
import static au.edu.anu.qgraph.queries.CoreQueries.*;

import java.util.List;


/**
 * Check that either at least one out edge or a property is present in a node, but not both. 
 * Uses EDGE labels.
 * 
 * @author Jacques Gignoux - 16 déc. 2021
 *
 */
public class OutEdgeXorPropertyQuery extends QueryAdaptor {
	private final String edgeLabel;
	private final String propertyName;

	public OutEdgeXorPropertyQuery(StringTable args) {
		edgeLabel = args.getWithFlatIndex(0);
		propertyName = args.getWithFlatIndex(1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		Node localItem = (Node) input;
		boolean propertyPresent = false;
		if (localItem instanceof ReadOnlyDataHolder)
			propertyPresent = (((ReadOnlyDataHolder) localItem).properties().hasProperty(propertyName));
		List<Edge> le = (List<Edge>) get(localItem, 
			outEdges(), 
			selectZeroOrMany(hasTheLabel(edgeLabel)));
		boolean edgePresent = (!le.isEmpty());
		if (!(propertyPresent ^ edgePresent)) {
			String[] msgs = TextTranslations.getEdgeXorPropertyQuery(propertyName,edgeLabel);
			actionMsg = msgs[0];
			errorMsg = msgs[1];
		}
		return this;
	}

}
