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
import java.util.Collection;
import java.util.List;

import au.edu.anu.rscs.aot.errorMessaging.ErrorList;
import au.edu.anu.rscs.aot.util.FileUtilities;
import au.edu.anu.twcore.data.FieldNode;
import au.edu.anu.twcore.ecosystem.dynamics.ProcessNode;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import au.edu.anu.twcore.ecosystem.structure.SpaceNode;
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
import fr.cnrs.iees.twcore.constants.DataElementType;
import fr.cnrs.iees.twcore.generators.data.TwDataGenerator;
import fr.cnrs.iees.twcore.generators.process.TwFunctionGenerator;
import fr.cnrs.iees.twcore.generators.process.TwInitialiserGenerator;
import fr.ens.biologie.codeGeneration.JavaCompiler;
import fr.cnrs.iees.properties.ExtendablePropertyList;
import fr.cnrs.iees.properties.ResizeablePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;

/**
 * @author Ian Davies
 * @date 27 Dec. 2017
 * 
 *       Refactoring of Jacques code to transfer artifacts between generated
 *       code and a linked user project
 */
public class CodeGenerator {

	private TreeGraph<TreeGraphDataNode, ALEdge> graph = null;

	public CodeGenerator(TreeGraph<TreeGraphDataNode, ALEdge> graph) {
		super();
		this.graph = graph;
	}

