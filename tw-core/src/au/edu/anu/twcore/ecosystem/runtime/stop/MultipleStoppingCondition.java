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
package au.edu.anu.twcore.ecosystem.runtime.stop;

import java.util.List;

import au.edu.anu.twcore.ecosystem.runtime.StoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.simulator.Simulator;

/**
 * 
 * @author Jacques Gignoux - 9 août 2019
 *
 */
public abstract class MultipleStoppingCondition extends AbstractStoppingCondition {

	protected StoppingCondition[] conditions = null;

	public MultipleStoppingCondition(List<StoppingCondition> conds) {
		super();
		conditions = new StoppingCondition[conds.size()];
		int i=0;
		for (StoppingCondition sc:conds) {
			conditions[i] = sc;
			i++;
		}
	}
	
	// recursion to attach the simulator to all component conditions
	@Override
	public void attachSimulator(Simulator sim) {
		for (StoppingCondition stop:conditions)
			stop.attachSimulator(sim);
		super.attachSimulator(sim);
	}


}
