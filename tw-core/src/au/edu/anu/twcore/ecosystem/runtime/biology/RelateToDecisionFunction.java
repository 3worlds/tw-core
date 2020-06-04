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

import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

/**
 * @author Jacques Gignoux - 4/8/2014
 *
 * interface for user-defined ecological functions deciding to establish a relation
 * with another ComplexSystem.
 * result is a decision as a boolean.
 *
 */
public abstract class RelateToDecisionFunction extends AbstractDecisionFunction {

	public RelateToDecisionFunction() {
		super();
		fType = TwFunctionTypes.RelateToDecision;
	}

	/**
	 * <em>focal</em> system component establishes a new relation to <em>other</em> system component.
	 * Notice that some parameters may be null when calling the method (as denoted by 'if any').
	 *
	 * @param t current time
	 * @param dt current time step
	 * @param arena
	 * @param lifeCycle
	 * @param group
	 * @param space
	 * @param focal
	 * @param otherLifeCycle
	 * @param otherGroup
	 * @param other
	 * @param nextFocalLoc
	 * @param nextOtherLoc
	 * @return
	 */
	public abstract boolean relate(
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

}
