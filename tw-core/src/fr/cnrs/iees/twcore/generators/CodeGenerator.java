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
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.ens.biologie.generic.utils.NameUtils.*;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;

import au.edu.anu.rscs.aot.errorMessaging.ErrorList;
import au.edu.anu.rscs.aot.util.FileUtilities;
import au.edu.anu.twcore.ecosystem.dynamics.ProcessSpaceEdge;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import au.edu.anu.twcore.errorMessaging.ModelBuildErrorMsg;
import au.edu.anu.twcore.errorMessaging.ModelBuildErrors;
import au.edu.anu.twcore.exceptions.TwcoreException;
import au.edu.anu.twcore.graphState.GraphState;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.project.ProjectPaths;
import au.edu.anu.twcore.userProject.UserProjectLink;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.twcore.generators.data.TwDataGenerator;
import fr.cnrs.iees.twcore.generators.process.ModelGenerator;
import fr.cnrs.iees.twcore.generators.process.TwFunctionGenerator;
import fr.ens.biologie.codeGeneration.JavaCompiler;
import fr.cnrs.iees.properties.ResizeablePropertyList;

/**
 * @author Ian Davies
 * @date 27 Dec. 2017
 *
 *       Refactoring of Jacques code to transfer artifacts between generated
 *       code and a linked user project
 */
public class CodeGenerator {

	private static Logger log = Logger.getLogger(CodeGenerator.class.getName());
//	static { log.setLevel(Level.SEVERE); }

	private TreeGraph<TreeGraphDataNode, ALEdge> graph = null;
	// the generator for the single user model file
	private ModelGenerator modelgen = null;

	public CodeGenerator(TreeGraph<TreeGraphDataNode, ALEdge> graph) {
		super();
		this.graph = graph;
	}

