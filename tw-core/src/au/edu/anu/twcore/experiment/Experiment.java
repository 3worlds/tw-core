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
import fr.cnrs.iees.twcore.constants.DeploymentType;
import fr.ens.biologie.generic.Sealable;
import fr.ens.biologie.generic.Singleton;
import fr.ens.biologie.generic.utils.Logging;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.dynamics.SimulatorNode;
import au.edu.anu.twcore.experiment.runtime.Deployer;
import au.edu.anu.twcore.experiment.runtime.deployment.ParallelDeployer;
import au.edu.anu.twcore.experiment.runtime.deployment.SimpleDeployer;

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
	private Deployer deployer = null;
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
			Design d = (Design) get(getChildren(), selectOne(hasTheLabel(N_DESIGN.label())));

			SimulatorNode sim = null;
			if (d.properties().hasProperty(P_DESIGN_TYPE.key())) {
				if (get(edges(Direction.OUT), selectZeroOrOne(hasTheLabel(E_BASELINE.label()))) != null) {
					// multiple systems
					sim = (SimulatorNode) get(edges(Direction.OUT), selectOne(hasTheLabel(E_BASELINE.label())),
							endNode(), children(), selectOne(hasTheLabel(N_DYNAMICS.label())));
				} else {
					// single system -NB baseline is now [0..1]
					sim = (SimulatorNode) get(getParent().getChildren(), selectOne(hasTheLabel(N_SYSTEM.label())),
							children(), selectOne(hasTheLabel(N_DYNAMICS.label())));
				}
				DeploymentType deptype = DeploymentType.defaultValue(); // local deployer
				if (properties().hasProperty(P_EXP_DEPLOYMENT.key()))
					deptype = (DeploymentType) properties().getPropertyValue(P_EXP_DEPLOYMENT.key());
				switch (deptype) {
				case multipleRemote:
					log.warning(()->"Multiple remote deployment not yet implemented - moving to multiple local deployment");
				case multipleLocal:
					deployer = new ParallelDeployer();
					// actually we do not have to setup these right now - we may defer this to the
					// design node, who is able to compute how many simulators will be needed
					// remember that number of threads is independent of number of simulators.
					for (int i = 0; i < nLocalSimulators(); i++)
						deployer.attachSimulator(sim.getInstance(N_SIMULATORS++));
					break;
				case singleLocal:
					deployer = new SimpleDeployer();
					deployer.attachSimulator(sim.getInstance(N_SIMULATORS++));
					break;
				}
//				// single run experiment
//				if (d.properties().getPropertyValue(P_DESIGN_TYPE.key()).equals(singleRun)) {
//					int nSims = 1;
//						int nReps = (Integer) d.properties().getPropertyValue(P_TREATMENT_REPLICATES.key());
//						nSims += nReps;
//					if (nSims == 1) {
//						deployer = new SimpleDeployer();
//						deployer.attachSimulator(sim.getInstance(N_SIMULATORS++));
//					} else {
//						deployer = new ParallelDeployer();
//						for (int i = 0; i < nSims; i++)
//							deployer.attachSimulator(sim.getInstance(N_SIMULATORS++));
//					}
//
//				}
//				// file, factorial etc  etc design
//				// multiple simulators, remote
//				// TODO
				controller = new StateMachineController(deployer);
				// this puts the deployer in "waiting" state
				// controller.sendEvent(initialise.event());
			}
			sealed = true;
		}
	}

	// TODO for Ian: implement something clever here
	private int nLocalSimulators() {
		return Runtime.getRuntime().availableProcessors();
//		return 2;
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
