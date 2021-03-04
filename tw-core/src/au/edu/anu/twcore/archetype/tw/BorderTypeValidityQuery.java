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

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_SPACETYPE;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_SPACE_BORDERTYPE;

import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.ecosystem.structure.SpaceNode;
import fr.cnrs.iees.twcore.constants.BorderListType;
import fr.cnrs.iees.twcore.constants.SpaceType;
/**
 * A query to check that (1) if edge effec correction = "custom", then a
 * borderType property is provided and (2) borderType dimensions are compatible
 * with the space type and (3) borderType values are valid and (4) 'wrap'
 * borders come in pairs
 * 
 * @author Jacques Gignoux - 16 sept. 2020
 *
 */
public class BorderTypeValidityQuery extends QueryAdaptor{

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		SpaceNode spnode = (SpaceNode) input;
		SpaceType stype = (SpaceType) spnode.properties().getPropertyValue(P_SPACETYPE.key());
		int spdim = stype.dimensions();
		BorderListType borderTypes = (BorderListType) spnode.properties().getPropertyValue(P_SPACE_BORDERTYPE.key());
		if (spdim != borderTypes.size() / 2) {
			errorMsg = "Space of type " + stype + "' must have define " + (spdim * 2) + " borders.";
			return this;
		}
		int i = BorderListType.getUnpairedWrapIndex(borderTypes);
		if (i >= 0) {
			errorMsg = "Wrap-around in dimension " + i + " is unpaired.";
			return this;
		}

		if (BorderListType.isWrongTubularOrientation(borderTypes)) {
			errorMsg = "Tubular wrap-around is only supported in the x-dimension.";
			return this;
		}

		return this;

	}

}
