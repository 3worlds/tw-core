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
import fr.cnrs.iees.twcore.constants.DateTimeType;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;
import fr.ens.biologie.generic.utils.Interval;

import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_STOPSYSTEM;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.ArenaType;
import au.edu.anu.twcore.ecosystem.dynamics.initial.Component;
import au.edu.anu.twcore.ecosystem.runtime.StoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.stop.InRangeStoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.stop.MultipleAndStoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.stop.MultipleOrStoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.stop.OutRangeStoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.stop.SimpleStoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.stop.ValueStoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.system.ArenaComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.ArenaFactory;
import au.edu.anu.twcore.ecosystem.structure.ElementType;

/**
 * Class matching the "ecosystem/dynamics/stoppingCondition" node label in the 3Worlds configuration tree.
 * Has no properties. 
 * 
 * @author Jacques Gignoux - 7 juin 2019
 *
 */
public class StoppingConditionNode 
		extends InitialisableNode 
		implements LimitedEdition<StoppingCondition>, Sealable {
	
	private boolean sealed = false;
	private Map<Integer,StoppingCondition> stopcd = new HashMap<>();
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
	private List<StoppingCondition> getComponentConditions(int id) {
		List<StoppingCondition> lsc = new LinkedList<>();
		for (TreeNode tn:getChildren())
			if (tn instanceof StoppingConditionNode)
				lsc.add(((StoppingConditionNode)tn).getInstance(id));
		return lsc;
	}
	
	// gets the system which properties will be searched for the stopping criterion
	// (for all descendants of PropertyStoppingCondition)
	@SuppressWarnings("unchecked")
	private ReadOnlyPropertyList getStoppingSystem(int id) {
		TreeNode stopSystemNode = (TreeNode) get(edges(Direction.OUT),
			selectOne(hasTheLabel(E_STOPSYSTEM.label())),
			endNode()); 		
		ReadOnlyPropertyList system = null;
		if (stopSystemNode instanceof ArenaType)
			system = ((ElementType<ArenaFactory, ArenaComponent>) stopSystemNode)
				.getInstance(id).getInstance().readOnlyProperties();
		else if (stopSystemNode instanceof Component) {
			// TODO !
		}
		return system;
	}
	
	@Override
	public void initialise() {
		super.initialise();
		sealed = true;
	}
	
	private StoppingCondition makeStoppingCondition(int id)  {
		String subClass = (String)properties().getPropertyValue(P_STOPCD_SUBCLASS.key());
		StoppingCondition result = null;
		if (SimpleStoppingCondition.class.getName().equals(subClass)) {
			// compute end time from parameter + timeLine timeOrigin
			DateTimeType dtt = (DateTimeType) properties().getPropertyValue(P_STOPCD_ENDTIME.key());
			TreeNode system = this;
			while (!(system instanceof SimulatorNode))
				system = system.getParent();
			Timeline tl = (Timeline) get(system, children(), 
				selectOne(hasTheLabel(N_TIMELINE.label())));
			long to = 0L;
			if (tl.properties().hasProperty(P_TIMELINE_TIMEORIGIN.key()))
				to = ((DateTimeType) tl.properties()
					.getPropertyValue(P_TIMELINE_TIMEORIGIN.key())).getDateTime();
			result = new SimpleStoppingCondition(dtt.getDateTime()+to);
		}
		else if (ValueStoppingCondition.class.getName().equals(subClass))
			result = new ValueStoppingCondition(
				(String) properties().getPropertyValue(P_STOPCD_STOPVAR.key()),
				getStoppingSystem(id),
				(double) properties().getPropertyValue(P_STOPCD_STOPVAL.key()));
		else if (InRangeStoppingCondition.class.getName().equals(subClass))
			result = new InRangeStoppingCondition(
				(String) properties().getPropertyValue(P_STOPCD_STOPVAR.key()),
				getStoppingSystem(id),
				(Interval) properties().getPropertyValue(P_STOPCD_RANGE.key()));
		else if (OutRangeStoppingCondition.class.getName().equals(subClass))
			result = new OutRangeStoppingCondition(
				(String) properties().getPropertyValue(P_STOPCD_STOPVAR.key()),
				getStoppingSystem(id),
				(Interval) properties().getPropertyValue(P_STOPCD_RANGE.key()));
		else if (MultipleOrStoppingCondition.class.getName().equals(subClass))
			result = new MultipleOrStoppingCondition(getComponentConditions(id));
		else if (MultipleAndStoppingCondition.class.getName().equals(subClass))
			result = new MultipleAndStoppingCondition(getComponentConditions(id));
		return result;
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
	public Sealable seal() {
		sealed = true;
		return this;
	}

	@Override
	public boolean isSealed() {
		return sealed;
	}

	@Override
	public StoppingCondition getInstance(int id) {
		if (!sealed)
			initialise();
		if (!stopcd.containsKey(id))
			stopcd.put(id, makeStoppingCondition(id));
		return stopcd.get(id);
	}

}
