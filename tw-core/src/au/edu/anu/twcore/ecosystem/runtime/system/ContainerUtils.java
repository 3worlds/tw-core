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
package au.edu.anu.twcore.ecosystem.runtime.system;

/**
 * Static methods to find container in the arena > life cycle > group hierarchy
 * 
 * @author Jacques Gignoux - 7 janv. 2022
 *
 */
public class ContainerUtils {
	
	// to prevent instantiation
	private ContainerUtils() {}
	
	public static ComponentContainer getArenaContainer(ArenaComponent arena) {
		return (ComponentContainer)arena.content();
	}

	public static ComponentContainer getGroupContainer() {
		return null;
	}
	
	public static ComponentContainer getLifeCycleContainer(ArenaComponent arena, String lcId) {
		if (arena.content().contains(lcId))
			return (ComponentContainer) arena.content().findContainer(lcId);
		else {
			
		}
		return null;
	}
	
}
