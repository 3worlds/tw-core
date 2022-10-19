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

import static fr.cnrs.iees.omhtk.utils.NameUtils.*;
import static fr.cnrs.iees.twcore.generators.TwComments.*;
import static fr.cnrs.iees.omhtk.codeGeneration.CodeGenerationUtils.*;
import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static fr.cnrs.iees.twcore.generators.process.TwFunctionArguments.*;
import java.io.File;
import java.util.*;
import java.util.logging.Logger;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.dynamics.FunctionNode;


import au.edu.anu.twcore.ecosystem.dynamics.TimerNode;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.biology.TwFunctionAdapter;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentData;
import au.edu.anu.twcore.ecosystem.runtime.system.ContainerData;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.project.Project;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;
import fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;
import fr.cnrs.iees.twcore.generators.TwCodeGenerator;
import fr.cnrs.iees.twcore.generators.process.ModelGenerator.memberInfo;
import fr.cnrs.iees.twcore.generators.process.ModelGenerator.recInfo;
import fr.cnrs.iees.omhtk.codeGeneration.ClassGenerator;
import fr.cnrs.iees.omhtk.codeGeneration.MethodGenerator;
import fr.cnrs.iees.omhtk.utils.Logging;

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
	private String modelCodeClassName = null;
//	private List<String> inBodyCode = null;
	private SortedSet<String> eventTimerNames = new TreeSet<>();
	private Set<TreeGraphDataNode> functionFocalCategories = new TreeSet<>();
	private Set<TreeGraphDataNode> functionOtherCategories = new TreeSet<>();
