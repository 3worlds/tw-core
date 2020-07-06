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

import static fr.ens.biologie.codeGeneration.CodeGenerationUtils.*;
import static fr.ens.biologie.generic.utils.NameUtils.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static fr.cnrs.iees.twcore.generators.TwComments.*;
import static fr.cnrs.iees.twcore.generators.process.TwFunctionArguments.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static au.edu.anu.twcore.DefaultStrings.*;
import static fr.cnrs.iees.twcore.constants.TwFunctionTypes.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.twcore.data.FieldNode;
import au.edu.anu.twcore.data.Record;
import au.edu.anu.twcore.data.TableNode;
import au.edu.anu.twcore.ecosystem.dynamics.FunctionNode;
import au.edu.anu.twcore.ecosystem.dynamics.LifeCycle;
import au.edu.anu.twcore.ecosystem.dynamics.ProcessNode;
import au.edu.anu.twcore.ecosystem.dynamics.TimerNode;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.process.AbstractRelationProcess;
import au.edu.anu.twcore.ecosystem.runtime.process.ComponentProcess;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentData;
import au.edu.anu.twcore.ecosystem.runtime.system.ContainerData;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.timer.EventQueue;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.CategorySet;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.project.ProjectPaths;
import fr.cnrs.iees.graph.DataHolder;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;
import fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels;
import fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames;
import fr.cnrs.iees.twcore.constants.TimeUnits;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;
import fr.cnrs.iees.twcore.generators.TwCodeGenerator;
import fr.cnrs.iees.uit.space.Box;
import fr.cnrs.iees.uit.space.Distance;
import fr.cnrs.iees.uit.space.Point;
import fr.ens.biologie.generic.JavaCode;
import fr.ens.biologie.generic.utils.Logging;

/**
 * A class to generate the skeleton of a user-editable model code
 *
 * @author Jacques Gignoux - 2 avr. 2020
 *
 */
public class ModelGenerator extends TwCodeGenerator implements JavaCode {

	private static Logger log = Logging.getLogger(ModelGenerator.class);
	private String className = null;
	private String modelName = null;
	private String generatedClassName = null;
	private String packageName = null;
	private String packagePath;
	// general comment / package comment
	private String packageComment = null;
	// class comment
	private String classComment = null;
	// all the imports
	private Set<String> imports = new HashSet<String>();
	// all the methods to add to this code
	private Map<String, ModelMethodGenerator> methods = new HashMap<>();
	// method scope
	private static String methodScope = "public static";
	// set to make sure there are no two fields with the same name
	private Set<String> replicateNames = new HashSet<>();
	// store all the generated data class matchings to categories
	private Map<Set<Category>, generatedData> dataClassNames = new HashMap<>();
	private Map<String, List<String>> snippets = new HashMap<>();
	// if applies to a relation type, its name
	private String relationType = null;
	// the categories of the arena component
	private Map<TreeGraphDataNode, SortedSet<Category>> elementTypeCategories = new HashMap<>();

	private class generatedData {
		private String autoVars;
		private String drivers;
		private String decorators;
		private String lifetimeConstants;
	}

