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
 * 
 * @author Jacques Gignoux - 31 mai 2019
 *
 * NB at the moment I only listed the property names I needed in the code. More will probably
 * come in later.
 *
 */
import static au.edu.anu.twcore.archetype.TwArchetypeConstants.*;

public enum ConfigurationPropertyNames {
	
	P_DIMENSIONER_SIZE 			("size"),
	P_FIELD_TYPE 				("type"),
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
	P_COMPONENT_LIFESPAN		("lifeSpan"),
	P_PARAMETERCLASS			("parameterClass"),
	P_DRIVERCLASS				("driverClass"), 
	P_DECORATORCLASS			("decoratorClass"), 
	P_DYNAMIC					("dynamic"),
	P_FUNCTIONTYPE				("type"),
	P_FUNCTIONCLASS				("userClassName"), 
	P_DATAELEMENTTYPE			("dataElementType"),
	P_FIELDTYPE					("type"),
	P_STOPCD_SUBCLASS			(twaSubclass),// not subClass: to align with TwArchetypeConstants(IDD)
	;

	private final String pname;
	
	private ConfigurationPropertyNames(String pname) {
		this.pname = pname;
	}
	
	public String key() {
		return pname;
	}
}
