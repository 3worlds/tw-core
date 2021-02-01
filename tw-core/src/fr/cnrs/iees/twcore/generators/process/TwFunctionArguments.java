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
package fr.cnrs.iees.twcore.generators.process;

import fr.cnrs.iees.uit.space.Box;

/**
 * Arguments used in TwFunction descendants
 *
 * @author J. Gignoux - 27 avr. 2020

 */
public enum TwFunctionArguments {

	t				("double", "current time"),
	dt				("double", "current time step"),
	// component Processes
	arena			("au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent", "whole system "),
	lifeCycle		("au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent", "focal life cycle "),
	group			("au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent", "focal group "),
	focal			("au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent", "focal component "),
	// relation Processes
	otherLifeCycle	("au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent", "other life cycle "),
	otherGroup		("au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent", "other group "),
	other			("au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent", "other component "),
	// space
	space			("au.edu.anu.twcore.ecosystem.runtime.space.DynamicSpace", "space "),
	limits			(Box.class.getCanonicalName(), "limits "),
	searchRadius	("double", "maximal search radius "),

//	focalLoc		(Point.class.getCanonicalName(), "focal component location "),
//	otherLoc		(Point.class.getCanonicalName(), "other component location "),
	// writeable arguments
//	nextFocalLoc	("double[]", "focal component new location"),
//	nextOtherLoc	("double[]", "other component new location"),
	// utilities (local to TwFunction)
	random			("java.util.Random", "random number generator"),
	decider			("au.edu.anu.twcore.ecosystem.runtime.biology.DecisionFunction", "decision function"),
	selector		("au.edu.anu.twcore.ecosystem.runtime.biology.SelectionFunction", "selection function"),
	recruit			("au.edu.anu.twcore.ecosystem.runtime.biology.RecruitFunction", "recruitment function"),
	timer			("au.edu.anu.twcore.ecosystem.runtime.timer.EventQueue", "event timers fed by this method"),
	;
	private final String type;
	private final String description;
	private TwFunctionArguments(String type, String description) {
		this.type = type;
		this.description = description;
	}
	public String type() {
		return type;
	}

	public String description() {
		return description;
	}

}
