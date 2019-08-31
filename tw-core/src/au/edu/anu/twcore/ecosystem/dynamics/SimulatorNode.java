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
import java.util.List;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.StoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.ecosystem.runtime.simulator.Simulator;

/**
 * Class matching the "ecosystem/dynamics" node label in the 3Worlds configuration tree.
 * Has no properties. This <em>is</em> the simulator.
 * 
 * TODO: this class is a first version, one must add the simulation capability later, probably
 * through another interface
 * 
 * @author Jacques Gignoux - 27 mai 2019
 *
 */
public class SimulatorNode extends InitialisableNode implements Factory<Simulator> {

	public SimulatorNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}
	
	public SimulatorNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_DYNAMICS.initRank();
	}

	/**
	 * Returns the current simulation time but does NOT compute it (read-only method)
	 * @return
	 */
	public long currentTime() {
		// TODO: implement this
		return 0;
	}

	@Override
	public Simulator newInstance() {
		// TODO improve this
		
		// IDD temp code for today
		// more than one sc not supported yet but arch says [0..*] (I've changed that to [1..*].
		List<StoppingConditionNode> scnodes = (List<StoppingConditionNode>) get(getChildren(),selectOneOrMany(hasTheLabel(N_STOPPINGCONDITION.label())));
		
		TimeLine  timeLine = (TimeLine) get(getChildren(),selectOne(hasTheLabel(N_TIMELINE.label())));
		List<TimeModel> timeModels = (List<TimeModel>)get(timeLine.getChildren(),selectOneOrMany(hasTheLabel(N_TIMEMODEL.label())));
		List<Timer> timers= new ArrayList<>();
		for (TimeModel tm:timeModels)
			timers.add(tm.getInstance());
		return new Simulator(scnodes.get(0).getInstance(),timeLine,timers);
	}
	
	
}
