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
import au.edu.anu.twcore.ecosystem.runtime.space.DynamicSpace;
import au.edu.anu.twcore.ecosystem.runtime.space.LocatedSystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

/**
 * @author Jacques Gignoux - 4/8/2014
 *
 * interface for user-defined ecological functions deciding to delete a system.
 * result is a decision as a boolean.
 *
 */
public abstract class DeleteDecisionFunction extends AbstractDecisionFunction {

	private List<ChangeOtherStateFunction> consequences = new LinkedList<ChangeOtherStateFunction>();

	public DeleteDecisionFunction() {
		super();
		fType = TwFunctionTypes.DeleteDecision;
	}

	/**
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
	public abstract boolean delete(double t, double dt,
		CategorizedComponent<ComponentContainer> arena,
		CategorizedComponent<ComponentContainer> lifeCycle,
		CategorizedComponent<ComponentContainer> group,
		CategorizedComponent<ComponentContainer> focal,
		DynamicSpace<SystemComponent,LocatedSystemComponent> space);

	@Override
	public void addConsequence(TwFunction function) {
		consequences.add((ChangeOtherStateFunction) function);
	}

	@Override
	public Iterable<ChangeOtherStateFunction> getConsequences() {
		return consequences;
	}


}
