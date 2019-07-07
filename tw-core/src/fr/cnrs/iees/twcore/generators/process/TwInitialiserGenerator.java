package fr.cnrs.iees.twcore.generators.process;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.ens.biologie.generic.utils.NameUtils.*;
import static fr.cnrs.iees.twcore.generators.TwComments.*;
import static fr.ens.biologie.codeGeneration.CodeGenerationUtils.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import au.edu.anu.rscs.aot.collections.tables.Table;
import au.edu.anu.rscs.aot.init.Initialiser;
import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.project.Project;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.twcore.constants.FileType;
import fr.cnrs.iees.twcore.constants.SnippetLocation;
import fr.cnrs.iees.twcore.constants.TimeUnits;
import fr.cnrs.iees.twcore.generators.TwCodeGenerator;
import fr.ens.biologie.codeGeneration.ClassGenerator;
import fr.ens.biologie.codeGeneration.JavaCompiler;
import fr.ens.biologie.codeGeneration.MethodGenerator;

/**
 * A class to generate code for secondary parameter initialisation
 * Actually most of the code here is copied from TwFunctionGenerator...
 * @author gignoux - 19 juin 2017
 *
 */
public class TwInitialiserGenerator extends TwCodeGenerator {

	private static String INITIALISER_ROOT_PACKAGE = Initialiser.class.getPackageName(); 
	
	private Logger log = Logger.getLogger(TwInitialiserGenerator.class.getName());
	private String name = null;
	private String model = null;
	private List<String> inBodyCode = null;
	private List<String> inClassCode = null;
	private String generatedClassName = null;
	
	@SuppressWarnings("unchecked")
	public TwInitialiserGenerator(String className, TreeGraphDataNode spec,String modelName) {
		super(spec);
		name = className;
		model = modelName;
		Collection<TreeGraphDataNode> snippets = (Collection<TreeGraphDataNode>) get(spec.edges(Direction.OUT), 
			edgeListEndNodes(),
			selectZeroOrMany(hasTheLabel("snippet")));
		for (TreeGraphDataNode snip:snippets) {
			if (!snip.properties().hasProperty("file"))
				continue;
			FileType ft = (FileType) snip.properties().getPropertyValue("file");
			if (!ft.getFile().exists())
				continue;			
			SnippetLocation insert = (SnippetLocation) snip.properties().getPropertyValue("insertion");
			if (insert.equals(SnippetLocation.inClassBody)) inClassCode = snippetCode(snip);
			else inBodyCode = snippetCode(snip);
		}
		if (inBodyCode==null) {
			inBodyCode = new ArrayList<String>();
			String defLine = "System.out.println(getClass().getName());";
			inBodyCode.add(defLine);
		}
	}

	private List<String> snippetCode(TreeGraphDataNode snippet) {
		List<String> code = new LinkedList<String>();
//		File f = Project.makeFile(PROJECT_MODEL_GRAPHS,(String)((File)snippet.getPropertyValue("file")).getName());
		FileType ft = (FileType) snippet.properties().getPropertyValue("file");
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
		log.info("    generating file "+name+".java ...");
		log.info("  done.");
		File ctGeneratedCodeDir =  getModelCodeDir(model);			
		ctGeneratedCodeDir.mkdirs();
		String ctmodel = validJavaName(wordUpperCaseName(model));
		String packageName = ctmodel+"."+TW_CODE;		
		String ancestorClassName = INITIALISER_ROOT_PACKAGE+".SecondaryParametersInitialiser";		
		String comment = comment(general,classComment(name),generatedCode(false,model, ""));				
		ClassGenerator generator = new ClassGenerator(packageName,comment,name,ancestorClassName);
		generator.setImport(SystemComponent.class.getCanonicalName());
		generator.setImport(Table.class.getPackageName()+".*");
		generator.setImport(TwData.class.getCanonicalName());
		generator.setImport(TimeUnits.class.getCanonicalName());
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
		File file = Project.makeFile(ctmodel,TW_CODE,name+".java");
		writeFile(generator,file,name);
		generatedClassName = packageName+"."+name;
		log.info("  done.");
		
		JavaCompiler compiler = new JavaCompiler();
		return compiler.compileCode(file,Project.makeFile());
	}
	
	public String generatedClassName() {
		return generatedClassName;
	}

}