	// NOTE: called by ConfigGraph.validateGraph in tw-apps
	// (au.edu.anu.twapps.mm.configGraph)
	@SuppressWarnings("unchecked")
	public boolean generate() {
		//UserProjectLink.clearFiles();

		File localCodeRoot = Project.makeFile(ProjectPaths.LOCALJAVA);
		try {
			if (localCodeRoot.exists())
				FileUtilities.deleteFileTree(localCodeRoot);
		} catch (IOException e1) {
			throw new TwcoreException("Unable to delete [" + localCodeRoot + "]", e1);
		}
		
		List<TreeGraphDataNode> systemNodes = (List<TreeGraphDataNode>) getChildrenLabelled(graph.root(),
				N_SYSTEM.label());

		//File systemDir = null;
		for (TreeGraphDataNode systemNode : systemNodes) {

			/**
			 * TODO :This is crap - there can be many systems but we have one dir - see ref
			 * to 'systemDir' outside this loop below
			 */
			// wordUpperCaseName is "camelBack" format used for java package names
			File systemDir = Project.makeFile(ProjectPaths.LOCALJAVACODE, wordUpperCaseName(systemNode.id()));
			systemDir.mkdirs();

			TreeGraphDataNode dynamics = (TreeGraphDataNode) get(systemNode.getChildren(),
					selectOne(hasTheLabel(N_DYNAMICS.label())));

			TreeGraphDataNode structure = (TreeGraphDataNode) get(systemNode.getChildren(),
					selectOne(hasTheLabel(N_STRUCTURE.label())));
			// generate data classes for SystemComponents
			List<TreeGraphDataNode> componentTypes = getChildrenLabelled(structure, N_COMPONENTTYPE.label());
			for (TreeGraphDataNode componentType : componentTypes) {
				generateDataCode(componentType, systemNode.id());
				// out of here system has the names of the generated data classes

			}
			// generate data classes for LifeCycles, if any
			List<TreeGraphDataNode> lifeCycles = getChildrenLabelled(dynamics, N_LIFECYCLE.label());
			for (TreeGraphDataNode lifeCycle : lifeCycles) {
				generateDataCode(lifeCycle, systemNode.id());
			}
			// generate data classes for Ecosystem, if any
			// caution here: Ecosystem may have no category at all
			Collection<Category> cats = (Collection<Category>) get(systemNode.edges(Direction.OUT),
					selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())), edgeListEndNodes());
			if (!cats.isEmpty())
				generateDataCode(systemNode, systemNode.id());
			// prepare user modifiable model file
			modelgen = new ModelGenerator(graph.root(), systemNode.id());
			// generate TwFunction classes
			// NB expected multiplicities are 1..1 and 1..* but keeping 0..1 and 0..*
			// enables to run tests on incomplete specs
			List<TreeGraphDataNode> timerNodes = (List<TreeGraphDataNode>) get(dynamics.getChildren(),
					selectZeroOrOne(hasTheLabel(N_TIMELINE.label())), children(),
					selectZeroOrMany(hasTheLabel(N_TIMER.label())));
			if (timerNodes != null)
				for (TreeGraphDataNode timerNode : timerNodes) {
					List<TreeGraphDataNode> processes = getChildrenLabelled(timerNode, N_PROCESS.label());
					for (TreeGraphDataNode process : processes) {
						generateProcessCode(process, systemNode.id());
					}
				}
			// initialiser function code here
			List<TreeGraphDataNode> initables = (List<TreeGraphDataNode>) get(systemNode, children(),
					selectOne(hasTheLabel(N_STRUCTURE.label())), children(),
					selectZeroOrMany(orQuery(hasTheLabel(N_LIFECYCLE.label()), hasTheLabel(N_GROUP.label()),
							hasTheLabel(N_SPACE.label()), hasTheLabel(N_COMPONENTTYPE.label()))));
			initables.add(systemNode);
			for (TreeGraphDataNode tgn : initables) {
				List<TreeGraphDataNode> initFuncs = getChildrenLabelled(tgn, N_INITFUNCTION.label());
				// NB there is only one initfunc.
				if (initFuncs != null)
					if (!initFuncs.isEmpty())
						generateFunctionCode(initFuncs.get(0), systemNode.id());
			}
		}
		// write the user code file
		modelgen.generateCode();
		//UserProjectLink.setModelFile(modelgen.getFile());
		
		String result = compileLocalTree(localCodeRoot);
		
		if (!result.isBlank())
			ErrorList.add(new ModelBuildErrorMsg(ModelBuildErrors.COMPILER_ERROR, localCodeRoot, result));
		
		if (!ErrorList.haveErrors()) {
			//UserProjectLink.pushFiles(); 
			UserProjectLink.pushCompiledTree(localCodeRoot,modelgen.getFile());
		}
		return !ErrorList.haveErrors();
	}

	// Q&D testing
	private String compileLocalTree(File rootDir) {
		List<File> files = new ArrayList<File>();
		String [] ext = {"java"};
		for (File f:FileUtils.listFiles(rootDir, ext, true))
			files.add(f);
		javax.tools.JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(null, Locale.getDefault(), null);
		Iterable<? extends JavaFileObject> compilationUnits = stdFileManager.getJavaFileObjectsFromFiles(files);
		LinkedList<String> options = new LinkedList<String>();
		options.add("-sourcepath");
		options.add(rootDir + File.separator);
		options.add("-classpath");
		options.add(System.getProperty("java.class.path"));
		options.add("-Xlint"); // due to a strange error with DataContainer (usually a warning, actually ??)
		StringWriter errors = new StringWriter();
		javax.tools.JavaCompiler.CompilationTask task = compiler.getTask(errors, null, null, options, null,
				compilationUnits);
		task.call();
		String result = errors.toString();
		try {
			stdFileManager.close();
		} catch (IOException e1) {

			e1.printStackTrace();
		}
		return result;
	}


	private void generateDataCode(TreeGraphDataNode spec, TreeGraphDataNode system, String modelName,
			String dataGroup) {
		if (spec != null) {
			TwDataGenerator gen = new TwDataGenerator(modelName, spec);
			gen.generateCode();
			// UserProjectLink.addDataFile(gen.getFile());
			if (system.properties().hasProperty(dataGroup)) {
				String oldValue = (String) system.properties().getPropertyValue(dataGroup);
				String newValue = gen.generatedClassName();
				if (!newValue.equals(oldValue)) {
					system.properties().setProperty(dataGroup, newValue);
					GraphState.setChanged(); // Seems to be secret French business so we won't look
				}
			} else {
				((ResizeablePropertyList) system.properties()).addProperty(dataGroup, gen.generatedClassName());
				GraphState.setChanged();
			}
			if (spec.properties().hasProperty("generated"))
				if (spec.properties().getPropertyValue("generated").equals(true)) {
					spec.disconnect();
					graph.removeNode(spec);
				}
		} else if (system.properties().hasProperty(dataGroup)) {
			((ResizeablePropertyList) system.properties()).removeProperty(dataGroup);
			GraphState.setChanged();
		}
	}

	// kept for recycling wherever it could be useful
