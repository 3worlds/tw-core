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

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemData;
import fr.cnrs.iees.uit.space.Box;
import fr.cnrs.iees.uit.space.Point;

/**
 * @author Jacques Gignoux - 4/8/2014
 *
 * interface for user-defined ecological functions changing the state of a ComplexSystem
 * this function is meant to read data in focal.currentState() and compute new data into focal.nextState()
 */
public abstract class ChangeStateFunction extends TwFunctionAdapter {

	/**
	 * change the state, ie the values of internal variables, of a system component.
	 * Notice that some parameters may be null when
	 * calling the method (as denoted by 'if any').
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
	 *
	 * @param nextFocal Drv focal driver values at next time (t+dt)
	 * @param nextFocalLoc focal new spatial coordinates at next time (t+dt)
	 */
//	public abstract void changeState(double t,
//		double dt,
//		SystemComponent focal);

	// new new version
//	public abstract void changeState(double t, double dt,
//	Box limits,Point focalLoc,
//	CategorizedComponent ecosystem, CategorizedComponent lifeCycle, CategorizedComponent group,
//	CategorizedComponent focal);

	// new version
	public abstract void changeState (
			double t,
			double dt,
			Box limits,
			TwData ecosystemPar,
			ComponentContainer ecosystemPop,
			TwData lifeCyclePar,
			ComponentContainer lifeCyclePop,
			TwData groupPar,
			ComponentContainer groupPop,
			SystemData focalAuto,
			TwData focalLtc,
			TwData focalDrv,
			TwData focalDec, // read write
			Point focalLoc,
			// returning data (read write parameters)
			TwData nextFocalDrv,
			double[] nextFocalLoc
			);
//			// read only arguments
//			// time, always present
//			double t,
//			double dt,
//			// ecosystem data, optional
//			TwData ecosystemPar,
//			ComponentContainer ecosystemPopulationData,
//			// life cycle data, optional
//			TwData lifeCyclePar,
//			ComponentContainer lifeCyclePopulationData,
//			// group data, optional
//			TwData groupPar,
//			ComponentContainer groupPopulationData,
//			// main/driver space limits
//			Box limits,
//			// autoVar, present only for ephemeral SystemComponent
//			SystemData auto,
//			// drivers, current values (optional)
//			TwData drv,
//			// lifetime constants (optional)
//			TwData ltc,
//			// current location if any (optional)
//			Point loc,
//			// read-write arguments for return values
//			// decorators, if present
//			TwData dec,
//			// next values for drivers, if present
//			TwData nextDrv,
//			// next location, if needed
//			double[] nextLoc);

}
