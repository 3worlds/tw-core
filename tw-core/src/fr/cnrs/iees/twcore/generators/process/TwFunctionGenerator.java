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
import static fr.cnrs.iees.twcore.generators.process.ArgumentGroups.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.bouncycastle.util.Strings;

import au.edu.anu.rscs.aot.collections.tables.Table;
import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.biology.TwFunctionAdapter;
import au.edu.anu.twcore.ecosystem.runtime.space.Location;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemData;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.project.ProjectPaths;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;
import fr.cnrs.iees.twcore.constants.FileType;
import fr.cnrs.iees.twcore.constants.SnippetLocation;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;
import fr.cnrs.iees.twcore.generators.TwCodeGenerator;
import fr.cnrs.iees.uit.space.Box;
import fr.cnrs.iees.uit.space.Point;
import fr.ens.biologie.codeGeneration.ClassGenerator;
//import fr.ens.biologie.codeGeneration.JavaCompiler;
import fr.ens.biologie.codeGeneration.MethodGenerator;
import fr.ens.biologie.generic.utils.Duple;
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
//	private List<String> inClassCode = null;

	@SuppressWarnings("unchecked")
	public TwFunctionGenerator(String className, TreeGraphDataNode spec, String modelName) {
		super(spec);
		name = className;
		// type = (String)spec.getPropertyValue("type");
		type = (TwFunctionTypes) spec.properties().getPropertyValue(P_FUNCTIONTYPE.key());
		model = modelName;
		packagePath = Project.makeFile(LOCALCODE,validJavaName(wordUpperCaseName(modelName))).getAbsolutePath();
// OLD CODE - dealing with snippet files.
		// maybe useful in model generator though
//		Collection<TreeGraphDataNode> snippets = (Collection<TreeGraphDataNode>) get(spec.edges(Direction.OUT), edgeListEndNodes(),
//			selectZeroOrMany(hasTheLabel("snippet")));
//		for (TreeGraphDataNode snip : snippets) {
//			/*
//			 * (Ian) dont report error here if file is missing. Actually, this can't work
//			 * because these files are relative to PROJECT_MODEL_GRAPH dir There is no way
//			 * to independently know the project root of a File object. Therefore the
//			 * property editor for File class can never work! For file handling in 3w we
//			 * actually need a class that allows the class to select a file, if not in
//			 * fileRoot then import it to file root But then where do these properities come
//			 * from? Bit of a mess. We need a different class for each project sub dir
//			 * (graphs,jars files etc??).
//			 *
//			 *
//			 */
//			if (!snip.properties().hasProperty("file"))
//				// file: java.io.File("local/models/snippet-main-t3.txt")
//				continue;
//			FileType ft = (FileType) snip.properties().getPropertyValue("file");
//			if (!ft.getFile().exists())
//				continue;
//			SnippetLocation insert = (SnippetLocation) snip.properties().getPropertyValue("insertion");
//			if (insert.equals(SnippetLocation.inClassBody));
////				inClassCode = snippetCode(snip);
//			else
//				inBodyCode = snippetCode(snip);
//		}
		if (inBodyCode==null) {
			inBodyCode = new ArrayList<String>();
			// TODO: this is useful for debugging only, should be replaced by some
			// logging in the final version
			String defLine = "System.out.println(getClass().getSimpleName()+\"\tTime\t\"+t)";
			inBodyCode.add(defLine);
		}

	}

