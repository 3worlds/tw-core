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

import au.edu.anu.rscs.aot.collections.tables.*;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;

/**
 * @author Ian Davies - 24 Sep 2020
 */
public class BorderListType extends StringTable {
	
	// TODO changing to a 2d array of bounds pairs

	public BorderListType(Dimensioner[] readDimensioners) {
		super(readDimensioners);
	}

	public static BorderListType valueOf(String value) {
		StringTable st = StringTable.valueOf(value);
		BorderListType result = new BorderListType(st.getDimensioners());
		for (int i = 0; i < st.size(); i++)
			result.setWithFlatIndex(st.getWithFlatIndex(i), i);
		return result;
	}

	public static BorderListType defaultValue() {
		return valueOf("([2]wrap,wrap)");
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
				island = false;
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

	public static boolean isWrongTubularOrientation(BorderListType blt) {
		String wrap = BorderType.wrap.name();
		if (blt.size()!=4)
			return false;
		else if (blt.getWithFlatIndex(2).equals(wrap) && !blt.getWithFlatIndex(0).equals(wrap)) 
				return true;
		return false;
	}
	
	static {
		ValidPropertyTypes.recordPropertyType(BorderListType.class.getSimpleName(), BorderListType.class.getName(),
				defaultValue());
	}
}
