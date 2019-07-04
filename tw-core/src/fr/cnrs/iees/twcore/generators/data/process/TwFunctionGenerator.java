package fr.ens.biologie.threeWorlds.build.codeGeneration.process;

import static fr.ens.biologie.threeWorlds.build.codeGeneration.CodeGenerationUtils.writeFile;
import static fr.ens.biologie.threeWorlds.build.codeGeneration.Comments.*;
import static fr.ens.biologie.threeWorlds.core.ecology.data.NameUtils.*;
import static fr.ens.biologie.threeWorlds.resources.core.constants.SnippetLocation.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.SequenceQuery.get;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import au.edu.anu.rscs.aot.collections.AotList;
import au.edu.anu.rscs.aot.graph.AotNode;
import au.edu.anu.rscs.aot.graph.generic.Direction;
import au.edu.anu.rscs.aot.logging.Logger;
import au.edu.anu.rscs.aot.logging.LoggerFactory;
import fr.ens.biologie.threeWorlds.build.JavaCompiler;
import fr.ens.biologie.threeWorlds.build.codeGeneration.ClassGenerator;
import fr.ens.biologie.threeWorlds.build.codeGeneration.MethodGenerator;
import fr.ens.biologie.threeWorlds.build.codeGeneration.TwCodeGenerator;
import fr.ens.biologie.threeWorlds.core.computingGrid.deployment.Project;
import fr.ens.biologie.threeWorlds.resources.core.constants.SnippetLocation;
import fr.ens.biologie.threeWorlds.resources.core.constants.TwFunctionTypes;
import fr.ens.biologie.threeWorlds.ui.modelMakerfx.model.propertyEditor.fileType.FileType;

/**
 * A class to generate a skeleton java file for a single EcologicalProcess
 * descendant
 * 
 * @author J. Gignoux - 23 nov. 2016
 *
 */
public class TwFunctionGenerator extends TwCodeGenerator {

	public static final String FUNCTION_ROOT_PACKAGE = "fr.ens.biologie.threeWorlds.core.ecology.process.biology";

	private Logger log = LoggerFactory.getLogger(TwFunctionGenerator.class, "3Worlds");
	private String name = null;
	// private String type = null;
	private TwFunctionTypes type = null;
	private String model = null;
	private String generatedClassName = null;
	private List<String> inBodyCode = null;
	private List<String> inClassCode = null;

	@SuppressWarnings("unchecked")
	public TwFunctionGenerator(String className, AotNode spec, String modelName) {
		super(spec);
		name = className;
		// type = (String)spec.getPropertyValue("type");
		type = (TwFunctionTypes) spec.getPropertyValue("type");
		model = modelName;
		AotList<AotNode> snippets = (AotList<AotNode>) get(spec.getEdges(Direction.OUT), edgeListEndNodes(),
				selectZeroOrMany(hasTheLabel("snippet")));
		for (AotNode snip : snippets) {
			/*
			 * (Ian) dont report error here if file is missing. Actually, this can't work
			 * because these files are relative to PROJECT_MODEL_GRAPH dir There is no way
			 * to independently know the project root of a File object. Therefore the
			 * property editor for File class can never work! For file handling in 3w we
			 * actually need a class that allows the class to select a file, if not in
			 * fileRoot then import it to file root But then where do these properities come
			 * from? Bit of a mess. We need a different class for each project sub dir
			 * (graphs,jars files etc??).
			 * 
			 *
			 */
			if (!snip.hasProperty("file"))
				// file: java.io.File("local/models/snippet-main-t3.txt")
				continue;
			FileType ft = (FileType) snip.getPropertyValue("file");
			if (!ft.getFile().exists())
				continue;
			SnippetLocation insert = (SnippetLocation) snip.getPropertyValue("insertion");
			if (insert.equals(inClassBody))
				inClassCode = snippetCode(snip);
			else
				inBodyCode = snippetCode(snip);
		}
		if (inBodyCode==null) {
			inBodyCode = new ArrayList<String>();
			String defLine = "System.out.println(getClass().getName()+\"\tTime\t\"+t);";
			inBodyCode.add(defLine);
		}
		
	}

	private List<String> snippetCode(AotNode snippet) {
		List<String> code = new LinkedList<String>();
		// File f =
		// Project.makeFile(PROJECT_MODEL_GRAPHS,(String)((File)snippet.getPropertyValue("file")).getName());
		FileType ft = (FileType) snippet.getPropertyValue("file");
		File f = ft.getFile();
		if (!f.isDirectory() & f.exists()) {
			try {
				code = Files.readAllLines(f.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			return null;
		return code;
	}

	@Override
	public boolean generateCode() {
		log.debug("    generating file " + name + ".java ...");
		File ctGeneratedCodeDir = getModelCodeDir(model);
		ctGeneratedCodeDir.mkdirs();
		String ctmodel = validJavaName(wordUpperCaseName(model));
		String packageName = ctmodel + "." + THREE_WORLDS_CODE;
		String ancestorClassName = FUNCTION_ROOT_PACKAGE + "." + type.name() + "Function";
		String comment = comment(general, classComment(name), generatedCode(true, model, ""));
		ClassGenerator generator = new ClassGenerator(packageName, comment, name, ancestorClassName);
		generator.setImport("fr.ens.biologie.threeWorlds.core.ecology.ecosystem.SystemComponent");
		generator.setImport("au.edu.anu.rscs.aot.collections.tables.*");
		// generator.setImport("java.util.Map");
		Collection<MethodGenerator> lmg = generator.getMethods();
		for (MethodGenerator mg : lmg) {
//			mg.insertCodeInsertionComment();
			// specific problems with arguments of methods.
			if (mg.name().equals("nNew")) {
				mg.setArgumentNames("t", "dt", "focal", "newType");
			} else if (mg.name().equals("maternalEffect")) {
				// mg.setArgumentType(4, "Map<String,Iterable<ComplexSystem>>");
				mg.setArgumentNames("t", "dt", "parent", "offspring", "environment");
			} else if ((mg.name().equals("changeCategory")) | (mg.name().equals("changeState"))
					| (mg.name().equals("dies"))) {
				// mg.setArgumentType(3, "Map<String,Iterable<ComplexSystem>>");
				mg.setArgumentNames("t", "dt", "focal");
			} else if (mg.name().equals("disturb")) {
				// mg.setArgumentType(4, "Map<String,Iterable<ComplexSystem>>");
				mg.setArgumentNames("t", "dt", "focal", "target", "environment");
			}
			if (inBodyCode != null) {
				String ss = "";
				for (String s : inBodyCode)
					ss += s + "\n";
				mg.setStatement(ss);
			}
		}
		generator.setRawMethodCode(inClassCode);
		File file = Project.makeFile(ctmodel,THREE_WORLDS_CODE, name + ".java");
		writeFile(generator, file, name);
		generatedClassName = packageName + "." + name;
		log.debug("  done.");

		JavaCompiler compiler = new JavaCompiler();
		return compiler.compileCode(file);
	}

	public String generatedClassName() {
		return generatedClassName;
	}

}
