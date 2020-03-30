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
package au.edu.anu.twcore.rngFactory;

import java.util.Random;

import au.edu.anu.twcore.rngFactory.RngFactory.Generator;
import fr.cnrs.iees.twcore.constants.RngAlgType;
import fr.cnrs.iees.twcore.constants.RngResetType;
import fr.cnrs.iees.twcore.constants.RngSeedSourceType;

/**
 * An interface for objects that hold a random number generator (RNG)
 * 
 * @author Jacques Gignoux - 5 févr. 2020
 *
 */
public interface RngHolder {

	/** The name of the 3worlds default RNG*/
	public static final String defRngName = "default 3wRNG";

	/** return the unique rng associated to the instance */
	public Random rng();

	/** sets the rng (meant to be used once per instance */
	public void setRng(Random rng);
	
	/** returns the same instance of the default 3Worlds RNG for a given index value */
	public default Random defaultRng(int index) {
		Generator gen = RngFactory.find(defRngName+":"+index);
		if (gen != null)
			return gen.getRandom();
		else {
			gen = RngFactory.newInstance(defRngName+":"+index, 0, RngResetType.never, 
				RngSeedSourceType.secure,RngAlgType.Pcg32);
			return gen.getRandom();
		}
	}

}
