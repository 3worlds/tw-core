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
package au.edu.anu.twcore.ecosystem.runtime.biology;

import java.util.LinkedList;
import java.util.List;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentData;
import fr.cnrs.iees.uit.space.Box;
import fr.cnrs.iees.uit.space.Point;

/**
 * @author Jacques Gignoux - 4/8/2014
 *
 * interface for user-defined ecological functions deciding to delete a system.
 * result is a decision as a boolean.
 *
 */
public abstract class DeleteDecisionFunction extends AbstractDecisionFunction {

	private List<ChangeOtherStateFunction> consequences = new LinkedList<ChangeOtherStateFunction>();

	/**
	 * @param t			current time
	 * @param dt		current time interval
	 * @param focal		system to delete
	 * @param environment read-only systems to help for computations
	 * @return	true to delete focal, false to keep it
	 */
//	public abstract boolean delete(double t,
//		double dt,
//		SystemComponent focal);

	/**
	 * delete self
	 *
	 * @param t	current time
	 * @param dt current time step
	 * @param limits boundary of the space set in the enclosing Process, if any
	 * @param ecosystemPar ecosystem parameters, if any
	 * @param ecosystemPop ecosystem population data
	 * @param lifeCyclePar life cycle parameters, if any
	 * @param lifeCyclePop life cycle population data, if any
	 * @param groupPar focal group parameters, if any
	 * @param groupPop focal group population data
	 * @param focalAuto focal automatic variables (age and birthDate)
	 * @param focalLtc focal lifetime constants, if any
	 * @param focalDrv focal driver variables at current time, if any
	 * @param focalDec focal decorator variables, if any
	 * @param focalLoc focal location at current time, if any
	 * @return true to delete focal, false to keep it
	 */
	public abstract boolean delete(
			double t,
			double dt,
			Box limits,
			TwData ecosystemPar,
			ComponentContainer ecosystemPop,
			TwData lifeCyclePar,
			ComponentContainer lifeCyclePop,
			TwData groupPar,
			ComponentContainer groupPop,
			ComponentData focalAuto,
			TwData focalLtc,
			TwData focalDrv,
			TwData focalDec,
			Point focalLoc
	);

	@Override
	public void addConsequence(TwFunction function) {
		consequences.add((ChangeOtherStateFunction) function);
	}

	@Override
	public Iterable<ChangeOtherStateFunction> getConsequences() {
		return consequences;
	}


}
