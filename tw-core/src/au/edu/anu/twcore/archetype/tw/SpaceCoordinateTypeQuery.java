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
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.List;

import au.edu.anu.qgraph.queries.*;
import au.edu.anu.twcore.TextTranslations;
import au.edu.anu.twcore.ecosystem.structure.SpaceNode;
import fr.cnrs.iees.graph.DataHolder;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.twcore.constants.DataElementType;

/**
 * Check that a field pointed at by a space coordinate edge is of a numeric type
 *
 * @author J. Gignoux - 19 nov. 2020
 *
 */

public class SpaceCoordinateTypeQuery extends QueryAdaptor {

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		SpaceNode space = (SpaceNode) input;
		List<DataHolder> fields = (List<DataHolder>) get(space.edges(Direction.OUT),
				selectZeroOrMany(hasTheLabel(E_COORDMAPPING.label())), edgeListEndNodes());
//		EnumSet<DataElementType> numberTypes = EnumSet.of(Double,Integer,Long,Float,Short,Byte);

		for (DataHolder f : fields) {
			if (f.properties().hasProperty(P_FIELD_TYPE.key())) {
				DataElementType ftype = (DataElementType) f.properties().getPropertyValue(P_FIELD_TYPE.key());
//				if (!numberTypes.contains(ftype))
				if (!ftype.isNumeric()) {
					String[] msgs = TextTranslations.getSpaceCoordinateTypeQuery(ftype.name());
					actionMsg = msgs[0];
					errorMsg = msgs[1];
//					actionMsg = "Change coordinate fields to be numeric.";
//					errorMsg = "coordinate fields must be numeric but found '"+ftype+"'.";
					return this;
				}
			}
		}
		return this;
	}

}
