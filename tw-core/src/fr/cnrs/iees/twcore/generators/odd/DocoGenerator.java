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
package fr.cnrs.iees.twcore.generators.odd;

import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationReservedNodeId.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import fr.cnrs.iees.twcore.constants.BorderListType;
import fr.cnrs.iees.twcore.constants.ConfigurationReservedNodeId;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.table.Column;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.table.TableTemplate;
import org.odftoolkit.simple.text.Paragraph;

import com.google.common.io.Files;

import fr.cnrs.iees.omugi.collections.tables.StringTable;
import au.edu.anu.omhtk.util.IntegerRange;
import au.edu.anu.twcore.archetype.TWA;
import au.edu.anu.twcore.data.Record;
import au.edu.anu.twcore.ecosystem.runtime.timer.ClockTimer;
import au.edu.anu.twcore.ecosystem.runtime.timer.EventTimer;
import au.edu.anu.twcore.ecosystem.runtime.timer.ScenarioTimer;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.userProject.SnippetReader;
import fr.cnrs.iees.omugi.graph.*;
import fr.cnrs.iees.omugi.graph.impl.*;
import fr.cnrs.iees.omugi.identity.impl.LocalScope;
import fr.cnrs.iees.twcore.constants.DataElementType;
import fr.cnrs.iees.twcore.constants.EdgeEffectCorrection;
import fr.cnrs.iees.twcore.constants.SpaceType;
import fr.cnrs.iees.twcore.constants.TimeScaleType;
import fr.cnrs.iees.twcore.constants.TimeUnits;
import fr.cnrs.iees.twcore.constants.TrackerType;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;
import fr.cnrs.iees.omhtk.utils.*;

import org.odftoolkit.simple.style.StyleTypeDefinitions;

/**
 * ODD generator
 * 
 * @author Ian Davies - 13 Jul 2020
 */
// JG 4/2/2022 CAUTION: I hav disabled all code relying on the Component nodes because these have 
// been deprecated. So this code needs revision.
public class DocoGenerator {
	/**
	 * Minimal config baseline
	 */
	private static int baseNodes = 28;
	private static int baseEdges = 10;
	private static int baseDrvs = 0;
	private static int baseCnts = 0;
	private static int baseDecs = 0;
	private static int baseProps = 28;
	// these?
	private static int baseCT = 1; // the system
	private static int baseRT = 0; // pre-def don't count unless used
	private static int baseGT = 0;// just in case
	// size of compressed .class files in a minimal config jar
	private static long baseClassByteCount = 554;

	// import static java.lang.Math.*;
	private static int baseLineCount = 1;

	private int nNodes;
	private int nEdges;
	private int nCnts;
	private int nDrvs;
	private int nDecs;
	private int nProps;
	private String authors;
	private String version;

	private static String sep = "\t";

	private List<TreeGraphDataNode> timersClock;
	private List<TreeGraphDataNode> timersEvent;
	private List<TreeGraphDataNode> timersScenario;
	private Map<TreeNode, String> timerDesc;
	private Map<TreeNode, String> spaceDesc;
	private Map<TreeNode, String> funcDesc;
	private Map<TreeNode, String> scDesc;
	private Map<TreeNode, String> dtDesc;
	private TreeGraphDataNode timeline;
	private TreeGraphDataNode system;
	// private TreeGraphDataNode struct;
	private TreeGraphDataNode dDef;
	private List<TreeGraphDataNode> drvTypes;
	private List<TreeGraphDataNode> decTypes;
	private List<TreeGraphDataNode> cnstTypes;
	private List<TreeGraphDataNode> compTypes; // including the system
	private List<TreeGraphDataNode> relTypes;
	private List<TreeGraphDataNode> groupTypes;
	private List<TreeGraphDataNode> spaceTypes;
	private List<TreeGraphDataNode> rootStoppingConditions;
	private List<TreeGraphDataNode> initTypes;
	private List<TreeGraphDataNode> trackerTypes;
//	private List<TreeGraphDataNode> ephemeralComponents;
	private List<TreeGraphDataNode> instanceComponents;
	private List<TreeGraphDataNode> rngs;
	private Map<TreeGraphDataNode, Map<TreeGraphDataNode, List<TreeGraphDataNode>>> ctFuncLookup;
	private Map<String, List<String>> snippetMap;
	// private List<TreeGraphDataNode>

	private TreeGraph<TreeGraphDataNode, ALEdge> cfg;

	private final File oddFile;
	private final File flowChartFile;

	private static final int level1 = 1;
	private static final int level2 = 2;
	private static final int level3 = 3;
	private static final int level4 = 4;

	private static int tableNumber;
	private static int figureNumber;

	// NB: Ignore ui. experiment and snippets
	private static List<String> countedNodes = new ArrayList<>();
	static {
		countedNodes.add(N_DIMENSIONER.label());
		countedNodes.add(N_TABLE.label());
		countedNodes.add(N_RECORD.label());
		countedNodes.add(N_FIELD.label());
		countedNodes.add(N_RNG.label());
		countedNodes.add(N_SYSTEM.label());
		countedNodes.add(N_DYNAMICS.label());
		countedNodes.add(N_TIMELINE.label());
		countedNodes.add(N_TIMER.label());
		countedNodes.add(N_PROCESS.label());
		countedNodes.add(N_FUNCTION.label());
//		countedNodes.add(N_LIFECYCLE.label());
		countedNodes.add(N_RECRUIT.label());
		countedNodes.add(N_PRODUCE.label());
		countedNodes.add(N_INITFUNCTION.label());
//		countedNodes.add(N_GROUP.label());
		countedNodes.add(N_GROUPTYPE.label());
//		countedNodes.add(N_COMPONENT.label());
		countedNodes.add(N_STRUCTURE.label());
		countedNodes.add(N_CATEGORYSET.label());
		countedNodes.add(N_CATEGORY.label());
		countedNodes.add(N_COMPONENTTYPE.label());
		countedNodes.add(N_RELATIONTYPE.label());
		countedNodes.add(N_SPACE.label());
		countedNodes.add(N_PREDEFINED.label());
	}

//	private long startTime;

