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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.session.SimulationSession;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.graph.io.GraphImporter;

/**
 * 
 * @author Jacques Gignoux - 9 août 2019
 *
 */
class StoppingConditionNodeTest {
	
	private TreeGraph<TreeGraphDataNode,ALEdge> specs = null;
	
	@BeforeEach
	@SuppressWarnings("unchecked")
	private void init() {
		specs = (TreeGraph<TreeGraphDataNode,ALEdge>) 
				GraphImporter.importGraph("stoppingCondition.utg",this.getClass());
//		System.out.println(specs.toDetailedString());
	}

	@Test
	final void testInitialise() {
		// this should call initialise in proper order
		new SimulationSession(specs);
		for (TreeGraphNode nn:specs.nodes()) {
			InitialisableNode n = (InitialisableNode) nn;
			if (n instanceof StoppingConditionNode) {
				assertNotNull(((StoppingConditionNode)n).getInstance());
				System.out.println(((StoppingConditionNode)n).getInstance().toString());
			}
		}
	}

	@Test
	final void testInitRank() {
		for (TreeGraphNode nn:specs.nodes()) {
			InitialisableNode n = (InitialisableNode) nn;
			System.out.println(n.classId()+":"+n.id()+":"+n.initRank());
			if (n.id().equals("A"))
				assertEquals(n.initRank(),10);
			if (n.id().equals("C"))
				assertEquals(n.initRank(),12);
			if (n.id().equals("I"))
				assertEquals(n.initRank(),10);
			if (n.id().equals("F"))
				assertEquals(n.initRank(),11);
			if (n.id().equals("E"))
				assertEquals(n.initRank(),10);
			if (n.id().equals("G"))
				assertEquals(n.initRank(),10);
		}
	}

}
