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

package au.edu.anu.twcore.ecosystem.runtime.simulator;

import au.edu.anu.twcore.exceptions.TwcoreException;

/**
 * @author Ian Davies
 *
 * @date 21 Feb 2020
 */
// A dummy change in master branch
public class RunTimeId {
	private RunTimeId() {
	};

	/**
	 * Maybe I'm wrong about all this. Maybe MR should not be the unit of OpenMole
	 * simulation. Maybe some exp design for Openmole generates all the artifacts
	 * required for submitting to OM either by hand or remotely through some account
	 * with OM. I'm not sure what the workflow would look like - especially for exp
	 * alg such as Genetic Alg etc
	 */

	private static int runTimeId = Integer.MIN_VALUE;

	/* set once from the cmd line when MR starts */
	public static void setRunTimeId(int id) {
		if (runTimeId != Integer.MIN_VALUE)
			throw new TwcoreException("Attempt to reinitialise RunTimeId.");
		runTimeId = id;
	}

	/* obtained by anything that wants to know - file i/o etc */
	public static int runTimeId() {
		if (runTimeId == Integer.MIN_VALUE)
			throw new TwcoreException("Attempt to access uninitialised RunTimeId.");
		return runTimeId;
	}
}
