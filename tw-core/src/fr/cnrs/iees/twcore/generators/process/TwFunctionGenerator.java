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

import static fr.ens.biologie.generic.utils.NameUtils.*;
import static fr.cnrs.iees.twcore.generators.TwComments.*;
import static fr.ens.biologie.codeGeneration.CodeGenerationUtils.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.edgeListStartNodes;
import static au.edu.anu.rscs.aot.queries.CoreQueries.hasTheLabel;
import static au.edu.anu.rscs.aot.queries.CoreQueries.selectZeroOrMany;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_FEDBY;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static fr.cnrs.iees.twcore.generators.process.TwFunctionArguments.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import au.edu.anu.twcore.ecosystem.dynamics.FunctionNode;

//import org.bouncycastle.util.Strings; something goes wrong with this library when running from jar (security??)

import au.edu.anu.twcore.ecosystem.dynamics.TimerNode;
import au.edu.anu.twcore.ecosystem.runtime.biology.TwFunctionAdapter;
import au.edu.anu.twcore.ecosystem.runtime.space.LocatedSystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentData;
import au.edu.anu.twcore.ecosystem.runtime.system.ContainerData;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.project.ProjectPaths;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;
import fr.cnrs.iees.twcore.generators.TwCodeGenerator;
import fr.cnrs.iees.twcore.generators.process.ModelGenerator.memberInfo;
import fr.cnrs.iees.twcore.generators.process.ModelGenerator.recInfo;
import fr.cnrs.iees.uit.space.Point;
import fr.ens.biologie.codeGeneration.ClassGenerator;
//import fr.ens.biologie.codeGeneration.JavaCompiler;
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
	private SortedSet<String> eventTimerNames = new TreeSet<>();