	/**
	 * Constructor able to manage previously generated code
	 *
	 */
	@SuppressWarnings("unchecked") // graph root, ecology.id()
	public ModelGenerator(TreeGraphDataNode root3w, String modelDir) {
		super(null);
		className = validJavaName(wordUpperCaseName(initialUpperCase(root3w.id())));
		modelName = modelDir;
		packageName = ProjectPaths.CODE.replace(File.separator, ".") + "." + modelDir;
		// package comment - standard
		packageComment = comment(general, license, separatingLine);
		// class comment - with authorship etc.
		generateClassComment(root3w);
		// method comments:
		// working explanations

		/*- NOTE: Need to remove ref any previous user code here. It does not need to be managed like this- IAN*/
		// check if a file was already generated for this model in user project
		// if yes, extract all user code as snippets.
		packagePath = Project.makeFile(LOCALJAVACODE, validJavaName(wordUpperCaseName(modelDir))).getAbsolutePath();
//		String previousModel = UserProjectLink.srcRoot()+File.separator+
//			ProjectPaths.REMOTECODE+File.separator+
//			validJavaName(wordUpperCaseName(modelDir))+File.separator+
//			className+".java";
//		if (Files.exists(Path.of(previousModel))) {
//			File previousFile = new File(previousModel);
//			List<String> lines = new LinkedList<>();
//			try {
//				lines = Files.readAllLines(previousFile.toPath());
//			} catch (IOException e) {
//				log.severe(()->"File "+previousFile+" could not be read - file regenerated instead");
//			}
//			String record = null;
//			for (String line:lines) {
//				if (line.strip().startsWith("import "))
//					imports.add(line.substring(line.indexOf("import")+6,line.indexOf(';')).strip());
////				if (line.contains("END CODE INSERTION ZONE"))
//				if (line.contains(Comments.endCodeInsert))
//					record = null;
//				if (record!=null) {
//					if (snippets.get(record)==null)
//						snippets.put(record,new LinkedList<>());
//					// TODO: this will fail if there are more than one return statement per method
//					if (line.strip().startsWith("return "))
//						snippets.get(record).add(line);
//					else
//						if (line.startsWith("//"))
//							snippets.get(record).add(line);
//						else
//							snippets.get(record).add("//"+line);
//				}
////				if (line.contains("INSERT YOUR CODE BELOW THIS LINE"))
//				if (line.contains(Comments.beginCodeInsert))
//					record = line.substring(line.indexOf("//")+2,line.indexOf('-')).strip();
//			}
////			// debugging
////			for (String s:snippets.keySet()) {
////				System.out.println("Snippet for method'"+s+"': ");
////				for (String ss: snippets.get(s))
////					System.out.println("\t"+ss);
////			}
//		}
//		else {
		imports.add("static java.lang.Math.*");
//		}
		// get all nodes susceptible to require generated data:
		// system/arena, lifecycle, group, component, space
		// NB these nodes may also have setInitialState functions
		/** must eventually deal with the fact that System is [1..*]? -IDD */
		TreeGraphDataNode systemNode = (TreeGraphDataNode) get(root3w, children(),
				selectOne(hasTheLabel(N_SYSTEM.label())));

		List<TreeGraphDataNode> cpt = (List<TreeGraphDataNode>) get(systemNode, children(),
				selectZeroOrOne(hasTheLabel(N_STRUCTURE.label())), // NB -structure is now [0..1] -IDD
				children(), selectZeroOrMany(orQuery(hasTheLabel(N_LIFECYCLE.label()), hasTheLabel(N_GROUP.label()),
						hasTheLabel(N_SPACE.label()), hasTheLabel(N_COMPONENTTYPE.label()))));
		// The above query returns null if there is no structureNode - left tidy up for
		// now -IDD
		if (cpt == null)
			cpt = new ArrayList<TreeGraphDataNode>();
		cpt.add(systemNode);
		for (TreeGraphDataNode tn : cpt) {
			generatedData cl = new generatedData();
			// search for autovar definitions, if any
			List<TreeGraphDataNode> ccats = (List<TreeGraphDataNode>) get(tn.edges(Direction.OUT),
					selectOneOrMany(hasTheLabel(E_BELONGSTO.label())), edgeListEndNodes());
			for (TreeGraphDataNode cc : ccats) {
				TreeGraphDataNode auto = (TreeGraphDataNode) get(cc.edges(Direction.OUT),
						selectZeroOrOne(hasTheLabel(E_AUTOVAR.label())), endNode());
				if (auto != null) {
					if (cc.id().equals(Category.ephemeral))
						cl.autoVars = ComponentData.class.getName();
					if (cc.id().equals(Category.population))
						cl.autoVars = ContainerData.class.getName();
				}
			}
			// get generated life time constant definitions
			if (tn.properties().hasProperty(P_LTCONSTANTCLASS.key()))
				if (tn.properties().getPropertyValue(P_LTCONSTANTCLASS.key()) != null) {
					String s = (String) tn.properties().getPropertyValue(P_LTCONSTANTCLASS.key());
					if (s.isEmpty())
						cl.lifetimeConstants = null;
					else
						cl.lifetimeConstants = s;
				}
			// get generated driver definitions
			if (tn.properties().hasProperty(P_DRIVERCLASS.key()))
				if (tn.properties().getPropertyValue(P_DRIVERCLASS.key()) != null) {
					String s = (String) tn.properties().getPropertyValue(P_DRIVERCLASS.key());
					if (s.isEmpty())
						cl.drivers = null;
					else
						cl.drivers = s;
				}
			// get generated decorator definitions
			if (tn.properties().hasProperty(P_DECORATORCLASS.key()))
				if (tn.properties().getPropertyValue(P_DECORATORCLASS.key()) != null) {
					String s = (String) tn.properties().getPropertyValue(P_DECORATORCLASS.key());
					if (s.isEmpty())
						cl.decorators = null;
					else
						cl.decorators = s;
				}
			// this because ComponentType is unsealed at that time
			SortedSet<Category> categories = new TreeSet<>(); // caution: sorted set !
			Collection<Category> nl = (Collection<Category>) get(tn.edges(Direction.OUT),
					selectOneOrMany(hasTheLabel(E_BELONGSTO.label())), edgeListEndNodes());
			categories.addAll(((Categorized<SystemComponent>) tn).getSuperCategories(nl));
			dataClassNames.put(categories, cl);
			elementTypeCategories.put(tn, categories);
		}
	}