	// NOTE: called by ConfigGraph.validateGraph in tw-apps (au.edu.anu.twapps.mm.configGraph)
	@SuppressWarnings("unchecked")
	public boolean generate() {
		UserProjectLink.clearFiles();
		File codeDir = Project.makeFile(ProjectPaths.JAVAPROJECT);
		try {
			if (codeDir.exists())
				FileUtilities.deleteFileTree(codeDir);
		} catch (IOException e1) {
			throw new TwcoreException("Unable to delete [" + codeDir + "]", e1);
		}
		List<TreeGraphDataNode> ecologies = (List<TreeGraphDataNode>) getChildrenLabelled(graph.root(),
				N_SYSTEM.label());
		File ecologyFiles = null;
		for (TreeGraphDataNode ecology : ecologies) {
			ecologyFiles = Project.makeFile(ProjectPaths.LOCALCODE, wordUpperCaseName(ecology.id()));
			ecologyFiles.mkdirs();
			TreeGraphDataNode dynamics = (TreeGraphDataNode) get(ecology.getChildren(),
				selectOne(hasTheLabel(N_DYNAMICS.label())));
			TreeGraphDataNode structure = (TreeGraphDataNode) get(ecology.getChildren(),
				selectOne(hasTheLabel(N_STRUCTURE.label())));
			TreeGraphDataNode arena =  (TreeGraphDataNode) get(structure.getChildren(),
				selectZeroOrOne(hasTheLabel(N_ARENA.label())));
			List<TreeGraphDataNode> spaces = null;
			if (arena!=null)
				spaces = (List<TreeGraphDataNode>) get(arena.getChildren(),
					selectOneOrMany(hasTheLabel(N_SPACE.label())));
			// generate data classes for SystemComponents
			List<TreeGraphDataNode> systems = getChildrenLabelled(structure, N_COMPONENTTYPE.label());
			for (TreeGraphDataNode system : systems) {
				generateDataCode(system, ecology.id(),spaces);
			}
			// generate data classes for LifeCycles, if any
			List<TreeGraphDataNode> lifeCycles = getChildrenLabelled(dynamics, N_LIFECYCLE.label());
			for (TreeGraphDataNode lc : lifeCycles) {
				generateDataCode(lc, ecology.id(),null);
			}
			// generate data classes for Ecosystem, if any
			// caution here: Ecosystem may have no category at all
			Collection<Category> cats = (Collection<Category>) get(ecology.edges(Direction.OUT),
					selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())), edgeListEndNodes());
			if (!cats.isEmpty())
				generateDataCode(ecology, ecology.id(),null);
			// generate TwFunction classes
			// NB expected multiplicities are 1..1 and 1..* but keeping 0..1 and 0..*
			// enables to run tests on incomplete specs
			List<TreeGraphDataNode> timeModels = (List<TreeGraphDataNode>) get(dynamics.getChildren(),
					selectZeroOrOne(hasTheLabel(N_TIMELINE.label())), children(),
					selectZeroOrMany(hasTheLabel(N_TIMEMODEL.label())));
			if (timeModels != null)
				for (TreeGraphDataNode timeModel : timeModels) {
					List<TreeGraphDataNode> processes = getChildrenLabelled(timeModel, N_PROCESS.label());
					for (TreeGraphDataNode process : processes) {
						generateProcessCode(process, ecology.id());
					}
				}
			// generate Initialiser classes
			List<TreeGraphDataNode> initialisers = getChildrenLabelled(dynamics, N_INITIALISER.label());
			for (TreeGraphDataNode initialiser : initialisers)
				generateInitialiserCode(initialiser, ecology.id());
		}

		// compile whole code directory here
		JavaCompiler compiler = new JavaCompiler();
		String result = compiler.compileCode(ecologyFiles);
		if (result != null)
			ErrorList.add(new ModelBuildErrorMsg(ModelBuildErrors.COMPILER_ERROR, ecologyFiles,
					result));
		if (!ErrorList.haveErrors())
			UserProjectLink.pushFiles();
		return !ErrorList.haveErrors();
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
	
	@SuppressWarnings("unchecked")
	private boolean isComponentInSpace(TreeGraphDataNode compType, TreeGraphDataNode space) {
		Collection<Category> superC = Categorized.getSuperCategories(compType);
		for (Category cat:superC) {
			// 1 one of my super categories is involved in a CategoryProcess pointing to space
			List<ProcessNode> procs = (List<ProcessNode>) get(cat.edges(Direction.IN),
				selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())),
				edgeListStartNodes());
			for (ProcessNode pn:procs) {
				TreeGraphDataNode procSpace = (TreeGraphDataNode) get(pn.edges(Direction.OUT),
					selectZeroOrOne(hasTheLabel(E_SPACE.label())),
					endNode());
				if (procSpace!=null)
					if (procSpace.equals(space))
						return true;
			}
			// 2 one of my super categories is involved in a RelationProcess pointing to space
			List<RelationType> rels = (List<RelationType>) get(cat.edges(Direction.IN),
				selectZeroOrMany(orQuery(hasTheLabel(E_TOCATEGORY.label()),hasTheLabel(E_FROMCATEGORY.label()))),
				edgeListStartNodes());
			for (RelationType rel:rels) {
				ProcessNode pn = (ProcessNode) get(rel.edges(Direction.IN),
					selectZeroOrOne(hasTheLabel(E_APPLIESTO.label())),
					startNode());
				if (pn!=null) {
					TreeGraphDataNode procSpace = (TreeGraphDataNode) get(pn.edges(Direction.OUT),
						selectZeroOrOne(hasTheLabel(E_SPACE.label())),
						endNode());
					if (procSpace!=null)
						if (procSpace.equals(space))
							return true;
				}
			}
		}
		return false;
	}

	private void addSpaceCoordinates(TreeGraphDataNode spec,
			TreeGraphDataNode compType, 
			List<TreeGraphDataNode> spaces) {
		for (TreeGraphDataNode space:spaces)
			if (isComponentInSpace(compType,space))
				for (String prop:((SpaceNode)space).coordinateNames()) {
					ExtendablePropertyList fieldprops = new ExtendablePropertyListImpl();
					fieldprops.addProperty("_generated",true);
					fieldprops.addProperty(P_FIELD_TYPE.key(), DataElementType.Double);
					if (prop.equals(P_SPACE_XNAME.key()))
						fieldprops.addProperty(P_FIELD_INTERVAL.key(), 
							space.properties().getPropertyValue(P_SPACE_XLIM.key()));
					else if (prop.equals(P_SPACE_YNAME.key()))
						fieldprops.addProperty(P_FIELD_INTERVAL.key(), 
							space.properties().getPropertyValue(P_SPACE_YLIM.key()));
					else if (prop.equals(P_SPACE_ZNAME.key()))
						fieldprops.addProperty(P_FIELD_INTERVAL.key(), 
							space.properties().getPropertyValue(P_SPACE_ZLIM.key()));
					if (space.properties().hasProperty(P_FIELD_UNITS.key()))
						fieldprops.addProperty(P_FIELD_UNITS.key(), 
							space.properties().getPropertyValue(P_FIELD_UNITS.key()));
					if (space.properties().hasProperty(P_FIELD_PREC.key()))
						fieldprops.addProperty(P_FIELD_PREC.key(), 
							space.properties().getPropertyValue(P_FIELD_PREC.key()));
					// veeeery dirty - but different ComponentType may share the same space
					if (spec.scope().contains(prop))
						spec.scope().removeId(prop); 
					FieldNode fn  = (FieldNode) spec.factory().makeNode(FieldNode.class,prop,fieldprops);
					spec.connectChild(fn);
		}
	}
	
	private void generateDataCode(TreeGraphDataNode system, 
			String modelName, 
			List<TreeGraphDataNode> spaces) {
		// 1. drivers
		TreeGraphDataNode spec = Categorized.buildUniqueDataList(system, E_DRIVERS.label());
		// add space coordinates for every space in which this component type will go (mobile components)
		if (spaces!=null) {
			if (!system.properties().hasProperty(P_COMPONENT_MOBILE.key())) 
				addSpaceCoordinates(spec,system,spaces); // default: assumes mobile
			else if ((boolean)system.properties().getPropertyValue(P_COMPONENT_MOBILE.key())==true)
				addSpaceCoordinates(spec,system,spaces);
		}
		generateDataCode(spec, system, modelName, P_DRIVERCLASS.key());
//		if (spec!=null) {
//			List<TreeNode> gtn = (List<TreeNode>) get(spec.getChildren(),
//				selectZeroOrMany(hasProperty("_generated")));
//			for (TreeNode tn:gtn)
//				tn.disconnect();
//		}
		// 2. parameters
		spec = Categorized.buildUniqueDataList(system, E_PARAMETERS.label());
		generateDataCode(spec, system, modelName, P_PARAMETERCLASS.key());
		// 3. decorators
		spec = Categorized.buildUniqueDataList(system, E_DECORATORS.label());
		// add space coordinates for every space in which this component type will go (immobile components)
		if (spaces!=null) {
			if (system.properties().hasProperty(P_COMPONENT_MOBILE.key())) 
				if ((boolean)system.properties().getPropertyValue(P_COMPONENT_MOBILE.key())==false)
					addSpaceCoordinates(spec,system,spaces);
		}
		generateDataCode(spec, system, modelName, P_DECORATORCLASS.key());
//		if (spec!=null) {
//			List<TreeNode> gtn = (List<TreeNode>) get(spec.getChildren(),
//				selectZeroOrMany(hasProperty("_generated")));
//			for (TreeNode tn:gtn)
//				tn.disconnect();
//		}
	}

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
	}

	private void generateFunctionCode(TreeGraphDataNode function, String modelName) {
		TwFunctionGenerator generator = new TwFunctionGenerator(function.id(), function, modelName);
		generator.generateCode();
		UserProjectLink.addFunctionFile(generator.getFile());
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

	private void generateInitialiserCode(TreeGraphDataNode initialiser, String modelName) {
		TwInitialiserGenerator generator = new TwInitialiserGenerator(initialiser.id(), initialiser, modelName);
		generator.generateCode();
		UserProjectLink.addInitialiserFile(generator.getFile());
		String genClassName = generator.generatedClassName();
		if (initialiser.properties().hasProperty(P_FUNCTIONCLASS.key())) {
			String lastValue = (String) initialiser.properties().getPropertyValue(P_FUNCTIONCLASS.key());
			if (!lastValue.equals(genClassName)) {
				initialiser.properties().setProperty(P_FUNCTIONCLASS.key(), genClassName);
				GraphState.setChanged();
			}
		} else {
			((ResizeablePropertyList) initialiser.properties()).addProperty(P_FUNCTIONCLASS.key(), genClassName);
		}

	}

	@SuppressWarnings("unchecked")
	private static List<TreeGraphDataNode> getChildrenLabelled(TreeGraphDataNode root, String label) {
		return (List<TreeGraphDataNode>) get(root.getChildren(), selectZeroOrMany(hasTheLabel(label)));
	}

}
