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
package fr.cnrs.iees.twcore.generators;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import au.edu.anu.twcore.graphState.GraphState;
import au.edu.anu.twcore.project.Project;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.graph.io.GraphImporter;

/**
 * Testing code generation for 3Worlds
 * 
 * @author Jacques Gignoux - 5 juil. 2019
 *
 */
class CodeGeneratorTest {

	@SuppressWarnings("unchecked")
	@Test
	final void testGenerate1() {
//		GraphState.initialise(null);
//		Project.create("test1");
//		CodeGenerator gen = new CodeGenerator();
//		//This won't crash because of changes in THIS utg file cf below.
//		TreeGraph<TreeGraphDataNode,ALEdge> specs = (TreeGraph<TreeGraphDataNode,ALEdge>) 
//				GraphImporter.importGraph("generateData_Ian.utg",this.getClass());
//		gen.generate(specs);
//		Project.close();
//		assertFalse(Project.isOpen()); // if there are problems, we wont reach this point
	}

	@SuppressWarnings("unchecked")
	@Test
	final void testGenerate2() {
//		GraphState.initialise(null);
//		Project.create("test2");
//		CodeGenerator gen = new CodeGenerator();
//		// this will crash on typecasting of "type" as ElementDataType because i've uncommented some code
//		TreeGraph<TreeGraphDataNode,ALEdge> specs = (TreeGraph<TreeGraphDataNode,ALEdge>) 
//			GraphImporter.importGraph("generateData2.utg",this.getClass());
//		gen.generate(specs);
//		Project.close();
//		assertFalse(Project.isOpen()); // if there are problems, we wont reach this point
	}

	@SuppressWarnings("unchecked")
	@Test
	final void testGenerate3() {
		GraphState.initialise(null);
		Project.create("test3");
		CodeGenerator gen = new CodeGenerator();
		TreeGraph<TreeGraphDataNode,ALEdge> specs = (TreeGraph<TreeGraphDataNode,ALEdge>) 
			GraphImporter.importGraph("generateFunction.utg",this.getClass());
		gen.generate(specs);
		for (TreeGraphDataNode n: specs.nodes())
			System.out.println(n);
		Project.close();
		assertFalse(Project.isOpen()); // if there are problems, we wont reach this point
	}

}