//	private List<String> inClassCode = null;

	@SuppressWarnings("unchecked")
	public TwFunctionGenerator(String className, TreeGraphDataNode spec, String modelName) {
		super(spec);
		name = className;
		// type = (String)spec.getPropertyValue("type");
		type = (TwFunctionTypes) spec.properties().getPropertyValue(P_FUNCTIONTYPE.key());
		model = modelName;
		packagePath = Project.makeFile(LOCALJAVACODE,validJavaName(wordUpperCaseName(modelName))).getAbsolutePath();
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
		// name of event timer queues fed by this function
		Collection<TimerNode> queues = (Collection<TimerNode>) get(spec.edges(Direction.IN),
			selectZeroOrMany(hasTheLabel(E_FEDBY.label())),
			edgeListStartNodes());
		if (!queues.isEmpty() )
			for (TimerNode q:queues)
				eventTimerNames.add(q.id());
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
		packageName = ProjectPaths.CODE.replace(File.separator,".")+"."+ctmodel;
		String ancestorClassName = FUNCTION_ROOT_PACKAGE + "." + type.name() + "Function";
		String comment = comment(general, classComment(name), generatedCode(false, model, ""));
		ClassGenerator generator = new ClassGenerator(packageName, comment, name, null, ancestorClassName);

		// imports in the TwFunction descendant
//		generator.setImport(Table.class.getPackageName()+".*"); // not needed anymore, I think.
		Set<String> argClasses = new TreeSet<>(); // constant order
		for (TwFunctionArguments arggrp:type.readOnlyArguments())
			if (!ValidPropertyTypes.isPrimitiveType(arggrp.type()))
				if (!arggrp.type().equals("double[]")) {
					argClasses.add(arggrp.type());
//					if (arggrp.type().contains("CategorizedComponent"))
//						argClasses.add(ComponentContainer.class.getName());
					if (arggrp.type().contains("DynamicSpace")) {
						argClasses.add(SystemComponent.class.getName());
						argClasses.add(LocatedSystemComponent.class.getName());
					}
				}
//		for (TwFunctionArguments arggrp:type.writeableArguments())
//			if (!ValidPropertyTypes.isPrimitiveType(arggrp.type()))
//				if (!arggrp.type().equals("double[]"))
//					argClasses.add(arggrp.type());
//		if (!eventTimerNames.isEmpty())
//			argClasses.add(EventQueue.class.getName()); // not needed!
		for (String s:argClasses)
			generator.setImport(s);
		for (String s:dataClassesToImport)
			generator.setImport(s);

		// inner classes for returned values
		List<String> innerClasses = new LinkedList<>();
		for (String s:innerClassDecl.keySet())
			innerClasses.addAll(innerClassDecl.get(s));
		generator.setRawMethodCode(innerClasses);
		// main method settings
		Collection<MethodGenerator> lmg = generator.getMethods();
		for (MethodGenerator mg : lmg) { // only 1 assumed
			// use a ModelMethodGenerator in order to be able to add arguments
//			ModelMethodGenerator mmg = new ModelMethodGenerator(mg);
			//argument list
			Set<TwFunctionArguments> argSet = new TreeSet<>();
			argSet.addAll(type.readOnlyArguments());
//			argSet.addAll(type.writeableArguments());
			// argument names
			String[] argNames = new String[argSet.size()];
			int i=0;
			for (TwFunctionArguments ag:argSet)
				argNames[i++] = ag.name();
			// argument types (not from ancestor, for consistency here)
			// BUT the ancestor TwFunction MUST have its arguments in the proper order!
			String[] argTypes = new String[argNames.length];
			i=0;
			for (TwFunctionArguments ag:argSet) {
				String[] ss = ag.type().split("\\.");
				argTypes[i++] = ss[ss.length-1];
			}
			// fix generic types and fill mmg with arguments names and types
			for (int j=0; j<argTypes.length; j++) {
//				if (argTypes[j].contains("CategorizedComponent"))
//					argTypes[j] += "<ComponentContainer>";
				if (argTypes[j].contains("DynamicSpace"))
					argTypes[j] += "<SystemComponent,LocatedSystemComponent>";
//				mmg.setArgument(argNames[j], argTypes[j], "");
				mg.setArgumentName(j,argNames[j]);
				mg.setArgumentType(j, argTypes[j]);
			}
			// return type
			mg.setReturnType(type.returnType());
			// preparing call to user model function: initialising read-write data
			for (String k:innerVarInit.keySet())
				for (String s: innerVarInit.get(k))
					mg.setStatement(s);
			// call to user code
			if (type.returnType().equals("void")) {
				mg.setReturnStatement("");
				mg.setStatement(callStatement);
			}
			else
				mg.setReturnStatement("return "+callStatement);
			// getting results from user code
			for (String k:innerVarCopy.keySet())
				for (String s: innerVarCopy.get(k))
					mg.setStatement(s);
			// replace method in class generator
//			generator.setMethod(mg.name(), mg);
//			if (inBodyCode != null)
//				for (String s : inBodyCode)
//					mg.setStatement(s);
		}
//		generator.setRawMethodCode(inClassCode);
		File file = Project.makeFile(LOCALJAVACODE,ctmodel, name + ".java");
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
		String name = generatedClassName.replace(packageName+".", "");
		String path = packagePath+File.separator+name;
		return new File(path+".java");
	}

	// interfacing statements indexed by group (varname)
	// Statements to declare an Inner class to handle returned values for drivers, decorators or lifetime constants
	private Map<String,List<String>> innerClassDecl = new HashMap<>();
	// statements to instantiate and initialise all fields of the previous classes to pass as arguments to the user method
	private Map<String,List<String>> innerVarInit = new HashMap<>();
	// statements to copy returned values back to the proper spots.
	private Map<String,List<String>> innerVarCopy = new HashMap<>();
	private Set<String> dataClassesToImport = new HashSet<>();
	private String callStatement ="";

	// extracts the simple class name from a full class name
	private String classShortName(String classFullName) {
		String result = classFullName;
		if (classFullName.contains(".")) {
			String[] s = classFullName.split("\\.");
			result = s[s.length-1];
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public void setArgumentCalls(ModelGenerator gen) {
		// mapping between inerVar names and rec.name
		Map<String,String> recToInnerVar = new HashMap<>();
		recToInnerVar.put("autoVar",null);
		recToInnerVar.put("constants","Cnt");
		recToInnerVar.put("decorators","Dec");
		recToInnerVar.put("currentState","Drv");
		String classToCall = gen.className();
		String indent = "\t";
		callStatement = classToCall+"."+
				name.substring(0,1).toLowerCase()+
				name.substring(1)+"(\n";
		innerClassDecl.clear();
		innerVarInit.clear();
		innerVarCopy.clear();
		//t, dt
		Set<TwFunctionArguments> intersection =new TreeSet<>(type.readOnlyArguments());
		intersection.retainAll(EnumSet.of(t,dt));
		for (TwFunctionArguments arg:intersection)
			callStatement += indent+indent+indent+ arg.name() + ",\n";
		// arena, lifeCycle, group, space focal, other, otherGroup, otherLifeCycle
		// including return values
//		SortedSet<Category> focalCats = gen.findCategories(spec,focal);
		for (TwFunctionArguments arg:gen.dataStructure(name).keySet()) {
			List<recInfo> comp = gen.dataStructure(name).get(arg);
			// generation of inner classes for return values
//			if (!(arg.equals(arena) & gen.skipArena(focalCats)))
			if (!gen.excludeArgument(arg, (FunctionNode) spec))
				for (recInfo rec:comp)
					if (rec!=null)
						if (rec.klass!=null) // this occurs when a category has no data attached
							if (rec.members!=null) {
								if ((arg==focal)||(arg==other))
									for (String innerVar:type.innerVars() ) {
					// generation of inner classes for return values: unique statements
					if (recToInnerVar.get(rec.name)!=null)
						if ((innerVar.contains(recToInnerVar.get(rec.name))) &&
							(innerVar.contains(arg.name())))  {
								String innerClass = initialUpperCase(innerVar);
								List<String> innerClassDeclaration = new LinkedList<>();
								// e.g.: public class FocalDrv {
								innerClassDeclaration.add(indent+"public class "+innerClass+" {");
								innerClassDecl.put(innerVar,innerClassDeclaration);
								List<String> innerVarInitialisation = new LinkedList<>();
								// e.g.: FocalDrv _focalDrv = new FocalDrv();
								innerVarInitialisation.add(innerClass+" _"+innerVar+" = new "+innerClass+"()");
								innerVarInit.put(innerVar,innerVarInitialisation);
								List<String> innerVarBackCopy = new LinkedList<>();
								innerVarCopy.put(innerVar,innerVarBackCopy);
								// e.g.: Vars focalDrv = (Vars)focal.nextState();
								if ((rec.name.equals("currentState")) & !(type==TwFunctionTypes.SetInitialState))
									innerVarInit.get(innerVar).add(classShortName(rec.klass)+" "+innerVar+" = ("+classShortName(rec.klass)+")"+arg.toString()+".nextState()");
								else
									innerVarInit.get(innerVar).add(classShortName(rec.klass)+" "+innerVar+" = ("+classShortName(rec.klass)+")"+arg.toString()+"."+rec.name+"()");
					}
				}
				// special case for automatic variables
				if (rec.klass.equals(ComponentData.class.getName()) || rec.klass.equals(ContainerData.class.getName()))
					dataClassesToImport.add(rec.klass);
				// generating record members arguments
				for (memberInfo field:rec.members) {
					// generate calls to inner methods to write as arguments to user method
					String callArg = null;
					// ((Vars)focal.currentState()).y()),
					callArg = "((" + classShortName(rec.klass)+ ")"+arg.toString()+"."+rec.name+"())."+field.name+"()";
					if (callArg!=null)
						callStatement += indent+indent+indent+ callArg + ",\n";
					// for returned values, generate inner class and proper calls
					if ((arg==focal)||(arg==other))
						for (String innerVar:type.innerVars() )
							if (recToInnerVar.get(rec.name)!=null)
								if ((innerVar.contains(recToInnerVar.get(rec.name))) &&
									(innerVar.contains(arg.name()))) {
						// imports needed for non primitive field classes
						if (field.fullType!=null)
							dataClassesToImport.add(field.fullType);
						// e.g.: double y;
						innerClassDecl.get(innerVar).add(indent+indent+field.type+" "+field.name+";");
						// e.g.: _focalDrv.y = focalDrv.y();
						innerVarInit.get(innerVar).add("_"+innerVar+"."+field.name+" = "+innerVar+"."+field.name+"()");
						// e.g.: focalDrv.y(_focalDrv.y);
						if (field.isTable)
							; // nothing to do with tables since they can be directly modified.
						else
							innerVarCopy.get(innerVar).add(innerVar+"."+field.name+"(_"+innerVar+"."+field.name+")");
					}
				} // rec.members
				if ((arg==focal)||(arg==other))
					for (String innerVar:type.innerVars() )
						if (recToInnerVar.get(rec.name)!=null)
							if ((innerVar.contains(recToInnerVar.get(rec.name))) &&
								(innerVar.contains(arg.name())))
//						if (innerVar.contains("Drv"))
						// e.g.:_focalDrv // next value
								callStatement += indent+indent+indent+"_"+innerVar+ ",\n";
			}
		}
		// space calls
		if (gen.hasSpace) {
			// read-only argument read from space
			if (type.innerVars().contains("limits"))
				callStatement += indent+indent+indent+ "space.boundingBox(),\n";
			if (type.innerVars().contains("focalLoc")) {
				dataClassesToImport.add(Point.class.getCanonicalName());
				List<String> innerVarInitialisation = new LinkedList<>();
				innerVarInitialisation.add("Point focalLoc = space.locationOf((SystemComponent)focal).asPoint()");
				innerVarInit.put("focalLoc",innerVarInitialisation);
				callStatement += indent+indent+indent+ "focalLoc,\n";
			}
			if (type.innerVars().contains("otherLoc")) {
				dataClassesToImport.add(Point.class.getCanonicalName());
				List<String> innerVarInitialisation = new LinkedList<>();
				innerVarInitialisation.add("Point otherLoc = space.locationOf((SystemComponent)other).asPoint()");
				innerVarInit.put("otherLoc",innerVarInitialisation);
				callStatement += indent+indent+indent+ "space.fixOtherLocation(focalLoc,otherLoc),\n";
			}
			// writeable arguments
//			if (type.writeableArguments().contains(nextFocalLoc))
//				callStatement += indent+indent+indent+ "nextFocalLoc,\n";
//			if (type.writeableArguments().contains(nextOtherLoc))
//				callStatement += indent+indent+indent+ "nextOtherLoc,\n";
		}
//		else
//			callStatement += indent+indent+indent+ "null,\n";

		// random, decide
		for (TwFunctionArguments arg:type.localArguments()) {
			String callArg = null;
			if (arg==random)
				callArg = "rng()";
			else if (arg==decider)
				callArg = "this";
			if (callArg!=null)
				callStatement += indent+indent+indent+ callArg + ",\n";
		}

		// event timer queues, if any
		Collection<TimerNode> queues = (Collection<TimerNode>) get(spec.edges(Direction.IN),
			selectZeroOrMany(hasTheLabel(E_FEDBY.label())),
			edgeListStartNodes());
//		if (type!=SetInitialState)
			if (!queues.isEmpty() ) {
				SortedSet<String> queueNames = new TreeSet<>();
				for (TimerNode q:queues)
					queueNames.add(q.id());
				String callArg = null;
				for (String qn: queueNames) {
					callArg = "getEventQueue(\""+qn+"\")";
					callStatement += indent+indent+indent+ callArg + ",\n";
				}
		}

		for (String innerVar:type.innerVars())
			if (innerClassDecl.get(innerVar)!=null)
				innerClassDecl.get(innerVar).add(indent+"}\n");
		// completion of user method call
		callStatement = callStatement.substring(0, callStatement.length()-2);
		callStatement +=")";
	}

//	// TODO: Must check ll this
//	@Deprecated
//	public void setArgumentCalls(ModelGenerator gen) {
//		String classToCall = gen.className();
//		Map<ArgumentGroups,List<Duple<String,String>>> reqArgs = gen.method(name).callerArguments();
//		String indent = "\t";
//		callStatement = classToCall+"."+
//			Strings.toLowerCase(name.substring(0,1))+
//			name.substring(1)+"(\n";
//		String innerClass = "";
//		String innerVar = "";
//		Map<ConfigurationEdgeLabels, SortedSet<memberInfo>> membersForFocal =
//			gen.getAllMembers(gen.findCategories(spec,true));
//		Map<ConfigurationEdgeLabels, SortedSet<memberInfo>> membersForOther =
//			gen.getAllMembers(gen.findCategories(spec,false));
//		boolean makeInnerClass = false;
//		for (ArgumentGroups ag: reqArgs.keySet()) {
//			makeInnerClass = type.writeableArguments().contains(ag) & !ag.name().contains("Loc");
//			if (makeInnerClass) {
//				innerVar = ag.toString();
//				innerClass = initialUpperCase(innerVar);
////				innerClass = Strings.toUpperCase(innerVar.substring(0,1))+innerVar.substring(1);
//				List<String> innerClassDeclaration = innerClassDecl.get(innerVar);
//				if (innerClassDeclaration==null) {
//					innerClassDeclaration = new LinkedList<>();
//					innerClassDecl.put(innerVar,innerClassDeclaration);
//					innerClassDeclaration.add(indent+"public class "+innerClass+" {");
//				}
//				List<String> innerVarInitialisation = innerVarInit.get(innerVar);
//				if (innerVarInitialisation==null) {
//					innerVarInitialisation = new LinkedList<>();
//					innerVarInit.put(innerVar,innerVarInitialisation);
//					innerVarInitialisation.add(innerClass+" _"+innerVar+" = new "+innerClass+"()");
//				}
//				List<String> innerVarBackCopy = innerVarCopy.get(innerVar);
//				if (innerVarBackCopy==null) {
//					innerVarBackCopy = new LinkedList<>();
//					innerVarCopy.put(innerVar,innerVarBackCopy);
////					innerVarBackCopy.add("// copy back statements");
//				}
//			}
//			for (Duple<String,String> d: reqArgs.get(ag)){
//				String an = d.getFirst(); // argument name
//				String at = d.getSecond(); // argument type
//				String callArg = null;
//				if ((ag==t) || (ag==dt) || (ag==limits) ||
//					(ag==focalLoc) || (ag==otherLoc) || (ag==nextFocalLoc))
//					callArg = ag.name();
//				else if ((ag==ecosystemPar) || (ag==lifeCyclePar))
//					;
//				else if (ag==ecosystemPop)
//					for (PopulationVariables pv: EnumSet.of(TCOUNT,TNADDED,TNREMOVED)) {
////						if (an.equals(validJavaName(wordUpperCaseName("ecosystem."+pv.longName()))))
////							callArg = ag.name()+".populationData()."+pv.getter()+"()";
//					}
//				else if (ag==lifeCyclePop)
//					for (PopulationVariables pv: EnumSet.of(TCOUNT,TNADDED,TNREMOVED)) {
////						if (an.equals(validJavaName(wordUpperCaseName("lifeCycle."+pv.longName()))))
////							callArg = ag.name()+".populationData()."+pv.getter()+"()";
//					}
//				else if ((ag==groupPop))
//					for (PopulationVariables pv: EnumSet.of(COUNT,NADDED,NREMOVED)) {
////						if (an.equals(validJavaName(wordUpperCaseName("group."+pv.longName()))))
////							callArg = ag.name()+".populationData()."+pv.getter()+"()";
//					}
//				else if ((ag==otherGroupPop))
//					for (PopulationVariables pv: EnumSet.of(COUNT,NADDED,NREMOVED)) {
////						if (an.equals(validJavaName(wordUpperCaseName("other.group."+pv.longName()))))
////							callArg = ag.name()+".populationData()."+pv.getter()+"()";
//					}
//				else if ((ag==focalAuto) || (ag==otherAuto))
//					callArg = ag.name()+"."+an+"()*1.0"; // TODO: remove the *1.0 and replace by proper timer conversion
//				else if (ag==random)
//					callArg = "rng()";
//				else if (ag==decider)
//					callArg = "this";
//				else if (type.writeableArguments().contains(ag)) {
//					callArg = defaultPrefix+innerVar;
//					Map<ConfigurationEdgeLabels, SortedSet<memberInfo>> searchList = null;
//					if (ag.name().contains("ocal")) // "F" or "f" are possible
//						searchList = membersForFocal;
//					else if (ag.name().contains("ther")) // "O" or "o" are possible
//						searchList = membersForOther;
//					for (ConfigurationEdgeLabels cel: // not the most efficient way to search, this loop
//						EnumSet.of(E_PARAMETERS,E_LTCONSTANTS,E_DECORATORS,E_DRIVERS)) {
//						String sc = null;
//						for (memberInfo mb:searchList.get(cel)) {
//							if (sc==null) {
//								String lc = gen.containingClass(mb.name);
//								dataClassesToImport.add(lc);
//								sc = lc.split("\\.")[lc.split("\\.").length-1];
//							}
//							innerClassDecl.get(innerVar).add(indent+indent+mb.type+" "+mb.name+";");
//							innerVarInit.get(innerVar).add(defaultPrefix+innerVar+"."+mb.name+" = ((" + sc+ ")"+ag.name()+")."+mb.name+"()");
//							if (!mb.isTable)
//								innerVarCopy.get(innerVar).add("((" + sc+ ")"+ag.name()+")."+mb.name+"("+defaultPrefix+innerVar+"."+mb.name+")");
//						}
//					}
//				}
//				else {
//					String lc = gen.containingClass(an);
//					if (lc!=null) { // fix for a bug ue to entering here with a wrong argument
//						dataClassesToImport.add(lc);
//						String sc = lc.split("\\.")[lc.split("\\.").length-1];
//						callArg = "((" + sc+ ")"+ag.toString()+")."+an+"()";
//					}
//				}
//				if (callArg!=null)
//					callStatement += indent+indent+indent+ callArg + ",\n";
//			}
//			if (makeInnerClass) {
//				innerClassDecl.get(innerVar).add(indent+"}\n");
//				if (innerClassDecl.get(innerVar).size()<=2) { // means the class has no fields
//					innerClassDecl.remove(innerVar);
//					innerVarInit.remove(innerVar);
//					innerVarCopy.remove(innerVar);
//				}
//			}
//		} // for
//		// completion of user method call
//		callStatement = callStatement.substring(0, callStatement.length()-2);
//		callStatement +=")";
//	}

}