	@SuppressWarnings("unchecked")
	//public DocoGenerator(TreeGraph<TreeGraphDataNode, ALEdge> cfg, TreeGraphDataNode system)
	public DocoGenerator(TreeGraph<TreeGraphDataNode, ALEdge> cfg) {
		File dir = Project.makeFile(Project.RUNTIME);
		dir.mkdirs();
		LocalScope scope = new LocalScope("Files");
		for (String fileName : dir.list()) {
			int dotIndex = fileName.lastIndexOf('.');
			fileName = (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
			scope.newId(true, fileName);
		}
		String dirName = scope.newId(false, "documentation1").id();
		oddFile = Project.makeFile(Project.RUNTIME, dirName, cfg.root().id() + ".odt");
		flowChartFile = Project.makeFile(Project.RUNTIME, dirName, "flowChart.svg");

		oddFile.getParentFile().mkdirs();

//		startTime = System.currentTimeMillis();
		this.cfg = cfg;
		timerDesc = new HashMap<>();
		spaceDesc = new HashMap<>();
		funcDesc = new HashMap<>();
		scDesc = new HashMap<>();
		dtDesc = new HashMap<>();
		ctFuncLookup = new HashMap<>();
		compTypes = new ArrayList<>();
		groupTypes = new ArrayList<>();
		relTypes = new ArrayList<>();
		spaceTypes = new ArrayList<>();
		rootStoppingConditions = new ArrayList<>();
		initTypes = new ArrayList<>();
		drvTypes = new ArrayList<>();
		decTypes = new ArrayList<>();
		cnstTypes = new ArrayList<>();
		rngs = new ArrayList<>();
//		ephemeralComponents = new ArrayList<>();
		instanceComponents = new ArrayList<>();
		trackerTypes = new ArrayList<>();
		tableNumber = 0;
		figureNumber = 0;
		List<TreeGraphDataNode> snippetNodes = new ArrayList<>();
		// basic metrics
		for (TreeGraphDataNode n : cfg.nodes()) {
			if (countedNodes.contains(n.classId())) {
				nNodes++;
				nProps += n.properties().size();
				for (ALEdge e : n.edges(Direction.OUT)) {
					if (countedNodes.contains(e.endNode().classId())) {
						nEdges++;
						if (e instanceof ALDataEdge)
							nProps += ((ALDataEdge) e).properties().size();
					}
				}
			}

			if (n.classId().equals(N_DATADEFINITION.label()))
				dDef = n;
			else if (n.classId().equals(N_SYSTEM.label())) {
				system = n;
				compTypes.add(n);
			} else if (n.classId().equals(N_FUNCTION.label()) || n.classId().equals(N_INITFUNCTION.label())) {
				TwFunctionTypes ft = (TwFunctionTypes) n.properties().getPropertyValue(P_FUNCTIONTYPE.key());
				funcDesc.put(n, n.id() + formatClassifier(ft.name() + "(...)"));
				if (n.classId().equals(N_INITFUNCTION.label())) {
					initTypes.add(n);
				}
			} else if (n.classId().equals(N_COMPONENTTYPE.label())) {
				compTypes.add(n);
//				List<TreeGraphDataNode> components = (List<TreeGraphDataNode>) get(n.getChildren(),
//						selectZeroOrMany(hasTheLabel(N_COMPONENT.label())));
//				for (TreeGraphDataNode cmp : components)
//					if (cmp.properties().hasProperty(P_COMPONENT_NINST.key()))
//						if (((Integer) cmp.properties().getPropertyValue(P_COMPONENT_NINST.key())) > 0)
//							instanceComponents.add(cmp);

				if (get(n.edges(Direction.OUT), selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())), edgeListEndNodes(),
						selectZeroOrOne(hasTheName(ephemeral.id()))) != null) {
//					List<TreeGraphDataNode> cmps = (List<TreeGraphDataNode>) get(n.getChildren(),
//							selectZeroOrMany(hasTheLabel(N_COMPONENT.label())));
//					ephemeralComponents.addAll(cmps);
				}
			} else if (n.classId().equals(N_RELATIONTYPE.label())) {
				Collection<ALEdge> ate = (Collection<ALEdge>) get(n.edges(Direction.IN),
						selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())));
				if (!ate.isEmpty())
					relTypes.add(n);
				// TODO if either of the the related components have ephemeral lifespans then
				// this indicates an ephemeral relation - yes?
			} else if (n.classId().equals(N_SPACE.label())) {
				spaceTypes.add(n);
				SpaceType st = (SpaceType) n.properties().getPropertyValue(P_SPACETYPE.key());
				spaceDesc.put(n, n.id() + formatClassifier(st.name()));
			} else if (n.classId().equals(N_STRUCTURE.label())) {
				// struct = n;
			} else if (n.classId().equals(N_TIMELINE.label())) {
				timeline = n;
			} else if (n.classId().equals(N_STOPPINGCONDITION.label())) {
				String sn = getSimpleName((String) n.properties().getPropertyValue(TWA.SUBCLASS));
				// TODO: later use these as root a sc tree. All these are implicitly "OR".
				if (n.getParent().classId().equals(N_DYNAMICS.label()))
					rootStoppingConditions.add(n);
				scDesc.put(n, n.id() + formatClassifier(sn));
			} else if (n.classId().equals(N_DATATRACKER.label())) {
				trackerTypes.add(n);
				String sn = getSimpleName((String) n.properties().getPropertyValue(TWA.SUBCLASS));
				dtDesc.put(n, n.id() + formatClassifier(sn));
			} else if (n.classId().equals(N_FUNCTION.label()) || n.classId().equals(N_INITFUNCTION.label())) {
				snippetNodes.add(n);
			} else if (n.classId().equals(N_RNG.label())) {
				rngs.add(n);
			}
		}
		/**
		 * get the file name:
		 * 
		 * project_Logistic1_2021-07-12-03-34-38-765/local/java/code/sys1
		 * 
		 * TODO: Assume one system for now!
		 */

		File f = Project.makeFile(Project.LOCAL_JAVA_CODE, system.id(), cfg.root().id() + ".java");
		snippetMap = SnippetReader.readSnippetsFromFile(f);

		// Count drivers, constants and decorators
		for (TreeGraphDataNode n : cfg.subTree(dDef)) {
			TreeGraphDataNode drvCat = (TreeGraphDataNode) get(n.edges(Direction.IN),
					selectZeroOrOne(hasTheLabel(E_DRIVERS.label())), startNode());
			if (drvCat != null) {
				nDrvs += getDimensions(n);
				drvTypes.add(n);
			} else if (get(n.edges(Direction.IN), selectZeroOrOne(hasTheLabel(E_CONSTANTS.label()))) != null) {
				nCnts += getDimensions(n);
				cnstTypes.add(n);
			} else if (get(n.edges(Direction.IN), selectZeroOrOne(hasTheLabel(E_DECORATORS.label()))) != null) {
				nDecs += getDimensions(n);
				decTypes.add(n);
			}

		}

		timersClock = new ArrayList<>();
		timersEvent = new ArrayList<>();
		timersScenario = new ArrayList<>();
		for (TreeGraphDataNode timer : (List<TreeGraphDataNode>) get(timeline.getChildren(),
				selectOneOrMany(hasTheLabel(N_TIMER.label())))) {
			if (timer.properties().getPropertyValue(TWA.SUBCLASS).equals(ClockTimer.class.getName())) {
				timersClock.add(timer);
				timerDesc.put(timer, timer.id() + formatClassifier(ClockTimer.class.getSimpleName()));
			} else if (timer.properties().getPropertyValue(TWA.SUBCLASS).equals(EventTimer.class.getName())) {
				timersEvent.add(timer);
				timerDesc.put(timer, timer.id() + formatClassifier(EventTimer.class.getSimpleName()));
			} else {
				timersScenario.add(timer);
				timerDesc.put(timer, timer.id() + formatClassifier(ScenarioTimer.class.getSimpleName()));
			}
		}

		timersClock.sort(new Comparator<TreeGraphDataNode>() {

			@Override
			public int compare(TreeGraphDataNode t1, TreeGraphDataNode t2) {
				TimeUnits tu1 = (TimeUnits) t1.properties().getPropertyValue(P_TIMEMODEL_TU.key());
				TimeUnits tu2 = (TimeUnits) t2.properties().getPropertyValue(P_TIMEMODEL_TU.key());
				return tu1.compareTo(tu2);
			}
		});

		// build a list of componentTypes (incl system) with a unique list of assoc.
		// functions that apply to it organised by timer
		buildCTFunctionTable();

		StringTable tblAuthors = (StringTable) cfg.root().properties().getPropertyValue(P_MODEL_AUTHORS.key());
		StringBuilder authorssb = new StringBuilder();
		LocalDateTime currentDate = LocalDateTime.now(ZoneOffset.UTC);
		String datetime = currentDate.format(DateTimeFormatter.ofPattern("d-MMM-uuuu"));

		for (int i = 0; i < tblAuthors.size(); i++)
			authorssb.append(tblAuthors.getWithFlatIndex(i)).append("\n");

		authorssb.append("Date: ").append(datetime).append("\n");
		authors = authorssb.toString();
		version = (String) cfg.root().properties().getPropertyValue(P_MODEL_VERSION.key());
		version = version.trim();
	}

	// cf: https://odftoolkit.org/simple/document/cookbook/Text%20Document.html
	public void generate() {
		try {
			TextDocument document = TextDocument.newTextDocument();
			writeTitle(document, "Overview, Design concepts and Details", level1);
			// setHeading(document, level1);

			writePurpose(document, level2);

			writeEVS(document, level2);
			writeAgentsIndividuals(document, level3);
			writeSpatialUnits(document, level3);
			writeEnvironment(document, level3);
			writeCollectives(document, level3);

			writeProcessScheduling(document, level2);

			writeDesignConcepts(document, level2);
			writeBasicPrinciples(document, level3);
			writeEmergenceConcepts(document, level3);
			writeAdaptationConcepts(document, level3);
			writeObjectivesConcepts(document, level3);
			writeLearningConcepts(document, level3);
			writePredictionConcepts(document, level3);
			writeSensingConcepts(document, level3);
			writeInteractionConcepts(document, level3);
			writeStochasticityConcepts(document, level3);
			writeCollectivesConcepts(document, level3);
			writeObservationConcepts(document, level3);

			writeInitialisation(document, level2);

			writeInputData(document, level2);

			writeSubmodels(document, level2);

			writeReferences(document, level2);

			// ----- end ODD

			document.appendSection("Appendix1");
			// document.addPageBreak();

			writeTitle(document, "Appendix 1: Specification graph metrics", level1);

			writeMetrics(document, "Specification metrics", level2);
			writeGraphImages(document, "Specification graph", level2);
			writeSrcCode(document, "Source code snippets", level2, level4);

			// ----- end Appendix 1

			// Use this table as the style default
			TableTemplate template = document.LoadTableTemplateFromForeignTable(
					this.getClass().getResourceAsStream("TableTemplate.odt"), "Table1");
			for (Table t : document.getTableList()) {
				t.applyStyle(template);
				/**
				 * Trying to set the column widths but this doesn't seem to work.
				 */
				Iterator<Column> ci = t.getColumnIterator();
				while (ci.hasNext())
					ci.next().setUseOptimalWidth(true);
				t.setWidth(t.getWidth());
			}

			document.save(oddFile);

			// free resources
			document.close();

//			long endTime = System.currentTimeMillis();
//			System.out.println("DOC GENERATION TIME: " + (endTime - startTime));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public File[] getArtifactFiles() {
		File[] result = { oddFile, flowChartFile };
		return result;
	}

	private void writeTitle(TextDocument doc, String heading, int level) {
		StringBuilder sb = new StringBuilder();
		sb.append(heading);
		doc.addParagraph(sb.toString()).applyHeading(true, level);

		sb = new StringBuilder();
		sb.append(Project.getDisplayName());
		if (!version.isBlank())
			sb.append(" (Version: ").append(version).append(")");

		doc.addParagraph(sb.toString()).applyHeading(true, level);

		doc.addParagraph(authors);

	}

	private void writePurpose(TextDocument doc, int level) {
		doc.addParagraph("Purpose").applyHeading(true, level);
		String precis = (String) cfg.root().properties().getPropertyValue(P_MODEL_PRECIS.key());
		if (precis.trim().isBlank()) {
			Paragraph para1 = doc.addParagraph(""//
					+ "[Explanation: Every model has to start from a clear question, problem, or hypothesis. "//
					+ "Therefore, ODD starts with a concise summary of the overall objective(s) for which the model was developed. "//
					+ "Do not describe anything about how the model works here, only what it is to be used for. "//
					+ "We encourage authors to use this paragraph independently of any presentation of the purpose in the introduction of their article, "//
					+ "since the ODD protocol should be complete and understandable by itself and not only in connection with the whole publication "//
					+ "(as it is also the case for figures, tables and their legends). "//
					+ "If one of the purposes of a model is to expand from basic principles to richer representation of real-world scenarios, "//
					+ "this should be stated explicitly.]");

			Font font = para1.getFont();
			font.setFontStyle(StyleTypeDefinitions.FontStyle.ITALIC);
			para1.setFont(font);
		} else
			doc.addParagraph((String) cfg.root().properties().getPropertyValue(P_MODEL_PRECIS.key()));
	}

	/**
	 * Questions: What kinds of entities are in the model? By what state variables,
	 * or attributes, are these entities characterized? What are the temporal and
	 * spatial resolutions and extents of the model?
	 * 
	 * Explanation: An entity is a distinct or separate object or actor that behaves
	 * as a unit and may interact with other entities or be affected by external
	 * environmental factors. Its current state is characterized by its state
	 * variables or attributes. A state variable or attribute is a variable that
	 * distinguishes an entity from other entities of the same type or category, or
	 * traces how the entity changes over time. Examples are weight, sex, age,
	 * hormone level, social rank, spatial coordinates or which grid cell the entity
	 * is in, model parameters characterizing different types of agents (e.g.,
	 * species), and behavioral strategies. The entities of an ABM are thus
	 * characterized by a set, or vector (Chambers, 1993; Huse et al., 2002), of
	 * attributes, which can contain both numerical variables and references to
	 * behavioral strategies. One way to define entities and state variables is the
	 * following: if you want (as modelers often do) to stop the model and save it
	 * in its current state, so it can be re-started later in exactly the same
	 * state, what kinds of information must you save? If state variables have
	 * units, they should be provided. State variables can change in the course of
	 * time (e.g. weight) or remain constant (e.g. sex, species-specific parameters,
	 * location of a non-mobile entity). State variables should be low level or
	 * elementary in the sense that they cannot be calculated from other state
	 * variables. For example, if farmers are represented by grid cells which have
	 * certain spatial coordinates, the distance of a farmer to a certain service
	 * centre would not be a state variable because it can be calculated from the
	 * farmer’s and service centre’s positions. Most ABMs include the following
	 * types of entities:
	 */
	private void writeEVS(TextDocument doc, int level) {
		List<String> entries;
		doc.addParagraph("Entities, state variables, and scales").applyHeading(true, level);

		// ComponentTypes,
		entries = getEntityDetails();
		doc.addParagraph("Table " + (++tableNumber) + ". Component (Entity) description");
		writeTable(doc, entries, "Component type", "Categories", "Component", "Drivers", "Time", "Space");

		// RelationType
		entries = getRelationsDetails();
		if (!entries.isEmpty()) {
			doc.addParagraph("Table " + (++tableNumber) + ". Component type relation description");
			writeTable(doc, entries, "Relation", "Component type interaction");
		}

		// Drivers,
		entries = getDataDetails(drvTypes, E_DRIVERS.label());
		if (!entries.isEmpty()) {
			doc.addParagraph("Table " + (++tableNumber) + ". Drivers (state variables).");
			writeTable(doc, entries, "Driver", "name", "Description", "Dimensions", "Type", "Units", "Range");
		}
		// Decorators,
		entries = getDataDetails(decTypes, E_DECORATORS.label());
		if (!entries.isEmpty()) {
			doc.addParagraph("Table " + (++tableNumber) + ". Decorators (dependent state variable).");
			writeTable(doc, entries, "Decorator", "name", "Description", "Dimensions", "Type", "Units", "Range");
		}
		// Constants,
		entries = getDataDetails(cnstTypes, E_CONSTANTS.label());
		if (!entries.isEmpty()) {
			doc.addParagraph("Table " + (++tableNumber) + ". Constants");
			writeTable(doc, entries, "Constants", "name", "Description", "Dimensions", "Type", "Units", "Range");
		}
	}

	/**
	 * Agents/individuals. A model can have different types of agents; for example,
	 * wolves and sheep, and even different sub-types within the same type, for
	 * example different functional types of plants or different life stages of
	 * animals. Examples of types of agents include the following: organisms,
	 * humans, or institutions. Example state variables include: identity number
	 * (i.e., even if all other state variables would be the same, the agent would
	 * still maintain a unique identity), age, sex, location (which may just be the
	 * grid cell it occupies instead of coordinates), size, weight, energy reserves,
	 * signals of fitness, type of land use, political opinion, cell type,
	 * species-specific parameters describing, for example, growth rate and maximum
	 * age, memory (e.g., list of friends or quality of sites visited the previous
	 * 20 time steps), behavioral strategy, etc.
	 */
	private void writeAgentsIndividuals(TextDocument doc, int level) {
		List<String> entries = getAgentsIndividualsDetails();
		if (!entries.isEmpty()) {
			doc.addParagraph("Agents/individuals").applyHeading(true, level);

			doc.addParagraph("Table " + (++tableNumber) + ". Agent description");
			writeTable(doc, entries, "Component type", "Component");
		}

	}

	/**
	 * Spatial units (e.g., grid cells). Example state variables include the
	 * following: location, a list of agents in the cell, and descriptors of
	 * environmental conditions (elevation, vegetation cover, soil type, etc.)
	 * represented by the cell. In some ABMs, grid cells are used to represent
	 * agents: the state and behavior of trees, businesses, etc., that can be
	 * modeled as characteristics of a cell. Some overlap of roles can occur. For
	 * example, a grid cell may be an entity with its own variables (e.g., soil
	 * moisture content, soil nutrient concentration, etc., for a terrestrial cell),
	 * but may also function as a location, and hence an attribute, of an organism.
	 * 
	 * In describing spatial and temporal scales and extents (the amount of space
	 * and time represented in a simulation), it is important to specify what the
	 * model’s units represent in reality. For example: “One time step represents
	 * one year and simulations were run for 100 years. One grid cell represents 1
	 * ha and the model landscape comprised 1,000 x 1,000 ha; i.e., 10,000 square
	 * kilometers”.
	 */
	private void writeSpatialUnits(TextDocument doc, int level) {
		doc.addParagraph("Spatial units").applyHeading(true, level);
		List<String> entries = getSpatialUnitsDetails();
		if (!entries.isEmpty()) {
			doc.addParagraph("Table " + (++tableNumber) + ". Spatial details");
			writeTable(doc, entries, "Type", "Details");
		} else
			doc.addParagraph("Non-spatial model.");
	}

	/**
	 * Environment. While spatial units often represent environmental conditions
	 * that vary over space, this entity refers to the overall environment, or
	 * forces that drive the behavior and dynamics of all agents or grid cells.
	 * Examples of environmental variables are temperature, rainfall, market price
	 * and demand, fishing pressure, and tax regulations.
	 */
	private void writeEnvironment(TextDocument doc, int level) {
		// random number generation
		List<String> entries = getRNGDetails();
		if (!entries.isEmpty()) {
			doc.addParagraph("Environment").applyHeading(true, level);
			doc.addParagraph("Table " + (++tableNumber) + ". Random number application");
			writeTable(doc, entries, "Generators", "Properties", "Values", "Function uses");
		}
//		// get arena constants
//		List<String> entries = getArenaConstantsDetails();
//		if (!entries.isEmpty()) {
//			doc.addParagraph("Environment").applyHeading(true, level);
//			doc.addParagraph("Table " + (++tableNumber) + ". Arena constants");
//			writeTable(doc, entries, "constants", "name", "Description", "Dimensions", "Type", "Units", "Range");
//		}
	}

	/**
	 * Collectives. Groups of agents can have their own behaviors, so that it can
	 * make sense to distinguish them as entities; for example, social groups of
	 * animals, households of human agents, or organs consisting of cells. A
	 * collective is usually characterized by the list of its agents, and by
	 * specific actions that are only performed by the collective, not by their
	 * constitutive entities.
	 */
	private void writeCollectives(TextDocument doc, int level) {
		doc.addParagraph("Collectives").applyHeading(true, level);
		doc.addParagraph("Write Group details here");
	}

	/**
	 * Questions: Who (i.e., what entity) does what, and in what order? When are
	 * state variables updated? How is time modeled, as discrete steps or as a
	 * continuum over which both continuous processes and discrete events can occur?
	 * Except for very simple schedules, one should use pseudo-code to describe the
	 * schedule in every detail, so that the model can be re-implemented from this
	 * code. Ideally, the pseudo-code corresponds fully to the actual code used in
	 * the program implementing the ABM.
	 * 
	 * Explanation: The “does what?” in the first question refers to the model’s
	 * processes. In this ODD element only the self-explanatory names of the model’s
	 * processes should be listed: ‘update habitat’, ‘move’, ‘grow’, ‘buy’, ‘update
	 * plots’, etc. These names are then the titles of the submodels that are
	 * described in the last ODD element, ‘Submodels’. Processes are performed
	 * either by one of the model’s entities (for example: ‘move’), or by a
	 * higher-level controller that does things such as updating plots or writing
	 * output to files. To handle such higher-level processes, ABM software
	 * platforms like Swarm (Minar et al., 1996) and NetLogo (Wilensky, 1999)
	 * include the concept of the ‘Model’, or ‘Observer’, itself; that is, a
	 * controller object that performs such processes. By “in what order?” we refer
	 * to both the order in which the different processes are executed and the order
	 * in which a process is performed by a set of agents. For example, feeding may
	 * be a process executed by all the animal agents in a model, but we must also
	 * specify the order in which the individual animals feed; that is, whether they
	 * feed in random order, or fixed order, or size-sorted order. Differences in
	 * such ordering can have a very large effect on model outputs (Bigbee et al.,
	 * 2006; Caron-Lormier et al., 2008). The question of when variables are updated
	 * includes the question of whether a state variable is immediately assigned a
	 * new value as soon as that value is calculated by a process (asynchronous
	 * updating), or whether the new value is stored until all agents have executed
	 * the process, and then all are updated at once (synchronous updating). Most
	 * ABMs represent time simply by using time steps: assuming that time moves
	 * forward in chunks. But time can be represented in other ways (Grimm and
	 * Railsback, 2005, Chapter 5). Defining a model’s schedule includes stating how
	 * time is modeled, if it is not clear from the ‘Entities, State Variables, and
	 * Scales’ element.
	 */
	private void writeProcessScheduling(TextDocument doc, int level) {

		doc.addParagraph("Process overview and scheduling").applyHeading(true, level);

		byte[] svg = DiagramGenerator.flowChart(cfg.root()).getBytes();
		// save to file
		try {
			Files.write(svg, flowChartFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		doc.addParagraph("see: [" + flowChartFile.getName() + " - " + flowChartFile.getParent() + "]");
		doc.addParagraph("Figure " + (++figureNumber) + ". Flow chart");
		doc.addParagraph("");

		List<String> entries;
		entries = getTimeDetails();
		doc.addParagraph("Table " + (++tableNumber) + ". Temporal description");
		writeTable(doc, entries, "Name", "Details");

		// Entity, timer and functions
		entries = getEntityFunctionsEntries();
		doc.addParagraph("Table " + (++tableNumber) + ". Component (Entity) functions");
		writeTable(doc, entries, "Component", "Timer", "Functions");

		entries = getStoppingConditionDetails();
		if (!entries.isEmpty()) {
			doc.addParagraph("Table " + (++tableNumber) + ". Stopping conditions");
			writeTable(doc, entries, "Name", "Details");
		} else
			doc.addParagraph("No stopping conditions.");

	}

	/**
	 * Questions: There are eleven design concepts. Most of these were discussed
	 * extensively by Railsback (2001) and Grimm and Railsback (2005; Chapter. 5),
	 * and are summarized here via the following questions:
	 * 
	 * Explanation: The ‘Design concepts’ element of the ODD protocol does not
	 * describe the model per se; i.e., it is not needed to replicate a model.
	 * However, these design concepts tend to be characteristic of ABMs, though
	 * certainly not exclusively. They may also be crucial to interpreting the
	 * output of a model, and they are not described well via traditional model
	 * description techniques such as equations and flow charts. Therefore, they are
	 * included in ODD as a kind of checklist to make sure that important model
	 * design decisions are made consciously and that readers are aware of these
	 * decisions (Railsback, 2001; Grimm and Railsback, 2005). For example, almost
	 * all ABMs include some kinds of adaptive traits, but if these traits do not
	 * use an explicit objective measure the ‘Objectives’ and perhaps ‘Prediction’
	 * concepts are not relevant (though many ABMs include hidden or implicit
	 * predictions). Also, many ABMs do not include learning or collectives. Unused
	 * concepts can be omitted in the ODD description. There might be important
	 * concepts underlying the design of an ABM that are not included in the ODD
	 * protocol. If authors feel that it is important to understand a certain new
	 * concept to understand the design of their model, they should give it a short
	 * name, clearly announce it as a design concept not included in the ODD
	 * protocol, and present it at the end of the Design concepts element.
	 */
	private void writeDesignConcepts(TextDocument doc, int level) {
		doc.addParagraph("Design concepts").applyHeading(true, level);
	}

	/**
	 * Basic principles. Which general concepts, theories, hypotheses, or modeling
	 * approaches are underlying the model’s design? Explain the relationship
	 * between these basic principles, the complexity expanded in this model, and
	 * the purpose of the study. How were they taken into account? Are they used at
	 * the level of submodels (e.g., decisions on land use, or foraging theory), or
	 * is their scope the system level (e.g., intermediate disturbance hypotheses)?
	 * Will the model provide insights about the basic principles themselves, i.e.
	 * their scope, their usefulness in real-world scenarios, validation, or
	 * modification (Grimm, 1999)? Does the model use new, or previously developed,
	 * theory for agent traits from which system dynamics emerge (e.g.,
	 * ‘individual-based theory’ as described by Grimm and Railsback [2005; Grimm et
	 * al., 2005])?
	 * 
	 */
	private void writeBasicPrinciples(TextDocument doc, int level) {
		doc.addParagraph("Basic principles").applyHeading(true, level);
	}

	/**
	 * Emergence. What key results or outputs of the model are modeled as emerging
	 * from the adaptive traits, or behaviors, of individuals? In other words, what
	 * model results are expected to vary in complex and perhaps unpredictable ways
	 * when particular characteristics of individuals or their environment change?
	 * Are there other results that are more tightly imposed by model rules and
	 * hence less dependent on what individuals do, and hence ‘built in’ rather than
	 * emergent results?
	 */
	private void writeEmergenceConcepts(TextDocument doc, int level) {
		doc.addParagraph("Emergence").applyHeading(true, level);
	}

	/**
	 * Adaptation. What adaptive traits do the individuals have? What rules do they
	 * have for making decisions or changing behavior in response to changes in
	 * themselves or their environment? Do these traits explicitly seek to increase
	 * some measure of individual success regarding its objectives (e.g., “move to
	 * the cell providing fastest growth rate”, where growth is assumed to be an
	 * indicator of success; see the next concept)? Or do they instead simply cause
	 * individuals to reproduce observed behaviors (e.g., “go uphill 70% of the
	 * time”) that are implicitly assumed to indirectly convey success or fitness?
	 */
	private void writeAdaptationConcepts(TextDocument doc, int level) {
		doc.addParagraph("Adaptation").applyHeading(true, level);
	}

	/**
	 * Objectives. If adaptive traits explicitly act to increase some measure of the
	 * individual's success at meeting some objective, what exactly is that
	 * objective and how is it measured? When individuals make decisions by ranking
	 * alternatives, what criteria do they use? Some synonyms for ‘objectives’ are
	 * ‘fitness’ for organisms assumed to have adaptive traits evolved to provide
	 * reproductive success, ‘utility’ for economic reward in social models or
	 * simply ‘success criteria’. (Note that the objective of such agents as members
	 * of a team, social insects, organs—e.g., leaves—of an organism, or cells in a
	 * tissue, may not refer to themselves but to the team, colony or organism of
	 * which they are a part.)
	 */
	private void writeObjectivesConcepts(TextDocument doc, int level) {
		doc.addParagraph("Objectives").applyHeading(true, level);
	}

	/**
	 * Learning. Many individuals or agents (but also organizations and
	 * institutions) change their adaptive traits over time as a consequence of
	 * their experience? If so, how?
	 */
	private void writeLearningConcepts(TextDocument doc, int level) {
		doc.addParagraph("Learning").applyHeading(true, level);
	}

	/**
	 * Prediction. Prediction is fundamental to successful decision-making; if an
	 * agent’s adaptive traits or learning procedures are based on estimating future
	 * consequences of decisions, how do agents predict the future conditions
	 * (either environmental or internal) they will experience? If appropriate, what
	 * internal models are agents assumed to use to estimate future conditions or
	 * consequences of their decisions? What tacit or hidden predictions are implied
	 * in these internal model assumptions?
	 */
	private void writePredictionConcepts(TextDocument doc, int level) {
		doc.addParagraph("Prediction").applyHeading(true, level);
	}

	/**
	 * Sensing. What internal and environmental state variables are individuals
	 * assumed to sense and consider in their decisions? What state variables of
	 * which other individuals and entities can an individual perceive; for example,
	 * signals that another individual may intentionally or unintentionally send?
	 * Sensing is often assumed to be local, but can happen through networks or can
	 * even be assumed to be global (e.g., a forager on one site sensing the
	 * resource levels of all other sites it could move to). If agents sense each
	 * other through social networks, is the structure of the network imposed or
	 * emergent? Are the mechanisms by which agents obtain information modeled
	 * explicitly, or are individuals simply assumed to know these variables?
	 */
	private void writeSensingConcepts(TextDocument doc, int level) {
		doc.addParagraph("Sensing").applyHeading(true, level);
	}

	/**
	 * Interaction. What kinds of interactions among agents are assumed? Are there
	 * direct interactions in which individuals encounter and affect others, or are
	 * interactions indirect, e.g., via competition for a mediating resource? If the
	 * interactions involve communication, how are such communications represented?
	 */
	private void writeInteractionConcepts(TextDocument doc, int level) {
		doc.addParagraph("Interaction").applyHeading(true, level);
	}

	/**
	 * Stochasticity. What processes are modeled by assuming they are random or
	 * partly random? Is stochasticity used, for example, to reproduce variability
	 * in processes for which it is unimportant to model the actual causes of the
	 * variability? Is it used to cause model events or behaviors to occur with a
	 * specified frequency?
	 */
	private void writeStochasticityConcepts(TextDocument doc, int level) {
		doc.addParagraph("Stochasticity").applyHeading(true, level);
	}

	/**
	 * Collectives. Do the individuals form or belong to aggregations that affect,
	 * and are affected by, the individuals? Such collectives can be an important
	 * intermediate level of organization in an ABM; examples include social groups,
	 * fish schools and bird flocks, and human networks and organizations. How are
	 * collectives represented? Is a particular collective an emergent property of
	 * the individuals, such as a flock of birds that assembles as a result of
	 * individual behaviors, or is the collective simply a definition by the
	 * modeler, such as the set of individuals with certain properties, defined as a
	 * separate kind of entity with its own state variables and traits?
	 */
	private void writeCollectivesConcepts(TextDocument doc, int level) {
		// if writeCollectives has been addressed then enable this?
		doc.addParagraph("Collectives").applyHeading(true, level);
	}

	/**
	 * Observation. What data are collected from the ABM for testing, understanding,
	 * and analyzing it, and how and when are they collected? Are all output data
	 * freely used, or are only certain data sampled and used, to imitate what can
	 * be observed in an empirical study (“Virtual Ecologist” approach; Zurell et
	 * al., 2010)?
	 */
	private void writeObservationConcepts(TextDocument doc, int level) {

		List<String> entries = getTrackerDetails();
		if (!entries.isEmpty()) {
			doc.addParagraph("Observation").applyHeading(true, level);
			doc.addParagraph("Table " + (++tableNumber) + ". Data tracker details.");
			writeTable(doc, entries, "Data tracker", "Details", "Tracked data", "Time", "Process", "Component");

			doc.addParagraph("\nAdd space and graph widget outputs if present");
		}
	}

	/**
	 * Questions: What is the initial state of the model world, i.e., at time t = 0
	 * of a simulation run? In detail, how many entities of what type are there
	 * initially, and what are the exact values of their state variables (or how
	 * were they set stochastically)? Is initialization always the same, or is it
	 * allowed to vary among simulations? Are the initial values chosen arbitrarily
	 * or based on data? References to those data should be provided.
	 * 
	 * Explanation: Model results cannot be accurately replicated unless the initial
	 * conditions are known. Different models, and different analyses using the same
	 * model, can of course depend quite differently on initial conditions.
	 * Sometimes the purpose of a model is to analyze consequences of its initial
	 * state, and other times modelers try hard to minimize the effect of initial
	 * conditions on results.
	 */
	private void writeInitialisation(TextDocument doc, int level) {
		doc.addParagraph("Initialisation").applyHeading(true, level);
		List<String> entries;
		entries = getEntityInitialiserDetails();
		if (!entries.isEmpty()) {
			doc.addParagraph("Table " + (++tableNumber) + ". Component type initialisation methods");
			writeTable(doc, entries, "Component type", "Initialiser");
		} else
			doc.addParagraph("No initialising functions.");

		entries = getInstanceComponentDetails();
		if (!entries.isEmpty()) {
			doc.addParagraph("Table " + (++tableNumber) + ". Initial component instances.");
			writeTable(doc, entries, "ComponentType", "Component", "Number");

		}
	}

	/**
	 * Question: Does the model use input from external sources such as data files
	 * or other models to represent processes that change over time?
	 * 
	 * Explanation: In models of real systems, dynamics are often driven in part by
	 * a time series of environmental variables, sometimes called external forcings;
	 * for example annual rainfall in semi-arid savannas (Jeltsch et al., 1996).
	 * “Driven” means that one or more state variables or processes are affected by
	 * how these environmental variables change over time, but these environmental
	 * variables are not themselves affected by the internal variables of the model.
	 * For example, rainfall may affect the soil moisture variable of grid cells
	 * and, therefore, how the recruitment and growth of trees change. Often it
	 * makes sense to use observed time series of environmental variables so that
	 * their statistical qualities (mean, variability, temporal autocorrelation,
	 * etc.) are realistic. Alternatively, external models can be used to generate
	 * input, e.g. a rainfall time series (Eisinger and Wiegand, 2008). Obviously,
	 * to replicate an ABM, any such input has to be specified and the data or
	 * models provided, if possible. (Publication of input data for some social
	 * simulations can be constrained by confidentiality considerations.) If a model
	 * does not use external data, this element should nevertheless be included,
	 * using the statement: “The model does not use input data to represent
	 * time-varying processes.” Note that ‘Input data’ does not refer to parameter
	 * values or initial values of state variables.
	 */
	private void writeInputData(TextDocument doc, int level) {
		doc.addParagraph("Input data").applyHeading(true, level);

		doc.addParagraph("Add refs to all input files here");
	}

	/**
	 * Questions: What, in detail, are the submodels that represent the processes
	 * listed in ‘Process overview and scheduling’? What are the model parameters,
	 * their dimensions, and reference values? How were submodels designed or
	 * chosen, and how were they parameterized and then tested?
	 */
	private void writeSubmodels(TextDocument doc, int level) {
		doc.addParagraph("Submodels").applyHeading(true, level);

		doc.addParagraph("Ha interesting! What should be put here - display a hierarchy if present?");
	}

	private void writeReferences(TextDocument doc, int level) {
		doc.addParagraph("References").applyHeading(true, level);
		int counter = 0;

		StringTable tblRefs = (StringTable) cfg.root().properties().getPropertyValue(P_MODEL_CITATIONS.key());
		StringBuilder refs = new StringBuilder();
		for (int i = 0; i < tblRefs.size(); i++) {
			String entry = tblRefs.getWithFlatIndex(i);
			if (entry == null)
				entry = "";
			entry = entry.trim();
			if (!entry.isBlank()) {
				refs.append(++counter).append(". ").append(entry).append("\n");
			}
		}

		doc.addParagraph(refs.toString());
	}

//------------ end of ODD ------------------

//------------- appendix 1 -----------------
	private void writeMetrics(TextDocument doc, String title, int level) {
		doc.addParagraph(title).applyHeading(true, level);
		List<String> entries = getMetricsDetails();
		doc.addParagraph("Table " + (++tableNumber) + ". Configuration graph metrics");
		writeTable(doc, entries, "Metric", "Value");
		doc.addParagraph("Values are calculated after subtracting the values of a minimal configuration."
				+ "\nIf the descriptor is a table, the count is increased by the table size."
				+ "\nComplexity is the sum the compressed size of the user .class files.");
	}

	private void writeGraphImages(TextDocument doc, String title, int level) {
		doc.addParagraph(title).applyHeading(true, level);
		doc.addParagraph("[Add selected graph images here]");
	}

	private void writeSrcCode(TextDocument doc, String title, int level, int sublevel) {
		doc.addParagraph(title).applyHeading(true, level);
		// doc.addParagraph("\nCode snippets:");

		for (Map.Entry<String, List<String>> snp : snippetMap.entrySet()) {
			doc.addParagraph(snp.getKey() + "(...)").applyHeading(true, sublevel);
			Paragraph para1 = doc.addParagraph("");
			Font font = para1.getFont();
			font.setFamilyName("Liberation Mono");
			font.setSize(10);
			para1.setFont(font);
			int lineCount = 0;
			for (String line : snp.getValue()) {
				if (line.startsWith("\t\t"))
					line = line.replaceFirst("\t\t", "");
				para1.appendTextContent(line + "\n", true);
				if (!line.trim().startsWith("//") && !line.isBlank()) {
					lineCount++;
				}
			}
			para1.appendTextContent("[Lines: " + lineCount + "]\n", true);
		}
	}

// ------------- end appendix 1 -----------------
	@SuppressWarnings("unchecked")
	private List<String> getTrackerDetails() {
		List<String> entries = new ArrayList<>();
		for (TreeGraphDataNode t : trackerTypes) {
			String c1 = dtDesc.get(t);
			StringBuilder sb = new StringBuilder();
			String[] keys = t.properties().getKeysAsArray();
			for (int i = 0; i < keys.length; i++) {
				if (!keys[i].equals(TWA.SUBCLASS))
					sb.append(keys[i]).append("=").append(t.properties().getPropertyValue(keys[i])).append("\n");
			}
			String c2 = sb.toString();

			String c3 = getTrackedDesc(t);

			TreeNode timer = t.getParent();
			while (!timer.classId().equals(N_TIMER.label()))
				timer = timer.getParent();

			String c4 = timerDesc.get(timer);

			String c5 = t.getParent().id();

			List<TreeGraphDataNode> comps = (List<TreeGraphDataNode>) get(t.edges(Direction.OUT),
//					selectZeroOrMany(orQuery(hasTheLabel(E_TRACKCOMPONENT.label()), hasTheLabel(E_TRACKPOP.label()))),
					selectZeroOrMany(hasTheLabel(E_SAMPLECOMPONENT.label())), edgeListEndNodes());

			sb = new StringBuilder();
			for (TreeGraphDataNode comp : comps)
				sb.append(comp.id()).append("\n");

			String c6 = sb.toString();
			entries.add(new StringBuilder().append(c1).append(sep).append(c2).append(sep).append(c3).append(sep)
					.append(c4).append(sep).append(c5).append(sep).append(c6).toString());

		}
		if (!spaceTypes.isEmpty()) {

		}
		return entries;
	}

	@SuppressWarnings("unchecked")
	private String getTrackedDesc(TreeGraphDataNode tracker) {
		// This should return a list of strings otherwise it will get too long
		List<ALEdge> tableEdges = (List<ALEdge>) get(tracker.edges(Direction.OUT),
				selectZeroOrMany(hasTheLabel(E_TRACKTABLE.label())));

		List<TreeGraphDataNode> fields = (List<TreeGraphDataNode>) get(tracker.edges(Direction.OUT),
				selectZeroOrMany(hasTheLabel(E_TRACKFIELD.label())), edgeListEndNodes());

		StringBuilder sb = new StringBuilder();
		for (ALEdge edge : tableEdges) {

			if (edge instanceof ALDataEdge) {
				TrackerType tp = (TrackerType) ((ALDataEdge) edge).properties()
						.getPropertyValue(P_TRACKEDGE_INDEX.key());
				String s = "";
				for (int i = 0; i < tp.size(); i++) {
					s += "," + tp.getWithFlatIndex(i);
				}
				s = s.replaceFirst(",", "");
				sb.append(", ").append(edge.endNode().id()).append(" (").append(s).append(")");
			}
		}
		sb = new StringBuilder(sb.toString().replaceFirst(", ", ""));
		if (!fields.isEmpty()) {
			String ss = "";
			for (TreeGraphDataNode f : fields)
				ss += ", " + f.id();
			ss = ss.replaceFirst(", ", "");
			sb.append(ss);
		}
		return sb.append("\n").toString();
	}

	private List<String> getInstanceComponentDetails() {
		List<String> entries = new ArrayList<>();
		for (TreeGraphDataNode cmp : instanceComponents) {
			StringBuilder sb = new StringBuilder().append(cmp.getParent().id()).append(sep).append(cmp.id())
					.append(sep);
			sb.append(cmp.properties().getPropertyValue(P_COMPONENT_NINST.key()));
			entries.add(sb.toString());
		}
		return entries;
	}

	@SuppressWarnings("unchecked")
	private List<String> getEntityFunctionsEntries() {
		List<String> entries = new ArrayList<>();
//Map<TreeGraphDataNode, Map<TreeGraphDataNode, List<TreeGraphDataNode>>> ctfLookup
		for (Map.Entry<TreeGraphDataNode, Map<TreeGraphDataNode, List<TreeGraphDataNode>>> entry : ctFuncLookup
				.entrySet()) {
			String c1 = entry.getKey().id();
			Map<TreeGraphDataNode, List<TreeGraphDataNode>> timerMap = entry.getValue();
			for (Map.Entry<TreeGraphDataNode, List<TreeGraphDataNode>> timerFuncs : timerMap.entrySet()) {
				String c2 = timerDesc.get(timerFuncs.getKey());
				List<TreeGraphDataNode> funcs = timerFuncs.getValue();
				for (TreeGraphDataNode func : funcs) {
					String c3 = funcDesc.get(func);
					entries.add(
							new StringBuilder().append(c1).append(sep).append(c2).append(sep).append(c3).toString());
					c1 = "";
					c2 = c1;
				}
			}
		}

		// get all relationTypes and their functions
		TreeGraphDataNode struc = (TreeGraphDataNode) get(system.getChildren(),
				selectZeroOrOne(hasTheLabel(N_STRUCTURE.label())));
		if (struc != null) {
			List<TreeGraphDataNode> rts = (List<TreeGraphDataNode>) get(struc.getChildren(),
					selectZeroOrMany(hasTheLabel(N_RELATIONTYPE.label())));
			for (TreeGraphDataNode rt : rts) {
				String c1 = rt.toShortString();
				List<TreeGraphDataNode> procs = (List<TreeGraphDataNode>) get(rt.edges(Direction.IN),
						selectOneOrMany(hasTheLabel(E_APPLIESTO.label())), edgeListStartNodes());
				for (TreeGraphDataNode proc : procs) {
					TreeNode timer = proc.getParent();
					String c2 = timerDesc.get(timer);
					List<TreeGraphDataNode> funcs = (List<TreeGraphDataNode>) get(proc.getChildren(),
							selectZeroOrMany(hasTheLabel(N_FUNCTION.label())));
					for (TreeGraphDataNode func : funcs) {
						TwFunctionTypes ft = (TwFunctionTypes) func.properties().getPropertyValue(P_FUNCTIONTYPE.key());

						String c3 = funcDesc.get(func);
						entries.add(new StringBuilder().append(c1).append(sep).append(c2).append(sep).append(c3)
								.append("\n").append("Description: ").append(ft.description()).toString());
						c1 = "";
						c2 = c1;
					}
				}
			}
		}

		return entries;
	}

//	@SuppressWarnings("unchecked")
	private List<String> getAgentsIndividualsDetails() {
		List<String> entries = new ArrayList<>();
		// any component whose type is ephemeral - list the components, constants and
		// drivers.
		for (TreeGraphDataNode ct : compTypes) {
//			String c1 = ct.id();
			TreeGraphDataNode eph = (TreeGraphDataNode) get(ct.edges(Direction.OUT),
					selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())), edgeListEndNodes(),
					selectZeroOrOne(hasTheName(ConfigurationReservedNodeId.ephemeral.id())));
			if (eph != null) {
//				List<TreeGraphDataNode> comps = (List<TreeGraphDataNode>) get(ct.getChildren(),
//						selectZeroOrMany(hasTheLabel(N_COMPONENT.label())));
//				for (TreeGraphDataNode comp : comps) {
//					String c2 = comp.id();
//					entries.add(new StringBuilder().append(c1).append(sep).append(c2).toString());
//					c1 = "";
//				}
			}
		}

		return entries;
	}

	@SuppressWarnings("unchecked")
	private List<String> getRNGDetails() {
		List<String> entries = new ArrayList<>();
		for (TreeGraphDataNode rng : rngs) {
			String c1 = rng.id();
			List<TreeGraphDataNode> users = (List<TreeGraphDataNode>) get(rng.edges(Direction.IN),
					selectZeroOrMany(hasTheLabel(E_USERNG.label())), edgeListStartNodes());
			String[] keys = rng.properties().getKeysAsArray();
			for (int i = 0; i < Math.max(users.size(), keys.length); i++) {
				String c2 = "";
				String c3 = "";
				if (i < keys.length) {
					c2 = keys[i];
					c3 = rng.properties().getPropertyValue(keys[i]).toString();
				}

				String c4 = "";
				if (i < users.size())
					c4 = users.get(i).id();
				StringBuilder sb = new StringBuilder();
				sb.append(c1).append(sep).append(c2).append(sep).append(c3).append(sep).append(c4);
				entries.add(sb.toString());
				c1 = "";
			}
		}

		return entries;
	}

	@SuppressWarnings("unchecked")
	private List<String> getTimeDetails() {
		/**
		 * Its not very readable to just dump all the property names and values so this
		 * can't be generalised and is vulnerable to changes to the archetype.
		 */
		List<String> entries = new ArrayList<>();

		StringBuilder sb = new StringBuilder();
		// we only need the scale and origin in units of shortest time unit
		sb.append(timeline.toShortString());
		sb.append(sep).append(P_TIMELINE_SCALE.key()).append(": ")
				.append(timeline.properties().getPropertyValue(P_TIMELINE_SCALE.key()));
		TimeScaleType tst = (TimeScaleType) timeline.properties().getPropertyValue(P_TIMELINE_SCALE.key());
		sb.append("\n(").append(tst.description()).append(")");
		entries.add(sb.toString());
		sb = new StringBuilder();
		sb.append(sep).append(P_TIMELINE_TIMEORIGIN.key())//
				.append("=")//
				.append(timeline.properties().getPropertyValue(P_TIMELINE_TIMEORIGIN.key()))//
				.append(" (units: ").append(timeline.properties().getPropertyValue(P_TIMELINE_SHORTTU.key()))
				.append(")");
		entries.add(sb.toString());

		// clocks: need only TimeUnits and nTimeUnits - actually what is dt for???
		for (TreeGraphDataNode timer : timersClock) {
			sb = new StringBuilder();
			sb.append(timer.toShortString());
			sb.append(sep).append("Type: ").append(ClockTimer.class.getSimpleName());
			entries.add(sb.toString());
			sb = new StringBuilder();

			sb.append(sep).append(P_TIMEMODEL_TU.key()).append(": ")
					.append(timer.properties().getPropertyValue(P_TIMEMODEL_TU.key()));
			TimeUnits tu = (TimeUnits) timer.properties().getPropertyValue(P_TIMEMODEL_TU.key());
			sb.append("\n(").append(tu.description()).append(")");
			entries.add(sb.toString());
			sb = new StringBuilder();

			sb.append(sep).append(P_TIMEMODEL_NTU.key()).append(": ")
					.append(timer.properties().getPropertyValue(P_TIMEMODEL_NTU.key()));
			entries.add(sb.toString());
		}
		for (TreeGraphDataNode timer : timersEvent) {
			sb = new StringBuilder();
			sb.append(timer.toShortString());
			sb.append(sep).append("Type=").append(EventTimer.class.getSimpleName());
			entries.add(sb.toString());
			sb = new StringBuilder();
			for (TreeGraphDataNode feeder : (List<TreeGraphDataNode>) get(timer.edges(Direction.OUT),
					selectOneOrMany(hasTheLabel(E_FEDBY.label())), edgeListEndNodes())) {
				sb.append(sep).append("Fed by: ").append(feeder.toShortString());
				entries.add(sb.toString());
				sb = new StringBuilder();
			}
		}
		for (TreeGraphDataNode timer : timersScenario) {
			sb = new StringBuilder();
			sb.append(timer.toShortString());
			sb.append(sep).append("Type=").append(ScenarioTimer.class.getSimpleName());
			;
			entries.add(sb.toString());/**
										 * TODO: Properties yet to be defined. Probably just a file name and time unit.
										 */

		}
		return entries;
	}

	private List<String> getStoppingConditionDetails() {
		List<String> entries = new ArrayList<>();
		for (TreeGraphDataNode sc : rootStoppingConditions) {
			String c1 = scDesc.get(sc);
			for (String key : sc.properties().getKeysAsArray()) {
				Object value = sc.properties().getPropertyValue(key);
				if (!key.equals(TWA.SUBCLASS)) {
					entries.add(new StringBuilder().append(c1).append(sep).append(key).append("=").append(value)
							.toString());
					c1 = "";
				}
			}
		}
		return entries;

	}

	private List<String> getEntityInitialiserDetails() {
		List<String> entries = new ArrayList<>();
		// NB system is in the compTypes list

		for (TreeGraphDataNode ct : compTypes) {
			TreeGraphDataNode init = (TreeGraphDataNode) get(ct.getChildren(),
					selectZeroOrOne(hasTheLabel(N_INITFUNCTION.label())));
			if (init != null) {
				entries.add(new StringBuilder().append(ct.id()).append(sep).append(funcDesc.get(init)).toString());
			}
		}
		return entries;
	}

//	@SuppressWarnings("unchecked")
//	private String getFlowChart() {
//		SimulatorNode sim = (SimulatorNode) get(cfg.root().getChildren(), selectOne(hasTheLabel(N_SYSTEM.label())),
//				children(), selectOne(hasTheLabel(N_DYNAMICS.label())));
//		Map<Integer, List<List<ProcessNode>>> pco = sim.getProcessCallingOrder();
//
//		/*-
//		 * initialise
//		 * for each time event
//		 * 	if (stopping condition)
//		 * 		for each (a,b,c) (belonging to category with decs)
//		 * 			assign decs  ← zero
//		 * 		if time for time1
//		 * 			with (entities) cf JG: for entities string desc see getProcessAppliesToDesc()
//		 * 				if (decision)
//		 * 					functions
//		 * 						consequence function
//		 *  	etc...
//		 *  	advance time
//		 *  	create/destroy ephemeral relations and components
//		 * 		assign next drivers  ← current values 	
//		 * 
//		 * */
//		StringBuilder flowChart = new StringBuilder();
//		// Stopping conditions
//
//		// initialisation
//		for (TreeGraphDataNode init : initTypes)
//			flowChart.append(funcDesc.get(init)).append("\n");
//
//		flowChart.append("for each time event\n");
//		if (!rootStoppingConditions.isEmpty()) {
//			StringBuilder sb = new StringBuilder().append("\tif ");
//			// TODO: build a better string when we have examples of complex stopping
//			// conditions. All sc that are children of Dynamics are implicitly "OR"
//			for (TreeGraphDataNode sc : rootStoppingConditions) {
//				StoppingConditionNode stpCond = (StoppingConditionNode) sc;
//				sb.append(stpCond.getInstance(0).toString()).append(" or ");
//			}
//			sb.replace(sb.length() - 4, sb.length(), " then\n");
//			sb.append("\t\tend simulation\n");
//
//			flowChart.append(sb.toString());
//		}
//		// init decorators
//		for (TreeGraphDataNode dec : decTypes) {
//			TreeGraphDataNode cat = (TreeGraphDataNode) get(dec.edges(Direction.IN),
//					selectOne(hasTheLabel(E_DECORATORS.label())), startNode());
//			List<TreeGraphDataNode> targets = (List<TreeGraphDataNode>) get(cat.edges(Direction.IN),
//					selectOneOrMany(hasTheLabel(E_BELONGSTO.label())), edgeListStartNodes());
//			String actors = "";
//			for (TreeGraphDataNode target : targets)
//				actors += ", " + target.id();
//			actors = actors.replaceFirst(", ", "");
//			flowChart.append("\tfor each(").append(actors).append(")\n");
//			Map<String, List<String>> details = getDataTreeDetails(dec);
//			for (Map.Entry<String, List<String>> entry : details.entrySet())
//				// we need the element class here assign boolean <- false etc...
//				flowChart.append(sep).append("\tassign ").append(entry.getKey()).append(" ← zero\n");
//		}
//
//		// get dependsOn orders
//		Map<Integer, List<TreeNode>> timerCombos = new HashMap<>();
//
//		for (Map.Entry<Integer, List<List<ProcessNode>>> combo : pco.entrySet()) {
//			List<TreeNode> timerList = new ArrayList<>();
//			timerCombos.put(combo.getKey(), timerList);
//			for (List<ProcessNode> lst : combo.getValue()) {
//				TreeNode timer = lst.get(0).getParent();
//				if (!timerList.contains(timer))
//					timerList.add(timer);
//			}
//		}
//
//		boolean first = true;
//		String procIndent = "\t";
//		for (Map.Entry<Integer, List<TreeNode>> timerCombo : timerCombos.entrySet()) {
//			StringBuilder sb = new StringBuilder();
//			if (timerCombos.size() > 1) {
//				procIndent = "\t\t";
//				if (first) {
//					sb.append("\tif time for (");
//					first = false;
//				} else
//					sb.append("\telse if time for (");
//				for (TreeNode timer : timerCombo.getValue()) {
//					sb.append(timer.id()).append(" & ");
//				}
//				sb.replace(sb.length() - 3, sb.length(), "");
//				sb.append(")\n");
//				flowChart.append(sb.toString());
//			}
//			String funcIndent = procIndent + "\t";
//			List<List<ProcessNode>> pSeq = pco.get(timerCombo.getKey());
//			for (List<ProcessNode> procs : pSeq) {
//				for (ProcessNode proc : procs) {
//					flowChart.append(procIndent).append("with each (").append(getProcessAppliesToDesc(proc))
//							.append(")\n");
//
//					List<TreeNode> funcs = new ArrayList<>();
//					List<TreeNode> trackers = new ArrayList<>();
//					for (TreeNode c : proc.getChildren()) {
//						if (c.classId().equals(N_FUNCTION.label()))
//							funcs.add(c);
//						else if (c.classId().equals(N_DATATRACKER.label()))
//							trackers.add(c);
//					}
//
//					// cf: comments for this method
//					funcs = orderFunc(funcs);
//					Iterator<TreeNode> iter = funcs.iterator();
//					while (iter.hasNext()) {
//						TreeNode func = iter.next();
//						if (hasConsequence(func)) {
//							flowChart.append(funcIndent).append("if ").append(funcDesc.get(func)).append("\n");
//							appendConsequences(funcIndent + "\t", flowChart, func);
//						} else if (isDecisionFunc((TreeGraphDataNode) func)) {
//							if (negationFunc((TreeGraphDataNode) func))
//								flowChart.append(funcIndent).append("if not ").append(funcDesc.get(func))
//										.append(" then\n");
//							else
//								flowChart.append(funcIndent).append("if ").append(funcDesc.get(func)).append(" then\n");
//
//							flowChart.append(funcIndent).append(sep)
//									.append(getDecisionConsequence((TreeGraphDataNode) func)).append("\n");
//						} else {
//							flowChart.append(funcIndent).append(funcDesc.get(func)).append("\n");
//						}
//					}
//
//					// Do trackers last
//					for (TreeNode tracker : trackers) {
//						flowChart.append(funcIndent).append("record ")
//								.append(getTrackedDesc((TreeGraphDataNode) tracker));
//					}
//
//				}
//			}
//		}
//		/**
//		 * TODO Don't know what to do here? How do i know that a relation is ephemeral?
//		 * 
//		 * 1. if either of the to/from cats belongsTo ephemeral category?
//		 * 
//		 * 2. if their processes are located in space?
//		 * 
//		 * TODO: state updates for structure and attributes.
//		 */
////		boolean haveEphemeralRelations = false;
////		flowChart.append("\tadvance time\n");
////		if (haveEphemeralRelations) {
////			flowChart.append("\tremove old relations\n");
////			flowChart.append("\tcreate new relations\n");
////		}
////
////		for (TreeGraphDataNode cmp : ephemeralComponents) {
////			flowChart.append("\tremove old ").append(cmp.id()).append("\n");
////			flowChart.append("\tcreate new ").append(cmp.id()).append("\n");
////		}
////
////		if (!driverTypes.isEmpty())
////			flowChart.append("\tassign drivers  ← newly computed values\n");
//
//		return flowChart.toString();
//	}

//	private String getDecisionConsequence(TreeGraphDataNode func) {
//		String result = "";
//		TwFunctionTypes ft = (TwFunctionTypes) func.properties().getPropertyValue(P_FUNCTIONTYPE.key());
//		switch (ft) {
//		case MaintainRelationDecision: {
//			return "deleteRelation";
//		}
//		case RelateToDecision: {
//			return "setRelation (" + getProcessAppliesToDesc((ProcessNode) func.getParent()) + ")";
//		}
//		case DeleteDecision: {
//			return "deleteComponent";
//		}
//		default: {
//			return result;
//		}
//		}
//	}

//	private boolean negationFunc(TreeGraphDataNode func) {
//		TwFunctionTypes ft = (TwFunctionTypes) func.properties().getPropertyValue(P_FUNCTIONTYPE.key());
//		return ft.equals(TwFunctionTypes.MaintainRelationDecision);
//	}
//
//	private boolean isDecisionFunc(TreeGraphDataNode func) {
//		TwFunctionTypes ft = (TwFunctionTypes) func.properties().getPropertyValue(P_FUNCTIONTYPE.key());
//		return ft.returnType().equals("boolean");
//	}

	/**
	 * ComponentProcess.execute
	 * 
	 * 1. ChangeStateFunction
	 * 
	 * 2. DeleteDecisionFunction something about consequent function here
	 * 
	 * 3. CreateOtherDecisionFunction if space locate
	 * 
	 * 4. others to come....
	 * 
	 * RelationProcess.executeFunctions
	 * 
	 * 1. ChangeOtherStateFunction
	 * 
	 * 2. if !permanent MaintainRelationDecisionFunction if space delete line
	 * 
	 * 3. ChangeRelationStateFunction if space relocate
	 * 
	 * 4. Others to come...
	 */
//	private List<TreeNode> orderFunc(List<TreeNode> funcs) {
//		// TODO: for now we just trust to the order of the enums?
//		funcs.sort(new Comparator<TreeNode>() {
//
//			@Override
//			public int compare(TreeNode o1, TreeNode o2) {
//				TreeGraphDataNode n1 = (TreeGraphDataNode) o1;
//				TreeGraphDataNode n2 = (TreeGraphDataNode) o2;
//				TwFunctionTypes ft1 = (TwFunctionTypes) n1.properties().getPropertyValue(P_FUNCTIONTYPE.key());
//				TwFunctionTypes ft2 = (TwFunctionTypes) n2.properties().getPropertyValue(P_FUNCTIONTYPE.key());
//				return ft1.compareTo(ft2);
//			}
//
//		});
//		return funcs;
//	}

//	@SuppressWarnings("unchecked")
//	private String getProcessAppliesToDesc(ProcessNode proc) {
//
//		// JG: This is where I need your help.
//		List<TreeGraphDataNode> categories = (List<TreeGraphDataNode>) get(proc.edges(Direction.OUT),
//				selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())), edgeListEndNodes(),
//				selectZeroOrMany(hasTheLabel(N_CATEGORY.label())));
//		TreeGraphDataNode relationType = (TreeGraphDataNode) get(proc.edges(Direction.OUT),
//				selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())), edgeListEndNodes(),
//				selectZeroOrOne(hasTheLabel(N_RELATIONTYPE.label())));
//
//		// is this selectOne? must a category that has proc applying to it belong to
//		// just
//		// one componentType?
//		StringBuilder sb = new StringBuilder();
//		// TODO: well this is only a start
//		Set<TreeGraphDataNode> cmpTypes = new HashSet<>();
//		for (TreeGraphDataNode category : categories) {
//			List<TreeGraphDataNode> ct_sys = (List<TreeGraphDataNode>) get(category.edges(Direction.IN),
//					selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())), edgeListStartNodes());
//			// many categories can belong to the same componentType of course so collect in
//			// a set
//			for (TreeGraphDataNode ct : ct_sys)
//				cmpTypes.add(ct);
//		}
//		// One could list all the categories this component belongs to but it will be a
//		// long string and make the flow chart messy.
//		for (TreeGraphDataNode ct : cmpTypes) {
////			List<TreeNode> cmps = (List<TreeNode>) get(ct.getChildren(),
////					selectZeroOrMany(hasTheLabel(N_COMPONENT.label())));
////			// if have components then mention them in preference to the componentType
////			if (!cmps.isEmpty()) {
////				for (TreeNode cmp : cmps) {
////					sb.append(", ").append(cmp.id());
////				}
////			} else
////				sb.append(", ").append(ct.id());
//		}
//		// need a better string here. from/to but this depends on the function type
//		if (relationType != null) {
//			Duple<List<TreeGraphDataNode>, List<TreeGraphDataNode>> fromToCats = getFromToComponentTypes(relationType);
//			String fromStr = getIdList(fromToCats.getFirst());
//			String toStr = getIdList(fromToCats.getSecond());
//			sb.append(", ").append(relationType.id()).append(" (").append(fromStr).append("→").append(toStr)
//					.append(")");
//		}
//
//		return sb.toString().replaceFirst(", ", "");
//	}

	private String getIdList(List<TreeGraphDataNode> l) {
		String result = "";
		if (l == null)
			return result;
		for (TreeGraphDataNode i : l)
			result += ", " + i.id();
		return result.replaceFirst(", ", "");
	}

//	private void appendConsequences(String indent, StringBuilder flowChart, TreeNode parent) {
//		for (TreeNode c : parent.getChildren()) {
//			if (hasConsequence(c)) {
//				flowChart.append(indent).append("if ").append(funcDesc.get(c)).append("\n");
//				appendConsequences(indent + "\t", flowChart, c);
//			} else if (c.classId().equals(N_FUNCTION.label()))
//				flowChart.append(indent).append(funcDesc.get(c)).append("\n");
//		}
//	}

//	@SuppressWarnings("unchecked")
//	private boolean hasConsequence(TreeNode c) {
//		if (!c.classId().equals(N_FUNCTION.label()))
//			return false;
//		List<TreeNode> lst = (List<TreeNode>) get(c.getChildren(), selectZeroOrMany(hasTheLabel(N_FUNCTION.label())));
//		return !lst.isEmpty();
//	}

	@SuppressWarnings("unchecked")
	private List<String> getEntityDetails() {
		List<String> entries = new ArrayList<>();
		for (TreeGraphDataNode ct : compTypes) {

			String c1 = ct.id();
			List<TreeGraphDataNode> drivers = new ArrayList<>();
			List<TreeGraphDataNode> timers = new ArrayList<>();
			List<TreeGraphDataNode> spaces = new ArrayList<>();
//			List<TreeGraphDataNode> comps = (List<TreeGraphDataNode>) get(ct.getChildren(),
//					selectZeroOrMany(hasTheLabel(N_COMPONENT.label())));
			List<TreeGraphDataNode> cats = (List<TreeGraphDataNode>) get(ct.edges(Direction.OUT),
					selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())), edgeListEndNodes());

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < cats.size(); i++) {
				String classifier = cats.get(i).id().replace("*", "");
				sb.append(classifier);
				if (i != cats.size() - 1)
					sb.append("\n");
				TreeGraphDataNode drv = (TreeGraphDataNode) get(cats.get(i).edges(Direction.OUT),
						selectZeroOrOne(hasTheLabel(E_DRIVERS.label())), endNode());
				if (drv != null)
					drivers.add(drv);
				List<TreeGraphDataNode> procs = (List<TreeGraphDataNode>) get(cats.get(i).edges(Direction.IN),
						selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())), edgeListStartNodes());
				for (TreeGraphDataNode proc : procs) {
					if (!timers.contains(proc.getParent()))
						timers.add((TreeGraphDataNode) proc.getParent());
					TreeGraphDataNode space = (TreeGraphDataNode) get(proc.edges(Direction.OUT),
							selectZeroOrOne(hasTheLabel(E_SPACE.label())), endNode());
					if (space != null)
						if (!spaces.contains(space))
							spaces.add(space);
				}
			}
			String c2 = sb.toString();

			sb = new StringBuilder();
