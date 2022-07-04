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
package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.TextTranslations;
import fr.cnrs.iees.graph.impl.ALDataEdge;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.twcore.constants.DataElementType;

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

/**
 * @author Ian Davies - 23 July 2021
 */

public class TreatmentTableQuery extends QueryAdaptor {

	/**
	 * input: Treat edge (E_TREATS)
	 * 
	 * Checks all strings can be converted to dataType and that there is at least
	 * one element.
	 */
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		ALDataEdge localItem = (ALDataEdge) input;
		StringTable values = (StringTable) localItem.properties().getPropertyValue(P_TREAT_VALUES.key());
		TreeGraphDataNode endNode = (TreeGraphDataNode) localItem.endNode();
		DataElementType dataType = (DataElementType) endNode.properties().getPropertyValue(P_FIELD_TYPE.key());
		int nvalid = 0;
		for (int i = 0; i < values.size(); i++) {
			String s = values.getByInt(i).trim();
			if (!s.isBlank()) {
					if (!isCorrectType(s, dataType)) {
					// error: one of the values is not of the proper type - 
					// the first value will be reported 
					String[] msgs = TextTranslations.getTreatmentTableQuery2(
						localItem.toShortString(),P_TREAT_VALUES.key(), s, dataType, i);
					actionMsg = msgs[0];
					errorMsg = msgs[1];
					return this;
				}
				nvalid++;
			}
		}
		if (nvalid==0) {
			// error: there is no valid value (only empty strings)
			String[] msgs = TextTranslations.getTreatmentTableQuery(
				localItem.toShortString(),P_TREAT_VALUES.key());
			actionMsg = msgs[0];
			errorMsg = msgs[1];
		}

		return this;
	}

	private static boolean isCorrectType(String s, DataElementType dataType) {
		if (s.isBlank())
			return false;

		switch (dataType) {
		case Double: {
			try {
				Double.parseDouble(s);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		case Integer: {
			try {
				Integer.parseInt(s);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		case Long: {
			try {
				Long.parseLong(s);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		case Float: {
			try {
				Float.parseFloat(s);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		case Boolean: {
			try {
				Boolean.parseBoolean(s);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		case Short: {
			try {
				Short.parseShort(s);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		case Char: {
			return true;
		}
		case Byte: {
			try {
				Byte.parseByte(s);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		default: {
			return true;
		}
		}
	}

}
