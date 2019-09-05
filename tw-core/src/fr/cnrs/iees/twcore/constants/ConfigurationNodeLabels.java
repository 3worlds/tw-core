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
import au.edu.anu.twcore.ecosystem.Ecosystem;
import au.edu.anu.twcore.experiment.*;
import au.edu.anu.twcore.ui.*;
import au.edu.anu.twcore.ecosystem.structure.*;
import au.edu.anu.twcore.ecosystem.dynamics.*;
import au.edu.anu.twcore.ecosystem.dynamics.initial.*;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemFactory;
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
	//							| label				| 	class					| initialisation rank
	//----------------------------------------------------------------------------------------
	N_ROOT 						("3worlds",				World.class,				0),
		N_DATADEFINITION 		("dataDefinition", 		DataDefinition.class,		0),
			N_DIMENSIONER 		("dimensioner",			DimNode.class,				DIMBASE),
			N_TABLE 			("table",				TableNode.class,			DIMBASE+10),
			N_RECORD 			("record",				Record.class,				0),
			N_FIELD 			("field",				Field.class,				0),
		N_SYSTEM 				("system",				Ecosystem.class,			ECOBASE), 
			N_DYNAMICS 			("dynamics",			SimulatorNode.class,		Math.max(SIMBASE+10,TIMEBASE+20)), // after stopping conditions AND TimerModels
			N_TIMELINE 			("timeLine",			TimeLine.class,				TIMEBASE), 
			N_TIMEMODEL			("timeModel",			TimeModel.class,			TIMEBASE+10),
				N_EVENTQUEUE	("eventQueue",			EventQueue.class,			TIMEBASE+20),
				N_PROCESS 		("process",				ProcessNode.class,			TIMEBASE+30), // after TimeModel, Function & EventQueue
				N_FUNCTION 		("function",			FunctionNode.class,			0), // ***
				N_DATATRACKER 	("dataTracker",			DataTracker.class,			0),
				N_LIFECYCLE 	("lifeCycle",			LifeCycle.class,			ECOBASE+10), 
				N_RECRUIT 		("recruit",				Recruit.class,				0),
				N_PRODUCE 		("produce",				Produce.class,				0),
				N_STOPPINGCONDITION("stoppingCondition",StoppingConditionNode.class,SIMBASE), // before Simulator
				N_INITIALISER 	("initialiser",			Initialiser.class,			CATEGORYBASE+20), // after relation
				N_INITIALSTATE 	("initialState",		InitialState.class,			ECOBASE+10), // after Ecosystem
				N_GROUP 		("group",				Group.class,				CATEGORYBASE+20), // after LifeCycle and SystemFactory
				N_INDIVIDUAL 	("individual",			Individual.class,			CATEGORYBASE+30), // after Group 
				N_PARAMETERVALUES("parameterValues",	ParameterValues.class,		CATEGORYBASE+40), // after InitialState, Group and Individual
				N_VARIABLEVALUES("variableValues",		VariableValues.class,		CATEGORYBASE+50), // after ParameterValues
			N_STRUCTURE 		("structure",			Structure.class,			0),
				N_CATEGORYSET 	("categorySet",			CategorySet.class,			0),
				N_CATEGORY 		("category",			Category.class,				CATEGORYBASE), 
				N_COMPONENT 	("component",			SystemFactory.class,		CATEGORYBASE+10), // after category and categorySet
				N_RELATIONTYPE 	("relationType",		RelationType.class,			CATEGORYBASE+10), // after category
				// I am not sure this one is needed - for initialisation maybe ?
//				N_REALISEDCOMPONENT("realisedComponent",Object.class,				0), // ComplexSystem ?
				// IDD: As i understood it, component was "species" and realisedComponent was "Stage"
		N_EXPERIMENT 			("experiment",			Experiment.class,			SIMBASE+20),
			N_DESIGN 			("design",				Design.class,				0),
			N_TREATMENT 		("treatment",			Treatment.class,			0),
			N_TIMEPERIOD 		("timePeriod",			TimePeriod.class,			0),
			N_MODELCHANGE 		("modelChange",			ModelChange.class,			0),
			N_DATASOURCE 		("dataSource",			DataSource.class,			0),
			N_DATASINK 			("dataSink",			DataSink.class,				0),
			//TODO
//			N_DATAIO 			("dataIO",				Object.class,				0),
		N_UI 					("userInterface",		TwUI.class,					UIBASE+10), // after widget
			// TODO - Ian, I dont know how this fits with your work - I just defined these node classes
		// to get the configuration load correctly, but feel free to merge them with your classes as needed
		// NB you can also use the Singleton<T> interface if you want them to just be a node producing
		// a class doing the real job
			N_UITOP 			("top",					UITop.class,					UIBASE),
			N_UIBOTTOM 			("bottom",				UIBottom.class,				UIBASE),
			N_UITAB 			("tab",					UITab.class,					UIBASE),
			N_UICONTAINER		("container",			UIContainer.class,					UIBASE),
			N_UIWIDGET 			("widget",				WidgetNode.class,			UIBASE), 
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