//	@SuppressWarnings("unchecked")
//	private boolean isComponentInSpace(TreeGraphDataNode compType, TreeGraphDataNode space) {
//		Collection<Category> superC = Categorized.getSuperCategories(compType);
//		for (Category cat:superC) {
//			// 1 one of my super categories is involved in a CategoryProcess pointing to space
//			List<ProcessNode> procs = (List<ProcessNode>) get(cat.edges(Direction.IN),
//				selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())),
//				edgeListStartNodes());
//			for (ProcessNode pn:procs) {
//				TreeGraphDataNode procSpace = (TreeGraphDataNode) get(pn.edges(Direction.OUT),
//					selectZeroOrOne(hasTheLabel(E_SPACE.label())),
//					endNode());
//				if (procSpace!=null)
//					if (procSpace.equals(space))
//						return true;
//			}
//			// 2 one of my super categories is involved in a RelationProcess pointing to space
//			List<RelationType> rels = (List<RelationType>) get(cat.edges(Direction.IN),
//				selectZeroOrMany(orQuery(hasTheLabel(E_TOCATEGORY.label()),hasTheLabel(E_FROMCATEGORY.label()))),
//				edgeListStartNodes());
//			for (RelationType rel:rels) {
//				ProcessNode pn = (ProcessNode) get(rel.edges(Direction.IN),
//					selectZeroOrOne(hasTheLabel(E_APPLIESTO.label())),
//					startNode());
//				if (pn!=null) {
//					TreeGraphDataNode procSpace = (TreeGraphDataNode) get(pn.edges(Direction.OUT),
//						selectZeroOrOne(hasTheLabel(E_SPACE.label())),
//						endNode());
//					if (procSpace!=null)
//						if (procSpace.equals(space))
//							return true;
//				}
//			}
//		}
//		return false;
//	}

	private void generateDataCode(TreeGraphDataNode system, String modelName) {
		// 0. Automatic variables
		// NO CODE GENERATION for automatic variables!
		// 1. drivers
		TreeGraphDataNode spec = Categorized.buildUniqueDataList(system, E_DRIVERS.label(), log);
		generateDataCode(spec, system, modelName, P_DRIVERCLASS.key());
		// 2. parameters
		spec = Categorized.buildUniqueDataList(system, E_PARAMETERS.label(), log);
		generateDataCode(spec, system, modelName, P_PARAMETERCLASS.key());
		// 3. decorators
		spec = Categorized.buildUniqueDataList(system, E_DECORATORS.label(), log);
		generateDataCode(spec, system, modelName, P_DECORATORCLASS.key());
		// 4. lifetime constants
		spec = Categorized.buildUniqueDataList(system, E_LTCONSTANTS.label(), log);
		generateDataCode(spec, system, modelName, P_LTCONSTANTCLASS.key());
		// add space coordinates for every space in which this component type will go
		// (immobile components)
	}

