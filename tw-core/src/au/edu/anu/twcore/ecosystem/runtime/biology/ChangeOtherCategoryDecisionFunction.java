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

import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

/**
 * @author Jacques Gignoux - 4/8/2014
 *
 * interface for user-defined ecological functions deciding to recruit a ComplexSystem
 * to a new set of categories.
 * result is a decision index (based on dsl, will chose which transformation to apply to focal)
 *
 */
public abstract class ChangeOtherCategoryDecisionFunction extends AbstractDecisionFunction {

	private List<SetOtherInitialStateFunction> consequences = new LinkedList<SetOtherInitialStateFunction>();

//	public abstract String changeCategory(double t,
//		double dt,
//		SystemComponent focal,
//		SystemComponent other);

	public ChangeOtherCategoryDecisionFunction() {
		super();
		fType = TwFunctionTypes.ChangeOtherCategoryDecision;
	}

	/**
	 * <em>focal</em> system component changes the category of <em>other</em> system component
	 * (requires a life cycle for <em>other</em>). Notice that some parameters may be null when
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
	 * @param otherGroupPar other group parameters,if any
	 * @param otherGroupPop other group population data
	 * @param focalAuto focal automatic variables (age and birthDate)
	 * @param focalLtc focal lifetime constants, if any
	 * @param focalDrv focal driver variables at current time, if any
	 * @param focalDec focal decorator variables, if any
	 * @param focalLoc focal location at current time, if any
	 * @param otherAuto other automatic variables (age and birthDate)
	 * @param otherLtc other lifetime constants, if any
	 * @param otherDrv other driver variables at current time, if any
	 * @param otherDec other decorator variables, if any
	 * @param otherLoc other location at current time, if any
	 * @return the name of the new category <em>other</em> will move to
	 */
//	public abstract String changeCategory(
//			double t,
//			double dt,
//			Box limits,
//			TwData ecosystemPar,
//			ComponentContainer ecosystemPop,
//			TwData lifeCyclePar,
//			ComponentContainer lifeCyclePop,
//			TwData groupPar,
//			ComponentContainer groupPop,
//			TwData otherGroupPar,
//			ComponentContainer otherGroupPop,
//			ComponentData focalAuto,
//			TwData focalLtc,
//			TwData focalDrv,
//			TwData focalDec,
//			Point focalLoc,
//			ComponentData otherAuto,
//			TwData otherLtc,
//			TwData otherDrv,
//			TwData otherDec,
//			Point otherLoc
//	);

	public abstract String changeCategory(
			double t,
			double dt,
			CategorizedComponent<ComponentContainer> arena,
			CategorizedComponent<ComponentContainer> lifeCycle,
			CategorizedComponent<ComponentContainer> group,
			CategorizedComponent<ComponentContainer> space,
			CategorizedComponent<ComponentContainer> focal,
			CategorizedComponent<ComponentContainer> otherLifeCycle,
			CategorizedComponent<ComponentContainer> otherGroup,
			CategorizedComponent<ComponentContainer> other,
			double[] nextFocalLoc,
			double[] nextOtherLoc
	);

	@Override
	public void addConsequence(TwFunction function) {
		consequences.add((SetOtherInitialStateFunction) function);
	}

	@Override
	public Iterable<SetOtherInitialStateFunction> getConsequences() {
		return consequences;
	}

}
