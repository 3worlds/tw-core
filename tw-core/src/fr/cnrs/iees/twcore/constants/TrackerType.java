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

import fr.cnrs.iees.omugi.collections.tables.*;
import fr.cnrs.iees.omugi.io.parsing.ValidPropertyTypes;

/**
 * @author Ian Davies - 12 Oct 2019
 */
public class TrackerType extends StringTable {

	public TrackerType(Dimensioner[] readDimensioners) {
		super(readDimensioners);
	}

	public static TrackerType valueOf(String value) {
//		if ((value==null)||(value.isBlank())||(value.isEmpty())||(value.equals("null")))
//			return null;
		StringTable st = StringTable.valueOf(value);
		TrackerType result = new TrackerType(st.getDimensioners());
		for (int i = 0; i < st.size(); i++)
			result.setWithFlatIndex(st.getWithFlatIndex(i), i);
		return result;
	}

	public static TrackerType defaultValue() {
		return valueOf("([1]null)");
	}

	static {
		ValidPropertyTypes.recordPropertyType(TrackerType.class.getSimpleName(), TrackerType.class.getName(),
				defaultValue());
	}
}