//	private void generateRelocateFunction(TreeGraphDataNode comp, String space, String modelName) {
//		// NB: we need to flag if and only if a change in this StringTable property has
//		// taken place
//		// as per other generate functions (cf lines with GraphState.setChanged() in
//		// this file.
//		// Unfortunately, to be properly reentrant, this needs to be done carefully
//		// so the flag is set only if there has truly been a change or you will never
//		// be able to save. This will prove difficult because sometimes classes are
//		// added here and, as the graph is edited, things change leaving entries which
//		// are
//		// no longer valid.
//
//		// Possibly, the only sure way of doing this is to copy the
//		// stringTables and clear the property at the start of codeGen and then compare
//		// and set the flag as required at the end of codeGen. Note that codeGen runs
//		// when every the graph changes and is valid so the checking falls through to
//		// code gen. So if you rename nodes all entries need to be check that the
//		// appropriate file exists and if not remove it.
//		String funcname = initialUpperCase(space) + DefaultStrings.defaultPrefix // this to make sure the space name can
//																					// be easily extracted
//				+ initialUpperCase(comp.id()) + initialUpperCase(TwFunctionTypes.Relocate.toString()) + "Function";
//		ExtendablePropertyList props = new ExtendablePropertyListImpl();
//		props.addProperty(P_FUNCTIONTYPE.key(), TwFunctionTypes.Relocate);
//		// QAD: do not use NodeFactory otherwise it's included in the graph
//		// this is a throw-away free-floating Node just for initialisation of code
//		// generator
//		TreeGraphDataNode function = new TreeGraphDataNode(null, props, null);
//		TwFunctionGenerator generator = new TwFunctionGenerator(funcname, function, modelName);
//		generator.generateCode();
//		UserProjectLink.addFunctionFile(generator.getFile());
//		String genClassName = generator.generatedClassName();
//		if (!comp.properties().hasProperty(P_RELOCATEFUNCTION.key())) {
//			StringTable tb = new StringTable(new Dimensioner(1));
//			tb.setWithFlatIndex(genClassName, 0);
//			((ExtendablePropertyList) comp.properties()).addProperty(P_RELOCATEFUNCTION.key(), tb);
//		} else {
//			StringTable prop = (StringTable) comp.properties().getPropertyValue(P_RELOCATEFUNCTION.key());
//			Set<String> set = new HashSet<>();
//			for (int i = 0; i < prop.size(); i++)
//				set.add(prop.getWithFlatIndex(i));
//			set.add(genClassName);
//			StringTable tb = new StringTable(new Dimensioner(set.size()));
//			// CAUTION HERE: no repeats !
//			int i = 0;
//			for (String s : set)
//				tb.setWithFlatIndex(s, i++);
//			((ExtendablePropertyList) comp.properties()).setProperty(P_RELOCATEFUNCTION.key(), tb);
//		}
//	}

	@SuppressWarnings("unchecked")
	private void generateProcessCode(TreeGraphDataNode process, String modelName) {
		// crash here is if 0 functions
		List<TreeGraphDataNode> functions = getChildrenLabelled(process, N_FUNCTION.label());
		for (TreeGraphDataNode function : functions) {
			// 1 generate code for this function
			generateFunctionCode(function, modelName);
			// 2 generate code for its children (consequence) functions
			List<TreeGraphDataNode> consequences = (List<TreeGraphDataNode>) get(function.getChildren(),
					selectZeroOrMany(hasTheLabel(N_FUNCTION.label())));
			for (TreeGraphDataNode csq : consequences)
				generateFunctionCode(csq, modelName);
		}

		// this code is now useless
		// 3 if the process has a space, then a relocatefunction is always generated
		// CAUTION: relocate functions are only attached to category processes, so if
		// the space applies to a relation process, two relocate functions are
		// generated,
		// attached to each category set of the relation.
		// This poses no problem at execution since the relocate functions will only be
		// called at SystemComponent creation, ie after a call to a
		// CreateOtherDecisionFunction.nNew(...)
		ProcessSpaceEdge spaceEdge = (ProcessSpaceEdge) get(process.edges(Direction.OUT),
				selectZeroOrOne(hasTheLabel(E_SPACE.label())));
		if (spaceEdge != null) {
			List<TreeGraphDataNode> ltgn = (List<TreeGraphDataNode>) get(process.edges(Direction.OUT),
					selectOneOrMany(hasTheLabel(E_APPLIESTO.label())), edgeListEndNodes());
			// RelationProcess
			if (ltgn.get(0) instanceof RelationType) {
				Set<TreeGraphDataNode> cptypes = new HashSet<>();
				List<TreeGraphDataNode> fromcats = (List<TreeGraphDataNode>) get(ltgn.get(0).edges(Direction.OUT),
						selectOneOrMany(hasTheLabel(E_FROMCATEGORY.label())), edgeListEndNodes());
				for (TreeGraphDataNode cat : fromcats) {
					List<TreeGraphDataNode> lcomp = (List<TreeGraphDataNode>) get(cat.edges(Direction.IN),
							selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())), edgeListStartNodes(),
							selectZeroOrMany(hasTheLabel(N_COMPONENTTYPE.label())));
					cptypes.addAll(lcomp);
				}
				List<TreeGraphDataNode> tocats = (List<TreeGraphDataNode>) get(ltgn.get(0).edges(Direction.OUT),
						selectOneOrMany(hasTheLabel(E_TOCATEGORY.label())), edgeListEndNodes());
				for (TreeGraphDataNode cat : tocats) {
					List<TreeGraphDataNode> lcomp = (List<TreeGraphDataNode>) get(cat.edges(Direction.IN),
							selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())), edgeListStartNodes(),
							selectZeroOrMany(hasTheLabel(N_COMPONENTTYPE.label())));
					cptypes.addAll(lcomp);
				}
				// generate functions
