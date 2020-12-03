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
package au.edu.anu.twcore.experiment.runtime.deployment;

import au.edu.anu.twcore.ecosystem.runtime.simulator.Simulator;
import au.edu.anu.twcore.experiment.runtime.Deployer;

/**
 * @author Ian Davies
 *
 * @date 1 Dec. 2020
 */

/**
 * A simulation thread for unattended simulations i.e. one using headless
 * widgets only. Intendend use is a system such as openMole but can be used
 * anywhere. There is nothing that specifies local/remote usage.
 */
@Deprecated  // keep until sure it's not needed.
public class UnattendedThread implements Runnable {

	private final Simulator sim; 
	private final Deployer dep;

	public UnattendedThread(Deployer dep, Simulator sim) {
		super();
		this.sim = sim;
		this.dep = dep;
	}

	@Override
	public void run() {
		sim.preProcess();
		while (!sim.stop())
			sim.step();
		sim.postProcess();
		dep.ended(sim);
	}

}
