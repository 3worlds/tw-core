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
import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.init.SecondaryParametersInitialiser;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.errorMessaging.ComplianceManager;
import au.edu.anu.twcore.errorMessaging.codeGenerator.CompileErr;
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
import fr.ens.biologie.generic.utils.Logging;

/**
 * A class to generate code for secondary parameter initialisation
 * Actually most of the code here is copied from TwFunctionGenerator...
 * @author gignoux - 19 juin 2017
 *
 */
public class TwInitialiserGenerator extends TwCodeGenerator {

	private static String INITIALISER_ROOT_PACKAGE = SecondaryParametersInitialiser.class.getPackageName(); 
	
	private static Logger log = Logging.getLogger(TwInitialiserGenerator.class);
	private String name = null;
	private String model = null;
	private List<String> inBodyCode = null;
	private List<String> inClassCode = null;
	private String generatedClassName = null;
	private String packageName = null;
	
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
		packageName = ctmodel;		
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
				mg.setArgumentNames("speciesParameters","stageParameters","timeOrigin","timeUnit");
			}
			if (inBodyCode!=null) {
				String ss = "";
				for (String s:inBodyCode) ss += s+"\n";
				mg.setStatement(ss);
			}
		}
		generator.setRawMethodCode(inClassCode);
//		File file = Project.makeFile(ctmodel,TW_CODE,name+".java");
		File file = Project.makeFile(CODE,ctmodel,name+".java");
		writeFile(generator,file,name);
		generatedClassName = packageName+"."+name;
		log.info("  done.");
		
		JavaCompiler compiler = new JavaCompiler();
		String result= compiler.compileCode(file,Project.makeFile());
		if (result!=null) 
			ComplianceManager.add(new CompileErr(file, result));
		return result==null;
	}
	
	public String generatedClassName() {
		return generatedClassName;
	}
	public File getFile() {
		String name = generatedClassName.replace(this.packageName+".", "");
		String path = packageName+File.separator+name;
		return new File(path+".java");
	}

}