	private void generateClassComment(TreeGraphDataNode root3w) {
		StringBuilder sb = new StringBuilder();
		String version = "<em>NA</em>";
		if (root3w.properties().hasProperty(P_MODEL_VERSION.key()))
			version = root3w.properties().getPropertyValue(P_MODEL_VERSION.key()).toString();
		sb.append("<h2>Model-specific code for model <em>").append(className).append("</em></h2>\n")
				.append("<p>version ").append(version).append(' ').append(dashSpacer).append(' ')
				.append(new Date().toString()).append("</p>\n");
		if (root3w.properties().hasProperty(P_MODEL_AUTHORS.key())) {
			StringTable auths = (StringTable) root3w.properties().getPropertyValue(P_MODEL_AUTHORS.key());
			if (auths.size() == 1)
				sb.append("\n<dl><dt>Author: </dt><dd>\n");
			else
				sb.append("\n<dl><dt>Authors: </dt><dd>\n");
			for (int i = 0; i < auths.size(); i++) {
				sb.append(auths.getWithFlatIndex(i)).append("<br/>\n");
				if (i > 0)
					sb.append("         ");
			}
			sb.append("</dd></dl>\n");
		} else {
			sb.append("\n<p><strong>Authors: </strong>\n");
			sb.append("&lt;Use the <em>").append(P_MODEL_AUTHORS.key()).append("</em> property of the <em>")
					.append(N_ROOT.label()).append("</em> node to display author names here&gt;");
			sb.append("</p>");
		}
		sb.append("\n");
		if (root3w.properties().hasProperty(P_MODEL_CONTACTS.key())) {
			StringTable contacts = (StringTable) root3w.properties().getPropertyValue(P_MODEL_CONTACTS.key());
			if (contacts.size() == 1)
				sb.append("\n<dl><dt>Contact: </dt><dd>\n");
			else
				sb.append("\n<dl><dt>Contacts: </dt><dd>\n");
			for (int i = 0; i < contacts.size(); i++) {
				sb.append(contacts.getWithFlatIndex(i)).append("<br/>\n");
				if (i > 0)
					sb.append("         ");
			}
			sb.append("</dd></dl>\n");
		} else {
			sb.append("\n<p><strong>Contacts: </strong>\n");
			sb.append("&lt;Use the <em>").append(P_MODEL_CONTACTS.key()).append("</em> property of the <em>")
					.append(N_ROOT.label()).append("</em> node to display author contacts here&gt;");
			sb.append("</p>");
		}
		sb.append("\n");
		if (root3w.properties().hasProperty(P_MODEL_CITATIONS.key())) {
			StringTable refs = (StringTable) root3w.properties().getPropertyValue(P_MODEL_CITATIONS.key());
			if (refs.size() == 1) {
				sb.append("\n<dl><dt>Reference publication: </dt><dd>\n");
				sb.append(refs.getWithFlatIndex(0)).append("</dd></dl>");
			} else {
				sb.append("\n<p><strong>Reference publications: </strong></p>\n");
				sb.append("<ol>");
				for (int i = 0; i < refs.size(); i++)
					sb.append("<li>").append(refs.getWithFlatIndex(i)).append("</li>\n");
				sb.append("</ol>");
			}
		} else {
			sb.append("\n<p><strong>Reference publications: </strong></p>\n");
			sb.append("&lt;Use the <em>").append(P_MODEL_CITATIONS.key()).append("</em> property of the <em>")
					.append(N_ROOT.label()).append("</em> node to display model reference publication(s) here&gt;");
		}
		sb.append("\n\n");
		sb.append("<h3>Instructions to model developers:</h3>\n");
		sb.append(
				"<ol><li>Non 3worlds-generated extra methods should be placed in other files linked\n to the present file through imports.</li>\n");
		sb.append(
				"<li><strong>Do not</strong> alter the code insertion markers. They are used to avoid \n losing your code when managing this file.</li>\n");
		sb.append(
				"<li>For convenience, all the static methods of the {@link Math} and\n {@link Distance} classes are directly accessible here</li>\n");
		sb.append(
				"<li>The particular random number stream attached to each {@link TwFunction} is \npassed as the <em>random</em> argument.</li>\n");
		sb.append(
				"<li>For all <em>Decision-</em> functions, a <em>decider</em> argument is provided \nto help make decisions out of probabilities</li></ol>\n");

		// String cs = WordUtils.wrap(sb.toString(), 80,"\n",false);
		classComment = javaDocComment("", sb.toString().split("\\n"));
	}

	// returns the generated data class that contains a given field name
	// this works because fields and tables all have unique names
	public String containingClass(String field) {
		for (Set<Category> set : dataClassNames.keySet()) {
			for (Category c : set) {
				TreeGraphDataNode rec = (TreeGraphDataNode) get(c.edges(Direction.OUT),
						selectZeroOrOne(hasTheLabel(E_DRIVERS.label())), endNode());
				if (rec != null) {
					for (TreeNode t : rec.getChildren())
						// this works for tables and fields
						if (field.equals(t.id()))
							return dataClassNames.get(set).drivers;
				}
				rec = (TreeGraphDataNode) get(c.edges(Direction.OUT),
						selectZeroOrOne(hasTheLabel(E_DECORATORS.label())), endNode());
				if (rec != null) {
					for (TreeNode t : rec.getChildren())
						if (field.equals(t.id()))
							return dataClassNames.get(set).decorators;
				}
				rec = (TreeGraphDataNode) get(c.edges(Direction.OUT),
						selectZeroOrOne(hasTheLabel(E_LTCONSTANTS.label())), endNode());
				if (rec != null) {
					for (TreeNode t : rec.getChildren())
						if (field.equals(t.id()))
							return dataClassNames.get(set).lifetimeConstants;
				}
				rec = (TreeGraphDataNode) get(c.edges(Direction.OUT), selectZeroOrOne(hasTheLabel(E_AUTOVAR.label())),
						endNode());
				if (rec != null) {
					for (TreeNode t : rec.getChildren())
						if (field.equals(t.id()))
							return dataClassNames.get(set).autoVars;
				}
			}
		}
		return null;
	}

	public ModelMethodGenerator method(String name) {
		return methods.get(name);
	}

	// prepare comments to explain arguments to end user
	@SuppressWarnings("unchecked")
	private String argComment(TreeGraphDataNode f, ConfigurationPropertyNames descro, ConfigurationPropertyNames units,
			ConfigurationPropertyNames prec, ConfigurationPropertyNames interval, ConfigurationPropertyNames range) {
		StringBuilder comment = new StringBuilder();
		if (f.properties().hasProperty(descro.key()))
			if (f.properties().getPropertyValue(descro.key()) != null)
				comment.append(f.properties().getPropertyValue(descro.key()));
			else
				comment.append(f.id());
		else
			comment.append(f.id());
		// get the dimensions for tables
		if (f instanceof TableNode) {
			List<Edge> edims = (List<Edge>) get(f.edges(Direction.OUT),
					selectOneOrMany(hasTheLabel(E_SIZEDBY.label())));
			Map<Integer, TreeGraphDataNode> dims = new TreeMap<>();
			for (Edge e : edims)
				if (e instanceof DataHolder)
					if (((DataHolder) e).properties().hasProperty(P_DIMENSIONER_RANK.key()))
						dims.put((Integer) ((DataHolder) e).properties().getPropertyValue(P_DIMENSIONER_RANK.key()),
								(TreeGraphDataNode) e.endNode());
			String s = "";
			for (TreeGraphDataNode dim : dims.values())
				s += dim.properties().getPropertyValue(P_DIMENSIONER_SIZE.key()) + ",";
			comment.append(" dim = [").append(s.substring(0, s.length() - 1)).append("]");
		}
		if (f.properties().hasProperty(units.key()))
			if (f.properties().getPropertyValue(units.key()) != null)
				if (!f.properties().getPropertyValue(units.key()).toString().isEmpty())
					comment.append(" (").append(f.properties().getPropertyValue(units.key())).append(")");
		if (f.properties().hasProperty(prec.key()))
			if (f.properties().getPropertyValue(prec.key()) != null)
				comment.append(" ± ").append(f.properties().getPropertyValue(prec.key()));
		if (f.properties().hasProperty(interval.key()))
			if (f.properties().getPropertyValue(interval.key()) != null)
				comment.append(" ϵ").append(f.properties().getPropertyValue(interval.key()));
		if (f.properties().hasProperty(range.key()))
			if (f.properties().getPropertyValue(range.key()) != null)
				comment.append(" ϵ[").append(f.properties().getPropertyValue(range.key())).append(']');
		return comment.toString();
	}

