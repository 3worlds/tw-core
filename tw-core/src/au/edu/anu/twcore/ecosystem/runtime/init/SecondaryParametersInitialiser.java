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
package au.edu.anu.twcore.ecosystem.runtime.init;

import au.edu.anu.twcore.data.runtime.TwData;

/**
 * Ancestor class for user-defined setting of secondary parameters
 * activated at simulator reset, or whenever parameters are edited
 *
 * TODO: what about parameters which values depend on time unit and time origin?
 * there is no timemodel concept here...
 *
 * @author Jacques Gignoux - 10 juil. 2019
 *
 */
@Deprecated
public abstract class SecondaryParametersInitialiser {

	/**
	 * Compute secondary parameters from other parameter values
	 *
	 * @param groupParameters parameters attached to a group of system components
	 * @param lifeCycleParameters parameters attached to a life cycle
	 * @param ecosystemParameters parameters attached to the whole ecosystem
	 */
    public abstract void setSecondaryParameters(TwData groupParameters,
    	TwData lifeCycleParameters,
    	TwData ecosystemParameters);

}
