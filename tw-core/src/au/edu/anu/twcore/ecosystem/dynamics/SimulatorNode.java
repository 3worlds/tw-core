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
package au.edu.anu.twcore.ecosystem.dynamics;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.Factory;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;

import au.edu.anu.rscs.aot.graph.property.Property;
import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.StoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.ecosystem.runtime.simulator.Simulator;
import au.edu.anu.twcore.ecosystem.runtime.stop.MultipleOrStoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.stop.SimpleStoppingCondition;
import au.edu.anu.twcore.ui.runtime.DataReceiver;

/**
 * Class matching the "ecosystem/dynamics" node label in the 3Worlds configuration tree.
 * Has no properties. This <em>is</em> the simulator.
 * 
 * @author Jacques Gignoux - 27 mai 2019
 *
 */
public class SimulatorNode extends InitialisableNode implements Factory<Simulator> {
	
	private StoppingCondition rootStop = null;
	private List<Timer> timers = new ArrayList<>();
	private TimeLine timeLine = null;
	private List<Simulator> instances = new LinkedList<>();

	public SimulatorNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}
	
	public SimulatorNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		super.initialise();
		timeLine = (TimeLine) get(getChildren(),
			selectOne(hasTheLabel(N_TIMELINE.label())));
		List<TimeModel> timeModels = (List<TimeModel>)get(timeLine.getChildren(),
			selectOneOrMany(hasTheLabel(N_TIMEMODEL.label())));
		for (TimeModel tm:timeModels)
			timers.add(tm.getInstance());
		List<StoppingConditionNode> scnodes = (List<StoppingConditionNode>) get(getChildren(),
			selectZeroOrMany(hasTheLabel(N_STOPPINGCONDITION.label())));
		// when there is no stopping condition, the default one is used (runs to infinite time)
		if (scnodes.isEmpty())
			rootStop = SimpleStoppingCondition.defaultStoppingCondition();
		// when there are many stopping conditions, any of them can stop the simulation
		else if (scnodes.size()>1) {
			List<StoppingCondition> lsc = new ArrayList<>();
			for (StoppingConditionNode scn:scnodes)
				lsc.add(scn.getInstance());
			rootStop = new MultipleOrStoppingCondition(lsc);
		} 
		// when there is only one stopping condition, then it is used
		else
			rootStop = scnodes.get(0).getInstance();
	}

	@Override
	public int initRank() {
		return N_DYNAMICS.initRank();
	}

	@Override
	public Simulator newInstance() {
		Simulator sim = new Simulator(rootStop,timeLine,timers);
		rootStop.attachSimulator(sim);
		instances.add(sim);
		return sim;
	}
	
	public void addObserver(DataReceiver<Property,SimplePropertyList> observer) {
		for (Simulator sim:instances)
			sim.addObserver(observer);
		instances.clear();
	}
	
}
