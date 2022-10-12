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

import au.edu.anu.rscs.aot.errorMessaging.ErrorMessageManager;
import au.edu.anu.rscs.aot.util.FileUtilities;
import au.edu.anu.twcore.ecosystem.dynamics.ProcessSpaceEdge;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import au.edu.anu.twcore.errorMessaging.ModelBuildErrorMsg;
import au.edu.anu.twcore.errorMessaging.ModelBuildErrors;
import au.edu.anu.twcore.graphState.GraphStateFactory;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.userProject.UserProjectLink;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.twcore.generators.data.TwCategoryEnumGenerator;
import fr.cnrs.iees.twcore.generators.data.TwDataGenerator;
import fr.cnrs.iees.twcore.generators.data.TwDataInterfaceGenerator;
import fr.cnrs.iees.twcore.generators.process.ModelGenerator;
import fr.cnrs.iees.twcore.generators.process.TwFunctionGenerator;
import fr.ens.biologie.generic.utils.Logging;
import fr.cnrs.iees.properties.ResizeablePropertyList;

/**
 * @author Ian Davies - 27 Dec. 2017
 *
 *       Refactoring of Jacques code to transfer artifacts between generated
 *       code and a linked user project
 */
public class CodeGenerator {

	private static Logger log = Logging.getLogger(CodeGenerator.class);
//	static { log.setLevel(Level.SEVERE); }
	private final TreeGraph<TreeGraphDataNode, ALEdge> graph;
	// the generator for the single user model file
	private ModelGenerator modelgen = null;

	public CodeGenerator(TreeGraph<TreeGraphDataNode, ALEdge> graph) {
		super();
		this.graph = graph;
	}

	// NOTE: called by ConfigGraph.validateGraph in tw-apps
	// (au.edu.anu.twapps.mm.configGraph)
	@SuppressWarnings("unchecked")
	public boolean generate() {// see if this helps avoid a thread problem if, in fact there is one?-IDD
		/**
		 * We must not delete user dependencies. These can be anywhere except
		 * 'generated'. So delete generated and the root Java project .class and .java
		 * files only!
		 * 
		 * BUT if there is a linked project we must pull over dependencies!!!
		 * 
		 * <localCodeRoot>/code/system:<name>/generated
		 * 
		 * and
		 * 
		 * <localCodeRoot>/code/system:<name>/<twRootNodeName>
		 */
		File localCodeRoot = Project.makeFile(Project.LOCAL_JAVA);
		List<TreeGraphDataNode> systemNodes = (List<TreeGraphDataNode>) getChildrenLabelled(graph.root(),
				N_SYSTEM.label());
		
		// manage package, directories, files for every system node
		for (TreeGraphDataNode sys : systemNodes) {
			File localGeneratedFiles = Project.makeFile(Project.LOCAL_JAVA_CODE, sys.id(),
					Project.GENERATED);
			if (localGeneratedFiles.exists()) {
				try {
					FileUtilities.deleteFileTree(localGeneratedFiles);
				} catch (IOException e) {
					System.err.println("WARNING: Unable to delete '" + localGeneratedFiles + "'.\nException: " + e);
				}
			}
			if (UserProjectLink.haveUserProject()) {
				File remoteGeneratedFiles = new File(UserProjectLink.srcRoot().getAbsoluteFile() + File.separator
						+ Project.CODE + File.separator + sys.id() + File.separator + Project.GENERATED);
				if (remoteGeneratedFiles.exists()) {
					try {
						FileUtilities.deleteFileTree(remoteGeneratedFiles);
					} catch (IOException e) {
						System.err.println("WARNING: Unable to delete '" + remoteGeneratedFiles + "'.\nException: " + e);
					}
				}
				// import deps to local
				File remoteMainModelClass = new File(UserProjectLink.srcRoot().getAbsoluteFile() + File.separator
						+ Project.CODE + File.separator + sys.id() + File.separator + graph.root().id() + ".java");
				// It won't exist the first time (i.e.upon newly connecting to a project)
				if (remoteMainModelClass.exists())
					UserProjectLink.pullDependentTree(remoteMainModelClass);
			}
		}

		// generate code for every system node
		for (TreeGraphDataNode systemNode : systemNodes) {
			/**
			 * TODO : There may be much to do if we have more than one system.
			 */
			// wordUpperCaseName: aka "camelBack" format used for java package names
			File systemDir = Project.makeFile(Project.LOCAL_JAVA_CODE, wordUpperCaseName(systemNode.id()));
			systemDir.mkdirs();
			TreeGraphDataNode dynamics = (TreeGraphDataNode) get(systemNode.getChildren(),
				selectOne(hasTheLabel(N_DYNAMICS.label())));
			TreeGraphDataNode structure = (TreeGraphDataNode) get(systemNode.getChildren(),
				selectZeroOrOne(hasTheLabel(N_STRUCTURE.label())));

			// generate data interfaces (matching categories)
			// NB predefined categories only have auto variables, not considered here
			Collection<TreeGraphDataNode> categories = (Collection<TreeGraphDataNode>) get(systemNode.subTree(),
					selectZeroOrMany(hasTheLabel(N_CATEGORY.label())));
			for (TreeGraphDataNode cat : categories)
				if (generateDataInterfaceCode(cat, systemNode.id()))
					GraphStateFactory.setChanged();
			
			// WIP - generate enum class for categories
			generateEnumCode(systemNode.id(),structure);

			// generate data classes
			if (structure != null) {
				// for ComponentTypes
				List<TreeGraphDataNode> componentTypes = (List<TreeGraphDataNode>) get(structure.subTree(),
						selectZeroOrMany(hasTheLabel(N_COMPONENTTYPE.label())));
				for (TreeGraphDataNode componentType : componentTypes)
					generateDataCode(componentType, systemNode.id());
				// out of here system has the names of the generated data classes
				// for GroupTypes
				List<TreeGraphDataNode> groupTypes = (List<TreeGraphDataNode>) get(structure.subTree(),
						selectZeroOrMany(hasTheLabel(N_GROUPTYPE.label())));
				for (TreeGraphDataNode groupType : groupTypes)
					generateDataCode(groupType, systemNode.id());
				// for LifeCycleTypes
				List<TreeGraphDataNode> lifeCycles = getChildrenLabelled(dynamics, N_LIFECYCLETYPE.label());
				for (TreeGraphDataNode lifeCycle : lifeCycles) {
					generateDataCode(lifeCycle, systemNode.id());
				}
			}
			// for Arena
			Collection<Category> cats = (Collection<Category>) get(systemNode.edges(Direction.OUT),
					selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())), edgeListEndNodes());
			if (!cats.isEmpty())
				generateDataCode(systemNode, systemNode.id());

