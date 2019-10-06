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
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import au.edu.anu.rscs.aot.collections.tables.Table;
import au.edu.anu.twcore.ecosystem.runtime.biology.TwFunctionAdapter;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.errorMessaging.ComplianceManager;
import au.edu.anu.twcore.errorMessaging.codeGenerator.CompileErr;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.project.ProjectPaths;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.twcore.constants.FileType;
import fr.cnrs.iees.twcore.constants.SnippetLocation;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;
import fr.cnrs.iees.twcore.generators.TwCodeGenerator;
import fr.ens.biologie.codeGeneration.ClassGenerator;
import fr.ens.biologie.codeGeneration.JavaCompiler;
import fr.ens.biologie.codeGeneration.MethodGenerator;
import fr.ens.biologie.generic.utils.Logging;

/**
 * A class to generate a skeleton java file for a single EcologicalProcess
 * descendant
 * 
 * @author J. Gignoux - 23 nov. 2016
 *
 */
public class TwFunctionGenerator extends TwCodeGenerator {

	// the package of all TwFunction ancestors to user-defined functions. eg au.edu.anu.twcore.ecosystem.runtime.biology
	public static final String FUNCTION_ROOT_PACKAGE =	TwFunctionAdapter.class.getPackageName();
	private static Logger log = Logging.getLogger(TwFunctionGenerator.class);
	private String name = null;
	// private String type = null;
	private TwFunctionTypes type = null;
	private String model = null;
	private String generatedClassName = null;
	private String packageName=null;
	private String packagePath;
	private List<String> inBodyCode = null;
	private List<String> inClassCode = null;

	@SuppressWarnings("unchecked")
	public TwFunctionGenerator(String className, TreeGraphDataNode spec, String modelName) {
		super(spec);
		name = className;
		// type = (String)spec.getPropertyValue("type");
		type = (TwFunctionTypes) spec.properties().getPropertyValue(P_FUNCTIONTYPE.key());
		model = modelName;
		packagePath = Project.makeFile(LOCALCODE,validJavaName(wordUpperCaseName(modelName))).getAbsolutePath();

		Collection<TreeGraphDataNode> snippets = (Collection<TreeGraphDataNode>) get(spec.edges(Direction.OUT), edgeListEndNodes(),
			selectZeroOrMany(hasTheLabel("snippet")));
		for (TreeGraphDataNode snip : snippets) {
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
			if (!snip.properties().hasProperty("file"))
				// file: java.io.File("local/models/snippet-main-t3.txt")
				continue;
			FileType ft = (FileType) snip.properties().getPropertyValue("file");
			if (!ft.getFile().exists())
				continue;
			SnippetLocation insert = (SnippetLocation) snip.properties().getPropertyValue("insertion");
			if (insert.equals(SnippetLocation.inClassBody))
				inClassCode = snippetCode(snip);
			else
				inBodyCode = snippetCode(snip);
		}
		if (inBodyCode==null) {
			inBodyCode = new ArrayList<String>();
			// TODO: this is useful for debugging only, should be replaced by some 
			// logging in the final version
			String defLine = "System.out.println(getClass().getSimpleName()+\"\tTime\t\"+t)";
			inBodyCode.add(defLine);
		}
		
	}

	private List<String> snippetCode(TreeGraphDataNode snippet) {
		List<String> code = new LinkedList<String>();
		// File f =
		// Project.makeFile(PROJECT_MODEL_GRAPHS,(String)((File)snippet.getPropertyValue("file")).getName());
		FileType ft = (FileType) snippet.properties().getPropertyValue("file");
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
		log.info("    generating file " + name + ".java ...");
		File ctGeneratedCodeDir = getModelCodeDir(model);
		ctGeneratedCodeDir.mkdirs();
		String ctmodel = validJavaName(wordUpperCaseName(model));
//		String packageName = ctmodel + "." + TW_CODE;
		packageName = ProjectPaths.REMOTECODE.replace(File.separator,".")+"."+ctmodel;
		String ancestorClassName = FUNCTION_ROOT_PACKAGE + "." + type.name() + "Function";
		String comment = comment(general, classComment(name), generatedCode(true, model, ""));
		ClassGenerator generator = new ClassGenerator(packageName, comment, name, ancestorClassName);
		generator.setImport(SystemComponent.class.getCanonicalName());
		generator.setImport(Table.class.getPackageName()+".*");
		// generator.setImport("java.util.Map");
		Collection<MethodGenerator> lmg = generator.getMethods();
		for (MethodGenerator mg : lmg) {
			mg.setArgumentNames(type.argumentNames().split(","));
			mg.setReturnType(type.returnType());
			if (type.returnType().equals("void"))
				mg.setReturnStatement("");
			else 
				mg.setReturnStatement("return "+zero(type.returnType()));
			if (inBodyCode != null)
				for (String s : inBodyCode)
					mg.setStatement(s);
		}
		generator.setRawMethodCode(inClassCode);
//		File file = Project.makeFile(ctmodel,TW_CODE, name + ".java");
		File file = Project.makeFile(LOCALCODE,ctmodel, name + ".java");
		writeFile(generator, file, name);
		generatedClassName = packageName + "." + name;
		log.info("  done.");

		JavaCompiler compiler = new JavaCompiler();
		String result =  compiler.compileCode(file,Project.makeFile());
		if (result!=null) 
			ComplianceManager.add(new CompileErr(file, result));
		return result==null;
	}

	public String generatedClassName() {
		return generatedClassName;
	}
	public File getFile() {
		String name = generatedClassName.replace(this.packageName+".", "");
		String path = packagePath+File.separator+name;
		return new File(path+".java");
	}

}
