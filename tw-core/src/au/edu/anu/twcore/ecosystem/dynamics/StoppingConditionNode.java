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

import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.properties.impl.ReadOnlyPropertyListImpl;
import fr.ens.biologie.generic.Singleton;
import fr.ens.biologie.generic.utils.Interval;

import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_STOPSYSTEM;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;

import java.util.LinkedList;
import java.util.List;

import au.edu.anu.rscs.aot.graph.property.Property;
import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.StoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.stop.InRangeStoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.stop.MultipleAndStoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.stop.MultipleOrStoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.stop.OutRangeStoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.stop.SimpleStoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.stop.ValueStoppingCondition;

/**
 * Class matching the "ecosystem/dynamics/stoppingCondition" node label in the 3Worlds configuration tree.
 * Has no properties. This <em>is</em> the simulator.
 * 
 * @author Jacques Gignoux - 7 juin 2019
 *
 */
public class StoppingConditionNode 
		extends InitialisableNode 
		implements Singleton<StoppingCondition> {
	
	private StoppingCondition stopcd = null;
	private static final int baseInitRank = N_STOPPINGCONDITION.initRank();

	public StoppingConditionNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public StoppingConditionNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	// gets the list of conditions this one depends on (if it's a multiple condition)
	// assuming that the dependencies have been initialised before (this is made
	// possible by the specific initRank() method of this class)
	private List<StoppingCondition> getComponentConditions() {
		List<StoppingCondition> lsc = new LinkedList<>();
		for (TreeNode tn:getChildren())
			if (tn instanceof StoppingConditionNode)
				lsc.add(((StoppingConditionNode)tn).getInstance());
		return lsc;
	}
	
	// gets the system which properties will be searched for the stopping criterion
	// (for all descendants of PropertyStoppingCondition)
	private ReadOnlyPropertyList getStoppingSystem() {
		// TODO: implement this properly
		get(edges(Direction.OUT),
			selectOne(hasTheLabel(E_STOPSYSTEM.label())),
			endNode()); // what do we do with this ? this is a SystemFactory.
		// dummy - THIS IS ONLY FOR TESTING !
		ReadOnlyPropertyList system = new ReadOnlyPropertyListImpl(new Property("x",2),new Property("y",12),new Property("z","AA")); 
		return system;
	}
	
	private SimulatorNode getSimulator(StoppingConditionNode sc) {
		if (sc.getParent() instanceof SimulatorNode)
			return (SimulatorNode) sc.getParent();
		else
			return getSimulator((StoppingConditionNode) sc.getParent());
	}
	
	@Override
	public void initialise() {
		super.initialise();
		SimulatorNode sim = getSimulator(this);
		String subClass = (String)properties().getPropertyValue(P_STOPCD_SUBCLASS.key());
		if (SimpleStoppingCondition.class.getName().equals(subClass))
			stopcd = new SimpleStoppingCondition(sim,
				(long) properties().getPropertyValue(P_STOPCD_ENDTIME.key()));
		else if (ValueStoppingCondition.class.getName().equals(subClass))
			stopcd = new ValueStoppingCondition(sim,
				(String) properties().getPropertyValue(P_STOPCD_STOPVAR.key()),
				getStoppingSystem(),
				(double) properties().getPropertyValue(P_STOPCD_STOPVAL.key()));
		else if (InRangeStoppingCondition.class.getName().equals(subClass))
			stopcd = new InRangeStoppingCondition(sim,
				(String) properties().getPropertyValue(P_STOPCD_STOPVAR.key()),
				getStoppingSystem(),
				(Interval) properties().getPropertyValue(P_STOPCD_RANGE.key()));
		else if (OutRangeStoppingCondition.class.getName().equals(subClass))
			stopcd = new OutRangeStoppingCondition(sim,
				(String) properties().getPropertyValue(P_STOPCD_STOPVAR.key()),
				getStoppingSystem(),
				(Interval) properties().getPropertyValue(P_STOPCD_RANGE.key()));
		else if (MultipleOrStoppingCondition.class.getName().equals(subClass))
			stopcd = new MultipleOrStoppingCondition(sim,getComponentConditions());
		else if (MultipleAndStoppingCondition.class.getName().equals(subClass))
			stopcd = new MultipleAndStoppingCondition(sim,getComponentConditions());
	}

	@Override
	public int initRank() {
		int depRank = 0;
		boolean foundOne = false;
		for (TreeNode tcn: getChildren()) {
			foundOne = true;
			StoppingConditionNode scn = (StoppingConditionNode) tcn;
			depRank = Math.max(depRank, scn.initRank()-baseInitRank);
		}
		if (foundOne)
			depRank +=1;
		return baseInitRank + depRank;
	}

	@Override
	public StoppingCondition getInstance() {
		return stopcd;
	}

}
