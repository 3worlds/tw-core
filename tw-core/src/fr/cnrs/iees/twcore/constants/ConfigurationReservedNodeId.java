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
package fr.cnrs.iees.twcore.constants;

import java.util.HashMap;
import java.util.Map;
import au.edu.anu.twcore.ecosystem.structure.Category;

/**
 * @author Ian Davies
 *
 * @date 20 May 2020
 */
public enum ConfigurationReservedNodeId {
	categories("*categories*"), //
	AVEphemeral("AVEphemeral"), //
	AVPopulation("AVPopulation"), //
	composition("*composition*"), //
	population(Category.population), //
	individual(Category.individual), //
	systemElements("*systemElements*"), //
	lifecycle(Category.lifeCycle), //
	group(Category.group), //
	relation(Category.relation), //
	component(Category.component), //
	space(Category.space), //
	arena(Category.arena), //
	lifespan("*lifespan*"), //
	ephemeral(Category.ephemeral), //
	permanent(Category.permanent),//
	age("age"),//
	birthDate("birthDate"),//
	count("count"),//
	nAdded("nAdded"),//
	nRemoved("nRemoved"),
	
	;

	private final String id;

	private ConfigurationReservedNodeId(String id) {
		this.id = id;
	}

	private static Map<String, ConfigurationReservedNodeId> lookup = new HashMap<>();
	static {
		for (ConfigurationReservedNodeId x : ConfigurationReservedNodeId.values())
			lookup.put(x.id, x);
	}

	public String id() {
		return id;
	}

	public static boolean isPredefined(String anId) {
		return lookup.get(anId) != null;
	}

}
