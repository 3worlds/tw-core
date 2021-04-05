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

import java.util.ArrayList;
import java.util.Collection;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.TextTranslations;
import au.edu.anu.twcore.data.Record;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.impl.ALEdge;

/**
 * Check that a root record is used by at most one category and for at most one usage among
 * {autoVar, decorators, drivers, constants}.
 * 
 * @author Jacques Gignoux - 9 févr. 2021
 *
 */
public class RecordUsedByAtMostOneCategoryQuery extends QueryAdaptor{
	private final Collection<String> edgeLabels; 
	public RecordUsedByAtMostOneCategoryQuery(StringTable params) {
		super();
		edgeLabels = new ArrayList<>(4); 
		for (int i=0; i<params.size(); i++)
			edgeLabels.add(params.getWithFlatIndex(i));
	}

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		Record record = (Record) input;
		int nEdges = 0;
		for (ALEdge e:record.edges(Direction.IN))
			if (edgeLabels.contains(e.classId()))
				nEdges++;
		if (nEdges>1) {
			String[] msgs = TextTranslations.getRecordUsedByAtMostOneCategoryQuery(nEdges);
			actionMsg = msgs[0];
			errorMsg = msgs[1];

//			actionMsg = "Remove "+(nEdges -1)+" to Category nodes from this root record.";
//			errorMsg = "A root record must have 1 edge to a Category but found "+ nEdges +".";
		}
		return this;
	}

}
