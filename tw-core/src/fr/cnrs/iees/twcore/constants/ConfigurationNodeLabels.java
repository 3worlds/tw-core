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
package fr.cnrs.iees.twcore.constants;

import au.edu.anu.twcore.data.*;
import au.edu.anu.twcore.ecosystem.structure.newapi.ComponentType;
import au.edu.anu.twcore.experiment.*;
import au.edu.anu.twcore.ui.*;
import au.edu.anu.twcore.ecosystem.structure.*;
import au.edu.anu.twcore.predefined.*;
import au.edu.anu.twcore.exceptions.TwcoreException;
import au.edu.anu.twcore.ecosystem.ArenaType;
import au.edu.anu.twcore.ecosystem.dynamics.*;
import au.edu.anu.twcore.ecosystem.dynamics.initial.*;
import au.edu.anu.twcore.ecosystem.runtime.system.ElementFactory;
import au.edu.anu.twcore.root.World;

/**
 * Mapping of 3w tree node labels to class types (for use in a factory)
 * + initialisation order for the init sequence
 *
 * @author Jacques Gignoux - 23 mai 2019
 *
 */
public enum ConfigurationNodeLabels implements InitialisationRanks {
	// (***) implementers of the Singleton interface
	// All nodes are 'ready' at anytime (i.e. order indifferent) except potentially those using sealable classes (***)
	//
	//========================================================================================
	//							| label 			|def name		| 	class					| initialisation rank
	//----------------------------------------------------------------------------------------
	N_ROOT 						("3worlds",			"tw",			World.class,				0),//
		N_PREDEFINED 			("predefined",		"pDef",			Predefined.class,			0),//
		N_DATADEFINITION 		("dataDefinition",	"dDef",			DataDefinition.class,		0),//
			N_DIMENSIONER 		("dimensioner",		"dim1",			DimNode.class,				DIMBASE),//
			N_TABLE 			("table",			"tbl1",			TableNode.class,			DIMBASE+10),//
			N_RECORD 			("record",			"rec1",			Record.class,				0),//
			N_FIELD 			("field",			"fld1",			FieldNode.class,				0),//
			N_RNG               ("rng",				"gen1",			RngNode.class,              DIMBASE),//
		N_SYSTEM 				("system",			"sys1",			ArenaType.class,			ECOBASE),//
			N_DYNAMICS 			("dynamics",		"sim1",			SimulatorNode.class,Math.max(SIMBASE+10,// after stopping conditions
																						Math.max(TIMEBASE+20,	// AND TimerModels
																							CATEGORYBASE+60))), // AND a fully initialised ECOSYSTEM
				N_TIMELINE 		("timeline",		"tmLn1",		Timeline.class,				TIMEBASE),//
				N_TIMER			("timer",			"tmr1",			TimerNode.class,			TIMEBASE+10),// after timeLine
				N_PROCESS 		("process",			"p1",			ProcessNode.class,			TIMEBASE+30),	// after TimeModel, Function & EventQueue
				N_FUNCTION 		("function",		"F1",			FunctionNode.class,			0), // ***
				N_DATATRACKER 	("dataTracker",		"trk1",			DataTrackerNode.class,			0),//
				N_LIFECYCLE 	("lifeCycle",		"lfcy1",		LifeCycle.class,			ECOBASE+10),	// after Ecosystem
				N_RECRUIT 		("recruit",			"rct1",			Recruit.class,				0),//
				N_PRODUCE 		("produce",			"prd1",			Produce.class,				0),//
				N_STOPPINGCONDITION("stoppingCondition","stCd1",	StoppingConditionNode.class,SIMBASE), // before Simulator
				N_INITFUNCTION 	("initFunction",	"init1",		InitFunctionNode.class,		CATEGORYBASE+20), // after relation
				N_INITIALSTATE 	("initialState",	"state",		InitialState.class,			ECOBASE+10), // after Ecosystem
				N_GROUP 		("group",			"grp1",			Group.class,				CATEGORYBASE+20), // after LifeCycle and SystemFactory
				N_COMPONENT 	("component",		"cmp1",			Component.class,			CATEGORYBASE+30), // after Group
				N_CONSTANTVALUES("constantValues",	"initCsts1",	ConstantValues.class,		CATEGORYBASE+40), // after InitialState, Group and Individual
				N_VARIABLEVALUES("variableValues",	"initVars1",	VariableValues.class,		CATEGORYBASE+50), // after ParameterValues
			N_STRUCTURE 		("structure",		"struc1",		Structure.class,			0),//
				N_CATEGORYSET 	("categorySet",		"set1",			CategorySet.class,			0),//
				N_CATEGORY 		("category",		"cat1",			Category.class,				CATEGORYBASE),
				N_TEMPLATE 		("template",		"tpl1",			ElementFactory.class,		CATEGORYBASE+10),
				N_COMPONENTTYPE ("componentType",	"compTyp1",		ComponentType.class,		CATEGORYBASE+10), // after category and categorySet
				N_RELATIONTYPE 	("relationType",	"relTyp1",		RelationType.class,			CATEGORYBASE+10), // after category
				N_ARENA 		("arena",			"arena",		ArenaNode.class,			CATEGORYBASE),
				N_SPACE 		("space",			"space1",		SpaceNode.class,			CATEGORYBASE),
		N_EXPERIMENT 			("experiment",		"expt",			Experiment.class,			SIMBASE+20),	// after fully iinitialised simulator
			N_DESIGN 			("design",			"dsgn",			Design.class,				0),//
			N_TREATMENT 		("treatment",		"trt",			Treatment.class,			0),//
			N_TIMEPERIOD 		("timePeriod",		"tp",			TimePeriod.class,			0),// Unused. not required for SingleRun types?
			N_MODELCHANGE 		("modelChange",		"moChg",		ModelChange.class,			0),//
			N_DATASOURCE 		("dataSource",		"dSrc",			DataSource.class,			0),//
			N_DATASINK 			("dataSink",		"dSnk",			DataSink.class,				0),//
		N_UI 					("userInterface",	"gui",			TwUI.class,					UIBASE+10), // after widget
		    N_UIHEADLESS        ("headless",        "hl",           UIHeadless.class,           UIBASE),//
			N_UITOP 			("top",				"top",			UITop.class,				UIBASE),//
			N_UIBOTTOM 			("bottom",			"btm",			UIBottom.class,				UIBASE),//
			N_UITAB 			("tab",				"tab1",			UITab.class,				UIBASE),//
			N_UICONTAINER		("container",		"cont1",		UIContainer.class,			UIBASE),//
			N_UIWIDGET 			("widget",			"wgt1",			WidgetNode.class,			UIBASE), //
	;
	//========================================================================================
	private final String label;
	private final String defName;
	private final Class<?> type;
	private final int initRank;

	private ConfigurationNodeLabels(String label, String defName,Class<?> type, int initRank) {
				this.label = label;
		this.defName= defName;
		this.type = type;
		this.initRank = initRank;
	}

	public String label() {
		return label;
	}

	public String defName() {
		return defName;
	}

	public Class<?> type() {
		return type;
	}

	public int initRank() {
		return initRank;
	}
	public static ConfigurationNodeLabels labelValueOf(String label) {
		for (ConfigurationNodeLabels lbl: ConfigurationNodeLabels.values()) {
			if (lbl.label.equals(label))
				return lbl;
		}
		throw new TwcoreException("'"+label+"' not found in "+ConfigurationNodeLabels.class.getSimpleName());
	}
}
