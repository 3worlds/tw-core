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

import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.ecosystem.structure.SpaceNode;
import fr.cnrs.iees.twcore.constants.BorderListType;
import fr.cnrs.iees.twcore.constants.SpaceType;

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

/**
 * A query to check that (1) if edge effec correction = "custom", then a
 * borderType property is provided and (2) borderType dimensions are compatible
 * with the space type and (3) borderType values are valid and (4) 'wrap'
 * borders come in pairs
 * 
 * @author Jacques Gignoux - 16 sept. 2020
 *
 */
//Tested OK 16/6/2020
public class BorderTypeValidityQuery extends Query {

	private String message = "";

	public BorderTypeValidityQuery() {
		super();
	}

	/**
	 * Do we need the P_SPACE_EDGEEFFECTS property. Because of the interdependency
	 * between borderTypes and this, it becomes difficult to manage.
	 * 
	 * It seems to me there should always be a borderType list (now called
	 * BorderListType in the Archetype) - a 1 dim array.
	 * 
	 * The number of dims is the table size/2;
	 * 
	 * We can deduce the P_SPACE_EDGEEFFECTS property from this 1 dim table if we
	 * assume the order North, south, east west. Maybe some other order is better?
	 * 
	 * 
	 */
	@Override
	public Query process(Object input) { // input is a space node
		defaultProcess(input);
		SpaceNode spnode = (SpaceNode) input;
		SpaceType stype = (SpaceType) spnode.properties().getPropertyValue(P_SPACETYPE.key());
		int spdim = stype.dimensions();
		BorderListType borderTypes = (BorderListType) spnode.properties()
				.getPropertyValue(P_SPACE_BORDERTYPE.key());
		if (spdim!=borderTypes.size()/2) {
			message = "Space of type "+stype+"' must have define "+(spdim*2)+" borders.";
			return this;
		} else {
			int i = BorderListType.getUnpairedWrapIndex(borderTypes);
			if (i>=0) {
				message = "Wrap-around in dimension "+i+ " is unpaired.";
				return this;
			}
		}
		satisfied = true;
		
//		if (spnode.properties().hasProperty(P_SPACE_EDGEEFFECTS.key())) {
//			EdgeEffectCorrection eec = (EdgeEffectCorrection) spnode.properties()
//					.getPropertyValue(P_SPACE_EDGEEFFECTS.key());
//			if (eec == EdgeEffectCorrection.custom) {
//				// check borderType exists
//				if (spnode.properties().hasProperty(P_SPACE_BORDERTYPE.key())) {
//					// check borderType dimensions
//					SpaceType stype = (SpaceType) spnode.properties().getPropertyValue(P_SPACETYPE.key());
//					int spdim = stype.dimensions();
//					StringTable borderTypes = (StringTable) spnode.properties()
//							.getPropertyValue(P_SPACE_BORDERTYPE.key());
//					if (borderTypes.ndim() == 2) {
//						if ((borderTypes.size(0) == 2) && // dim 1 = lower (0) vs. upper (1)
//								(borderTypes.size(1) == spdim)) { // dim 2 = space dimension
//							// check borderType values
//							satisfied = true;
//							for (int i = 0; i < borderTypes.size(); i++)
//								if (!BorderType.keySet().contains(borderTypes.getWithFlatIndex(i)))
//									satisfied = false; // one of the values is not a valid border type
//							// check 'wrap' values come in pairs
//							for (int i = 0; i < borderTypes.size(1); i++)
//								if (borderTypes.getByInt(0, i).equals(BorderType.wrap.name()))
//									if (!borderTypes.getByInt(1, i).equals(BorderType.wrap.name())) {
//										satisfied = false; // missing upper wrap
//										message = "upper border in dimension " + i + " must be of type 'wrap'";
//									}
//							for (int i = 0; i < borderTypes.size(1); i++)
//								if (borderTypes.getByInt(1, i).equals(BorderType.wrap.name()))
//									if (!borderTypes.getByInt(0, i).equals(BorderType.wrap.name())) {
//										satisfied = false; // missing lower wrap
//										message = "lower border in dimension " + i + " must be of type 'wrap'";
//									}
//						} else
//							message = "'borderType' property of class StringTable must have dimension 1 = 2 and dimension 2 = "
//									+ spdim;
//					} else
//						message = "'borderType' property of class StringTable must have two dimensions";
//				} else
//					message = "edge effect correction type 'custom' requires a 'borderType' property";
//			} else
//				satisfied = true; // valid value of edgeEffect not requiring borderType (NB borderType will be
//									// ignored)
//		} else
//			message = "missing 'edgeEffectCorrection' property";
		return this;
	}

	@Override
	public String toString() {
		return "[" + stateString() + "space :" + message + "]";
	}

}
