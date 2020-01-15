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
package au.edu.anu.twcore.ecosystem.dynamics.initial;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import au.edu.anu.twcore.ecosystem.Ecosystem;
import au.edu.anu.twcore.session.SimulationSession;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.graph.io.GraphImporter;

/**
 * 
 * @author Jacques Gignoux - 12 août 2019
 *
 */
class InitialStateTest {

	@SuppressWarnings("unchecked")
	@Test
	final void testInitialise() {
		TreeGraph<TreeGraphDataNode,ALEdge> specs = (TreeGraph<TreeGraphDataNode,ALEdge>) 
			GraphImporter.importGraph("initialState.utg",this.getClass());
		SimulationSession s = new SimulationSession(specs);
		Ecosystem eco = (Ecosystem) specs.findNode("my_ecosystem");
		System.out.println(eco.getInstance(0).toString());	
		System.out.println(eco.getInstance(0).subContainer("A").toString());
		assertNotNull(s);
	}

	@SuppressWarnings("unchecked")
	@Test
	final void testInitialise2() {
		TreeGraph<TreeGraphDataNode,ALEdge> specs = (TreeGraph<TreeGraphDataNode,ALEdge>) 
			GraphImporter.importGraph("initialState2.utg",this.getClass());
		SimulationSession s = new SimulationSession(specs);
		Ecosystem eco = (Ecosystem) specs.findNode("my_ecosystem");
		System.out.println(eco.getInstance(0).toString());	
		assertNotNull(s);
	}

}
