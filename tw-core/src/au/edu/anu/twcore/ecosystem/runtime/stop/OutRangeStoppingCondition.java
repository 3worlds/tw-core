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
package au.edu.anu.twcore.ecosystem.runtime.stop;

import fr.cnrs.iees.omugi.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.omhtk.utils.Interval;

/**
 * A stopping condition that stops when a variable falls outside its range
 * @author gignoux - 7 mars 2017
 *
 */
public class OutRangeStoppingCondition extends RangeStoppingCondition {

	public OutRangeStoppingCondition(String stopVariable, 
			ReadOnlyPropertyList system,
			Interval range) {
		super(stopVariable, system, range);
	}

	@Override
	public boolean stop() {
		return !range.contains(getVariable());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('(')
			.append(pname)
			.append(" \u2209 ")
			.append(range.toString())
			.append(')');
		return sb.toString();
	}

}
