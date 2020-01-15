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
import fr.cnrs.iees.twcore.constants.TimeUnits;

/**
 * Ancestor class for user-defined setting of secondary parameters
 * TODO: improve this implementation - (1) there are no stage or species
 * (2) Initialisers could actually be attached to SystemComponent, LifeCycle or Ecosystem
 * (3) Initialisers should have access to the parameter sets of the systems they are
 * nested in (eg SystemComponent to LifeCycle or Ecosystem, LifeCycle to Ecosystem)
 * 
 * @author Jacques Gignoux - 10 juil. 2019
 *
 */
public abstract class SecondaryParametersInitialiser {

    public abstract void setSecondaryParameters(TwData speciesParameters,
    	TwData stageParameters,
    	long timeOrigin,
    	TimeUnits timeUnit); 
	
}
