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

import au.edu.anu.twcore.ecosystem.runtime.space.DynamicSpace;
import au.edu.anu.twcore.ecosystem.runtime.space.LocatedSystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

/**
 * @author Jacques Gignoux - 10/3/2017
 *
 * interface for user-defined ecological functions changing the state of a relation between
 * two SystemComponents
 */
public abstract class ChangeRelationStateFunction extends TwFunctionAdapter {

	public ChangeRelationStateFunction() {
		super();
		fType = TwFunctionTypes.ChangeRelationState;
	}

	/**
	 * change the state of a relation, i.e. possibly both the state of <em>focal</em> system component
	 * and <em>other</em> system component at the same time. Notice that some parameters may be null when
	 * calling the method (as denoted by 'if any').
	 *
	 * @param t	current time
	 * @param dt current time step
	 * @param arena the arena component, i.e. the top of the component hierarchy
	 * @param lifeCycle the life cycle component of this focal component, if any
	 * @param group the group component of this focal component, if any
	 * @param focal the focal component (may be the arena, a group or lifecycle component)
	 * @param otherLifeCycle the life cycle component of the other component, if any
	 * @param otherGroup the group component of the other component, if any
	 * @param other the other component (may be the arena, a group or lifecycle component)
	 * @param space the space attached to the parent process, if any
	 * @param nextFocalLoc the new location of the focal component, if the parent process is using a space
	 * @param nextOtherLoc the new location of the other component, if the parent process is using a space
	 */
	public abstract void changeRelationState(
		double t,
		double dt,
		CategorizedComponent<ComponentContainer> arena,
		CategorizedComponent<ComponentContainer> lifeCycle,
		CategorizedComponent<ComponentContainer> group,
		CategorizedComponent<ComponentContainer> focal,
		CategorizedComponent<ComponentContainer> otherLifeCycle,
		CategorizedComponent<ComponentContainer> otherGroup,
		CategorizedComponent<ComponentContainer> other,
		DynamicSpace<SystemComponent,LocatedSystemComponent> space,
		double[] nextFocalLoc,
		double[] nextOtherLoc
	);


}
