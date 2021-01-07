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
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.structure.Category;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Jacques Gignoux - 4/8/2014
 *
 * interface for user-defined ecological functions deciding to recruit a ComplexSystem
 * to a new set of categories.
 * result is a decision index (based on dsl, will chose which transformation to apply to focal)
 *
 */
public abstract class ChangeCategoryDecisionFunction extends AbstractDecisionFunction
		implements RecruitFunction {

	private List<SetOtherInitialStateFunction> consequences = new LinkedList<SetOtherInitialStateFunction>();
	// the list of transitions - by convention transitionTo[0] means no transition
	private String[] transitionTo = null;

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
	// typical use in the code:
//
//		return transition(decide(proba));
//		return transition(select(wt1,wt2,wt3));
//
	public abstract String changeCategory(double t, double dt,
			CategorizedComponent arena,
			CategorizedComponent lifeCycle,
			CategorizedComponent group,
			CategorizedComponent focal,
			DynamicSpace<SystemComponent> space);

	@Override
	public void addConsequence(TwFunction function) {
		consequences.add((SetOtherInitialStateFunction) function);
	}

	@Override
	public Iterable<SetOtherInitialStateFunction> getConsequences() {
		return consequences;
	}

	/**
	 * record the list of transitions according to the lifecycle
	 *
	 * @param transitions
	 */
	public void setTransitions(Collection<Category> transitions) {
		SortedSet<Category> set = new TreeSet<>();
		set.addAll(transitions);
		transitionTo = new String[set.size()+1];
		transitionTo[0] = null;
		int i=0;
		for (Category cat:set)
			transitionTo[++i] = cat.id();
	}

	@Override
	public final String transition(int i) {
		return transitionTo[i];
	}

	@Override
	public final String transition(boolean change) {
		return transitionTo[change?1:0];
	}

}
