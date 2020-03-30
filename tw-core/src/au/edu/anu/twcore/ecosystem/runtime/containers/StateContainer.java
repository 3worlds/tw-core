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
package au.edu.anu.twcore.ecosystem.runtime.containers;

import au.edu.anu.twcore.data.runtime.TwData;

/**
 * Interface for a container with parameters and state variables
 *
 * @author Jacques Gignoux - 16 janv. 2020
 *
 */
public interface StateContainer extends Container {

	/**
	 * Returns the parameter set associated to this container. It is specified by
	 * the categories associated to the container, accessible through the
	 * {@code categoryInfo()} method.
	 *
	 * @return the parameter set - may be {@code null}
	 */
	public TwData parameters();

	/**
	 * Returns the variables associated to this container. It is specified by the
	 * categories associated to the container, accessible through the
	 * {@code categoryInfo()} method.
	 *
	 * @return the variables - may be {@code null}
	 */
	public TwData variables();

	public void clearVariables();

}