// OLD CODE - dealing with snippet files.
// maybe useful in model generator though
//	private List<String> snippetCode(TreeGraphDataNode snippet) {
//		List<String> code = new LinkedList<String>();
//		// File f =
//		// Project.makeFile(PROJECT_MODEL_GRAPHS,(String)((File)snippet.getPropertyValue("file")).getName());
//		FileType ft = (FileType) snippet.properties().getPropertyValue("file");
//		File f = ft.getFile();
//		if (!f.isDirectory() & f.exists()) {
//			try {
//				code = Files.readAllLines(f.toPath());
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		} else
//			return null;
//		return code;
//	}

	@Override
	public boolean generateCode() {
		log.info("    generating file " + name + ".java ...");
		File ctGeneratedCodeDir = getModelCodeDir(model);
		ctGeneratedCodeDir.mkdirs();
		String ctmodel = validJavaName(wordUpperCaseName(model));
		packageName = ProjectPaths.REMOTECODE.replace(File.separator,".")+"."+ctmodel;
		String ancestorClassName = FUNCTION_ROOT_PACKAGE + "." + type.name() + "Function";
		String comment = comment(general, classComment(name), generatedCode(true, model, ""));
		ClassGenerator generator = new ClassGenerator(packageName, comment, name, ancestorClassName);

		// imports in the TwFunction descendant
		generator.setImport(SystemComponent.class.getCanonicalName());
		generator.setImport(Table.class.getPackageName()+".*");
		Set<String> argClasses = new TreeSet<>(); // constant order
		for (ArgumentGroups arggrp:type.readOnlyArguments())
			if (!ValidPropertyTypes.isPrimitiveType(arggrp.type()))
				argClasses.add(arggrp.type());
		for (ArgumentGroups arggrp:type.writeableArguments())
			if (!ValidPropertyTypes.isPrimitiveType(arggrp.type()))
				if (!arggrp.type().equals("double[]"))
					argClasses.add(arggrp.type());
		for (String s:argClasses)
			generator.setImport(s);

		// inner classes for returned values
		List<String> innerClasses = new LinkedList<>();
		for (String s:innerClassDecl.keySet())
			innerClasses.addAll(innerClassDecl.get(s));
		generator.setRawMethodCode(innerClasses);
		// main method settings
		Collection<MethodGenerator> lmg = generator.getMethods();
		for (MethodGenerator mg : lmg) { // only 1 assumed?
			//argument list
			Set<ArgumentGroups> argSet = new TreeSet<>();
			argSet.addAll(type.readOnlyArguments());
			argSet.addAll(type.writeableArguments());
			// argument names
			String[] argNames = new String[argSet.size()];
			int i=0;
			for (ArgumentGroups ag:argSet)
				argNames[i++] = ag.name();
			mg.setArgumentNames(argNames);
			// argument types (not from ancestor, for consistency here)
			// BUT the ancestor TwFunction MUST have its arguments in the proper order!
			String[] argTypes = new String[argSet.size()];
			i=0;
			for (ArgumentGroups ag:argSet) {
				String[] ss = ag.type().split("\\.");
				argTypes[i++] = ss[ss.length-1];
			}
			for (int j=0; j<argTypes.length; j++)
				mg.setArgumentType(j,argTypes[j]);
			// return type
			mg.setReturnType(type.returnType());
			if (type.returnType().equals("void"))
				mg.setReturnStatement("");
			else
				mg.setReturnStatement("return "+zero(checkType(type.returnType())));
			// preparing call to user model function: initialising read-write data
			for (String k:innerVarInit.keySet())
				for (String s: innerVarInit.get(k))
					mg.setStatement(s);
			// call to user code
			mg.setStatement(callStatement);
			// getting results from user code
			for (String k:innerVarCopy.keySet())
				for (String s: innerVarCopy.get(k))
					mg.setStatement(s);
//			if (inBodyCode != null)
//				for (String s : inBodyCode)
//					mg.setStatement(s);
		}
//		generator.setRawMethodCode(inClassCode);
		File file = Project.makeFile(LOCALCODE,ctmodel, name + ".java");
		writeFile(generator, file, name);
		generatedClassName = packageName + "." + name;
		log.info("  done.");

//		JavaCompiler compiler = new JavaCompiler();
//		String result =  compiler.compileCode(file,Project.makeFile());
//		if (result!=null)
//			ComplianceManager.add(new CompileErr(file, result));
		return true; //result==null;
	}

	public String generatedClassName() {
		return generatedClassName;
	}
	public File getFile() {
		String name = generatedClassName.replace(this.packageName+".", "");
		String path = packagePath+File.separator+name;
		return new File(path+".java");
	}

	// interfacing statements indexed by group (varname)
	private Map<String,List<String>> innerClassDecl = new HashMap<>();
	private Map<String,List<String>> innerVarInit = new HashMap<>();
	private Map<String,List<String>> innerVarCopy = new HashMap<>();
	private Set<String> dataClassesToImport = new HashSet<>();
	private String callStatement ="";
	//TODO: pass the class,var name pair back to the ModelGenerator
	// TODO: import the inner classes to modelgenerator
	// TODO: special case for locations

	// TODO: Must check ll this
	public void setArgumentCalls(ModelGenerator gen) {
		String classToCall = gen.className();
		Map<ArgumentGroups,List<Duple<String,String>>> reqArgs = gen.method(name).callerArguments();
		System.out.println("***** Statement generation for "+name+" ("+type+") *****");
		String indent = "\t";
		callStatement = classToCall+"."+
			Strings.toLowerCase(name.substring(0,1))+
			name.substring(1)+"(\n";
		String innerClass = "";
		String innerVar = "";
		boolean makeInnerClass = false;
		for (ArgumentGroups ag: reqArgs.keySet()) {
			makeInnerClass = type.writeableArguments().contains(ag) & !ag.name().contains("Loc");
			if (makeInnerClass) {
				innerVar = ag.toString();
				innerClass = Strings.toUpperCase(innerVar.substring(0,1))+innerVar.substring(1);
				List<String> innerClassDeclaration = innerClassDecl.get(innerVar);
				if (innerClassDeclaration==null) {
					innerClassDeclaration = new LinkedList<>();
					innerClassDecl.put(innerVar,innerClassDeclaration);
					innerClassDeclaration.add(indent+"public class "+innerClass+" {");
				}
				List<String> innerVarInitialisation = innerVarInit.get(innerVar);
				if (innerVarInitialisation==null) {
					innerVarInitialisation = new LinkedList<>();
					innerVarInit.put(innerVar,innerVarInitialisation);
					innerVarInitialisation.add(innerClass+" "+innerVar+" = new "+innerClass+"()");
				}
				List<String> innerVarBackCopy = innerVarCopy.get(innerVar);
				if (innerVarBackCopy==null) {
					innerVarBackCopy = new LinkedList<>();
					innerVarCopy.put(innerVar,innerVarBackCopy);
					innerVarBackCopy.add("// copy back statements");
				}
			}
			for (Duple<String,String> d: reqArgs.get(ag)){
				String an = d.getFirst(); // argument name
				String at = d.getSecond(); // argument type
				String callArg = null;
				if ((ag==t) || (ag==dt) || (ag==limits) ||
					(ag==focalLoc) || (ag==otherLoc) || (ag==nextFocalLoc))
					callArg = ag.name();
				else if ((ag==ecosystemPar) || (ag==lifeCyclePar))
					;
				else if ((ag==ecosystemPop) || (ag==lifeCyclePop))
					;
				else if ((ag==groupPop))
					;
				else if ((ag==focalAuto) || (ag==otherAuto))
					callArg = ag.name()+"."+an+"()*1.0"; // TODO: remove the *1.0 and replace by proper timer conversion
				else if (type.writeableArguments().contains(ag)) {

// ================================== example code to generate

//					class NextFocalDrv { // OK
//						double x;
//						double r;
//					} // OK
//
//					NextFocalDrv nextFocalDrv = new NextFocalDrv(); // OK
//					nextFocalDrv.x = ((MyDrvClass)focalDrv).x();
//					nextFocalDrv.r = ((MyDrvClass)focalDrv).r();
//
//					method call arg: nextFocalDrv // OK
//
//					((MyDrvClass)focalDrv).x(nextFocalDrv.x);
//					((MyDrvClass)focalDrv).r(nextFocalDrv.r);
//	======================================
					// the information to generate the members is not in reqArgs, oris it there but
					// not at the proper place.

					// these are the inner classes
					callArg = innerVar; //OK

					// get the categories for this function
					gen.findCategories(spec, true); // this is for focal objects

//					String lc = null;
//					String sc = lc.split("\\.")[lc.split("\\.").length-1];
//					dataClassesToImport.add(lc);
					if (makeInnerClass) {
						innerClassDecl.get(innerVar).add(indent+indent+at+" "+an+";");
//						innerVarInit.get(innerVar).add(innerVar+"."+an+" = ((" + sc+ ")"+ag.toString()+")."+an+"()");
//						innerVarCopy.get(innerVar).add("((" + sc+ ")"+ag.toString()+")."+an+"("+innerVar+"."+an+")");
					}
				}
				else {
					String lc = gen.containingClass(an);
					dataClassesToImport.add(lc);
					String sc = lc.split("\\.")[lc.split("\\.").length-1];
					callArg = "((" + sc+ ")"+ag.toString()+")."+an+"()";
//					if (makeInnerClass) {
//						innerClassDecl.get(innerVar).add(indent+indent+at+" "+an+";");
//						innerVarInit.get(innerVar).add(innerVar+"."+an+" = "+callArg);
//						innerVarCopy.get(innerVar).add("((" + sc+ ")"+ag.toString()+")."+an+"("+innerVar+"."+an+")");
//					}
				}
				if (callArg!=null)
					callStatement += indent+indent+indent+ callArg + ",\n";
			}
			if (makeInnerClass) {
				innerClassDecl.get(innerVar).add(indent+"}\n");
				if (innerClassDecl.get(innerVar).size()<=2) { // means the class has no fields
					innerClassDecl.remove(innerVar);
					innerVarInit.remove(innerVar);
					innerVarCopy.remove(innerVar);
				}
			}
		} // for
		// inner class declarations
		for (String s:innerClassDecl.keySet()) {
			System.out.println(innerClassDecl.get(s));
			callStatement += indent+indent+indent+ s + ",\n";
		}
		// before user method call
		for (String s:innerVarInit.keySet())
			System.out.println(innerVarInit.get(s));

		// completion of user method call
		callStatement = callStatement.substring(0, callStatement.length()-2);
		callStatement +=")";
		// inner var initialisation
		System.out.println(callStatement);
		// after method call
		for (String s:innerVarCopy.keySet())
			System.out.println(innerVarCopy.get(s));
	}

}
