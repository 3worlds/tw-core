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

import au.edu.anu.omugi.graph.property.Property;
import au.edu.anu.qgraph.queries.*;
import au.edu.anu.twcore.TextTranslations;
import fr.cnrs.iees.omhtk.utils.Interval;

/**
 * @author Ian Davies - 4 Oct 2021
 * 
 * Checks that inf() and sup() are not infinite
 */
public class IsBoundedIntervalQuery extends QueryAdaptor {

	public IsBoundedIntervalQuery() {
		super();
	}

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		Property localItem = (Property) input;
		Interval interval = ((Interval) localItem.getValue());
		if (interval.inf()==Double.NEGATIVE_INFINITY|| interval.sup()==Double.POSITIVE_INFINITY) {	
			String[] msgs = TextTranslations.getIsBoundedIntervalQuery(interval);
			actionMsg = msgs[0];
			errorMsg = msgs[1];
		}
		return this;
	}

}