	private String simpleType(String className) {
		String[] ss = className.split("\\.");
		return ss[ss.length - 1];
	}

	// helper classes to gather all the required information
	class memberInfo implements Comparable<memberInfo> {
		String name = null;
		String type;
		String fullType;
		String comment;
		boolean isTable;

		@Override
		public int compareTo(memberInfo m) {
			return name.compareTo(m.name);
		}

		@Override
		public String toString() {
			String s = "[" + name + ":" + type + "(" + fullType + ")]";
			return s;
		}
	}

	class recInfo {
		String name = null;
		String klass;
		Collection<memberInfo> members = null;

		@Override
		public String toString() {
			String s = "[" + name + ":" + klass + " [ ";
			for (memberInfo f : members)
				s += f.toString() + " ";
			s += "]]";
			return s;
		}
	}

	// access stings to dataGroups of CategorizedComponents (excl. nextState)
	static EnumMap<ConfigurationEdgeLabels, String> dataAccessors = new EnumMap<>(ConfigurationEdgeLabels.class);
	static {
		dataAccessors.put(E_AUTOVAR, "autoVar");
		dataAccessors.put(E_LTCONSTANTS, "constants");
		dataAccessors.put(E_DECORATORS, "decorators");
		dataAccessors.put(E_DRIVERS, "currentState");// nextState ?
	}
	// A Map of all data structure information required by all methods
	private Map<String, EnumMap<TwFunctionArguments, List<recInfo>>> dataStructures = new HashMap<>();

	// gets all the fields and tables of a give category set. Returns them in always
	// the same order
	protected Map<ConfigurationEdgeLabels, SortedSet<memberInfo>> getAllMembers(Collection<Category> cats) {
		Map<ConfigurationEdgeLabels, SortedSet<memberInfo>> result = new HashMap<>();
		for (Category cat : cats)
			for (ConfigurationEdgeLabels cel : EnumSet.of(E_AUTOVAR, E_LTCONSTANTS, E_DECORATORS, E_DRIVERS)) {
				Record rec = (Record) get(cat.edges(Direction.OUT), selectZeroOrOne(hasTheLabel(cel.label())),
						endNode());
				if (rec != null) {
					for (TreeNode tn : rec.getChildren()) {
						memberInfo mb = new memberInfo();
						if (tn instanceof FieldNode) {
							FieldNode f = (FieldNode) tn;
							mb.isTable = false;
							mb.name = validJavaName(wordUpperCaseName(f.id()));
							mb.comment = argComment(f, P_FIELD_DESCRIPTION, P_FIELD_UNITS, P_FIELD_PREC,
									P_FIELD_INTERVAL, P_FIELD_RANGE);
							mb.type = f.properties().getPropertyValue(P_FIELD_TYPE.key()).toString();
							mb.fullType = ValidPropertyTypes.getJavaClassName(mb.type);
							if (ValidPropertyTypes.isPrimitiveType(mb.type)) {
								mb.fullType = null;
								if (mb.type.equals("Integer"))
									mb.type = "int";
								else {
									log.info("set type of " + mb);
									mb.type = mb.type.substring(0, 1).toLowerCase() + mb.type.substring(1);
								}
							}
						} else if (tn instanceof TableNode) {
							TableNode t = (TableNode) tn;
							mb.isTable = true;
							mb.name = validJavaName(wordUpperCaseName(t.id()));
							mb.comment = argComment(t, P_TABLE_DESCRIPTION, P_TABLE_UNITS, P_TABLE_PREC,
									P_TABLE_INTERVAL, P_TABLE_RANGE);
							// Add table dimensions to comment
							// table of primitive types
							if (t.properties().hasProperty(P_DATAELEMENTTYPE.key())) {
								mb.type = t.properties().getPropertyValue(P_DATAELEMENTTYPE.key()).toString();
								if (ValidPropertyTypes.isPrimitiveType(mb.type)) {
									if (mb.type.equals("Integer"))
										mb.type = "IntTable";
									else
										mb.type += "Table";
								}
								mb.fullType = ValidPropertyTypes.getJavaClassName(mb.type);
							}
							// table of user-defined records
							else {
								mb.type = validJavaName(initialUpperCase(wordUpperCaseName(t.id())));
								mb.fullType = null;
							}

						}
						if (result.get(cel) == null)
							result.put(cel, new TreeSet<>());
						result.get(cel).add(mb);
					}
				} else if (result.get(cel) == null) // in case a category has no fields, dont erase those of another one
					result.put(cel, new TreeSet<>()); // empty
			}
		return result;
	}

