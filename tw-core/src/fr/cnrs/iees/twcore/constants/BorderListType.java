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
import fr.cnrs.iees.OmugiException;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;

/**
 * @author Ian Davies
 *
 * @date 12 Oct 2019
 */
public class BorderListType extends StringTable {

	public BorderListType(Dimensioner[] readDimensioners) {
		super(readDimensioners);
	}

	// private static char QUOTE = '"';
	// TODO IDD something needs to be done about this duplication
	public static BorderListType valueOf(String value) {
		if ((value == null) || value.isBlank() || value.isEmpty())
			return null;
		char[][] bdel = bdel();
		char[] isep = isep();

		String ss = TableAdapter.getBlockContent(value, bdel[TABLEix]);
		String d = ss.substring(0, ss.indexOf(bdel[DIMix][BLOCK_CLOSE]) + 1);
		BorderListType result = new BorderListType(readDimensioners(d, bdel[DIMix], isep[DIMix]));
		ss = ss.substring(ss.indexOf(bdel[DIMix][BLOCK_CLOSE]) + 1);
		StringBuilder sb = new StringBuilder();
		int n = 0;
		boolean inquote = false;
		for (int i = 0; i < ss.length(); i++) {
			char c = ss.charAt(i);
			if (c == DOUBLEQUOTE)
				inquote = !inquote;
			else if (inquote)
				sb.append(c);
			else if (!inquote) {
				if (c == isep[TABLEix]) {
					if (n == result.flatSize - 1)
						throw new OmugiException("Too many values read: table size == " + result.flatSize);
					result.data[n++] = sb.toString().trim();
					sb = new StringBuilder();
				} else
					sb.append(c);
			}
		}
		result.data[n++] = sb.toString().trim();
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

	public static BorderListType defaultValue() {
		return valueOf("([1]null)");
	}

	public static EdgeEffectCorrection getEdgeEffectCorrection(BorderListType blt) {

		boolean periodic = true;
		for (int i = 0; i < blt.size(); i++)
			if (!blt.getWithFlatIndex(i).equals(BorderType.wrap.name()))
				periodic = false;
		if (periodic)
			return EdgeEffectCorrection.periodic;

		boolean reflective = true;
		for (int i = 0; i < blt.size(); i++)
			if (!blt.getWithFlatIndex(i).equals(BorderType.reflection.name()))
				reflective = false;
		if (reflective)
			return EdgeEffectCorrection.reflective;

		boolean island = true;
		for (int i = 0; i < blt.size(); i++)
			if (!blt.getWithFlatIndex(i).equals(BorderType.oblivion.name()))
				reflective = false;
		if (island)
			return EdgeEffectCorrection.island;

		boolean unbounded = true;
		for (int i = 0; i < blt.size(); i++)
			if (!blt.getWithFlatIndex(i).equals(BorderType.infinite.name()))
				unbounded = false;
		if (unbounded)
			return EdgeEffectCorrection.unbounded;

		boolean bounded = true;
		for (int i = 0; i < blt.size(); i++)
			if (!blt.getWithFlatIndex(i).equals(BorderType.sticky.name()))
				bounded = false;
		if (bounded)
			return EdgeEffectCorrection.bounded;

		boolean tubular = true;
		if (!blt.getWithFlatIndex(0).equals(BorderType.wrap.name()))
			tubular = false;
		if (!blt.getWithFlatIndex(1).equals(BorderType.wrap.name()))
			tubular = false;
		for (int i = 2; i < blt.size(); i++)
			if (blt.getWithFlatIndex(i).equals(BorderType.wrap.name()))
				tubular = false;
		if (tubular)
			return EdgeEffectCorrection.tubular;

		return EdgeEffectCorrection.custom;
	}

	public static int getUnpairedWrapIndex(BorderListType blt) {
		String wrap = BorderType.wrap.name();
		for (int i = 0; i < blt.size() - 1; i += 2) {
			String b1 = blt.getWithFlatIndex(i);
			String b2 = blt.getWithFlatIndex(i + 1);
			if (b1.equals(wrap) && !b2.equals(wrap))
				return i;
			if (!b1.equals(wrap) && b2.equals(wrap))
				return i;
		}
		return -1;
	}

	static {
		ValidPropertyTypes.recordPropertyType(BorderListType.class.getSimpleName(), BorderListType.class.getName(),
				defaultValue());
	}
}
