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

import java.util.List;

import au.edu.anu.twcore.ecosystem.runtime.StoppingCondition;

/**
 * Combines a list of stopping conditions with the AND operator
 * @author gignoux - 7 mars 2017
 *
 */
public class MultipleAndStoppingCondition extends MultipleStoppingCondition {

	public MultipleAndStoppingCondition(List<StoppingCondition> conds) {
		super(conds);
	}

	@Override
	public boolean stop() {
		for (int i=0; i<conditions.length; i++)
			if (!conditions[i].stop())
				return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		sb.append(conditions[0].toString());
		for (int i=1; i<conditions.length; i++)
			sb.append(" & ").append(conditions[i].toString());
		sb.append(')');
		return sb.toString();
	}

}
