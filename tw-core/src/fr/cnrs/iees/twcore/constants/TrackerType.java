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

import static fr.cnrs.iees.io.parsing.TextGrammar.*;
import au.edu.anu.rscs.aot.collections.tables.*;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;

/**
 * @author Ian Davies
 *
 * @date 12 Oct 2019
 */
public class TrackerType extends StringTable {

	public TrackerType(Dimensioner[] readDimensioners) {
		super(readDimensioners);
	}

	public static TrackerType valueOf(String value) {
		char[][] bdel = bdel();
		char[] isep = isep();
		String ss = TableAdapter.getBlockContent(value, bdel[TABLEix]);
		String d = ss.substring(0, ss.indexOf(bdel[DIMix][BLOCK_CLOSE]) + 1);
		TrackerType result = new TrackerType(readDimensioners(d, bdel[DIMix], isep[DIMix]));
		ss = ss.substring(ss.indexOf(bdel[DIMix][BLOCK_CLOSE]) + 1);
		String s = null;
		int i = 0;
		while (ss.indexOf(isep[TABLEix]) > 0) {
			s = ss.substring(0, ss.indexOf(isep[TABLEix]));
			ss = ss.substring(ss.indexOf(isep[TABLEix]) + 1);
			result.data[i] = s;
			i++;
		}
		result.data[i] = ss.trim();
		return result;
	}

	private static char[][] bdel() {
		char[][] result = new char[2][2];
		result[Table.DIMix] = DIM_BLOCK_DELIMITERS;
		result[Table.TABLEix] = TABLE_BLOCK_DELIMITERS;
		return result;
	}

	private static char[] isep() {
		char[] result = new char[2];
		result[Table.DIMix] = DIM_ITEM_SEPARATOR;
		result[Table.TABLEix] = TABLE_ITEM_SEPARATOR;
		return result;
	}

	@Override
	public String toString() {
		char[][] bdel = bdel();
		char[] isep = isep();
		StringBuilder sb = new StringBuilder(1024);
		sb.append(bdel[TABLEix][BLOCK_OPEN]).append(bdel[DIMix][BLOCK_OPEN]).append(dimensioners[0].getLength());
		for (int i = 1; i < dimensioners.length; i++)
			sb.append(isep[DIMix]).append(dimensioners[i].getLength());
		sb.append(bdel[DIMix][BLOCK_CLOSE]);
		if (flatSize > 0)
			sb.append(elementToString(0));
		for (int i = 1; i < flatSize; i++)
			sb.append(isep[TABLEix]).append(elementToString(i));
		sb.append(bdel[TABLEix][BLOCK_CLOSE]);
		return sb.toString();
	}

	public static TrackerType defaultValue() {
		return valueOf("([1]null)");
	}

	static {
		ValidPropertyTypes.recordPropertyType(TrackerType.class.getSimpleName(), TrackerType.class.getName(),
				defaultValue());
	}
}
