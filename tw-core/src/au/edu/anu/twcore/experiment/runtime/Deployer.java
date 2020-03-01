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
package au.edu.anu.twcore.experiment.runtime;

import static au.edu.anu.twcore.ecosystem.runtime.simulator.SimulatorEvents.*;
import static au.edu.anu.twcore.ecosystem.runtime.simulator.SimulatorStates.*;

import au.edu.anu.twcore.ecosystem.runtime.simulator.Simulator;
import fr.cnrs.iees.rvgrid.statemachine.StateMachineController;
import fr.cnrs.iees.rvgrid.statemachine.StateMachineEngine;
import fr.cnrs.iees.rvgrid.statemachine.Transition;

/**
 * The class which manages Simulators according to experiment size and constraints
 * <ul>
 * <li>receives commands from StateMachineController</li>
 * <li>sends status to StateMachineController</li>
 * <li>transmits commands to Simulator(s)</li>
 * <li>gets status from simulator(s)</li>
 * </ul>
 * understands the same commands as the simulator
 * 
 * @author Jacques Gignoux - 29 août 2019
 *
 */
public abstract class Deployer 
		extends StateMachineEngine<StateMachineController>
		implements DeployerProcedures	{

	public Deployer() {
		// swapping these lines means stateMachine().getCurrentState())==null in any controller?? - Ian
		//super(new Transition(waiting.state(),initialise.event()),stateList());
        super(new Transition(waiting.state(),reset.event()),stateList());
	}

	public void attachSimulator(Simulator sim) {
	}

	public void detachSimulator(Simulator sim) {
	}


	
}
