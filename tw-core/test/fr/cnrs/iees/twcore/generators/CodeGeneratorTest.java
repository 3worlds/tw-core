package fr.cnrs.iees.twcore.generators;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

import au.edu.anu.twcore.graphState.GraphState;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.project.ProjectPaths;
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

//	@SuppressWarnings("unchecked")
//	@Test
//	final void testGenerate1() {
//		GraphState.initialise(null);
//		Project.create("test1");
//		String codePath =Project.makeFile(ProjectPaths.CODE).getAbsolutePath();
//		CodeGenerator gen = new CodeGenerator();
//		TreeGraph<TreeGraphDataNode,ALEdge> specs = (TreeGraph<TreeGraphDataNode,ALEdge>) 
//			GraphImporter.importGraph("generateData.utg",this.getClass());
//		gen.generate(codePath, specs);
//		Project.close();
//	}
//
//	@SuppressWarnings("unchecked")
//	@Test
//	final void testGenerate2() {
//		GraphState.initialise(null);
//		Project.create("test2");
//		String codePath =Project.makeFile(ProjectPaths.CODE).getAbsolutePath();
//		CodeGenerator gen = new CodeGenerator();
//		TreeGraph<TreeGraphDataNode,ALEdge> specs = (TreeGraph<TreeGraphDataNode,ALEdge>) 
//			GraphImporter.importGraph("generateData2.utg",this.getClass());
//		gen.generate(codePath, specs);
//		Project.close();
//	}

	@SuppressWarnings("unchecked")
	@Test
	final void testGenerate3() {
		GraphState.initialise(null);
		Project.create("test3");
		String codePath =Project.makeFile(ProjectPaths.CODE).getAbsolutePath();
		CodeGenerator gen = new CodeGenerator();
		TreeGraph<TreeGraphDataNode,ALEdge> specs = (TreeGraph<TreeGraphDataNode,ALEdge>) 
			GraphImporter.importGraph("generateFunction.utg",this.getClass());
		gen.generate(codePath, specs);
		Project.close();
	}

}
