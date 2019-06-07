package fr.cnrs.iees.twcore.constants;

import au.edu.anu.twcore.data.*;
import au.edu.anu.twcore.ecosystem.Ecosystem;
import au.edu.anu.twcore.experiment.*;
import au.edu.anu.twcore.ui.TwUI;
import au.edu.anu.twcore.ecosystem.structure.*;
import au.edu.anu.twcore.ecosystem.dynamics.*;
import au.edu.anu.twcore.root.World;

/**
 * Mapping of 3w tree node labels to class types (for use in a factory)
 * + initialisation order for the init sequence
 * 
 * @author Jacques Gignoux - 23 mai 2019
 *
 */
public enum ConfigurationNodeLabels {

	// TODO: all 'Object.class' must be replaced by a valid Node descendant
	//========================================================================================
	//							| label				| 	class					| initialisation rank
	//----------------------------------------------------------------------------------------
	N_ROOT 						("3worlds",				World.class,				0),
		N_DATADEFINITION 		("dataDefinition", 		DataDefinition.class,		0),
			N_DIMENSIONER 		("dimensioner",			DimNode.class,				0),
			N_TABLE 			("table",				TableNode.class,			10), // after dimensioners
			N_RECORD 			("record",				Record.class,				0),
			N_FIELD 			("field",				Field.class,				0),
		N_SYSTEM 				("system",				Ecosystem.class,			0),
			N_DYNAMICS 			("dynamics",			Simulator.class,			0), 
				N_TIMELINE 		("timeLine",			TimeLine.class,				0),
				N_TIMEMODEL		("timeModel",			TimeModel.class,			0),
				N_EVENTQUEUE	("eventQueue",			EventQueue.class,			0),
				N_PROCESS 		("process",				ProcessNode.class,			0),
				N_FUNCTION 		("function",			FunctionNode.class,			0),
				N_DATATRACKER 	("dataTracker",			DataTracker.class,			0),
				N_LIFECYCLE 	("lifeCycle",			LifeCycle.class,			0),
				N_RECRUIT 		("recruit",				Recruit.class,				0),
				N_PRODUCE 		("produce",				Produce.class,				0),
				N_STOPPINGCONDITION("stoppingCondition",StoppingConditionNode.class,0),
				N_INITIALISER 	("initialiser",			Initialiser.class,			0),
				N_PARAMETERVALUES("parameterValues",	Object.class,				0),
				N_INITIALSTATE 	("initialState",		Object.class,				0),
			N_STRUCTURE 		("structure",			Structure.class,			0),
				N_CATEGORYSET 	("categorySet",			CategorySet.class,			0),
				N_CATEGORY 		("category",			Category.class,				0), 
				N_COMPONENT 	("component",			SystemFactory.class,		0),
				N_RELATIONTYPE 	("relationType",		RelationType.class,			0),
				// I am not sure this one is needed - for initialisation maybe ?
				N_REALISEDCOMPONENT("realisedComponent",Object.class,				0), // ComplexSystem ?
		N_EXPERIMENT 			("experiment",			Experiment.class,			0),
			N_DESIGN 			("design",				Design.class,				0),
			N_TREATMENT 		("treatment",			Treatment.class,			0),
			N_TIMEPERIOD 		("timePeriod",			TimePeriod.class,			0),
			N_MODELCHANGE 		("modelChange",			ModelChange.class,			0),
			//TODO
			N_DATAIO 			("dataIO",				Object.class,				0),
		N_UI 					("userInterface",		TwUI.class,					0),
			// TODO - for Ian
			N_UITOP 			("top",					Object.class,				0),
			N_UIBOTTOM 			("bottom",				Object.class,				0),
			N_UICENTER 			("center",				Object.class,				0),
			N_UIWIDGET 			("widget",				Object.class,				0), // GridNode + ElementUserInterface
	;
	//========================================================================================
	private final String label;
	private final Class<?> type;
	private final int initRank;
		
	private ConfigurationNodeLabels(String label, Class<?> type, int initRank) {
		this.label = label;
		this.type = type;
		this.initRank = initRank;
	}
	
	public String label() {
		return label;
	}
	
	public Class<?> type() {
		return type;
	}
	
	public int initRank() {
		return initRank;
	}
}
