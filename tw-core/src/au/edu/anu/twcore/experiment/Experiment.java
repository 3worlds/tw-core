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
import fr.cnrs.iees.twcore.constants.FileType;
import fr.ens.biologie.generic.Sealable;
import fr.ens.biologie.generic.Singleton;
import fr.ens.biologie.generic.utils.Logging;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;

import au.edu.anu.omugi.collections.tables.StringTable;
import au.edu.anu.omugi.graph.property.Property;
import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.dynamics.SimulatorNode;
import au.edu.anu.twcore.ecosystem.runtime.simulator.Simulator;
import au.edu.anu.twcore.experiment.runtime.Deployable;
import au.edu.anu.twcore.experiment.runtime.EddReadable;
import au.edu.anu.twcore.experiment.runtime.ExperimentDesignDetails;
import au.edu.anu.twcore.experiment.runtime.IEdd;
import au.edu.anu.twcore.experiment.runtime.deployment.ParallelDeployer;
import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.get;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

/**
 * Class matching the "experiment" node label in the 3Worlds configuration tree.
 * Has properties, nReps, output dir and precis. Returns a controller to
 * communicate with simulators
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
//	private final List<List<Property>> treatmentList;
//	private final Map<String,ExpFactor> factors;
//	private final Map<String, Object> baseline;
	private IEdd edd;

	// default constructor
	public Experiment(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
//		baseline = new HashMap<>();
//		treatmentList = new ArrayList<>();
//		factors = new LinkedHashMap<>();
	}

	// constructor with no properties
	public Experiment(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
//		baseline = new HashMap<>();
//		treatmentList = new ArrayList<>();
//		factors = new LinkedHashMap<>();
	}

	@Override
	public void initialise() {
		if (!sealed) {
			super.initialise();
			List<SimulatorNode> simulatorNodes=new ArrayList<>();
			List<TreeGraphDataNode> systems = (List<TreeGraphDataNode>) get(this.getParent().getChildren(),
					selectOneOrMany(hasTheLabel(N_SYSTEM.label())));
			for (TreeGraphDataNode system:systems) {
				SimulatorNode sn = (SimulatorNode)get(system.getChildren(),selectOne(hasTheLabel(N_DYNAMICS.label())));
				simulatorNodes.add(sn);
			}

			SimulatorNode simulatorNode = null;
			Design dsgn = (Design) get(getChildren(), selectOne(hasTheLabel(N_DESIGN.label())));
			if (dsgn.properties().hasProperty(P_DESIGN_TYPE.key())) {
				if (get(edges(Direction.OUT), selectZeroOrOne(hasTheLabel(E_BASELINE.label()))) != null) {
					// multiple systems
					simulatorNode = (SimulatorNode) get(edges(Direction.OUT),
							selectOne(hasTheLabel(E_BASELINE.label())), endNode(), children(),
							selectOne(hasTheLabel(N_DYNAMICS.label())));
				} else {
					// single system -NB baseline is now [0..1]
					simulatorNode = simulatorNodes.get(0);
				}

				edd = ExperimentDesignDetails.makeDetails(getDefaultPrecis(), getDefaultnReplicates(), getDesignType(),
						getDesignFile(), getDefaultExpDir());

				if (edd.getType() != null)
					switch (edd.getType()) {
					case singleRun: {

						deployer = new ParallelDeployer();

						for (int i = 0; i < edd.getReplicateCount(); i++) {
							for (SimulatorNode sn : simulatorNodes) {
								Simulator sim = sn.getInstance(N_SIMULATORS++);
								sim.applyTreatmentValues(edd.baseline(), null);
								deployer.attachSimulator(sim);
							}
						}
						break;
					}
					case crossFactorial: {
						// CAUTION: Limited protecting queries!
						// 1) Only Fields that are constants associated with the arena
						//
						buildTreatmentList(edd.getType());

						deployer = new ParallelDeployer();

						for (int r = 0; r < edd.getReplicateCount(); r++)
							for (int t = 0; t < edd.treatments().size(); t++) {
								Simulator sim = simulatorNode.getInstance(N_SIMULATORS++);
								sim.applyTreatmentValues(edd.baseline(), edd.treatments().get(t));
								deployer.attachSimulator(sim);
							}
						break;
					}
					case sensitivityAnalysis: {
						buildTreatmentList(edd.getType());

						deployer = new ParallelDeployer();

						for (int r = 0; r < edd.getReplicateCount(); r++)
							for (int t = 0; t < edd.treatments().size(); t++) {
								Simulator sim = simulatorNode.getInstance(N_SIMULATORS++);
								sim.applyTreatmentValues(edd.baseline(), edd.treatments().get(t));
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

	public EddReadable getExperimentDesignDetails() {
		return edd;
	}

	private int getDefaultnReplicates() {
		int result = 1;
		if (properties().hasProperty(P_EXP_NREPLICATES.key()))
			result = (Integer) properties().getPropertyValue(P_EXP_NREPLICATES.key());
		return result;
	}

	private String getDefaultPrecis() {
		String precis = null;
		if (properties().hasProperty(P_EXP_PRECIS.key()))
			precis = (String) properties().getPropertyValue(P_EXP_PRECIS.key());
		return precis;
	}

	private String getDefaultExpDir() {
		String expDir = "exp0";
		if (properties().hasProperty(P_EXP_DIR.key()))
			expDir = (String) properties().getPropertyValue(P_EXP_DIR.key());
		return expDir;
	}

	private ExperimentDesignType getDesignType() {
		Design dsgn = (Design) get(getChildren(), selectOne(hasTheLabel(N_DESIGN.label())));
		ExperimentDesignType result = null;
		if (dsgn.properties().hasProperty(P_DESIGN_TYPE.key()))
			result = (ExperimentDesignType) dsgn.properties().getPropertyValue(P_DESIGN_TYPE.key());
		return result;

	}

	private File getDesignFile() {
		Design dsgn = (Design) get(getChildren(), selectOne(hasTheLabel(N_DESIGN.label())));
		FileType result = null;
		if (dsgn.properties().hasProperty(P_DESIGN_FILE.key())) {
			result = (FileType) dsgn.properties().getPropertyValue(P_DESIGN_FILE.key());
		}
		if (result != null)
			return result.getFile();
		return null;

	}
	//

	@SuppressWarnings("unchecked")
	private void buildTreatmentList(ExperimentDesignType edt) {
		// should only be called for sa or factorial
//		treatmentList.clear();
//		factors.clear();
		if (get(this.getChildren(), selectZeroOrOne(hasTheLabel(N_TREATMENT.label()))) == null)
			return;
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
		List<ExpFactor> orderedFactors = new ArrayList<>();
		for (int i = 0; i < treats.size(); i++) {
			settings.add(new ArrayList<Property>());
			orderedFactors.add(null);
		}

		for (ALDataEdge e : treats) {
			StringTable values = (StringTable) e.properties().getPropertyValue(P_TREAT_VALUES.key());
			TreeGraphDataNode endNode = (TreeGraphDataNode) e.endNode();
			DataElementType type = (DataElementType) endNode.properties().getPropertyValue(P_FIELD_TYPE.key());
			int order = rankings.indexOf(e.properties().getPropertyValue(P_TREAT_RANK.key()));
			List<Property> props = getAsProperties(endNode.id(), type, values);
			StringTable valueNames = (StringTable) (e.properties().getPropertyValue(P_TREAT_VALUENAMES.key()));
			settings.set(order, props);
			orderedFactors.set(order, new ExpFactor(e.id(), props, valueNames));
		}
		for (int i = 0; i < orderedFactors.size(); i++) {
			String key = settings.get(i).get(0).getKey();
			edd.factors().put(key, orderedFactors.get(i));
		}

		// assume order is normalized and packed 0..n(??)
		switch (edt) {
		case crossFactorial: {
			int[] indices = new int[settings.size()];
			int[] maxIndex = new int[settings.size()];
			for (int i = 0; i < settings.size(); i++)
				maxIndex[i] = settings.get(i).size() - 1;

			buildTreatments(settings, indices, maxIndex, edd.treatments());
			break;
		}
		case sensitivityAnalysis: {
//			treatmentList.clear();
			for (List<Property> lst : settings) {
				for (Property p : lst) {
					List<Property> l = new ArrayList<>();
					l.add(p);
					edd.treatments().add(l);
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
