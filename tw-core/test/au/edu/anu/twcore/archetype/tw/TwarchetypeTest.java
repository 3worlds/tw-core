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
package au.edu.anu.twcore.archetype.tw;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import au.edu.anu.rscs.aot.archetype.CheckMessage;
import au.edu.anu.twcore.archetype.TWA;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.io.GraphImporter;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;
import fr.cnrs.iees.twcore.constants.EnumProperties;

/**
 * 
 * @author Jacques Gignoux - 29 mai 2019
 *
 */
class TwarchetypeTest {

	@Test
	void test() {
		EnumProperties.recordEnums();
		ValidPropertyTypes.listTypes();
		TreeGraph<?,?> specs = (TreeGraph<?, ?>) GraphImporter.importGraph("testSpecs2.ugt",this.getClass());
		for (Node n: specs.nodes()) {
			System.out.println(n.id()+", "+n.getClass().getName());
			
		}
		Iterable<CheckMessage> errors = TWA.checkSpecifications(specs);
		if (errors!=null) {
			System.out.println("There were errors in specifications: ");
			for (CheckMessage m:errors)
				System.out.println(m.toString()+"\n");
		}
		else 
			System.out.println("Specifications checked with no error.");
		System.out.println(specs.toDetailedString());
		assertNull(errors);
	}

}
