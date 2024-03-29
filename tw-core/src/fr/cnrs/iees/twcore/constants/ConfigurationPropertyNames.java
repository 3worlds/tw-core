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
	P_MODEL_IMPORTSNIPPET		("importSnippet"),
	
	/**
	 * Integer size of a dimension.
	 */
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
	P_TREAT_VALUES				("values"),
	P_TREAT_VALUENAMES			("valueNames"),
	P_TREAT_RANK				("rank"),
	
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
	P_TIMEMODEL_OFFSET			("offset"),
	
	P_COMPONENT_LIFESPAN		("lifeSpan"),
	P_COMPONENT_MOBILE			("mobile"),
	P_COMPONENT_NINST			("nInstances"),
	
	P_RELATION_LIFESPAN			("lifeSpan"),
	P_RELATION_DIRECTIONAL		("directional"),
	
	P_GROUP_NINST				("nInstances"),
	
//	P_PARAMETERCLASS			("parameterClass"),
	P_DRIVERCLASS				("driverClass"),
	P_DECORATORCLASS			("decoratorClass"),
	P_CONSTANTCLASS				("constantClass"),
	P_DYNAMIC					("dynamic"),
	
	P_FUNCTIONTYPE				("type"),
	P_FUNCTIONSNIPPET			("functionSnippet"),
	P_FUNCTIONCLASS				("userClassName"),
	
	P_TWDATACLASS				("generatedClassName"),
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
	
	P_UI_LAYOUT_ORIENT			("orientation"),
	P_UI_LAYOUT_ORDER			("layoutOrder"),
	
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
	P_SPACE_SEARCHNEIGHBOURS	("searchNeighbours"),
	P_SPACE_COORD_RANK			("rank"),
	P_SPACE_EDGEEFFECTS			("edgeEffects"),
	P_SPACE_BORDERTYPE			("borderType"),
	P_SPACE_GUARDAREA			("guardAreaWidth"),
	P_SPACE_OBSWINDOW			("observationWindow"),
	
	P_WIDGET_SIM_ID				("simId"),
	P_WIDGET_NSIMS				("nSims"),
	P_WIDGET_LEAST_SIM_ID		("leastSimId"),
	P_WIDGET_PALETTE			("palette"),
	P_WIDGET_MAXAXES			("maxAxes"),
	P_WIDGET_BUFFERSIZE			("bufferSize"),
	P_WIDGET_REFRESHRATE		("refreshRate"),
	P_WIDGET_NVIEWS				("nViews"),
	P_WIDGET_Z_RANGE			("zRange"),
	P_WIDGET_Z_PRECISION		("zPrecision"),
	P_WIDGET_ASAVERAGE			("asAverage"),
	P_WIDGET_SAMPLETIMES		("sampleTimes"),
	P_WIDGET_IMAGE_MAG			("imageMagnify"	),
	P_WIDGET_MV_COLOUR			("missingValueColour"),
	P_WIDGET_MV_METHOD			("missingValueMethod"),
	P_WIDGET_ELEMENT_SIZE		("elementSize"),
	
	P_EXP_DEPLOYMENT			("deployment"),
	P_EXP_NREPLICATES			("nReplicates"),
	P_EXP_DIR					("directory"),
	P_EXP_PRECIS				("precis"),
	P_EXP_DETAILS				("experimentDetails"),
	
	P_HLWIDGET_NLINES			("nLines"),
	P_HLWIDGET_ZERO_MIN			("isZeroMinMeansPlot"),
	
	P_DATASOURCE_IDLC			("idLifeCycle"),
	P_DATASOURCE_IDGROUP		("idGroup"),
	P_DATASOURCE_IDCOMPONENT	("idComponent"),
	P_DATASOURCE_IDRELATION		("idRelation"),
	P_DATASOURCE_IDVAR			("idVariable"),
	P_DATASOURCE_DIM			("dim"),
	P_DATASOURCE_READ			("read"),
	P_DATASOURCE_SEP			("separator"),
	P_DATASOURCE_SHEET			("sheet"),
	;

	private final String pname;

	private ConfigurationPropertyNames(String pname) {
		this.pname = pname;
	}

	public String key() {
		return pname;
	}
}
