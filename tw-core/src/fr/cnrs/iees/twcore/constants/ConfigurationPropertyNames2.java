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

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import java.util.HashMap;
import java.util.Map;

public enum ConfigurationPropertyNames2 {
	/*- won't work with subclasses?? e,g time models etc (WIP)*/
	P_MODEL_AUTHORS /*- */("authors", /*-     */N_ROOT.type(), "Original model authors"), //
	P_MODEL_CONTACTS/*- */("contacts", /*-    */N_ROOT.type(), "Contacts for model authors"), //
	P_MODEL_CITATIONS/*-*/("publication", /*-*/ N_ROOT.type(), "Model publications"), //
	P_MODEL_VERSION/*-  */("version", /*-     */N_ROOT.type(), "Model version number"), //
	P_MODEL_BUILTBY/*-  */("built-by", /*-    */N_ROOT.type(), "User account and date created"), //
	P_MODEL_PRECIS/*-  */("precis", /*-      */ N_ROOT.type(), "Short description of this model"), //
	P_DIMENSIONER_SIZE/*-*/("size", /*-      */ N_DIMENSIONER.type(), "size of a dimension of a table"), //
	P_DIMENSIONER_RANK/*-*/("rank", /*-      */ N_DIMENSIONER.type(), "The order of dimensions"), //
	P_FIELD_TYPE/*-      */("type", /*-        */ N_FIELD.type(), "Data type of the field"), //
	P_FIELD_RANGE/*-     */("range", /*-        */N_FIELD.type(), "Range of legal values for this field"), //
	P_FIELD_INTERVAL/*-  */("interval", /*-    */ N_FIELD.type(), "Range of legal values for this field"), //
	P_FIELD_PREC/*-      */("precision", /*-   */ N_FIELD.type(), "Precision used in outputs"), //
	P_FIELD_UNITS/*-      */("units", /*-      */ N_FIELD.type(), "Display units"), //
	P_FIELD_DESCRIPTION/*-*/("description", /*-*/ N_FIELD.type(), "Description used in generated code"), //
	P_FIELD_LABEL/*-      */("hlabel", /*-     */ N_FIELD.type(), "?"), //
	P_DESIGN_TYPE/*-*/("type", /*-*/N_DESIGN.type(), "In-built experimental design option"), //
	P_DESIGN_FILE/*-*/("file", /*-*/N_DESIGN.type(), "File used to define the experimental design"), //
	P_TREATMENT_REPLICATES/*-*/("replicates", /*-*/ N_TREATMENT.type(), "?"), //
	P_MODELCHANGE_PARAMETER/*-  */("parameter", /*-  */ N_MODELCHANGE.type(), "?"), //
	P_MODELCHANGE_REPLACEWITH/*-*/("replaceWith", /*-*/ N_MODELCHANGE.type(), "?"), //
	P_TIMEPERIOD_START/*-*/("start", /*-*/ N_TIMEPERIOD.type(), ""), //
	P_TIMEPERIOD_END/*-  */("end", /*-   */N_TIMEPERIOD.type(), ""), //
	P_TIMELINE_SCALE/*-*/("scale", /*-              */ N_TIMELINE.type(), ""), //
	P_TIMELINE_SHORTTU/*-*/("shortestTimeUnit", /*- */ N_TIMELINE.type(), ""), //
	P_TIMELINE_LONGTU/*-*/("longestTimeUnit", /*-   */ N_TIMELINE.type(), ""), //
	P_TIMELINE_TIMEORIGIN/*-*/("timeOrigin", /*-    */ N_TIMELINE.type(), ""), //
	P_TIMEMODEL_TU/*-*/("timeUnit", N_TIMER.type(), ""), //
	P_TIMEMODEL_NTU/*-*/("nTimeUnits", N_TIMER.type(), ""), //
	P_TIMEMODEL_SUBCLASS/*-*/("subclass", N_TIMER.type(), ""), //
	P_COMPONENT_LIFESPAN/*-*/("lifeSpan", N_COMPONENT.type(), ""), //
	P_COMPONENT_MOBILE/*-*/("mobile", N_COMPONENT.type(), ""), //
	P_PARAMETERCLASS/*-*/("parameterClass", null, ""), //
	P_DRIVERCLASS/*-*/("driverClass", null, ""), //
	P_DECORATORCLASS/*-*/("decoratorClass", null, ""), //
	P_LTCONSTANTCLASS/*-*/("constantClass", null, ""), //
	P_DYNAMIC/*-*/("dynamic", null, ""), //
	P_FUNCTIONTYPE/*-*/("type", null, ""), //
	P_FUNCTIONCLASS/*-*/("userClassName", null, ""), //
	P_INITIALISERCLASS/*-*/("userClassName", null, ""), //
	P_DATAELEMENTTYPE/*-*/("dataElementType", null, ""), //
	P_TABLE_DESCRIPTION/*-*/("description", null, ""), //
	P_TABLE_RANGE/*-*/("range", null, ""), //
	P_TABLE_INTERVAL/*-*/("interval", null, ""), //
	P_TABLE_PREC/*-*/("precision", null, ""), //
	P_TABLE_UNITS/*-*/("units", null, ""), //
	P_STOPCD_SUBCLASS/*-*/("subclass", null, ""), //
	P_WIDGET_SUBCLASS/*-*/("subclass", null, ""), //
	P_DATASOURCE_SUBCLASS/*-*/("subclass", null, ""), //
	P_DATATRACKER_SUBCLASS/*-*/("subclass", null, ""), //
	P_SA_SUBCLASS/*-*/("subclass", null, ""), //
	P_STOPCD_ENDTIME/*-*/("endTime", null, ""), //
	P_STOPCD_STOPVAR/*-*/("stopVariable", null, ""), //
	P_STOPCD_STOPVAL/*-*/("stopValue", null, ""), //
	P_STOPCD_RANGE/*-*/("range", null, ""), //
	P_UICONTAINER_ORIENT/*-*/("orientation", null, ""), //
	P_UIORDER/*-*/("order", null, ""), //
	P_DATATRACKER_SELECT/*-*/("samplingMode", null, ""), //
	P_DATATRACKER_STATISTICS/*-*/("statistics", null, ""), //
	P_DATATRACKER_TABLESTATS/*-*/("tableStatistics", null, ""), //
	P_DATATRACKER_TRACK /*-*/ ("track", null, ""), //
	P_DATATRACKER_SAMPLESIZE/*-*/ ("sampleSize", null, ""), //
	P_DATASOURCE_FILE/*-*/ ("file", null, ""), //
	P_TRACKPOP_VAR /*-*/ ("variables", null, ""), //
	P_TRACKEDGE_INDEX /*-*/ ("index", null, ""), //
	P_RNGALG /*-*/ ("algorithm", null, ""), //
	P_RNGSEEDSOURCE /*-*/ ("seedSource", null, ""), //
	P_RNGRESETIME /*-*/ ("resetTime", null, ""), //
	P_RNGTABLEINDEX /*-*/ ("tableIndex", null, ""), //
	P_RELATIONTYPE /*-*/ ("type", null, ""), //
	P_RELATEPRODUCT /*-*/ ("relateToProduct", null, ""), //
	P_SPACETYPE /*-*/ ("type", null, ""), //
	P_SPACE_XLIM /*-*/ ("x-limits", null, ""), //
	P_SPACE_YLIM /*-*/ ("y-limits", null, ""), //
	P_SPACE_ZLIM /*-*/ ("z-limits", null, ""), //
	P_SPACE_PREC /*-*/ ("precision", null, ""), //
	P_SPACE_UNITS /*-*/ ("units", null, ""), //
	P_SPACE_CELLSIZE /*-*/ ("cellSize", null, ""), //
	P_SPACE_NX /*-*/ ("x-nCells", null, ""), //
	P_SPACE_NY /*-*/ ("y-nCells", null, ""), //
	P_RELOCATEFUNCTION /*-*/ ("relocateFunctionName", null, ""), //
	P_SPACENAMES /*-*/ ("spaces", null, ""), //
	P_SPACE_SEARCHRADIUS/*-*/ ("searchRadius", null, ""), //
	P_SPACE_COORDINATES /*-*/ ("coordinates", null, ""), //
	P_SPACE_EDGEEFFECTS /*-*/ ("edgeEffects", null, ""),//
	;

	private final String pname;
	private final String description;
	private final Class<?> type;

	private ConfigurationPropertyNames2(String pname, Class<?> klass, String description) {
		this.type = klass;
		this.pname = pname;
		this.description = description;
	}

	public String key() {
		return pname;
	}

	private static Map<String, Map<String, String>> lookup = new HashMap<>();
	static {
		for (ConfigurationPropertyNames2 item : ConfigurationPropertyNames2.values()) {
			if (item.type != null) {
				addEntry(item.type.getSimpleName(), item.key());
			}
		}
	}

	private static void addEntry(String simpleName, String key) {
		// TODO Auto-generated method stub

	}

	public static String description(String classId, String key) {
		Map<String, String> entry = lookup.get(classId);
		if (entry != null)
			return entry.get(key);
		return null;
	}

}
