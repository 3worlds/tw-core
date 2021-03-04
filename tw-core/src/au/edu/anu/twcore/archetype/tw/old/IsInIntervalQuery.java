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
package au.edu.anu.twcore.archetype.tw.old;

import au.edu.anu.rscs.aot.graph.property.Property;
import au.edu.anu.rscs.aot.old.queries.Query;
import fr.ens.biologie.generic.utils.Interval;

/**
 * 
 * @author Jacques Gignoux - 9 sept. 2020
 *
 */
@Deprecated
public class IsInIntervalQuery extends Query {

	private Interval interval;
	private Property localItem;
	
	public IsInIntervalQuery(Interval interval) {
		super();
		this.interval = interval;
	}

	@Override
	public Query process(Object input) { // input is a property containing a number
		defaultProcess(input);
		localItem = (Property)input;
		double value = ((Number) localItem.getValue()).doubleValue();
		satisfied = interval.contains(value);
		return this;
	}

	public String toString() {
		//NB will crash if process has not been run
		return "[" + stateString() + "Property "+localItem.getKey()+"="+localItem.getValue()+"' must be within " + interval + " ]";
	}

}
