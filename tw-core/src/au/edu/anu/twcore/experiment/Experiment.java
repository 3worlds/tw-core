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
import fr.cnrs.iees.graph.impl.ALDataEdge;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.rvgrid.statemachine.StateMachineController;
import fr.cnrs.iees.twcore.constants.DataElementType;
import fr.cnrs.iees.twcore.constants.ExperimentDesignType;
import fr.ens.biologie.generic.Sealable;
import fr.ens.biologie.generic.Singleton;
import fr.ens.biologie.generic.utils.Logging;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.graph.property.Property;
import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.dynamics.SimulatorNode;
import au.edu.anu.twcore.ecosystem.runtime.simulator.Simulator;
import au.edu.anu.twcore.experiment.runtime.Deployable;
import au.edu.anu.twcore.experiment.runtime.deployment.ParallelDeployer;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	private final List<List<Property>> treatmentList;
	private final Map<String, Object> baseline;

	// default constructor
	public Experiment(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
		baseline = new HashMap<>();
		treatmentList = new ArrayList<>();
	}

	// constructor with no properties
	public Experiment(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
		baseline = new HashMap<>();
		treatmentList = new ArrayList<>();
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

				int nReps = getNReps();

				ExperimentDesignType expDesignType = getDesignType();
				if (expDesignType != null)
					switch (expDesignType) {
					case singleRun: {
						deployer = new ParallelDeployer();

						for (int i = 0; i < nReps; i++) {
							Simulator sim = baselineSimulator.getInstance(N_SIMULATORS++);
							sim.applyTreatmentValues(baseline,null);
							deployer.attachSimulator(sim);
						}
						break;
					}
					case crossFactorial: {
						// CAUTION: Limited protecting queries!
						// 1) Only Fields that are constants associated with the arena
						//
						buildTreatmentList(expDesignType);

						deployer = new ParallelDeployer();

						for (int r = 0; r < nReps; r++)
							for (int t = 0; t < treatmentList.size(); t++) {
								Simulator sim = baselineSimulator.getInstance(N_SIMULATORS++);
								sim.applyTreatmentValues(baseline,getTreatmentList().get(t));
								deployer.attachSimulator(sim);
							}
						break;
					}
					case sensitivityAnalysis: {
						buildTreatmentList(expDesignType);

						deployer = new ParallelDeployer();

						for (int r = 0; r < nReps; r++)
							for (int t = 0; t < treatmentList.size(); t++) {
								Simulator sim = baselineSimulator.getInstance(N_SIMULATORS++);
								sim.applyTreatmentValues(baseline,getTreatmentList().get(t));
								deployer.attachSimulator(sim);
							}
						break;

					}
					default: {
						log.warning(() -> "undefined deployment type");
					}
					}
				else {
					log.warning(() -> "file defined deployment not yet implemented");
				}
				controller = new StateMachineController(deployer);

//				log.info(() -> "reset any 'onExperimentStart' rngs.");
//				RngFactory.resetExperiment(); not needed. MR begins each exp when instanced

			}
			sealed = true;
		}

	}

	public int getNReps() {
		int result = 1;
		if (properties().hasProperty(P_EXP_NREPLICATES.key()))
			result = (Integer) properties().getPropertyValue(P_EXP_NREPLICATES.key());
		return result;
	}

	public List<List<Property>> getTreatmentList() {
		if (treatmentList.isEmpty())
			buildTreatmentList(getDesignType());
		return treatmentList;
	}

	public Map<String, Object> getBaseline() {
		return baseline;
	}

	public ExperimentDesignType getDesignType() {
		Design dsgn = (Design) get(getChildren(), selectOne(hasTheLabel(N_DESIGN.label())));
		ExperimentDesignType result = null;
		if (dsgn.properties().hasProperty(P_DESIGN_TYPE.key()))
			result = (ExperimentDesignType) dsgn.properties().getPropertyValue(P_DESIGN_TYPE.key());
		return result;

	}

	@SuppressWarnings("unchecked")
	private void buildTreatmentList(ExperimentDesignType edt) {
		// should only be called for sa or factorial
		treatmentList.clear();
		Treatment treatment = (Treatment) get(this.getChildren(), selectOne(hasTheLabel(N_TREATMENT.label())));
		List<ALDataEdge> treats = (List<ALDataEdge>) get(treatment.edges(Direction.OUT),
				selectOneOrMany(hasTheLabel(E_TREATS.label())));

		// we don't want the user to be bothered by needing the rank property to be
		// contiguous from zero
		// so we prepare sorted list and use by index
		List<Integer> rankings = new ArrayList<>();
		for (ALDataEdge e : treats)
			rankings.add((Integer) e.properties().getPropertyValue(P_TREAT_RANK.key()));

		Collections.sort(rankings);

		List<List<Property>> settings = new ArrayList<>();
		for (int i = 0; i < treats.size(); i++)
			settings.add(new ArrayList<Property>());

		for (ALDataEdge e : treats) {
			StringTable values = (StringTable) e.properties().getPropertyValue(P_TREAT_VALUES.key());
			TreeGraphDataNode endNode = (TreeGraphDataNode) e.endNode();
			DataElementType type = (DataElementType) endNode.properties().getPropertyValue(P_FIELD_TYPE.key());
			int order = rankings.indexOf(e.properties().getPropertyValue(P_TREAT_RANK.key()));
			List<Property> props = getAsProperties(endNode.id(), type, values);
			settings.set(order, props);
		}

		// assume order is normalized and packed 0..n(??)
		switch (edt) {
		case crossFactorial: {
			int[] indices = new int[settings.size()];
			int[] maxIndex = new int[settings.size()];
			for (int i = 0; i < settings.size(); i++)
				maxIndex[i] = settings.get(i).size() - 1;

			buildTreatments(settings, indices, maxIndex, treatmentList);
			break;
		}
		case sensitivityAnalysis: {
			treatmentList.clear();
			for (List<Property> lst : settings) {
				for (Property p : lst) {
					List<Property> l = new ArrayList<>();
					l.add(p);
					treatmentList.add(l);
				}
			}
			break;
		}
		// simpleCompare
		default: {
			// e.g.: Assume 3 properties with these values
			// a: 1,2,3
			// b: A,B
			// c: X
			// The sim property sets are the columns
			// The missing values are, by default, the baseline value

//			for (int i = 0; i < settings.size(); i++) {
//				List<Property> lst = new ArrayList<>();
//				treatmentList.add(lst);
//
//				for (Property p : lst) {
//					List<Property> l = new ArrayList<>();
//					l.add(p);
//					treatmentList.add(l);
//				}
//			}
		}
		}
	}

	private static void buildTreatments(List<List<Property>> s, int[] indices, int[] maxIndex,
			List<List<Property>> result) {
		// output the current set
		List<Property> newList = new ArrayList<>();
		for (int i = 0; i < indices.length; i++) {
			List<Property> lst = s.get(i);
			newList.add(lst.get(indices[i]));
		}
		result.add(newList);

		// increment the last
		indices[indices.length - 1]++;
		// if carry over, update indices recursively if length>1
		if (indices[indices.length - 1] > maxIndex[indices.length - 1])
			if (indices.length > 1)
				doCarry(s, indices, maxIndex, indices.length - 1);
		// stopping condition: if first dimension not finished, recurse
		if (!(indices[0] > maxIndex[0]))
			buildTreatments(s, indices, maxIndex, result);

	}

	private static void doCarry(List<List<Property>> s, int[] indices, int[] maxIndex, int i) {
		indices[i] = 0;
		int j = i - 1;
		indices[j]++;
		// stopping condition: don't carry beyond the first dim and don't carry if not
		// folding over
		if (indices[j] > maxIndex[j] && j > 0)
			doCarry(s, indices, maxIndex, j);

	}

	private static List<Property> getAsProperties(String key, DataElementType type, StringTable values) {
		List<Property> result = new ArrayList<>();
		switch (type) {
		case Double: {
			for (int i = 0; i < values.size(); i++) {
				String v = values.getByInt(i);
				result.add(new Property(key, Double.parseDouble(v)));
			}
			return result;
		}
		case Integer: {
			for (int i = 0; i < values.size(); i++) {
				String v = values.getByInt(i);
				result.add(new Property(key, Integer.parseInt(v)));
			}
			return result;
		}
		case Long: {
			for (int i = 0; i < values.size(); i++) {
				String v = values.getByInt(i);
				result.add(new Property(key, Long.parseLong(v)));
			}
			return result;
		}
		case Float: {
			for (int i = 0; i < values.size(); i++) {
				String v = values.getByInt(i);
				result.add(new Property(key, Float.parseFloat(v)));
			}
			return result;
		}
		case Boolean: {
			for (int i = 0; i < values.size(); i++) {
				String v = values.getByInt(i);
				result.add(new Property(key, Boolean.parseBoolean(v)));
			}
			return result;
		}
		case Short: {
			for (int i = 0; i < values.size(); i++) {
				String v = values.getByInt(i);
				result.add(new Property(key, Short.parseShort(v)));
			}
			return result;
		}
		case Char: {
			for (int i = 0; i < values.size(); i++) {
				String v = values.getByInt(i);
				result.add(new Property(key, v.charAt(0)));
			}
			return result;
		}
		case Byte: {
			for (int i = 0; i < values.size(); i++) {
				String v = values.getByInt(i);
				result.add(new Property(key, Byte.parseByte(v)));
			}
			return result;
		}
		default: {
			for (int i = 0; i < values.size(); i++) {
				String v = values.getByInt(i);
				result.add(new Property(key, v));
			}
			return result;
		}
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
