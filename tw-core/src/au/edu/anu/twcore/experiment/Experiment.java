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
package au.edu.anu.twcore.experiment;

import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.rvgrid.statemachine.StateMachineController;
import fr.ens.biologie.generic.Sealable;
import fr.ens.biologie.generic.Singleton;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.dynamics.SimulatorNode;
import au.edu.anu.twcore.experiment.runtime.Deployer;
import au.edu.anu.twcore.experiment.runtime.SimpleDeployer;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ExperimentDesignType.*;
import static au.edu.anu.twcore.ecosystem.runtime.simulator.SimulatorEvents.*;

/**
 * Class matching the "experiment" node label in the 3Worlds configuration tree.
 * Has no properties.
 * Returns a controller to communicate with simulators
 * 
 * @author Jacques Gignoux - 27 mai 2019
 *
 */
public class Experiment 
		extends InitialisableNode 
//		implements Singleton<ExperimentController>, Sealable {
		implements Singleton<StateMachineController>, Sealable {

	private boolean sealed = false;
//	private ExperimentController controller = null;
	private StateMachineController controller = null;
	private Deployer deployer = null;
	
	// default constructor
	public Experiment(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public Experiment(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		if (!sealed) {
			super.initialise();
			Design d = (Design) get(getChildren(),selectOne(hasTheLabel(N_DESIGN.label())));
			// single run experiment
			if (d.properties().hasProperty(P_DESIGN_TYPE.key()))
				if (d.properties().getPropertyValue(P_DESIGN_TYPE.key()).equals(singleRun)) {
					deployer = new SimpleDeployer();
					SimulatorNode sim = (SimulatorNode) get(edges(Direction.OUT),
						selectOne(hasTheLabel(E_BASELINE.label())),
						endNode(),
						children(),
						selectOne(hasTheLabel(N_DYNAMICS.label())));
					deployer.attachSimulator(sim.newInstance());
					controller = new StateMachineController(deployer);
					// this puts the deployer in "waiting" state
					controller.sendEvent(initialise.event());
				}
			// multiple simulators, local
			// TODO
			// multiple simulators, remote
			// TODO
			sealed = true;
		}
	}

	@Override
	public int initRank() {
		return N_EXPERIMENT.initRank();
	}

	@Override
	public StateMachineController getInstance() {
		if (!sealed)
			initialise();
		return controller;
	}
	
	@Override
	public Sealable seal() {
		sealed = true;
		return this;
	}

	@Override
	public boolean isSealed() {
		return sealed;
	}

}