//				for (TreeGraphDataNode comp : cptypes)
//					generateRelocateFunction(comp, spaceEdge.endNode().id(), modelName);
			}
			// CategoryProcess:
			// a function is generated for every ComponentType depending on the categories
			else if (ltgn.get(0) instanceof Category) {
				// get all component types pointing to all categories of this process
				Set<TreeGraphDataNode> cptypes = new HashSet<>();
				for (TreeGraphDataNode cat : ltgn) {
					List<TreeGraphDataNode> lcomp = (List<TreeGraphDataNode>) get(cat.edges(Direction.IN),
							selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())), edgeListStartNodes(),
							selectZeroOrMany(hasTheLabel(N_COMPONENTTYPE.label())));
					cptypes.addAll(lcomp);
				}
				// generate functions
//				for (TreeGraphDataNode comp : cptypes)
//					generateRelocateFunction(comp, spaceEdge.endNode().id(), modelName);
			}
		}
	}

	private void generateFunctionCode(TreeGraphDataNode function, String modelName) {
		modelgen.setMethod(function);
		TwFunctionGenerator generator = new TwFunctionGenerator(function.id(), function, modelName);
		generator.setArgumentCalls(modelgen);
		generator.generateCode();
		//UserProjectLink.addFunctionFile(generator.getFile());
		String genClassName = generator.generatedClassName();
		if (function.properties().hasProperty(P_FUNCTIONCLASS.key())) {
			String lastValue = (String) function.properties().getPropertyValue(P_FUNCTIONCLASS.key());
			if (!lastValue.equals(genClassName)) {
				function.properties().setProperty(P_FUNCTIONCLASS.key(), genClassName);
				GraphState.setChanged();
			}
		} else {
			((ResizeablePropertyList) function.properties()).addProperty(P_FUNCTIONCLASS.key(), genClassName);
			GraphState.setChanged();
		}
	}

//	private void generateInitialiserCode(TreeGraphDataNode initialiser, String modelName) {
//		TwInitialiserGenerator generator = new TwInitialiserGenerator(initialiser.id(), initialiser, modelName);
//		generator.generateCode();
//		UserProjectLink.addInitialiserFile(generator.getFile());
//		String genClassName = generator.generatedClassName();
//		if (initialiser.properties().hasProperty(P_FUNCTIONCLASS.key())) {
//			String lastValue = (String) initialiser.properties().getPropertyValue(P_FUNCTIONCLASS.key());
//			if (!lastValue.equals(genClassName)) {
//				initialiser.properties().setProperty(P_FUNCTIONCLASS.key(), genClassName);
//				GraphState.setChanged();
//			}
//		} else {
//			((ResizeablePropertyList) initialiser.properties()).addProperty(P_FUNCTIONCLASS.key(), genClassName);
//		}
//
//	}

	@SuppressWarnings("unchecked")
	private static List<TreeGraphDataNode> getChildrenLabelled(TreeGraphDataNode root, String label) {
		return (List<TreeGraphDataNode>) get(root.getChildren(), selectZeroOrMany(hasTheLabel(label)));
	}

}