//	private List<String> inClassCode = null;

	@SuppressWarnings("unchecked")
	public TwFunctionGenerator(String className, TreeGraphDataNode spec, String modelName) {
		super(spec);
		name = className;
		// type = (String)spec.getPropertyValue("type");
		type = (TwFunctionTypes) spec.properties().getPropertyValue(P_FUNCTIONTYPE.key());
		model = modelName;
//		packagePath = Project.makeFile(LOCALJAVACODE,validJavaName(wordUpperCaseName(modelName))).getAbsolutePath();
		packagePath = getModelGlueCodeDir(modelName).getAbsolutePath();
		// get the focal categories - spec is a FunctionNode
		TreeGraphDataNode proc = (TreeGraphDataNode) spec.getParent();
		TreeGraphDataNode parentFunc = null;
		if (proc.classId().equals(N_FUNCTION.label())) {
			parentFunc = proc;
			proc = (TreeGraphDataNode) proc.getParent(); // consequence function
		}
		if (proc.classId().equals(N_PROCESS.label())) {
			Collection<TreeGraphDataNode> appliances;
			appliances = (Collection<TreeGraphDataNode>) get(proc.edges(Direction.OUT), 
				selectOneOrMany(hasTheLabel(E_APPLIESTO.label())),
				edgeListEndNodes());
			TreeGraphDataNode first = appliances.iterator().next();
			if (first.classId().equals(N_RELATIONTYPE.label())) {
				// relation process
				appliances = (Collection<TreeGraphDataNode>) get(first.edges(Direction.OUT), 
					selectOneOrMany(hasTheLabel(E_FROMCATEGORY.label())), 
					edgeListEndNodes());
				functionFocalCategories.addAll(appliances);
				appliances = (Collection<TreeGraphDataNode>) get(first.edges(Direction.OUT), 
					selectOneOrMany(hasTheLabel(E_TOCATEGORY.label())), 
					edgeListEndNodes());
				functionOtherCategories.addAll(appliances);
			}
			else {
				// category process
				functionFocalCategories.addAll(appliances);
				// consequence functions
				if (parentFunc!=null) {
					TwFunctionTypes ptype = (TwFunctionTypes) parentFunc.properties().getPropertyValue(P_FUNCTIONTYPE.key());
					if (ptype.equals(TwFunctionTypes.CreateOtherDecision)) {
						Collection<TreeGraphDataNode> products = (Collection<TreeGraphDataNode>) get(parentFunc.edges(Direction.IN),
							selectZeroOrMany(hasTheLabel(E_EFFECTEDBY.label())),
							edgeListStartNodes());
						if (products.isEmpty()) // no life cycle: offspring categories = parent categories
							functionOtherCategories.addAll(functionFocalCategories);
						else // as per life cycle
							for (TreeGraphDataNode prod:products) 
								functionOtherCategories.addAll((Collection<Category>)get(prod.edges(Direction.OUT),
									selectOneOrMany(hasTheLabel(E_TOCATEGORY.label())),
									edgeListEndNodes()));
					}
					if (ptype.equals(TwFunctionTypes.ChangeCategoryDecision)) {
						Collection<TreeGraphDataNode> recruits = (Collection<TreeGraphDataNode>) get(parentFunc.edges(Direction.IN),
							selectOneOrMany(hasTheLabel(E_EFFECTEDBY.label())),
							edgeListStartNodes());
						for (TreeGraphDataNode rec:recruits)
							functionOtherCategories.addAll((Collection<Category>)get(rec.edges(Direction.OUT),
								selectOneOrMany(hasTheLabel(E_TOCATEGORY.label())),
								edgeListEndNodes()));
					}
				}
			}
		}
		else { // init function: proc is an ElementType, ie has belongsto categories
			Collection<TreeGraphDataNode> cats = (Collection<TreeGraphDataNode>) get(proc.edges(Direction.OUT),
				selectOneOrMany(hasTheLabel(E_BELONGSTO.label())),
				edgeListEndNodes());
			functionFocalCategories.addAll(cats);
		}
		// ad superCategories to focal and other category sets
		Collection<Category> foccats = new TreeSet<>();
		for (TreeGraphDataNode cat:functionFocalCategories)
			foccats.add((Category) cat);
		functionFocalCategories.clear();
		functionFocalCategories.addAll(Categorized.getSuperCategories(foccats));
		Collection<Category> othcats = new TreeSet<>();
		for (TreeGraphDataNode cat:functionOtherCategories)
			othcats.add((Category) cat);
		functionOtherCategories.clear();
		functionOtherCategories.addAll(Categorized.getSuperCategories(othcats));
		// snippet code ?
//		if (inBodyCode==null) {
//			inBodyCode = new ArrayList<String>();
//			// TODO: this is useful for debugging only, should be replaced by some
//			// logging in the final version
//			String defLine = "System.out.println(getClass().getSimpleName()+\"\tTime\t\"+t)";
//			inBodyCode.add(defLine);
//		}
		// name of event timer queues fed by this function
		Collection<TimerNode> queues = (Collection<TimerNode>) get(spec.edges(Direction.IN),
			selectZeroOrMany(hasTheLabel(E_FEDBY.label())),
			edgeListStartNodes());
		if (!queues.isEmpty() )
			for (TimerNode q:queues)
				eventTimerNames.add(q.id());
	}

	@Override
	public boolean generateCode(boolean reportErrors) {
		log.info("    generating file " + name + ".java ...");
//		File ctGeneratedCodeDir = getModelCodeDir(model);
		File ctGeneratedCodeDir = getModelGlueCodeDir(model);
		ctGeneratedCodeDir.mkdirs();
		String ctmodel = validJavaName(wordUpperCaseName(model));
//		packageName = ProjectPaths.CODE.replace(File.separator,".")+"."+ctmodel;
		packageName = Project.CODE.replace(File.separator,".")+"."+ctmodel+"."+Project.GENERATED;
		String ancestorClassName = FUNCTION_ROOT_PACKAGE + "." + type.name() + "Function";
		String comment = comment(general, classComment(name), generatedCode(false, model, ""));
		ClassGenerator generator = new ClassGenerator(packageName, comment, name, "public", null, ancestorClassName);

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
		generator.setImport(modelCodeClassName);

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
					argTypes[j] += "<SystemComponent>";
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
		File file = Project.makeFile(Project.LOCAL_JAVA_CODE,ctmodel,Project.GENERATED, name + ".java");
		writeFile(generator, file);
		generatedClassName = packageName + "." + name;
		log.info("  done.");
		return true; 
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
		String classToCall = gen.className();
		modelCodeClassName = gen.generatedClassName();
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
		for (TwFunctionArguments arg:gen.dataStructure(name).keySet()) {
			List<recInfo> comp = gen.dataStructure(name).get(arg);
			// generation of inner classes for return values
			if (!gen.excludeArgument(arg, (FunctionNode) spec))
				for (recInfo rec:comp)
					if (rec!=null)
						if (rec.klass!=null) // this occurs when a category has no data attached
							if (rec.members!=null) {
								if (EnumSet.of(focal,other,group,otherGroup,
									lifeCycle,otherLifeCycle,arena).contains(arg)) {
				List<String> list = type.innerVars().get(arg);
				if (list!=null)
					if (list.contains(rec.name)) {
						// generation of inner classes for return values: unique statements
						if (ModelGenerator.dataGroupPrefixes.get(rec.name)!=null) {
							String innerVar = arg.name()+ ModelGenerator.dataGroupPrefixes.get(rec.name);
							String innerClass = initialUpperCase(innerVar);
							List<String> innerClassDeclaration = new LinkedList<>();
							// workout inner class declaration first line
							// e.g.: "public class FocalDrv {"
							innerClassDeclaration.add(indent+"public class "+innerClass+" {");
							innerClassDecl.put(innerVar,innerClassDeclaration);
							// workout instantiation of inner class as a local variable
							// e.g.: FocalDrv _focalDrv = new FocalDrv();
							List<String> innerVarInitialisation = new LinkedList<>();
							innerVarInitialisation.add(innerClass+" _"+innerVar+" = new "+innerClass+"()");
							innerVarInit.put(innerVar,innerVarInitialisation);
							// workout copy of user-code-modified values back into SystemComponent data structures
							// (NB different treatment for dynamic variables, ie drivers, and others)
							// e.g.: TwData focalDrv = focal.nextState();
							List<String> innerVarBackCopy = new LinkedList<>();
							innerVarCopy.put(innerVar,innerVarBackCopy);
							dataClassesToImport.add(TwData.class.getCanonicalName());
							if (rec.name.equals("drivers")) { 
								if (type==TwFunctionTypes.SetInitialState)
									innerVarInit.get(innerVar).add(TwData.class.getSimpleName()+" "
										+innerVar+" = "+arg.toString()+".currentState()");
								else
									innerVarInit.get(innerVar).add(TwData.class.getSimpleName()+" "
											+innerVar+" = "+arg.toString()+".nextState()");
							}
							else
								innerVarInit.get(innerVar).add(TwData.class.getSimpleName()+" "
									+innerVar+" = "+arg.toString()+"."+rec.name+"()");
						}
					}
				}
				// special case for automatic variables (age, birthdate, population size etc)
				if (rec.klass.equals(ComponentData.class.getName()) || rec.klass.equals(ContainerData.class.getName()))
					dataClassesToImport.add(rec.klass);
				// generating record members arguments
				for (memberInfo field:rec.members) {
					// generate calls to inner methods to write as arguments to user method
					String callArg = null;
					// workout typecasting of SystemComponent data structures to user-generated TwData
					// e.g. ((Vars)focal.currentState()).y()),
					String typeCast = classShortName(rec.klass);
					Collection<TreeGraphDataNode> coll = null;
					if (arg==focal)
						coll = functionFocalCategories;
					else if (arg==other)
						coll = functionOtherCategories;
					if (coll!=null)
						for (TreeGraphDataNode tgncat:coll) {
							Category cat = (Category) tgncat;
							for (ConfigurationEdgeLabels cel:EnumSet.of(E_CONSTANTS,E_DECORATORS,E_DRIVERS)) {								
								if (cat.fields(cel.label()).contains(field.name)) {
									String pname = "";
									switch(cel) {
										case E_CONSTANTS:	pname = P_CONSTANTCLASS.key(); 	break;
										case E_DECORATORS:	pname = P_DECORATORCLASS.key(); break;
										case E_DRIVERS:		pname = P_DRIVERCLASS.key(); 	break;
										default: break;
									}
									if (cat.properties().getPropertyValue(pname)!=null)
										typeCast = (String) cat.properties().getPropertyValue(pname);
								}
							}
					}
					String dataGroup = rec.name;
					if (dataGroup.equals("drivers"))
						dataGroup = "currentState";
					callArg = "((" + typeCast + ")"+arg.toString()+"."+dataGroup+"())."+field.name+"()";
					if (callArg!=null)
						callStatement += indent+indent+indent+ callArg + ",\n";
					// for returned values, generate inner class and proper calls
					if (EnumSet.of(focal,other,group,otherGroup,
						lifeCycle,otherLifeCycle,arena).contains(arg)) {
						List<String> list = type.innerVars().get(arg);
						if (list!=null)
							if (list.contains(rec.name)) {
							if (ModelGenerator.dataGroupPrefixes.get(rec.name)!=null) {
								String innerVar = arg.name()+ ModelGenerator.dataGroupPrefixes.get(rec.name);
								// imports needed for non primitive field classes
								if (field.fullType!=null)
									dataClassesToImport.add(field.fullType);
								// workout inner class field declarations
								// e.g.: double y;
								innerClassDecl.get(innerVar).add(indent+indent+"public "+field.type+" "+field.name+";");
								// workout local variable initialisation from SystemComponent data structures
								// e.g.: _focalDec.y = ((DecVar)focalDec).y();
								innerVarInit.get(innerVar).add("_"+innerVar+"."+field.name
									+" = (("+typeCast+")"+innerVar+")."+field.name+"()");
								// workout back copy of user-code function output into SystemComponent data structures
								// e.g.: ((DrvVar)focalDrv).y(_focalDrv.y);
								if (field.isTable)
									; // nothing to do with tables since they can be directly modified.
								else
									innerVarCopy.get(innerVar).add("(("+typeCast+")"+innerVar+")."
										+field.name+"(_"+innerVar+"."+field.name+")");
							}
						}
					}
				} // rec.members
				if (EnumSet.of(focal,other,group,otherGroup,
					lifeCycle,otherLifeCycle,arena).contains(arg)) {
					List<String> list = type.innerVars().get(arg);
					if (list!=null)
						if (list.contains(rec.name))
							if (ModelGenerator.dataGroupPrefixes.get(rec.name)!=null) {
								String innerVar = arg.name() + 
									ModelGenerator.dataGroupPrefixes.get(rec.name);
							// e.g.:_focalDrv // next value
								callStatement += indent+indent+indent+"_"+innerVar+ ",\n";
						}
				}
			}
		}
		// space calls
		if (gen.hasSpace) {
			// read-only argument read from space
			if (type.innerVars().containsKey(limits))
				callStatement += indent+indent+indent+ "space.boundingBox(),\n";
			if (type.localArguments().contains(searchRadius))
				callStatement += indent+indent+indent+ "process().searchRadius(),\n";
		}

		// random, decide, select, recruit
		for (TwFunctionArguments arg:type.localArguments()) {
			String callArg = null;
			if (arg==random)
				callArg = "rng()";
			else if ((arg==decider) || (arg==selector) || (arg==recruit))
				callArg = "this";
			if (callArg!=null)
				callStatement += indent+indent+indent+ callArg + ",\n";
		}

		// event timer queues, if any
		Collection<TimerNode> queues = (Collection<TimerNode>) get(spec.edges(Direction.IN),
			selectZeroOrMany(hasTheLabel(E_FEDBY.label())),
			edgeListStartNodes());
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
		Map<TwFunctionArguments,List<String>> map = type.innerVars();
		for (TwFunctionArguments arg:map.keySet()) {
			List<String> list = map.get(arg);
			if (list!=null) 
				for (String s:list)
					if (ModelGenerator.dataGroupPrefixes.get(s)!=null) {
						String innerVar = arg.name()+ ModelGenerator.dataGroupPrefixes.get(s);
						if (innerClassDecl.get(innerVar)!=null)
							innerClassDecl.get(innerVar).add(indent+"}\n");
		}
		}
		// completion of user method call
		callStatement = callStatement.substring(0, callStatement.length()-2);
		callStatement +=")";
	}

}