	@SuppressWarnings("unchecked")
	protected SortedSet<Category> findCategories(TreeGraphDataNode function, TwFunctionArguments arg) {
		TwFunctionTypes ftype = (TwFunctionTypes) function.properties().getPropertyValue(P_FUNCTIONTYPE.key());
		TreeGraphDataNode fp = (TreeGraphDataNode) function.getParent();
		SortedSet<Category> cats = new TreeSet<>();
		// 1 general case: function parent is a process
		if (fp instanceof ProcessNode) {
			// get arena data types
			if (arg == arena) { // timeModel timeLine dynamics system
				TreeGraphDataNode systemNode = (TreeGraphDataNode) fp.getParent().getParent().getParent().getParent();
				cats.addAll((Collection<Category>) get(systemNode.edges(Direction.OUT),
						selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())), edgeListEndNodes()));
				return cats;
			}
			TreeGraphDataNode pn = fp;
			if ((arg == lifeCycle) || (arg == space) || (arg == group)) {
				// TODO: get the component type matching process categories, then get the
				// grouptype
				// and lifecycle type to get their categories
				System.out.println("TODO: missing code here! [ModelGenerator.findCategories(...)]");
			}
			if (arg == focal)
				// component Process
				for (TwFunctionTypes tft : ComponentProcess.compatibleFunctionTypes)
					if (tft.equals(ftype)) {
						cats.addAll((Collection<Category>) get(pn.edges(Direction.OUT),
								selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())), edgeListEndNodes()));
						return cats;
					}
			// relation process
			for (TwFunctionTypes tft : AbstractRelationProcess.compatibleFunctionTypes)
				if (tft.equals(ftype)) {
					RelationType relnode = (RelationType) get(pn.edges(Direction.OUT),
							selectZeroOrOne(hasTheLabel(E_APPLIESTO.label())), endNode());
					relationType = relnode.id();
					if ((arg == focal)) {
						cats.addAll((Collection<Category>) get(relnode.edges(Direction.OUT),
								selectOneOrMany(hasTheLabel(E_FROMCATEGORY.label())), edgeListEndNodes()));
						return cats;
					} else if (arg == other) {
						cats.addAll((Collection<Category>) get(relnode.edges(Direction.OUT),
								selectOneOrMany(hasTheLabel(E_TOCATEGORY.label())), edgeListEndNodes()));
						return cats;
					} else if ((arg == otherLifeCycle) || (arg == otherGroup)) {
						// TODO
					}
				}
		}
		// 2 parent is a functionNode, means we are dealing with a consequence
		else if (fp instanceof FunctionNode) {

		}
		// 3 parent is not a ProcessNode nor a FunctionNode, so it must be an
		// ElementType descendant
		// and in this case there is no relation function
		else if (arg == focal) {
			cats.addAll((Collection<Category>) get(fp.edges(Direction.OUT),
					selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())), edgeListEndNodes()));
			return cats;
		}
		return cats;
	}

