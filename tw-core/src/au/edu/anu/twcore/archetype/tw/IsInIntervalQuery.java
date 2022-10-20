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

import fr.cnrs.iees.omugi.graph.property.Property;
import au.edu.anu.qgraph.queries.*;
import au.edu.anu.twcore.TextTranslations;
import fr.cnrs.iees.omhtk.utils.Interval;

/**
 * 
 * @author Jacques Gignoux - 9 sept. 2020
 *
 */
public class IsInIntervalQuery extends QueryAdaptor {
	private final Interval interval;

	public IsInIntervalQuery(Interval interval) {
		super();
		this.interval = interval;
	}

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		Property localItem = (Property) input;
		double value = ((Number) localItem.getValue()).doubleValue();
		if (!interval.contains(value)) {
			
			String[] msgs = TextTranslations.getIsInIntervalQuery(localItem.getValue(),interval);
			actionMsg = msgs[0];
			errorMsg = msgs[1];
			
			
//			errorMsg = "Property " + localItem.getKey() + "=" + localItem.getValue() + "' must be within " + interval
//					+ ".";
//			errorMsg = "Expected '" + localItem.getKey() + "' to be within " + interval +" but found "+localItem.getValue() + ".";
//
//			actionMsg = "Set value within the interval "+interval+".";
		}
		return this;
	}

}
