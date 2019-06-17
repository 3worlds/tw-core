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

/**
 * Author Ian Davies
 *
 * Date 10 Jan. 2019
 */

/**
 * Node label (N_), Edge label (E_) and Property key (P_) for the 3Worlds
 * configuration graph
 */

/**
 * If we intend to continue with the setDefaultProperties approach (prefer to
 * always require a castingClass property) then make a separate class/enum that
 * associates these constants with casting class strings etc
 */
@Deprecated
public interface Configuration {
	// 3Worlds Node labels
	public static final String N_ROOT = "3worlds";
	public static final String N_DATADEFINITION = "dataDefinition";
	public static final String N_DIMENSIONER = "dimensioner";
	public static final String N_TABLE = "table";
	public static final String N_RECORD = "record";
	public static final String N_FIELD = "field";

	public static final String N_SYSTEM = "system";
	public static final String N_DYNAMICS = "dynamics";
	public static final String N_TIMELINE = "timeLine";
	public static final String N_TIMEMODEL = "timeModel";
	public static final String N_EVENTQUEUE = "eventQueue";
	public static final String N_PROCESS = "process";
	public static final String N_FUNCTION = "function";

	public static final String N_STRUCTURE = "structure";
	public static final String N_CATEGORYSET = "categorySet";
	public static final String N_CATEGORY = "category";
	public static final String N_COMPONENT = "component";
	public static final String N_REALISEDCOMPONENT = "realisedComponent";
	public static final String N_INITIALISER = "initialiser";
	public static final String N_RELATIONTYPE = "relationType";

	public static final String N_EXPERIMENT = "experiment";
	public static final String N_STOPPINGCONDITION = "stoppingCondition";
	public static final String N_DESIGN = "design";
	public static final String N_TREATMENT = "treatment";
	public static final String N_TIMEPERIOD = "timePeriod";
	public static final String N_MODELCHANGE = "modelChange";
	
	public static final String N_UI = "userInterface";
	public static final String N_UITOP = "top";
	public static final String N_UIBOTTOM = "bottom";
	public static final String N_UICENTER = "center";
	public static final String N_UIWIDGET = "widget";

	//TODO?
	public static final String N_LIFECYCLE = "lifeCycle";
	public static final String N_RECRUIT = "recruit";
	public static final String N_PRODUCE = "produce";
	public static final String N_PARAMETERVALUES = "parameterValues";
	public static final String N_DATATRACKER = "dataTracker";
	public static final String N_INITIALSTATE = "initialState";
	public static final String N_DATAIO = "dataIO";


	// 3Worlds edge labels
	public static final String E_FROM = "from";
	public static final String E_TO = "to";
	public static final String E_APPLIESTO = "appliesTo";
	public static final String E_DEPENDSON = "dependsOn";
	public static final String E_INITIALISEDBY = "initialisedBy";
	public static final String E_ENGINE = "engine";
	public static final String E_PARAMETERS = "parameters";
	public static final String E_DRIVERS = "drivers";
	public static final String E_DECORATORS = "decorators";
	public static final String E_MEMBEROF = "memberOf";
	public static final String E_DRIVENBY = "drivenBy";
	public static final String E_FROMCATEGORY = "fromCategory";
	public static final String E_TOCATEGORY = "toCategory";
	public static final String E_BASELINE = "baseline";
	public static final String E_SIZEDBY = "sizedBy";
	public static final String E_CHANNELLISTENER = "channelListener";
	public static final String E_TIMELISTENER = "timeListener";
	public static final String E_STATEMACHINELISTENER = "stateMachineListener";

	// 3Worlds properties
	public static final String P_PARAMETERCLASS = "parameterClass";
	public static final String P_DRIVERCLASS = "driverClass";
	public static final String P_DECORATORCLASS = "decoratorClass";
	public static final String P_LIFESPAN = "lifespan";
	public static final String P_VARIABLENUMBERS = "variableNumbers";
	public static final String P_MEMORY = "memory";
	public static final String P_GENERATEDCLASSNAME = "generatedClassName";
	public static final String P_USERCLASSNAME = "userClassName";
	public static final String P_DYNAMIC = "dynamic";
	public static final String P_PARAMFILE = "paramFile";
	public static final String P_INITFILE = "initFile";

}