//	@SuppressWarnings("unchecked")
//	@Deprecated
//	protected SortedSet<Category> findCategories(TreeGraphDataNode function, boolean focal) {
//		TwFunctionTypes ftype = (TwFunctionTypes) function.properties().getPropertyValue(P_FUNCTIONTYPE.key());
//		TreeNode fp = function.getParent();
//		SortedSet<Category> cats = new TreeSet<>();
//		// function parent is a process
//		if (fp instanceof ProcessNode) {
//			ProcessNode pn = (ProcessNode) fp;
//			if (focal)
//				for (TwFunctionTypes tft:ComponentProcess.compatibleFunctionTypes)
//					if (tft.equals(ftype)) {
//					cats.addAll((Collection<Category>) get(pn.edges(Direction.OUT),
//						selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())),
//						edgeListEndNodes()));
//					return cats;
//			}
//			for (TwFunctionTypes tft:AbstractRelationProcess.compatibleFunctionTypes)
//				if (tft.equals(ftype)) {
//					RelationType relnode = (RelationType) get(pn.edges(Direction.OUT),
//						selectZeroOrOne(hasTheLabel(E_APPLIESTO.label())),
//						endNode());
//					relationType = relnode.id();
//					if (focal) {
//						cats.addAll((Collection<Category>) get(relnode.edges(Direction.OUT),
//							selectOneOrMany(hasTheLabel(E_FROMCATEGORY.label())),
//							edgeListEndNodes()));
//						return cats;
//					}
//					else {
//						cats.addAll((Collection<Category>) get(relnode.edges(Direction.OUT),
//							selectOneOrMany(hasTheLabel(E_TOCATEGORY.label())),
//							edgeListEndNodes()));
//						return cats;
//					}
//			}
//		}
//		// function parent is a function
//		else {
//			// TODO: categories of consequence functions ??? good luck!
//			// if createOther --> lifeCycle
//			// if deleteOther --> returnTo relation
//			// if changeCategory --> life cycle
//			// there may be more than one set in each case --> multiple method generation
//		}
//		return cats;
//	}

	public String simpleCatSignature(Set<Category> set) {
		String result = " ";
		for (Category c : set)
			result += c.id() + " ";
		return result;
	}

	public boolean hasLifeCycle(Set<Category> cats) {
		for (Category cat : cats) {
			CategorySet cs = (CategorySet) cat.getParent();
			LifeCycle lc = (LifeCycle) get(cs.edges(Direction.IN), selectZeroOrOne(hasTheLabel(N_LIFECYCLE.label())),
					startNode());
			if (lc != null)
				return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public String timerComment(TreeGraphDataNode function, TwFunctionTypes ftype) {
		// functions independent from timers
		if (EnumSet.of(SetInitialState, SetOtherInitialState).contains(ftype)) {
			return "<p>- called once for every component, at creation time</p>\n";
		} else {
			// timer followed by the function
			ProcessNode proc = null;
			if (function.getParent() instanceof ProcessNode)
				proc = (ProcessNode) function.getParent();
			else
				proc = (ProcessNode) function.getParent().getParent();
			TimerNode tm = (TimerNode) proc.getParent();
			StringBuilder sb = new StringBuilder();
			String subc = tm.properties().getPropertyValue(P_TIMEMODEL_SUBCLASS.key()).toString();
			sb.append("<p>- follows timer <em>").append(tm.id()).append("</em> of type {@link ")
					.append(subc.split("\\.")[subc.split("\\.").length - 1]).append("}");
			if (tm.properties().hasProperty(P_TIMEMODEL_NTU.key())) {
				sb.append(", with time unit = ").append(tm.properties().getPropertyValue(P_TIMEMODEL_NTU.key()))
						.append(" ")
						.append(((TimeUnits) tm.properties().getPropertyValue(P_TIMEMODEL_TU.key())).abbreviation());
			} else { // use time line shortest time unit
				sb.append(", with time unit = 1 ").append(((TimeUnits) ((TreeGraphDataNode) tm.getParent()).properties()
						.getPropertyValue(P_TIMELINE_SHORTTU.key())).abbreviation());
			}
			sb.append("</p>\n");
			// if applicable, event timers fed by the function
			Collection<TimerNode> queues = (Collection<TimerNode>) get(function.edges(Direction.IN),
					selectZeroOrMany(hasTheLabel(E_FEDBY.label())), edgeListStartNodes());
			if (!queues.isEmpty()) {
				if (queues.size() == 1)
					sb.append("<p>- can post new time events to event timer <em>");
				else
					sb.append("<p>- can post new time events to event timers <em>");
				for (TimerNode q : queues)
					sb.append(q.id()).append(", ");
				sb.delete(sb.length() - 2, sb.length());
				sb.append("</em></p>\n");
			}
			return sb.toString();
		}
	}

	@SuppressWarnings("unchecked")
	private String dependComment(TreeGraphDataNode function, TwFunctionTypes ftype) {
		if (EnumSet.of(SetInitialState, SetOtherInitialState).contains(ftype)) {
			return null; // dependsOn only applies to processes
		} else {
			StringBuilder sb = new StringBuilder();
			// process function
			if (function.getParent() instanceof ProcessNode) {
				ProcessNode proc = (ProcessNode) function.getParent();
				List<TreeGraphDataNode> deps = (List<TreeGraphDataNode>) get(proc.edges(Direction.OUT),
						selectZeroOrMany(hasTheLabel(E_DEPENDSON.label())), edgeListEndNodes());
				Set<TreeGraphDataNode> depf = new HashSet<>();
				if (!deps.isEmpty())
					for (TreeGraphDataNode dp : deps)
						depf.addAll((Collection<? extends TreeGraphDataNode>) get(dp.getChildren(),
								selectZeroOrMany(hasTheLabel(N_FUNCTION.label()))));
				if (!depf.isEmpty()) {
					sb.append("<p>- called after function");
					if (depf.size() > 1)
						sb.append("s");
					sb.append(" <em>");
					for (TreeGraphDataNode fnk : depf)
						// sb.append(Strings.toLowerCase(fnk.id().substring(0,1)) +
						// fnk.id().substring(1)).append("(...), ");
						sb.append(fnk.id().substring(0, 1).toLowerCase() + fnk.id().substring(1))
								.append("(...), ");
					sb.delete(sb.length() - 2, sb.length());
					sb.append("</em>.</p>\n");
				}
				deps = (List<TreeGraphDataNode>) get(proc.edges(Direction.IN),
						selectZeroOrMany(hasTheLabel(E_DEPENDSON.label())), edgeListStartNodes());
				depf.clear();
				if (!deps.isEmpty())
					for (TreeGraphDataNode dp : deps)
						depf.addAll((Collection<? extends TreeGraphDataNode>) get(dp.getChildren(),
								selectZeroOrMany(hasTheLabel(N_FUNCTION.label()))));
				if (!depf.isEmpty()) {
					sb.append("<p>- called before function");
					if (depf.size() > 1)
						sb.append("s");
					sb.append(" <em>");
					for (TreeGraphDataNode fnk : depf)
//						sb.append(Strings.toLowerCase(fnk.id().substring(0, 1)) + fnk.id().substring(1))
//						.append("(...), ");
						sb.append(fnk.id().substring(0, 1).toLowerCase() + fnk.id().substring(1))
								.append("(...), ");
					sb.delete(sb.length() - 2, sb.length());
					sb.append("</em>.</p>\n");
				}
			}
			// consequence function
			else {
				FunctionNode func = (FunctionNode) function.getParent();
				TwFunctionTypes functype = (TwFunctionTypes) func.properties().getPropertyValue(P_FUNCTIONTYPE.key());
				if (EnumSet.of(CreateOtherDecision).contains(functype))
					sb.append("<p>- called just after <em>").append(func.id())
							.append("</em> when it results in new component creation.</p>\n");
				if (EnumSet.of(DeleteDecision, DeleteOtherDecision, ChangeCategoryDecision, ChangeOtherCategoryDecision)
						.contains(functype))
					sb.append("<p>- called just after <em>").append(func.id())
							.append("</em> when it returns <strong>true</strong>.</p>\n");
			}
			return sb.toString();
		}
	}

	// match between function names and spec nodes
	private Map<String, TreeGraphDataNode> functions = new HashMap<>();

	public EnumMap<TwFunctionArguments, List<recInfo>> dataStructure(String method) {
		if (dataStructures.get(method) == null) {
			TwFunctionTypes ftype = (TwFunctionTypes) functions.get(method).properties()
					.getPropertyValue(P_FUNCTIONTYPE.key());
			EnumMap<TwFunctionArguments, List<recInfo>> newDS = new EnumMap<>(TwFunctionArguments.class);
			dataStructures.put(method, newDS);
			for (TwFunctionArguments a : ftype.readOnlyArguments())
				if ((a != t) & (a != dt)) {
					newDS.put(a, new LinkedList<recInfo>());
					// TODO: make sure all categories are searched for all the context classes
					// ie goup, life cycle, arena etc.
					SortedSet<Category> cats = findCategories(functions.get(method), a);
					Map<ConfigurationEdgeLabels, SortedSet<memberInfo>> fields = getAllMembers(cats);
					for (ConfigurationEdgeLabels el : EnumSet.of(E_AUTOVAR, E_LTCONSTANTS, E_DECORATORS, E_DRIVERS)) {
						recInfo ri = new recInfo();
						ri.name = dataAccessors.get(el);
						ri.klass = null;
						newDS.get(a).add(ri);
						if (fields.get(el) != null)
							for (memberInfo mi : fields.get(el)) {
								if (ri.klass == null) {
									ri.klass = containingClass(mi.name);
									ri.members = fields.get(el);
									break;
								}
							}
					}
				}
//			// debug
//			for (String m:dataStructures.keySet()) {
//				System.out.println(m);
//				for (TwFunctionArguments a:dataStructures.get(m).keySet()) {
//					System.out.println("\t"+a);
//					for (recInfo r:dataStructures.get(m).get(a)) {
//						System.out.println("\t\t"+r.name+":"+r.klass);
//						if (r.members!=null)
//							for (memberInfo mb:r.members)
//								System.out.println("\t\t\t"+mb);
//					}
//				}
//			}
//			// end debug
		}
		return dataStructures.get(method);
	}

	// the arena arguments are skipped only when the focal element is the arena
	// this only occurs when focal belongs to *arena*
	public boolean skipArena(SortedSet<Category> focalCats) {
//		for (TreeGraphDataNode tgn:elementTypeCategories.keySet())
//			if (tgn instanceof ArenaType)
//				return elementTypeCategories.get(tgn).equals(focalCats);
		for (Category cat : focalCats)
			if (cat.id().contentEquals(Category.arena))
				return true;
		return false;
	}

	@SuppressWarnings({ "unchecked" })
	public ModelMethodGenerator setMethod(TreeGraphDataNode function) {
		Map<String, ConfigurationEdgeLabels> nameLabelMatches = new HashMap<>();
		nameLabelMatches.put("autoVar", E_AUTOVAR);
		nameLabelMatches.put("constants", E_LTCONSTANTS);
		nameLabelMatches.put("decorators", E_DECORATORS);
		nameLabelMatches.put("currentState", E_DRIVERS);
		// mapping between inerVar names and rec.name
		Map<String, String> recToInnerVar = new HashMap<>();
		recToInnerVar.put("autoVar", null);
		recToInnerVar.put("constants", "Ltc");
		recToInnerVar.put("decorators", "Dec");
		recToInnerVar.put("currentState", "Drv");

		StringBuilder headerComment = new StringBuilder();
		String returnComment = null;
		TwFunctionTypes ftype = (TwFunctionTypes) function.properties().getPropertyValue(P_FUNCTIONTYPE.key());
		String fname = function.id();
		functions.put(fname, function);
		EnumMap<TwFunctionArguments, List<recInfo>> dataStruk = dataStructure(fname);

		headerComment.append("<p><strong>").append(fname).append("</strong> method of type <em>").append(ftype)
				.append("</em>: ").append(ftype.description()).append("</p>\n");
		boolean hasSpace = false;
		if (function.getParent() instanceof ProcessNode)
			hasSpace = ((ProcessNode) function.getParent()).hasSpace();
		else if (function.getParent().getParent() instanceof ProcessNode)
			// check this: it's probably completely wrong!
			hasSpace = ((ProcessNode) function.getParent().getParent()).hasSpace();
		if (hasSpace) {
			imports.add("static " + Distance.class.getCanonicalName() + ".*");
			imports.add(Point.class.getCanonicalName());
			imports.add(Box.class.getCanonicalName());
		}
		// applies to : rel or cat
		SortedSet<Category> focalCats = findCategories(function, focal);
		SortedSet<Category> otherCats = findCategories(function, other);
//		boolean hasLifeCycle = hasLifeCycle(focalCats);
		if (otherCats.isEmpty())
			headerComment.append("<p>- applies to categories {<em>").append(simpleCatSignature(focalCats))
					.append("</em>}</p>\n\n");
		else {
			headerComment.append("<p>- applies to relation <em>").append(relationType).append(": {")
					.append(simpleCatSignature(focalCats)).append("} → {").append(simpleCatSignature(otherCats))
					.append("}</em></p>\n\n");
		}
		headerComment.append(timerComment(function, ftype)).append('\n');
		String sdc = dependComment(function, ftype);
		if (sdc != null)
			if (!sdc.isBlank())
				headerComment.append(sdc).append('\n');
		// follow time model:
		ModelMethodGenerator method = methods.get(fname);
		// if method does not exist, create it and set its header, return type and
		// return statement
		if (method == null) {
//			String mname = Strings.toLowerCase(fname.substring(0, 1)) + fname.substring(1);
			String mname = fname.substring(0, 1).toLowerCase() + fname.substring(1);
			method = new ModelMethodGenerator(methodScope, ftype.returnType(), mname);
			methods.put(fname, method);
			if (!ftype.returnType().equals("void")) {
				method.setReturnStatement("return " + zero(ftype.returnType()));
				returnComment = ftype.returnJavaDoc();
			}
			method.clearArguments();
			if (snippets.containsKey(mname))
				method.setRawCode(snippets.get(mname));
		}
		// retrieve all the parameters and parameter types
		replicateNames.clear();
		EnumSet<TwFunctionArguments> argSet2 = EnumSet.noneOf(TwFunctionArguments.class);
		argSet2.addAll(ftype.readOnlyArguments());
		argSet2.addAll(ftype.localArguments());
		argSet2.addAll(ftype.writeableArguments());

		// t, dt
		Set<TwFunctionArguments> intersection = new TreeSet<>(ftype.readOnlyArguments());
		intersection.retainAll(EnumSet.of(t, dt));
		for (TwFunctionArguments arg : intersection) {
			method.addArgument(/* arg,null, */arg.name(), arg.type(), arg.description());
			headerComment.append("@param ").append(arg.name()).append(' ').append(arg.description()).append('\n');
			replicateNames.add(arg.name());
		}

		// arena, lifeCycle, group, space focal, other, otherGroup, otherLifeCycle
		// including return values
		for (TwFunctionArguments arg : dataStruk.keySet()) {
			List<recInfo> comp = dataStruk.get(arg);
			if (!(arg.equals(arena) && skipArena(focalCats)))
				for (recInfo rec : comp)
					if (rec != null)
						if (rec.members != null) {
							// read-only arguments
							for (memberInfo mb : rec.members) {
								if (mb.name != null) {
									if (mb.fullType != null)
										imports.add(mb.fullType);
									String prefix = "";
									if (arg.name().contains("other"))
										prefix = "other_";
									method.addArgument(/* arg,nameLabelMatches.get(rec.name), */
										prefix+mb.name, mb.type,
										arg.description() + rec.name + " " + mb.comment);
									headerComment.append("@param ").append(mb.name).append(' ')
										.append(arg.description()).append(rec.name).append(' ').append(mb.comment)
										.append('\n');
									replicateNames.add(mb.name);
								}
							}
							// writeable arguments (inner variables)
							if ((arg == focal) || (arg == other))
								for (String innerVar : ftype.innerVars()) // otherDrv etc
									if (recToInnerVar.get(rec.name)!=null)
										if ((innerVar.contains(recToInnerVar.get(rec.name))) &&
											(innerVar.contains(arg.name()))) {
												String s = "";
												if (innerVar.contains("Drv"))
													s = "next drivers for ";
												else if (innerVar.contains("Ltc"))
													s = "new constants for ";
												else if (innerVar.contains("Dec"))
													s = "new decorators for ";
												// e.g.: Chaos.FocalDrv focalDrv // next
												method.addArgument(/* arg,nameLabelMatches.get(rec.name), */
													innerVar, fname + "." + initialUpperCase(innerVar),
													s + arg.description());
												headerComment.append("@param ").append(innerVar).append(' ').append(s)
													.append(arg.description()).append('\n');
												replicateNames.add(innerVar);
										}
						}
		}
		// location arguments ?

		// random, decide
		for (TwFunctionArguments arg : ftype.localArguments()) {
			imports.add(arg.type());
			method.addArgument(/* arg,null, */arg.name(), simpleType(arg.type()), arg.description());
			headerComment.append("@param ").append(arg.name()).append(' ').append(arg.description()).append('\n');
			replicateNames.add(arg.name());
		}

		// event timers, if any

		Collection<TimerNode> queues = (Collection<TimerNode>) get(function.edges(Direction.IN),
				selectZeroOrMany(hasTheLabel(E_FEDBY.label())), edgeListStartNodes());
//		if (ftype!=SetInitialState)
		if (!queues.isEmpty()) {
			SortedSet<String> queueNames = new TreeSet<>();
			for (TimerNode q : queues)
				queueNames.add(q.id());
			imports.add(EventQueue.class.getName());
			TwFunctionArguments arg = timer;
			for (String qn : queueNames) {
				method.addArgument(/* arg,null, */qn, simpleType(arg.type()), arg.description());
				headerComment.append("@param ").append(qn).append(' ').append(arg.description()).append('\n');
				replicateNames.add(qn);
			}
		}

		// old code
//			case focalLoc:
//			case otherLoc:
//				if (hasSpace) {
//					method.addArgument(arg,arg.name(),simpleType(arg.type()),arg.description());
//					headerComment.append("@param ").append(arg.name()).append(' ')
//						.append(arg.description()).append('\n');
//					replicateNames.add(arg.name());
//				}
//				break;
//			case nextFocalLoc:
//			case nextOtherLoc:
//				if (hasSpace) {
//					method.addArgument(arg,arg.name(),arg.type(),arg.description());
//					headerComment.append("@param ").append(arg.name()).append(' ')
//						.append(arg.description()).append('\n');
//					replicateNames.add(arg.name());
//				}
//				break;
		if (returnComment != null)
			headerComment.append("@return ").append(returnComment).append('\n');
//		String cs = WordUtils.wrap(headerComment.toString(),80,"\\n",false); // ***
		method.setMethodComment(javaDocComment("\t", headerComment.toString().split("\\n")));
		return method; // contains the arguments in the order in which they should be called
	}

	@Override
	public boolean generateCode() {
		log.info("    generating file " + className + ".java ...");
		File ctGeneratedCodeDir = getModelCodeDir(modelName);
		ctGeneratedCodeDir.mkdirs();
		File file = Project.makeFile(LOCALJAVACODE, modelName, className + ".java");
		writeFile(this, file, className);
		generatedClassName = packageName + "." + className;
		log.info("  done.");
		return true;
	}

	@Override
	public String asText(String indent) {
		String result = "";
		if (packageComment != null)
			result += packageComment + "\n";
		result += "package " + packageName + ";\n\n";
		for (String imp : imports)
			result += "import " + imp + ";\n";
		result += "// Hey, model developer! You may add your own imports here as needed\n";
		if (imports.size() > 0)
			result += "\n";
		if (classComment != null)
			result += classComment;
		result += "public interface " + className;
		result += " {\n\n"; // 1
		for (ModelMethodGenerator m : methods.values())
			result += m.asText(indent);
//		if (rawMethods!=null)
//			for (String s:rawMethods)
//				result += s+"\n";
		result += "}\n"; // 1
		return result;
	}

	public String className() {
		return className;
	}

	public String generatedClassName() {
		return generatedClassName;
	}

	public File getFile() {
		String name = generatedClassName.replace(this.packageName + ".", "");
		String path = packagePath + File.separator + name;
		return new File(path + ".java");
	}

}