//			for (int i = 0; i < comps.size(); i++) {
//				sb.append(comps.get(i).id());
//				if (i != comps.size() - 1)
//					sb.append("\n");
//			}
			String c3 = sb.toString();
			if (c3.isBlank())
				c3 = "none";

			sb = new StringBuilder();
			for (int i = 0; i < drivers.size(); i++) {
				sb.append(drivers.get(i).id());
				if (i != drivers.size() - 1)
					sb.append("\n");
			}
			String c4 = sb.toString();
			if (c4.isBlank())
				c4 = "none";

			sb = new StringBuilder();
			for (int i = 0; i < timers.size(); i++) {
				sb.append(timerDesc.get(timers.get(i)));
				if (i != timers.size() - 1)
					sb.append("\n");
			}
			String c5 = sb.toString();
			if (c5.isBlank())
				c5 = "none";// ??

			sb = new StringBuilder();
			for (int i = 0; i < spaces.size(); i++) {
				sb.append(spaceDesc.get(spaces.get(i)));
				if (i != spaces.size() - 1)
					sb.append("\n");
			}
			String c6 = sb.toString();
			if (c6.isBlank())
				c6 = "non-spatial";

			entries.add(new StringBuilder().append(c1).append(sep).append(c2).append(sep).append(c3).append(sep)
					.append(c4).append(sep).append(c5).append(sep).append(c6).toString());
			c1 = "";

		}
		return entries;
	}

	private List<String> getDataDetails(List<TreeGraphDataNode> dataTypes, String edgeLabel) {
		List<String> entries = new ArrayList<>();
		for (TreeGraphDataNode rec : dataTypes) {
			TreeGraphDataNode cat = (TreeGraphDataNode) get(rec.edges(Direction.IN), selectOne(hasTheLabel(edgeLabel)),
					startNode());
			String c1 = rec.id() + " [" + cat.id() + "]";
			Map<String, List<String>> details = getDataTreeDetails(rec);
			for (Map.Entry<String, List<String>> entry : details.entrySet()) {
				StringBuilder sb = new StringBuilder();
				sb.append(entry.getKey());
				for (String d : entry.getValue()) {
					sb.append(sep).append(d);
				}
				entries.add(c1 + sep + sb.toString());
				c1 = "";
			}
		}
		return entries;
	}

	private List<String> getRelationsDetails() {
		List<String> entries = new ArrayList<>();
		for (TreeGraphDataNode rt : relTypes) {
			String c1 = rt.id();
			Duple<List<TreeGraphDataNode>, List<TreeGraphDataNode>> fromToList = getFromToComponentTypes(rt);
			if (fromToList.getFirst() != null && fromToList.getSecond() != null) {
				String fList = getIdList(fromToList.getFirst());
				String tList = getIdList(fromToList.getSecond());
				entries.add(new StringBuilder().append(c1).append(sep).append(fList).append(" → ").append(tList)
						.toString());
			}
		}
		return entries;
	}

	@SuppressWarnings("unchecked")
	private Duple<List<TreeGraphDataNode>, List<TreeGraphDataNode>> getFromToComponentTypes(TreeGraphDataNode rt) {
		List<TreeGraphDataNode> toCats = (List<TreeGraphDataNode>) get(rt.edges(Direction.OUT),
				selectOneOrMany(hasTheLabel(E_TOCATEGORY.label())), edgeListEndNodes());
		List<TreeGraphDataNode> fromCats = (List<TreeGraphDataNode>) get(rt.edges(Direction.OUT),
				selectOneOrMany(hasTheLabel(E_FROMCATEGORY.label())), edgeListEndNodes());

		List<TreeGraphDataNode> validFromCT = new ArrayList<>();
		List<TreeGraphDataNode> validToCT = new ArrayList<>();

		// select only from/to edges whose categories connect to a componentType(s)
		for (TreeGraphDataNode fromCat : fromCats) {
			List<TreeGraphDataNode> ctFrom = (List<TreeGraphDataNode>) get(fromCat.edges(Direction.IN),
					selectOneOrMany(hasTheLabel(E_BELONGSTO.label())), edgeListStartNodes(),
					selectZeroOrMany(hasTheLabel(N_COMPONENTTYPE.label())));
			if (ctFrom != null)
				for (TreeGraphDataNode n : ctFrom) {
					if (!validFromCT.contains(n))
						validFromCT.add(n);
				}
		}
		for (TreeGraphDataNode toCat : toCats) {
			List<TreeGraphDataNode> ctTo = (List<TreeGraphDataNode>) get(toCat.edges(Direction.IN),
					selectOneOrMany(hasTheLabel(E_BELONGSTO.label())), edgeListStartNodes(),
					selectZeroOrMany(hasTheLabel(N_COMPONENTTYPE.label())));
			if (ctTo != null)
				for (TreeGraphDataNode n : ctTo) {
					if (!validToCT.contains(n))
						validToCT.add(n);
				}
		}
		if (validFromCT.isEmpty() && validToCT.isEmpty()) {
			return new Duple<List<TreeGraphDataNode>, List<TreeGraphDataNode>>(null, null);
		} else
			return new Duple<List<TreeGraphDataNode>, List<TreeGraphDataNode>>(validFromCT, validToCT);

	}

	private List<String> getSpatialUnitsDetails() {
		List<String> entries = new ArrayList<>();
		for (TreeGraphDataNode space : spaceTypes) {
			String c1 = space.toShortString();
			SpaceType st = (SpaceType) space.properties().getPropertyValue(P_SPACETYPE.key());
			entries.add(new StringBuilder().append(c1).append(sep).append("Description: ").append(st.description())
					.toString());
			c1 = "";
			BorderListType blt = (BorderListType) space.properties().getPropertyValue(P_SPACE_BORDERTYPE.key());
			// if (space.properties().hasProperty(P_SPACE_EDGEEFFECTS.key())) {
			EdgeEffectCorrection eEff = BorderListType.getEdgeEffectCorrection(blt);
			entries.add(new StringBuilder().append(c1).append(sep).append("Edge effects: ").append(eEff.description())
					.toString());
			c1 = "";
			// }
			for (String key : space.properties().getKeysAsSet()) {
				StringBuilder sb = new StringBuilder();
				if (!key.equals(P_SPACETYPE.key()) && !key.equals(P_SPACE_EDGEEFFECTS.key())) {
					sb.append(key)//
							.append(": ")//
							.append(space.properties().getPropertyValue(key));

					String c2 = sb.toString();
					entries.add(new StringBuilder().append(c1).append(sep).append(c2).toString());
					c1 = "";
				}
			}
		}
		return entries;

	}

	private List<String> getMetricsDetails() {
		List<String> entries = new ArrayList<>();
		long configSize = (nNodes - baseNodes) + (nEdges - baseEdges) + (nProps - baseProps);
		entries.add(new StringBuilder().append("1 #Nodes").append(sep).append(nf((nNodes - baseNodes))).toString());
		entries.add(new StringBuilder().append("2 #Edges").append(sep).append(nf((nEdges - baseEdges))).toString());
		entries.add(
				new StringBuilder().append("3 #Properties").append(sep).append(nf((nProps - baseProps))).toString());
		entries.add(new StringBuilder().append("4 configuration size (1+2+3)").append(sep).append(nf((configSize)))
				.toString());
		entries.add(new StringBuilder().append("5 #Drivers").append(sep).append(nf((nDrvs - baseDrvs))).toString());
		entries.add(new StringBuilder().append("6 #Constants").append(sep).append(nf((nCnts - baseCnts))).toString());
		entries.add(new StringBuilder().append("7 #Decorators").append(sep).append(nf((nDecs - baseDecs))).toString());
		entries.add(new StringBuilder().append("8 #ComponentTypes").append(sep).append(nf((compTypes.size() - baseCT)))
				.toString());
		entries.add(new StringBuilder().append("9 #RelationTypes").append(sep).append(nf((relTypes.size() - baseRT)))
				.toString());
		entries.add(new StringBuilder().append("10 #GroupTypes").append(sep).append((nf(groupTypes.size() - baseGT)))
				.toString());

		long lineCount = -baseLineCount;
		for (Map.Entry<String, List<String>> snp : snippetMap.entrySet()) {
			for (String line : snp.getValue()) {

				if (!line.trim().startsWith("//") && !line.isBlank()) {
					lineCount++;
				}
			}
		}
		entries.add(new StringBuilder().append("11 #Lines of code").append(sep).append(nf(lineCount)).toString());
		entries.add(new StringBuilder().append("12 #Complexity").append(sep).append(getClassCompressedByteCount())
				.toString());

		return entries;
	}

	private static String nf(long n) {
		return NumberFormat.getNumberInstance().format(n);
	}

	private String getClassCompressedByteCount() {
		long result = 0;
		File projectJarFile = Project.makeFile(cfg.root().id() + ".jar");
		JarFile jf;
		try {
			// NB: Don't use jar input streams. These will not contain the info required!
			jf = new JarFile(projectJarFile.getAbsolutePath());
			Enumeration<JarEntry> e = jf.entries();
			while (e.hasMoreElements()) {
				JarEntry je = (JarEntry) e.nextElement();
				String name = je.getName();
				if (name.endsWith(".class")) {
					result += je.getCompressedSize();
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return nf(result - baseClassByteCount);

	}

	// ------------------------------------- helpers
	private static Map<String, List<String>> getDataTreeDetails(TreeNode record) {
		List<TreeNode> items = new ArrayList<>();
		listItems(record, items);
		Map<String, List<String>> result = new HashMap<>();
		for (TreeNode item : items) {
			TreeGraphDataNode n = (TreeGraphDataNode) item;
			String kDesc = P_TABLE_DESCRIPTION.key();
			String kUnits = P_TABLE_UNITS.key();
			String kType = P_DATAELEMENTTYPE.key();
			String kRange = P_TABLE_RANGE.key();
			String kInterval = P_TABLE_INTERVAL.key();
			if (item.classId().equals(N_FIELD.label())) {
				kDesc = P_FIELD_DESCRIPTION.key();
				kUnits = P_FIELD_UNITS.key();
				kType = P_FIELD_TYPE.key();
				kRange = P_FIELD_RANGE.key();
				kInterval = P_FIELD_INTERVAL.key();
			}
			String desc = "";
			if (n.properties().hasProperty(kDesc))
				desc = (String) n.properties().getPropertyValue(kDesc);
			desc = desc.trim();

			int[][] sizes = Record.collectDims(item);
			String dims = "scalar";
			String txt = "[";
			for (int i = 0; i < sizes.length; i++)
				for (int s : sizes[i])
					txt += ", " + s;
			txt.replaceFirst("' ", "");
			if (txt.length() > 1)
				dims = txt + "]";

			DataElementType det = (DataElementType) n.properties().getPropertyValue(kType);
			String type = det.name();

			String units = "";
			if (n.properties().hasProperty(kUnits)) {
				units = (String) n.properties().getPropertyValue(kUnits);
				units = units.trim();
			}

			String range = "";
			if (n.properties().hasProperty(kRange)) {
				IntegerRange ir = (IntegerRange) n.properties().getPropertyValue(kRange);
				range = ir.toString();
			} else if (n.properties().hasProperty(kInterval)) {
				Interval ir = (Interval) n.properties().getPropertyValue(kInterval);
				range = ir.toString();
			}
			List<String> details = new ArrayList<>();
			details.add(desc);
			details.add(dims);
			details.add(type);
			details.add(units);
			details.add(range);
			result.put(n.id(), details);
		}
		return result;
	}

	private static void listItems(TreeNode record, List<TreeNode> items) {
		for (TreeNode child : record.getChildren()) {
			if (child.classId().equals(N_FIELD.label())) {
				items.add(child);
			} else if (child.classId().equals(N_TABLE.label())) {
				if (child.getChildren().iterator().hasNext()) {
					listItems(child.getChildren().iterator().next(), items);
				} else
					items.add(child);
			}
		}
	}

	private static void writeTable(TextDocument doc, List<String> entries, String... headers) {
		Table table = doc.addTable(entries.size() + 1, headers.length);
		table.setTableName("Table " + tableNumber + ".");

		// none of this optimal width stuff seems to have any effect!!

//		Iterator<Column> ci = table.getColumnIterator();
//		while (ci.hasNext()) {
//			ci.next().setUseOptimalWidth(true);
//		}
//
//		table.setWidth(table.getWidth());
		// col,row
		for (int i = 0; i < headers.length; i++)
			table.getCellByPosition(i, 0).setStringValue(headers[i]);

		for (int i = 0; i < entries.size(); i++) {
			String[] parts = entries.get(i).split(sep);
			for (int j = 0; j < parts.length; j++)
				table.getCellByPosition(j, i + 1).setStringValue(parts[j]);
		}

//		while (ci.hasNext())
//			ci.next().setUseOptimalWidth(true);
//
//		table.setWidth(table.getWidth());

		doc.addParagraph(null);
	}

	private static String getSimpleName(String classStr) {
		try {
			Class<?> klass = (Class<?>) Class.forName(classStr);
			return klass.getSimpleName();
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	private static String formatClassifier(String type) {
		return new StringBuilder().append(" [").append(type).append("]").toString();
	}

	@SuppressWarnings("unchecked")
	private void buildCTFunctionTable() {
		for (TreeGraphDataNode ct : compTypes) {
			List<TreeGraphDataNode> cats = (List<TreeGraphDataNode>) get(ct.edges(Direction.OUT),
					selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())), edgeListEndNodes());
			for (TreeGraphDataNode cat : cats) {
				List<TreeGraphDataNode> procs = (List<TreeGraphDataNode>) get(cat.edges(Direction.IN),
						selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())), edgeListStartNodes());
				for (TreeGraphDataNode proc : procs) {
					TreeGraphDataNode timer = (TreeGraphDataNode) proc.getParent();
					List<TreeGraphDataNode> funcs = (List<TreeGraphDataNode>) get(proc.getChildren(),
							selectZeroOrMany(hasTheLabel(N_FUNCTION.label())));
					for (TreeGraphDataNode func : funcs) {
						addCtfEntry(ct, timer, func);
					}
				}
			}
		}
	}

	private void addCtfEntry(TreeGraphDataNode ct, TreeGraphDataNode timer, TreeGraphDataNode func) {
		Map<TreeGraphDataNode, List<TreeGraphDataNode>> timerMap = ctFuncLookup.get(ct);
		if (timerMap == null) {
			timerMap = new HashMap<>();
			ctFuncLookup.put(ct, timerMap);
		}
		List<TreeGraphDataNode> funcs = timerMap.get(timer);
		if (funcs == null) {
			funcs = new ArrayList<>();
			timerMap.put(timer, funcs);
		}
		if (!funcs.contains(func))
			funcs.add(func);
	}

	// this must have been done somewhere already!
	private static int getDimensions(TreeNode rec) {
		int res = 0;
		for (TreeNode n : rec.getChildren()) {
			if (n.classId().equals(N_FIELD.label()))
				res++;
			if (n.classId().equals(N_TABLE.label())) {
				res += getTableDims(n);
			}
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	private static int getTableDims(TreeNode n) {
		List<TreeGraphDataNode> dims = (List<TreeGraphDataNode>) get(n.edges(Direction.OUT), edgeListEndNodes(),
				selectOneOrMany(hasTheLabel(N_DIMENSIONER.label())));
		int result = 1;
		for (TreeGraphDataNode dim : dims) {
			int s = (Integer) dim.properties().getPropertyValue(P_DIMENSIONER_SIZE.key());
			result *= s;
		}
		if (n.hasChildren())
			result += getDimensions(n.getChildren().iterator().next());
		return result;
	}

}