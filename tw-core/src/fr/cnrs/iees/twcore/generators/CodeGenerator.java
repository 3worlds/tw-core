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

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static org.junit.Assert.assertFalse;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.ens.biologie.generic.utils.NameUtils.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;

import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.errorMessaging.ComplianceManager;
import au.edu.anu.twcore.graphState.GraphState;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.project.ProjectPaths;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.twcore.generators.data.TwDataGenerator;
import fr.cnrs.iees.twcore.generators.process.TwFunctionGenerator;
import fr.cnrs.iees.twcore.generators.process.TwInitialiserGenerator;
import fr.cnrs.iees.properties.ResizeablePropertyList;

/**
 * @author Ian Davies
 * @date 27 Dec. 2017
 * 
 *       Refactoring of Jacques code into a separate class
 */
public class CodeGenerator {
	public static final String SRC = "src";
	public static final String BIN = "bin";

	private void overWriteReadOnlyFiles(String codePath, List<File> files) {
		String pp = Project.getProjectDirectory();
		for (File infile : files) {
			File outfile = null;
			if (infile.getAbsolutePath().endsWith(".java"))
				outfile = new File(infile.getAbsolutePath().replace(pp, codePath + File.separator + CodeGenerator.SRC));
			else
				outfile = new File(infile.getAbsolutePath().replace(pp, codePath + File.separator + CodeGenerator.BIN));

			outfile.mkdirs();
			try {
				// System.out.println("COPY FROM: "+infile.getAbsolutePath());
				// System.out.println(" TO: "+outfile.getAbsolutePath());
				Files.copy(infile.toPath(), outfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private static void deleteFileTree(File dir) throws IOException {
		if (dir.exists()) {
			Path root = dir.toPath();
			Files.walk(root)//
					.sorted(Comparator.reverseOrder())//
					.map(Path::toFile)//
					.forEach(File::delete);

			assertFalse("Directory still exists", Files.exists(root));
		}
	}

	@SuppressWarnings("unchecked")
	public boolean generate(String codePath, TreeGraph<TreeGraphDataNode,ALEdge> graph) {
		List<TreeGraphDataNode> ecologies = (List<TreeGraphDataNode>) getChildrenLabelled(graph.root(), N_SYSTEM.label());
		for (TreeGraphDataNode ecology : ecologies) {
			// I think this should be:
//			File ecologyFiles = Project.makeFile(ProjectPaths.CODE,wordUpperCaseName(ecology.id()));
			// create directory for code generation
			File ecologyFiles = new File(
				Project.getProjectDirectory() + File.separator + wordUpperCaseName(ecology.id()));
			try {
				deleteFileTree(ecologyFiles);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TreeGraphDataNode dynamics = (TreeGraphDataNode) get(ecology.getChildren(), 
				selectOne(hasTheLabel(N_DYNAMICS.label())));
			TreeGraphDataNode structure = (TreeGraphDataNode) get(ecology.getChildren(), 
				selectOne(hasTheLabel(N_STRUCTURE.label())));
			// generate data classes for SystemComponents
			List<TreeGraphDataNode> systems = getChildrenLabelled(structure, N_COMPONENT.label());
			for (TreeGraphDataNode system : systems) {
				List<File> files = generateDataCode(codePath, system, ecology.id());
				if (!codePath.equals(""))
					// ensure new or updated data code cannot be overwritten by user.
					// well actually, why not let them edit if they want??
					overWriteReadOnlyFiles(codePath, files);
			}
			// generate data classes for LifeCycles, if any
			List<TreeGraphDataNode> lifeCycles = getChildrenLabelled(dynamics, N_LIFECYCLE.label());
			for (TreeGraphDataNode lc:lifeCycles) {
				generateDataCode(codePath, lc, ecology.id());
			}
			// generate data classes for Ecosystem, if any
			// caution here: Ecosystem may have no category at all
			Collection<Category> cats = (Collection<Category>) get(ecology.edges(Direction.OUT),
				selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())), 
				edgeListEndNodes());
			if (!cats.isEmpty())
				generateDataCode(codePath, ecology, ecology.id());
			// generate TwFunction classes
			// NB expected multiplicities are 1..1 and 1..* but keeping 0..1 and 0..* enables
			// to run tests on incomplete specs 
			List<TreeGraphDataNode> timeModels = (List<TreeGraphDataNode>) get(dynamics.getChildren(),
				selectZeroOrOne(hasTheLabel(N_TIMELINE.label())),
				children(),
				selectZeroOrMany(hasTheLabel( N_TIMEMODEL.label()))); 
			if (timeModels!=null)
				for (TreeGraphDataNode timeModel: timeModels) {
					List<TreeGraphDataNode> processes = getChildrenLabelled(timeModel, N_PROCESS.label());
					for (TreeGraphDataNode process: processes)
						generateProcessCode(codePath, process, ecology.id());
			}
			// generate Initialiser classes
			List<TreeGraphDataNode> initialisers = getChildrenLabelled(dynamics, N_INITIALISER.label());
			for (TreeGraphDataNode initialiser: initialisers)
				generateInitialiserCode(codePath, initialiser, ecology.id());
			// copy generated files to user project for code editing
			String model = wordUpperCaseName(ecology.id());
			if (!codePath.equals(""))
				transferProjectArtifacts(codePath, Project.makeFile(model));

// TODO: generate the jars properly			
			// This can be moved to MM onDeploy. Also we only need to compile if we don't have a 
			// codePath
//			JarGenerator jgen = new JarGenerator(model, getInputPath(model), getOutputPath());
//			jgen.generateJar();
		}
		return !ComplianceManager.haveErrors();
	}

	private void transferProjectArtifacts(String codePath, File targetDir) {
		File srcJavaDir = new File(
				codePath + File.separator + CodeGenerator.SRC + File.separator + targetDir.getName());
		File srcClassDir = new File(
				codePath + File.separator + CodeGenerator.BIN + File.separator + targetDir.getName());
		try {
			srcJavaDir.mkdirs();
			srcClassDir.mkdirs();
			// copyDirectory copies all children 
			FileUtils.copyDirectory(srcJavaDir, targetDir);
			FileUtils.copyDirectory(srcClassDir, targetDir);
			/**
			 * Initially, there won't be any java files in the external project. Therefore,
			 * copy the newly created default ones (or the newly copied) back to the
			 * external Eclipse project.
			 */
			String[] extensions = new String[] { "java" };
			List<File> files = (List<File>) FileUtils.listFiles(targetDir, extensions, true);
			for (File inFile : files) {
				String inName = inFile.getAbsolutePath();
				String outName = inName.replace(Project.getProjectDirectory(), "");
				File outFile = new File(codePath + File.separator + CodeGenerator.SRC + File.separator + outName);
				outFile.mkdirs();
				Files.copy(inFile.toPath(), outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// only to be called by generateDataCode(String codePath, TreeGraphDataNode system, String modelName)
	private void generateDataCode(List<File> result, 
			TreeGraphDataNode spec,
			TreeGraphDataNode system, 
			String modelName, 
			String dataGroup) {
		if (spec != null) {
			TwDataGenerator gen = new TwDataGenerator(modelName, spec);
			gen.generateCode();
			result.addAll(gen.getFiles());
			if (system.properties().hasProperty(dataGroup))
				system.properties().setProperty(dataGroup, gen.generatedClassName());
			else
				((ResizeablePropertyList)system.properties()).addProperty(dataGroup, gen.generatedClassName());
			if (spec.properties().hasProperty("generated"))
				if (spec.properties().getPropertyValue("generated").equals(true))
					spec.disconnect();
		} 
		else if (system.properties().hasProperty(dataGroup))
			((ResizeablePropertyList)system.properties()).removeProperty(dataGroup);
		GraphState.setChanged();
	}

	private List<File> generateDataCode(String codePath, 
			TreeGraphDataNode system, 
			String modelName) {
		List<File> result = new ArrayList<File>();
		TreeGraphDataNode spec = Categorized.buildUniqueDataList(system, E_DRIVERS.label());
		generateDataCode(result,spec,system,modelName,P_DRIVERCLASS.key());
		spec = Categorized.buildUniqueDataList(system, E_PARAMETERS.label());
		generateDataCode(result,spec,system,modelName,P_PARAMETERCLASS.key());
		spec = Categorized.buildUniqueDataList(system, E_DECORATORS.label());
		generateDataCode(result,spec,system,modelName,P_DECORATORCLASS.key());
		return result;
	}

	@SuppressWarnings("unchecked")
	private void generateProcessCode(String codePath, TreeGraphDataNode process, String modelName) {
		// crash here is if 0 functions
		List<TreeGraphDataNode> functions =  getChildrenLabelled(process, N_FUNCTION.label());
		for (TreeGraphDataNode function : functions) {
			// 1 generate code for this function
			generateFunctionCode(function,modelName);
			// 2 generate code for its children (consequence) functions
			List<TreeGraphDataNode> consequences = (List<TreeGraphDataNode>) get(function.getChildren(),
				selectZeroOrMany(hasTheLabel(N_FUNCTION.label())));
			for (TreeGraphDataNode csq:consequences)
				generateFunctionCode(csq,modelName);
		}
	}

	private void generateFunctionCode(TreeGraphDataNode function, String modelName) {
		TwFunctionGenerator generator = new TwFunctionGenerator(function.id(), function, modelName);
		generator.generateCode();
		((ResizeablePropertyList)function.properties()).addProperty(P_FUNCTIONCLASS.key(), 
			generator.generatedClassName());
	}

	private void generateInitialiserCode(String codePath, 
			TreeGraphDataNode initialiser, 
			String modelName) {
//		TreeGraphDataNode initialiserSpec = (TreeGraphDataNode) get(initialiser, 
//			outEdges(), 
//			selectOne(hasTheLabel(E_SPEC.toString())),
//			endNode());
		TwInitialiserGenerator generator = new TwInitialiserGenerator(initialiser.id(), 
			initialiser,
			modelName);
		generator.generateCode();
		((ResizeablePropertyList)initialiser.properties()).addProperty(P_FUNCTIONCLASS.key(),
			generator.generatedClassName());
	}

	@SuppressWarnings("unchecked")
	private static List<TreeGraphDataNode> getChildrenLabelled(TreeGraphDataNode root, String label) {
		return (List<TreeGraphDataNode>) get(root.getChildren(),selectZeroOrMany(hasTheLabel(label)));
	}

}
