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

public enum ConfigurationPropertyNames {

	P_MODEL_AUTHORS 			("authors"),
	P_MODEL_CONTACTS 			("contacts"),
	P_MODEL_CITATIONS 			("publication"),
	P_MODEL_VERSION 			("version"),
	P_MODEL_BUILTBY             ("built-by"),
	P_MODEL_PRECIS              ("precis"),
	P_DIMENSIONER_SIZE 			("size"),
	P_DIMENSIONER_RANK 			("rank"),
	P_FIELD_TYPE 				("type"),
	P_FIELD_RANGE 				("range"),
	P_FIELD_INTERVAL 			("interval"),
	P_FIELD_PREC 				("precision"),
	P_FIELD_UNITS 				("units"),
	P_FIELD_DESCRIPTION			("description"),
	P_FIELD_LABEL 				("hlabel"),
	P_DESIGN_TYPE				("type"),
	P_DESIGN_FILE				("file"),
	P_TREATMENT_REPLICATES		("replicates"),
	P_MODELCHANGE_PARAMETER		("parameter"),
	P_MODELCHANGE_REPLACEWITH	("replaceWith"),
	P_TIMEPERIOD_START			("start"),
	P_TIMEPERIOD_END			("end"),
	P_TIMELINE_SCALE			("scale"),
	P_TIMELINE_SHORTTU			("shortestTimeUnit"),
	P_TIMELINE_LONGTU			("longestTimeUnit"),
	P_TIMELINE_TIMEORIGIN		("timeOrigin"),
	P_TIMEMODEL_TU				("timeUnit"),
	P_TIMEMODEL_NTU             ("nTimeUnits"),
	P_TIMEMODEL_DT				("dt"),
	P_TIMEMODEL_SUBCLASS		("subclass"),
	P_COMPONENT_LIFESPAN		("lifeSpan"),
	P_RELATION_LIFESPAN			("lifeSpan"),
	P_COMPONENT_MOBILE			("mobile"),
	P_COMPONENT_NINST			("nInstances"),
	P_GROUP_NINST				("nInstances"),
	P_PARAMETERCLASS			("parameterClass"),
	P_DRIVERCLASS				("driverClass"),
	P_DECORATORCLASS			("decoratorClass"),
	P_CONSTANTCLASS				("constantClass"),
	P_DYNAMIC					("dynamic"),
	P_FUNCTIONTYPE				("type"),
	P_FUNCTIONCLASS				("userClassName"),
	P_INITIALISERCLASS			("userClassName"),
	P_DATAELEMENTTYPE			("dataElementType"),
	P_TABLE_DESCRIPTION			("description"),
	P_TABLE_RANGE 				("range"),
	P_TABLE_INTERVAL 			("interval"),
	P_TABLE_PREC 				("precision"),
	P_TABLE_UNITS 				("units"),
	P_STOPCD_SUBCLASS			("subclass"),
	P_WIDGET_SUBCLASS			("subclass"),
	P_DATASOURCE_SUBCLASS		("subclass"),
	P_DATATRACKER_SUBCLASS		("subclass"),
	P_SA_SUBCLASS               ("subclass"),
	P_STOPCD_ENDTIME			("endTime"),
	P_STOPCD_STOPVAR			("stopVariable"),
	P_STOPCD_STOPVAL			("stopValue"),
	P_STOPCD_RANGE				("range"),
	P_UICONTAINER_ORIENT		("orientation"),
	P_UIORDER					("order"),
	P_DATATRACKER_SELECT		("samplingMode"),
	P_DATATRACKER_STATISTICS	("statistics"),
	P_DATATRACKER_TABLESTATS	("tableStatistics"),
	P_DATATRACKER_TRACK			("track"),
	P_DATATRACKER_SAMPLESIZE	("sampleSize"),
	P_DATASOURCE_FILE			("file"),
	P_TRACKPOP_VAR				("variables"),
	P_TRACKEDGE_INDEX           ("index"),
	P_RNGALG                    ("algorithm"),
	P_RNGSEEDSOURCE             ("seedSource"),
	P_RNGRESETIME               ("resetTime"),
	P_RNGTABLEINDEX             ("tableIndex"),
	P_RELATIONTYPE				("type"),
	P_RELATEPRODUCT				("relateToProduct"),
	P_SPACES_FIXED_POINTS		("fixedPoints"),
	P_SPACETYPE					("type"),
	P_SPACE_XLIM				("x-limits"),
	P_SPACE_YLIM				("y-limits"),
	P_SPACE_ZLIM				("z-limits"),
	P_SPACE_PREC 				("precision"),
	P_SPACE_UNITS 				("units"),
	P_SPACE_CELLSIZE			("cellSize"),
	P_SPACE_NX	 				("x-nCells"),
	P_SPACE_NY 					("y-nCells"),
	P_RELOCATEFUNCTION			("relocateFunctionName"),
	P_SPACENAMES				("spaces"),
	P_SPACE_SEARCHRADIUS		("searchRadius"),
//	P_SPACE_COORDINATES			("coordinates"),
	P_SPACE_COORD_RANK			("rank"),
	P_SPACE_EDGEEFFECTS			("edgeEffects"),
	P_SPACE_BORDERTYPE			("borderType"),
	P_SPACE_GUARDAREA			("guardAreaWidth"),
	P_SPACE_OBSWINDOW			("observationWindow"),
	P_SNIPPET_JAVACODE          ("javaCode"),
	P_WIDGET_SENDER				("sender"),
	P_WIDGET_MAXAXES			("maxAxes"),
	P_WIDGET_BUFFERSIZE			("bufferSize"),
	P_WIDGET_REFRESHRATE		("refreshRate"),
	P_EXP_DEPLOYMENT			("deployment"),
	P_EXP_NREPLICATES			("nReplicates"),
	;

	private final String pname;

	private ConfigurationPropertyNames(String pname) {
		this.pname = pname;
	}

	public String key() {
		return pname;
	}
}
