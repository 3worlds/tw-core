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
package au.edu.anu.twcore.session;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.ens.biologie.generic.Initialisable;

/**
 * This class represents a simulation experiment session in ModelRunner.
 * It should be coupled to a state machine (how?) with states initial, running, final (final being 
 * a success or a failure)
 * 
 * TODO: (1) not sure we need this. (2) maybe it should be in tw-apps, not tw-core
 * 
 * @author Jacques Gignoux - 17 juin 2019
 *
 */
public class SimulationSession {

	private TreeGraph<TreeGraphNode,ALEdge> configuration = null;
	
	/**
	 * In this constructor, we assume the graph has been checked and is valid.
	 * 
	 * @param config the (valid) configuration graph
	 */
	public SimulationSession(TreeGraph<TreeGraphNode,ALEdge> config) {
		super();
		configuration = config;
		initialise();
		// switch to 'initial' state
	}
	
	private void initialise() {
		Map<Integer,List<Initialisable>> inits = new TreeMap<>();
		for (TreeGraphNode tgn:configuration.nodes()) {
			if (tgn instanceof Initialisable) {
				Initialisable i = (Initialisable) tgn;
				int index = i.initRank();
				if (inits.get(index)==null)
					inits.put(index, new LinkedList<Initialisable>());
				inits.get(index).add(i);
			}
		}
		// this looping is done in initRank order because it's a TreeMap
		for (List<Initialisable> l:inits.values()) 
			for (Initialisable i:l)
				i.initialise();
	}
	
	// TODO: implement the state machine stuff

}
