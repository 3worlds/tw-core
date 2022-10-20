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
package au.edu.anu.twcore.experiment.runtime.io;

import java.util.*;
import java.util.logging.Logger;

import fr.cnrs.iees.omugi.collections.tables.*;
import au.edu.anu.twcore.experiment.runtime.DataLoader;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.omhtk.utils.Logging;
import au.edu.anu.twcore.data.runtime.*; 

/**
 * <p>
 * A data loader to read data from a table fragment. By table fragment I mean an
 * 2D array of strings, each string being a cell read from a table file.
 * </p>
 * <p>
 * This is meant to be used in conjunction with {@link TableDataLoader}.
 * </p>
 * <p>
 * Note that this loader only sets the values of the properties found in the
 * loaded table. Other properties are left untouched. This enables to use
 * multiple files to load a single {@link TwData} object.
 * </p>
 * 
 * TODO: see if we keep this: Note - the name format must use hierarchise record
 * names, eg a:b:c, in column headings if data structures are nested like record
 * within table within record
 * 
 * @author Jacques Gignoux - 31/5/2017, modified from SingleCsvDataLoader
 *         14/2/2012
 * 
 * @see TableDataLoader
 *
 */
public class PropertyDataLoader implements DataLoader<SimplePropertyList> {

	private static Logger log = Logging.getLogger(PropertyDataLoader.class);
	/**
	 * a set of text lines, each one representing a record of the original csv data
	 * file
	 */
	private String[][] input;
	/** the field names in the order in which they come in the original file */
	private String[] headers;
	/** which fields must be read */
	private Set<String> toRead;
	/**
	 * if reading a table, the indices of the columns that contain dimension values
	 */
	private int[] dimCols;

	/**
	 * Constructs a data loader from a csv file fragment.
	 * 
	 * @param input
	 *            - the input lines
	 * @param headers
	 *            - the column headers, if any
	 * @param toRead
	 *            - the columns/fields to read
	 * @param dimCols
	 *            - if reading a table, the indices of the columns that contain
	 *            dimension values
	 */
	public PropertyDataLoader(String[][] input, String[] headers, Set<String> toRead, int[] dimCols) {
		super();
		this.input = input;
		this.headers = headers;
		this.toRead = new HashSet<String>();
		this.toRead.addAll(toRead);
		this.dimCols = dimCols;
	}

	// single line loader - simpler.
	public PropertyDataLoader(String[] input, String[] headers, Set<String> toRead, int[] dimCols) {
		super();
		this.input = new String[1][];
		this.input[0] = input;
		this.headers = headers;
		this.toRead = new HashSet<String>();
		this.toRead.addAll(toRead);
		this.dimCols = dimCols;
	}

