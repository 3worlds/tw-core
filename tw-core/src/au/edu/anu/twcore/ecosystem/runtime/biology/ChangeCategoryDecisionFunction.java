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

import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.space.DynamicSpace;
import au.edu.anu.twcore.ecosystem.runtime.space.LocatedSystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Jacques Gignoux - 4/8/2014
 *
 * interface for user-defined ecological functions deciding to recruit a ComplexSystem
 * to a new set of categories.
 * result is a decision index (based on dsl, will chose which transformation to apply to focal)
 *
 */
public abstract class ChangeCategoryDecisionFunction extends AbstractDecisionFunction {

	private List<SetOtherInitialStateFunction> consequences = new LinkedList<SetOtherInitialStateFunction>();

	public ChangeCategoryDecisionFunction() {
		super();
		fType = TwFunctionTypes.ChangeCategoryDecision;
	}

	/**
	 * change category of a system component according to life cycle
	 * (has no effect if no life cycle is specified). Notice that some parameters may be null when
	 * calling the method (as denoted by 'if any').
	 *
	 * @param t	current time
	 * @param dt current time step
	 * @param arena the arena component, i.e. the top of the component hierarchy
	 * @param lifeCycle the life cycle component of this focal component, if any
	 * @param group the group component of this focal component, if any
	 * @param focal the focal component (may be the arena, a group or lifecycle component)
	 * @param space the space attached to the parent process, if any
	 * @return
	 */
	public abstract String changeCategory(double t, double dt,
			CategorizedComponent<ComponentContainer> arena,
			CategorizedComponent<ComponentContainer> lifeCycle,
			CategorizedComponent<ComponentContainer> group,
			CategorizedComponent<ComponentContainer> focal,
			DynamicSpace<SystemComponent,LocatedSystemComponent> space);

	@Override
	public void addConsequence(TwFunction function) {
		consequences.add((SetOtherInitialStateFunction) function);
	}

	@Override
	public Iterable<SetOtherInitialStateFunction> getConsequences() {
		return consequences;
	}

}
