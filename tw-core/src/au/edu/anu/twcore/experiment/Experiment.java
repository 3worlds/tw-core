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
import fr.cnrs.iees.twcore.constants.ExperimentDesignType;
import fr.ens.biologie.generic.Sealable;
import fr.ens.biologie.generic.Singleton;
import fr.ens.biologie.generic.utils.Logging;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.dynamics.SimulatorNode;
import au.edu.anu.twcore.experiment.runtime.Deployable;
import au.edu.anu.twcore.experiment.runtime.deployment.Deployer;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import java.util.logging.Logger;

/**
 * Class matching the "experiment" node label in the 3Worlds configuration tree.
 * Has no properties. Returns a controller to communicate with simulators
 *
 * @author Jacques Gignoux - 27 mai 2019
 *
 */
public class Experiment extends InitialisableNode implements Singleton<StateMachineController>, Sealable {

	private static Logger log = Logging.getLogger(Experiment.class);

	private boolean sealed = false;
	private StateMachineController controller = null;
	private Deployable deployer = null;
	/** class constant = number of simulators in this running session */
	private static int N_SIMULATORS = 0;

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

			SimulatorNode baselineSimulator = null;
			Design dsgn = (Design) get(getChildren(), selectOne(hasTheLabel(N_DESIGN.label())));
			if (dsgn.properties().hasProperty(P_DESIGN_TYPE.key())) {
				if (get(edges(Direction.OUT), selectZeroOrOne(hasTheLabel(E_BASELINE.label()))) != null) {
					// multiple systems
					baselineSimulator = (SimulatorNode) get(edges(Direction.OUT),
							selectOne(hasTheLabel(E_BASELINE.label())), endNode(), children(),
							selectOne(hasTheLabel(N_DYNAMICS.label())));
				} else {
					// single system -NB baseline is now [0..1]
					baselineSimulator = (SimulatorNode) get(getParent().getChildren(),
							selectOne(hasTheLabel(N_SYSTEM.label())), children(),
							selectOne(hasTheLabel(N_DYNAMICS.label())));
				}

				int nReps = 1;
				if (properties().hasProperty(P_EXP_NREPLICATES.key()))
					nReps = (Integer) properties().getPropertyValue(P_EXP_NREPLICATES.key());
				ExperimentDesignType expDesignType = null;
				if (dsgn.properties().hasProperty(P_DESIGN_TYPE.key()))
					expDesignType = (ExperimentDesignType) dsgn.properties().getPropertyValue(P_DESIGN_TYPE.key());
				deployer = new Deployer();
				controller = new StateMachineController(deployer);
				if (expDesignType != null)
					switch (expDesignType) {
					case singleRun: {
						for (int i = 0; i < nReps; i++)
							deployer.attachSimulator(baselineSimulator.getInstance(N_SIMULATORS++));
						break;
					}
					case crossFactorial: {
						log.warning(() -> "crossFactorial deployment not yet implemented");
						break;
					}
					default: {
						log.warning(() -> "undefined deployment type");
					}
					}
				else {
					log.warning(() -> "file defined deployment not yet implemented");
				}

//				log.info(() -> "reset any 'onExperimentStart' rngs.");
//				RngFactory.resetExperiment(); not needed. MR begins each exp when instanced

			}
			sealed = true;
		}

	}

	// TODO for Ian: implement something clever here
//	private int nLocalSimulators() {
//		return Runtime.getRuntime().availableProcessors();
////		return 2;
//	}

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