	@Override
	public SimplePropertyList load(SimplePropertyList data) {
		for (int row = 0; row < input.length; row++) {
			for (int col = 0; col < input[row].length; col++)
				if (toRead.contains(headers[col])) {
					String header = headers[col];
					String subHeader = header;
					if (headers[col].contains(":")) { // recursion in case of nested data structures
						String[] s = headers[col].split(":", 2);
						header = s[0];
						subHeader = s[1];
					}
					if (data.hasProperty(header)) { 
						Class<?> dataType = data.getPropertyClass(header);
						if (dataType == null)
							throw new NullPointerException("No datatype found in " + data.getClass().getName()
									+ " for column header " + header + ".");
	
						try {
							// Primitives
							if (dataType.isPrimitive()) {
								if (dataType.equals(int.class))
									data.setProperty(header, Integer.valueOf(input[row][col]));
								else if (dataType.equals(long.class))
									data.setProperty(header, Long.valueOf(input[row][col]));
								else if (dataType.equals(float.class))
									data.setProperty(header, Float.valueOf(input[row][col]));
								else if (dataType.equals(double.class))
									data.setProperty(header, Double.valueOf(input[row][col]));
								else if (dataType.equals(boolean.class))
									data.setProperty(header, Boolean.valueOf(input[row][col]));
								else if (dataType.equals(short.class))
									data.setProperty(header, Short.valueOf(input[row][col]));
								else if (dataType.equals(byte.class))
									data.setProperty(header, Byte.valueOf(input[row][col]));
								else if (dataType.equals(char.class))
									data.setProperty(header, input[row][col].charAt(0));
								// String
							} else if (dataType.equals(String.class))
								data.setProperty(header, input[row][col]);
							// Tables
							else if (Table.class.isAssignableFrom(dataType)) {
								int[] indexes = new int[dimCols.length];
								for (int j = 0; j < indexes.length; j++) {
									indexes[j] = Integer.valueOf(input[row][dimCols[j]]);
								}
								if (dataType.equals(DoubleTable.class))
									((DoubleTable) data.getPropertyValue(header))
										.setByInt(Double.valueOf(input[row][col]),indexes);
								else if (dataType.equals(IntTable.class))
									((IntTable) data.getPropertyValue(header))
										.setByInt(Integer.valueOf(input[row][col]),indexes);
								else if (dataType.equals(StringTable.class))
									((StringTable) data.getPropertyValue(header))
										.setByInt((input[row][col]), indexes);
								else if (dataType.equals(LongTable.class))
									((LongTable) data.getPropertyValue(header))
										.setByInt(Long.valueOf(input[row][col]),indexes);
								else if (dataType.equals(BooleanTable.class))
									((BooleanTable) data.getPropertyValue(header))
										.setByInt(Boolean.valueOf(input[row][col]),indexes);
								else if (dataType.equals(FloatTable.class))
									((FloatTable) data.getPropertyValue(header))
										.setByInt(Float.valueOf(input[row][col]),indexes);
								else if (dataType.equals(ShortTable.class))
									((ShortTable) data.getPropertyValue(header))
										.setByInt(Short.valueOf(input[row][col]),indexes);
								else if (dataType.equals(ByteTable.class))
									((ByteTable) data.getPropertyValue(header))
										.setByInt(Byte.valueOf(input[row][col]),indexes);
								else if (dataType.equals(CharTable.class))
									((CharTable) data.getPropertyValue(header))
										.setByInt(((String) (input[row][col])).charAt(0),indexes);
								// case of records nested within tables
								else if (dataType.equals(ObjectTable.class)) {
									SimplePropertyList recdata = (SimplePropertyList)
										((ObjectTable<?>)data.getPropertyValue(header)).getByInt(indexes);
									Class<?> recdataType = recdata.getPropertyClass(subHeader);
									if (recdataType == null)
										throw new NullPointerException("No datatype found in " + data.getClass().getName()
											+ " for column header " + headers[col]+ ".");
									// NB these are always wrapper classes, being in a property list
									if (recdataType.equals(Integer.class))
										recdata.setProperty(subHeader,Integer.valueOf(input[row][col]));
									else if (recdataType.equals(Long.class))
										recdata.setProperty(subHeader,Long.valueOf(input[row][col]));
									else if (recdataType.equals(Double.class))
										recdata.setProperty(subHeader,Double.valueOf(input[row][col]));
									else if (recdataType.equals(Float.class))
										recdata.setProperty(subHeader,Float.valueOf(input[row][col]));
									else if (recdataType.equals(Short.class))
										recdata.setProperty(subHeader,Short.valueOf(input[row][col]));
									else if (recdataType.equals(Byte.class))
										recdata.setProperty(subHeader,Byte.valueOf(input[row][col]));
									else if (recdataType.equals(Boolean.class))
										recdata.setProperty(subHeader,Boolean.valueOf(input[row][col]));
									else if (recdataType.equals(Character.class))
										recdata.setProperty(subHeader,input[row][col].charAt(0));
									else if (recdataType.equals(String.class))
										recdata.setProperty(subHeader,input[row][col]);
									else {
										throw new IllegalArgumentException("Cannot load data of type '" + recdataType + "'" 
											+ " for column header " + headers[col]+ " - unmanaged data type.");
									}
								}
							} else {
								// Primitive wrappers 
								Object obj = input[row][col];
								if (dataType.isAssignableFrom(obj.getClass()))
									data.setProperty(header, input[row][col]);
								else if (obj.getClass().equals(String.class)) {
									String v = (String) obj;
									if (dataType.equals(Double.class))
										data.setProperty(header, Double.parseDouble(v));
									else if (dataType.equals(Integer.class))
										data.setProperty(header, Integer.parseInt(v));
									else if (dataType.equals(Byte.class))
										data.setProperty(header, Byte.parseByte(v));
									else if (dataType.equals(Short.class))
										data.setProperty(header, Short.parseShort(v));
									else if (dataType.equals(Long.class))
										data.setProperty(header, Long.parseLong(v));
									else if (dataType.equals(Float.class))
										data.setProperty(header, Float.parseFloat(v));
									else if (dataType.equals(Boolean.class))
										data.setProperty(header, Boolean.parseBoolean(v));
									else if (dataType.equals(Character.class))
										data.setProperty(header, v);// crash?
								}
							}
							
						} catch (NumberFormatException e) {
							log.severe("Data could not be loaded: wrong number format for '"+header+"': "+input[row][col]);
						}
						
					} // property exists in list - otherwise, just gently read nothing
				} // col
		} // row
		return data;
	}

}