			// generate user modifiable model class file
			modelgen = new ModelGenerator(graph.root(), systemNode);
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
			List<TreeGraphDataNode> initables = (List<TreeGraphDataNode>) get(systemNode.subTree(),
					selectZeroOrMany(orQuery(hasTheLabel(N_LIFECYCLETYPE.label()), hasTheLabel(N_GROUPTYPE.label()),
//					hasTheLabel(N_SPACE.label()),
							hasTheLabel(N_COMPONENTTYPE.label()))));
			if (initables == null)
				initables = new ArrayList<TreeGraphDataNode>();
			initables.add(systemNode);
			for (TreeGraphDataNode tgn : initables) {
				List<TreeGraphDataNode> initFuncs = getChildrenLabelled(tgn, N_INITFUNCTION.label());
				// NB there is only one initfunc.
				if (initFuncs != null)
					if (!initFuncs.isEmpty())
						generateFunctionCode(initFuncs.get(0), systemNode.id());
			}
			// write the user code file
			modelgen.generateCode(true);
		}

		// compile code to check it
		String result = compileLocalTree(localCodeRoot);
		if (!result.isBlank())
			ErrorMessageManager
					.dispatch(new ModelBuildErrorMsg(ModelBuildErrors.COMPILER_ERROR, graph, localCodeRoot, result));
		// Push even if broken. Now that dependencies are managed, it's easier to fix in
		// an IDE than through snippets
		// if (!ErrorMessageManager.haveErrors())
		UserProjectLink.pushCompiledTree(localCodeRoot, modelgen.getFile());

		return !ErrorMessageManager.haveErrors();
	}

	/**
	 * Generates a data-interface code for all category records. use this to
	 * typecast ComponentType records to higher-level data
	 * 
	 * @param categories
	 */
	private boolean generateDataInterfaceCode(TreeGraphDataNode category, String modelName) {
		TwDataInterfaceGenerator ig = new TwDataInterfaceGenerator(modelName, category);
		return ig.generateCode(true);
	}

	// Q&D testing
	private String compileLocalTree(File rootDir) {
		List<File> files = new ArrayList<File>();
		String[] ext = { "java" };
		for (File f : FileUtils.listFiles(rootDir, ext, true))
			files.add(f);
		javax.tools.JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(null, Locale.getDefault(), null);
		Iterable<? extends JavaFileObject> compilationUnits = stdFileManager.getJavaFileObjectsFromFiles(files);
		LinkedList<String> options = new LinkedList<String>();
		options.add("-sourcepath");
		options.add(rootDir + File.separator);
		options.add("-classpath");
		options.add(System.getProperty("java.class.path"));
		options.add("-Xlint:unchecked");
		options.add("-Xlint:-processing"); // due to a strange error with DataContainer (usually a warning, actually ??)
		// prevent initial processing of annotations (until run time?)
		// options.add("-nowarn");// TODO: TMP: switching off warnings
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

	private void generateDataCode(TreeGraphDataNode spec, TreeGraphDataNode elementType, String modelName,
			String dataGroup) {
		if (spec != null) {
			// generate the new class
			Collection<Category> cats = Categorized.getSuperCategories(elementType);
			TwDataGenerator gen = new TwDataGenerator(modelName, spec, cats, dataGroup);
			gen.generateCode(true);
			// keep the graph in sync with the newly generated class
			// check the new generated class name replaces the old one in properties
			// driverClass, constantClass, etc.
			if (elementType.properties().hasProperty(dataGroup)) {
				String oldValue = (String) elementType.properties().getPropertyValue(dataGroup);
				String newValue = gen.generatedClassName();
				if (!newValue.equals(oldValue)) {
					elementType.properties().setProperty(dataGroup, newValue);
					GraphStateFactory.setChanged(); // Seems to be secret French business so we won't look
								// rhaa! it's just telling the graph the property value has changed!
				}
			} else {
				// set the properties driverClass, constantClass, etc. if they didnt exist
				((ResizeablePropertyList) elementType.properties()).addProperty(dataGroup, gen.generatedClassName());
				GraphStateFactory.setChanged();
			}
			// if the spec node itsef was generated, delete it
			if (spec.properties().hasProperty("generated"))
				if (spec.properties().getPropertyValue("generated").equals(true)) {
					spec.disconnect();
					graph.removeNode(spec);
				}
		} else if (elementType.properties().hasProperty(dataGroup)) {
			// if the spec was deleted from a previous version, remove the property refering
			// to the former
			// class name in the graph
			((ResizeablePropertyList) elementType.properties()).removeProperty(dataGroup);
			GraphStateFactory.setChanged();
		}
	}

	/**
	 * Called for every ComponentType/GroupType/LifeCycleType found in the structure
	 * sub-tree. Uses system node id as model name.
	 * 
	 * @param elementType the [...]Type node
	 * @param modelName   the system node id, or model name
	 */
	private void generateDataCode(TreeGraphDataNode elementType, String modelName) {
		// 1. automatic variables
		// NO CODE GENERATION for automatic variables!
		// 2. drivers
		TreeGraphDataNode spec = Categorized.buildUniqueDataList(elementType, E_DRIVERS.label(), log);
		generateDataCode(spec, elementType, modelName, P_DRIVERCLASS.key());
		// 3. decorators
		spec = Categorized.buildUniqueDataList(elementType, E_DECORATORS.label(), log);
		generateDataCode(spec, elementType, modelName, P_DECORATORCLASS.key());
		// 4. lifetime constants
		spec = Categorized.buildUniqueDataList(elementType, E_CONSTANTS.label(), log);
		generateDataCode(spec, elementType, modelName, P_CONSTANTCLASS.key());
	}

	// TODO HERE: arguments to user model functions change with the organisation
	// level, ie
	// group, arena, lifecylce, component...
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
			}
		}
	}

	private void generateFunctionCode(TreeGraphDataNode function, String modelName) {
		modelgen.setMethod(function);
		TwFunctionGenerator generator = new TwFunctionGenerator(function.id(), function, modelName);
		generator.setArgumentCalls(modelgen);
		generator.generateCode(true);
		// UserProjectLink.addFunctionFile(generator.getFile());
		String genClassName = generator.generatedClassName();
		if (function.properties().hasProperty(P_FUNCTIONCLASS.key())) {
			String lastValue = (String) function.properties().getPropertyValue(P_FUNCTIONCLASS.key());
			if (!lastValue.equals(genClassName)) {
				function.properties().setProperty(P_FUNCTIONCLASS.key(), genClassName);
				GraphStateFactory.setChanged();
			}
		} else {
			((ResizeablePropertyList) function.properties()).addProperty(P_FUNCTIONCLASS.key(), genClassName);
			GraphStateFactory.setChanged();
		}
	}
	
	private void generateEnumCode(String modelName,
			TreeGraphDataNode spec) {
		TwCategoryEnumGenerator egen = new TwCategoryEnumGenerator(modelName,spec);
		egen.generateCode(true);
	}

	@SuppressWarnings("unchecked")
	private static List<TreeGraphDataNode> getChildrenLabelled(TreeGraphDataNode root, String label) {
		return (List<TreeGraphDataNode>) get(root.getChildren(), selectZeroOrMany(hasTheLabel(label)));
	}

}
