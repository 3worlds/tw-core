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

import org.apache.commons.math3.util.Precision;

import fr.cnrs.iees.omugi.properties.ReadOnlyPropertyList;

/**
 * A Stopping condition that stops when a given property equals a particular value
 * @author gignoux - 7 mars 2017
 *
 */
public class ValueStoppingCondition extends PropertyStoppingCondition {

	/** relative tolerance to compare floating-point numbers */
	private static final double RELATIVE_EPSILON = 1E-20;
	private Double stopCriterion = 0.0;	
		
	public ValueStoppingCondition(String stopVariable, 
			ReadOnlyPropertyList system,
			double stopCrit) {
		super(stopVariable,system);
		stopCriterion = stopCrit;
	}
	
	@Override
	public boolean stop() {
		if (Precision.equalsWithRelativeTolerance(getVariable(), stopCriterion, RELATIVE_EPSILON))
			return true;
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('(')
			.append(pname)
			.append(" = ")
			.append(stopCriterion)
			.append(" ± ")
			.append(RELATIVE_EPSILON)
			.append(')');
		return sb.toString();
	}

}
