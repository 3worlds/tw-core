/**
 * 
 */
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
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;

import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemFactory;
import au.edu.anu.twcore.project.Project;
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
		String pp = Project.getProjectRoot().getAbsolutePath();
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
		List<TreeGraphDataNode> ecologies = (List<TreeGraphDataNode>) get(graph.root().getChildren(),
			selectOneOrMany(hasTheLabel(N_SYSTEM.label()))); 
//				getChildrenLabelled(graph.getRoot(), N_SYSTEM.label());
		for (TreeGraphDataNode ecology : ecologies) {
				File ecologyFiles = new File(
					Project.getProjectRoot().getAbsoluteFile() + File.separator + wordUpperCaseName(ecology.id()));
			try {
				deleteFileTree(ecologyFiles);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List<TreeGraphDataNode> processes = getChildrenLabelled(ecology, N_PROCESS.label());
			List<TreeGraphDataNode> initialisers = getChildrenLabelled(ecology, N_INITIALISER.label());
			List<TreeGraphDataNode> systems = getChildrenLabelled(ecology, N_COMPONENT.label());
			for (TreeGraphDataNode system : systems) {
				List<File> files = generateDataCode(codePath, system, ecology.id());
				if (!codePath.equals(""))
					// ensure new or updated data code cannot be overwritten by user.
					// well actually, why not let them edit if they want??
					overWriteReadOnlyFiles(codePath, files);
			}
			// JG - now code can also be generated for LifeCycle and for Ecosystem
			List<TreeGraphDataNode> lifeCycles = getChildrenLabelled(ecology, N_LIFECYCLE.label());
			for (TreeGraphDataNode lc:lifeCycles) {
				generateDataCode(codePath, lc, ecology.id());
			}
			generateDataCode(codePath, ecology, ecology.id());
			// end comment above - JG
			for (TreeGraphDataNode process : processes)
				generateProcessCode(codePath, process, ecology.id());

			for (TreeGraphDataNode initialiser : initialisers)
				generateInitialiserCode(codePath, initialiser, ecology.id());

			
			String model = wordUpperCaseName(ecology.id());
			if (!codePath.equals(""))
				transferProjectArtifacts(codePath, getInputPath(model));

// TODO: generate the jars properly			
			// This can be moved to MM onDeploy. Also we only need to compile if we don't have a 
			// codePath
//			JarGenerator jgen = new JarGenerator(model, getInputPath(model), getOutputPath());
//			jgen.generateJar();
		}
		return !CodeCompliance.haveErrors();

	}

	@SuppressWarnings("unchecked")
	private void transferProjectArtifacts(String codePath, File targetDir) {
	
		File srcJavaDir = new File(
				codePath + File.separator + CodeGenerator.SRC + File.separator + targetDir.getName());
		File srcClassDir = new File(
				codePath + File.separator + CodeGenerator.BIN + File.separator + targetDir.getName());
		try {
			srcJavaDir.mkdirs();
			srcClassDir.mkdirs();
			// System.out.println("Copy from: "+srcJavaDir.getAbsolutePath());
			// System.out.println(" to: "+modelDir.getAbsolutePath());

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
				String outName = inName.replace(Project.getProjectRoot().getAbsolutePath(), "");
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
		GraphState.isChanged(true);
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
//				(List<TreeGraphDataNode>) get(process.getChildren(),
//				selectOneOrMany(hasTheLabel(N_FUNCTION.label())));
		for (TreeGraphDataNode function : functions) {
			// 1 generate code for this function
			generateFunctionCode(function,modelName);
			// 2 generate code for its children (consequence) functions
			List<TreeGraphDataNode> consequences = (List<TreeGraphDataNode>) get(function.getChildren(),
				selectZeroOrMany(hasTheLabel(N_FUNCTION.label())));
			for (TreeGraphDataNode csq:consequences)
				generateFunctionCode(csq,modelName);
//			ALEdge espec = (ALEdge) get(function.edges(Direction.OUT),
//				selectZeroOrOne(hasTheLabel(E_SPEC.toString())));
//			if (espec != null)
//				generateFunctionCode(function, (TreeGraphDataNode) espec.endNode(), modelName);
//			TreeGraphDataNode consequence = (TreeGraphDataNode) get(function.getChildren(),
//				selectZeroOrOne(hasTheLabel(N_CONSEQUENCE.toString())));
//			if (consequence != null)
//				generateProcessCode(codePath, consequence, modelName);
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

	private static File getInputPath(String model) {
		// return new File(Project.getProjectRoot().getAbsolutePath() + File.separator +
		// PROJECT_FILES + File.separator + model);
		return new File(Project.getProjectRoot().getAbsolutePath() + File.separator + model);
	}

	private static File getOutputPath() {
		return new File(Project.getProjectRoot().getAbsolutePath());
	}

	@SuppressWarnings("unchecked")
	private static List<TreeGraphDataNode> getChildrenLabelled(TreeGraphDataNode root, String label) {
//		return (List<TreeGraphDataNode>) get(root.edges(Direction.OUT), edgeListEndNodes(),
//			selectZeroOrMany(hasTheLabel(label)));
		return (List<TreeGraphDataNode>) get(root.getChildren(),selectZeroOrMany(hasTheLabel(label)));
	}

}
