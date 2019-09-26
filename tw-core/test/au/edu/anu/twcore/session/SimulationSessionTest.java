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

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import au.edu.anu.rscs.aot.init.InitialiseMessage;
import au.edu.anu.rscs.aot.init.Initialiser;
import au.edu.anu.twcore.archetype.TWA;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.graph.io.GraphImporter;
import fr.ens.biologie.generic.Initialisable;

/**
 * 
 * @author Jacques Gignoux - 17 juin 2019
 *
 */
class SimulationSessionTest {

	@SuppressWarnings("unchecked")
	@Test
	final void testSimulationSession() {
		assertTrue(TWA.validArchetype());
		TreeGraph<TreeGraphNode,ALEdge> specs = (TreeGraph<TreeGraphNode,ALEdge>) 
			GraphImporter.importGraph("testSpecs.utg",this.getClass());
		TWA.checkSpecifications(specs);
		List<Initialisable> list = new ArrayList<>();
		for (TreeGraphNode tgn:specs.nodes())
			list.add((Initialisable) tgn);
		Initialiser initer = new Initialiser(list);
		initer.initialise();
		if (initer.errorList() != null) {
			for (InitialiseMessage msg : initer.errorList())
				System.out.println(msg.getTarget() + msg.getException().getMessage());
		}
	}

}
