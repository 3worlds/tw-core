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
package au.edu.anu.twcore.ecosystem.runtime.tracking;

import fr.cnrs.iees.omugi.collections.tables.*;
import au.edu.anu.twcore.data.runtime.DataLabel;
import au.edu.anu.twcore.data.runtime.IndexedDataLabel;
import au.edu.anu.twcore.data.runtime.OutputTwData;
import au.edu.anu.twcore.data.runtime.TwData;

/**
 * Two methods to recursively extract data from a TwData hierarchy and store them in any message
 * that implements the outpuTwData interface
 * 
 * @author Jacques Gignoux - 3 mars 2021
 *
 */
public class TwDataReader {
	
	// this to prevent any instantiation
	private TwDataReader() {}
	
	/**
	 * This will get any data in a twData hierarchy and put it in any OutputTwData message.
	 * Recursive.
	 * 
	 * @param root
	 * @param lab
	 * @param tsd
	 */
	public static void getValue(TwData root, DataLabel lab, OutputTwData tsd) {
		getRecValue(0,root,lab,tsd);
	}

	// cross-recursive with below
	private static void getTableValue(int depth, Table table, int[] index, DataLabel lab, OutputTwData tsd) {
		if (table instanceof ObjectTable<?>) {
			ObjectTable<?> t = (ObjectTable<?>) table;
			TwData next = (TwData) t.getByInt(index);
			getRecValue(depth, next, lab, tsd);
		} else { // this is a table of primitive types and we are at the end of the label
			if (table instanceof DoubleTable)
				tsd.setValue(lab, ((DoubleTable) table).getByInt(index));
			else if (table instanceof FloatTable)
				tsd.setValue(lab, ((FloatTable) table).getByInt(index));
			else if (table instanceof IntTable)
				tsd.setValue(lab, ((IntTable) table).getByInt(index));
			else if (table instanceof LongTable)
				tsd.setValue(lab, ((LongTable) table).getByInt(index));
			else if (table instanceof BooleanTable)
				tsd.setValue(lab, ((BooleanTable) table).getByInt(index));
			else if (table instanceof ShortTable)
				tsd.setValue(lab, ((ShortTable) table).getByInt(index));
			else if (table instanceof ByteTable)
				tsd.setValue(lab, ((ByteTable) table).getByInt(index));
			else if (table instanceof StringTable)
				tsd.setValue(lab, ((StringTable) table).getByInt(index));
		}
	}
	// cross-recursive with above
	private static void getRecValue(int depth, TwData root, DataLabel lab, OutputTwData tsd) {
		String key = lab.get(depth);
		if (key.contains("["))
			key = key.substring(0, key.indexOf("["));
		if (root.hasProperty(key)) {
			Object next = root.getPropertyValue(key);
			if (next instanceof Table) {
				// case 1: there is an index in the table, saying table elements must be read
				// one by one (slow)
				if (lab instanceof IndexedDataLabel)
					getTableValue(depth + 1, (Table) next, ((IndexedDataLabel) lab).getIndex(depth), lab, tsd);
				// case 2: there is no index with the table name, saying the table is a leaf table 
				// and must be read in full (fast)
				else
					getTableValue(depth + 1, (Table) next, lab, tsd);
			} else { // this is a primitive type and we should be at the end of the label
				if (next instanceof Double)
					tsd.setValue(lab, (double) next);
				else if (next instanceof Float)
					tsd.setValue(lab, (float) next);
				else if (next instanceof Integer)
					tsd.setValue(lab, (int) next);
				else if (next instanceof Long)
					tsd.setValue(lab, (long) next);
				else if (next instanceof Boolean)
					tsd.setValue(lab, (boolean) next);
				else if (next instanceof Short)
					tsd.setValue(lab, (short) next);
				else if (next instanceof Byte)
					tsd.setValue(lab, (byte) next);
				else if (next instanceof String)
					tsd.setValue(lab, (String) next);
			}
		}
	}

	// specific method for leaf tables with no indices, ie that must be read in full
	private static void getTableValue(int depth, Table table, DataLabel lab, OutputTwData tsd) {
		tsd.setValues(lab, table);
	}

	
}
