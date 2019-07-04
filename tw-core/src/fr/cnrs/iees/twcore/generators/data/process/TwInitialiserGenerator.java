package fr.ens.biologie.threeWorlds.build.codeGeneration.process;

import static au.edu.anu.rscs.aot.queries.CoreQueries.edgeListEndNodes;
import static au.edu.anu.rscs.aot.queries.CoreQueries.hasTheLabel;
import static au.edu.anu.rscs.aot.queries.CoreQueries.selectZeroOrMany;
import static au.edu.anu.rscs.aot.queries.SequenceQuery.get;
import static fr.ens.biologie.threeWorlds.build.codeGeneration.CodeGenerationUtils.writeFile;
import static fr.ens.biologie.threeWorlds.build.codeGeneration.Comments.classComment;
import static fr.ens.biologie.threeWorlds.build.codeGeneration.Comments.comment;
import static fr.ens.biologie.threeWorlds.build.codeGeneration.Comments.general;
import static fr.ens.biologie.threeWorlds.build.codeGeneration.Comments.generatedCode;
import static fr.ens.biologie.threeWorlds.core.ecology.data.NameUtils.validJavaName;
import static fr.ens.biologie.threeWorlds.core.ecology.data.NameUtils.wordUpperCaseName;
import static fr.ens.biologie.threeWorlds.resources.core.constants.SnippetLocation.inClassBody;

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
import fr.ens.biologie.threeWorlds.ui.modelMakerfx.model.propertyEditor.fileType.FileType;

/**
 * A class to generate code for secondary parameter initialisation
 * Actually most of the code here is copied from TwFunctionGenerator...
 * @author gignoux - 19 juin 2017
 *
 */
public class TwInitialiserGenerator extends TwCodeGenerator {

	private static String INITIALISER_ROOT_PACKAGE = "fr.ens.biologie.threeWorlds.core.ecology.init";
	
	private Logger log = LoggerFactory.getLogger(TwInitialiserGenerator.class,"3Worlds");
	private String name = null;
	private String model = null;
	private List<String> inBodyCode = null;
	private List<String> inClassCode = null;
	private String generatedClassName = null;
	
	@SuppressWarnings("unchecked")
	public TwInitialiserGenerator(String className, AotNode spec,String modelName) {
		super(spec);
		name = className;
		model = modelName;
		AotList<AotNode> snippets = (AotList<AotNode>) get(spec.getEdges(Direction.OUT), 
				edgeListEndNodes(),
				selectZeroOrMany(hasTheLabel("snippet")));
		for (AotNode snip:snippets) {
			if (!snip.hasProperty("file"))
				continue;
			FileType ft = (FileType) snip.getPropertyValue("file");
			if (!ft.getFile().exists())
				continue;			
			SnippetLocation insert = (SnippetLocation) snip.getPropertyValue("insertion");
			if (insert.equals(inClassBody)) inClassCode = snippetCode(snip);
			else inBodyCode = snippetCode(snip);
		}
		if (inBodyCode==null) {
			inBodyCode = new ArrayList<String>();
			String defLine = "System.out.println(getClass().getName());";
			inBodyCode.add(defLine);
		}
	}

	private List<String> snippetCode(AotNode snippet) {
		List<String> code = new LinkedList<String>();
//		File f = Project.makeFile(PROJECT_MODEL_GRAPHS,(String)((File)snippet.getPropertyValue("file")).getName());
		FileType ft = (FileType) snippet.getPropertyValue("file");
		File f = ft.getFile();
		try {
			code = Files.readAllLines(f.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return code;
	}
	
	@Override
	public boolean generateCode() {
		log.debug("    generating file "+name+".java ...");
		log.debug("  done.");
		File ctGeneratedCodeDir =  getModelCodeDir(model);			
		ctGeneratedCodeDir.mkdirs();
		String ctmodel = validJavaName(wordUpperCaseName(model));
		String packageName = ctmodel+"."+THREE_WORLDS_CODE;		
		String ancestorClassName = INITIALISER_ROOT_PACKAGE+".SecondaryParametersInitialiser";		
		String comment = comment(general,classComment(name),generatedCode(false,model, ""));				
		ClassGenerator generator = new ClassGenerator(packageName,comment,name,ancestorClassName);
		generator.setImport("fr.ens.biologie.threeWorlds.core.ecology.ecosystem.SystemComponent");
		generator.setImport("au.edu.anu.rscs.aot.collections.tables.*");
		generator.setImport("fr.ens.biologie.threeWorlds.core.ecology.data.TwData");
		generator.setImport("fr.ens.biologie.threeWorlds.resources.core.constants.TimeUnits");
		Collection<MethodGenerator> lmg = generator.getMethods();
		for (MethodGenerator mg:lmg) {
			mg.insertCodeInsertionComment();
			if  (mg.name().equals("setSecondaryParameters")) {
				mg.setArgumentNames("speciesParameters","stageParameters","timeGrain","timeOrigin","timeUnit");
			}
			if (inBodyCode!=null) {
				String ss = "";
				for (String s:inBodyCode) ss += s+"\n";
				mg.setStatement(ss);
			}
		}
		generator.setRawMethodCode(inClassCode);
		File file = Project.makeFile(ctmodel,THREE_WORLDS_CODE,name+".java");
		writeFile(generator,file,name);
		generatedClassName = packageName+"."+name;
		log.debug("  done.");
		
		JavaCompiler compiler = new JavaCompiler();
		return compiler.compileCode(file);
	}
	
	public String generatedClassName() {
		return generatedClassName;
	}

}
