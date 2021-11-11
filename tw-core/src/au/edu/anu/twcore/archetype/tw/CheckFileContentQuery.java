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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

import org.assertj.core.util.Arrays;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;

import au.edu.anu.rscs.aot.collections.tables.IntTable;
import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.experiment.DataSource;
import au.edu.anu.twcore.experiment.runtime.io.CsvFileLoader;
import au.edu.anu.twcore.experiment.runtime.io.OdfFileLoader;
import fr.cnrs.iees.twcore.constants.FileType;

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

/**
 * Check that the content of a data file contains column headers required by a data source.
 * 
 * @author Jacques Gignoux - 8 nov. 2021
 *
 */
// quickly tested with simple cases, csv only - seems ok
// TODO: move messages to TextTranslations.
public class CheckFileContentQuery extends QueryAdaptor {

	@SuppressWarnings("unused")
	@Override
	public Queryable submit(Object input) {  // input is a DataSource node
		initInput(input);
		if (input instanceof DataSource) {
			DataSource ds = (DataSource) input;
			String dstype = (String) ds.properties().getPropertyValue(P_DATASOURCE_SUBCLASS.key());
			File file = ((FileType) ds.properties().getPropertyValue(P_DATASOURCE_FILE.key())).getFile();
			if (file!=null) {
				LinkedList<String> lines = new LinkedList<String>();
				
				String[][] rawData = null;
				int linesRead = 0;
				// csv file
				if (dstype.contains(CsvFileLoader.class.getSimpleName())) {
					BufferedReader reader = null;
					try {
						reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
					    String line = reader.readLine();
					    // NB only read two lines: header + 1st data line
					    while ((line!=null)&&(linesRead<2)) {
							if (!line.trim().isEmpty()) { // skip empty lines wherever they are
							    lines.add(line);
							    linesRead++;
							}
							line = reader.readLine();
					    }
					    reader.close();
					} catch (Exception e) {
						errorMsg = "csv data file '"+file.getName()+"' could not be read";
						actionMsg = "check and fix format of csv file '"+file.getName()+"'";
					}
					rawData = new String[lines.size()][];
					int i=0;
					String fieldSeparator = "\t";
					if (ds.properties().hasProperty(P_DATASOURCE_SEP.key()))
						fieldSeparator = (String) ds.properties().getPropertyValue(P_DATASOURCE_SEP.key());
					for (String l:lines) {
						String[] s = l.split(fieldSeparator);
						rawData[i] = s;
						i++;
					}
				}
				// ods file
				else if (dstype.contains(OdfFileLoader.class.getSimpleName())) {
					SpreadsheetDocument odf;
					try {
						odf = SpreadsheetDocument.loadDocument(new FileInputStream(file));
						Table table = null;
						String sheet = (String) ds.properties().getPropertyValue(P_DATASOURCE_SHEET.key());
						if ((sheet==null)||(sheet.isEmpty()))
							table = odf.getSheetByIndex(0);	
						else
							table = odf.getSheetByName(sheet);						
						rawData = new String[2][];
						for (int row=0; row<2; row++) {
							rawData[row] = new String[table.getColumnCount()];
							for (int col=0; col<table.getColumnCount(); col++) {
								rawData[row][col] = table.getCellByPosition(col,row).getStringValue();
							}
						}
					} catch (Exception e) {
						errorMsg = "ods data file '"+file.getName()+"' could not be read";
						actionMsg = "check and fix format of ods file '"+file.getName()+"'";
					}
				}
				// now, the checks - what's in these data files ?
				if (rawData!=null ) {
					// headers
					if (rawData[0]!=null) {
						if (ds.properties().hasProperty(P_DATASOURCE_IDLC.key())) {
							String s = (String) ds.properties().getPropertyValue(P_DATASOURCE_IDLC.key());
							if (!Arrays.asList(rawData[0]).contains(s)) {
								errorMsg = "Life cycle identifier column '"+s+"' not found in file '"+file.getName()+"'";
								actionMsg = "Remove '" +s+ "' property from data source '"+ ds.id()
									+"' or add '" +s+ "' column to file '"+file.getName()+"'";
							}
						}
						if (ds.properties().hasProperty(P_DATASOURCE_IDGROUP.key())) {
							String s = (String) ds.properties().getPropertyValue(P_DATASOURCE_IDGROUP.key());
							if (!Arrays.asList(rawData[0]).contains(s)) {
								errorMsg = "Group identifier column '"+s+"' not found in file '"+file.getName()+"'";
								actionMsg = "Remove '" +s+ "' property from data source '"+ ds.id()
									+"' or add '" +s+ "' column to file '"+file.getName()+"'";
							}
						}
						if (ds.properties().hasProperty(P_DATASOURCE_IDCOMPONENT.key())) {
							String s = (String) ds.properties().getPropertyValue(P_DATASOURCE_IDCOMPONENT.key());
							if (!Arrays.asList(rawData[0]).contains(s)) {
								errorMsg = "Component identifier column '"+s+"' not found in file '"+file.getName()+"'";
								actionMsg = "Remove '" +s+ "' property from data source '"+ ds.id()
									+"' or add '" +s+ "' column to file '"+file.getName()+"'";
							}
						}
						if (ds.properties().hasProperty(P_DATASOURCE_IDRELATION.key())) {
							String s = (String) ds.properties().getPropertyValue(P_DATASOURCE_IDRELATION.key());
							if (!Arrays.asList(rawData[0]).contains(s)) {
								errorMsg = "Relation identifier column '"+s+"' not found in file '"+file.getName()+"'";
								actionMsg = "Remove '" +s+ "' property from data source '"+ ds.id()
									+"' or add '" +s+ "' column to file '"+file.getName()+"'";
							}
						}
						// NB: rawdata is String and IntTable is int!
						if (ds.properties().hasProperty(P_DATASOURCE_DIM.key())) {
							IntTable it = (IntTable) ds.properties().getPropertyValue(P_DATASOURCE_DIM.key());
							for (int i=0; i<it.size(); i++)
								if (!Arrays.asList(rawData[0]).contains(it.getWithFlatIndex(i))) {
									errorMsg = "Dimension column '"+it.getWithFlatIndex(i)+"' not found in file '"+file.getName()+"'";
									actionMsg = "Add '" +it.getWithFlatIndex(i)+ "' column to file '"+file.getName()+"'";
							}
						}
					}
					else {
						errorMsg = "Data file '"+file.getName()+"' does not contain enough data";
						actionMsg = "Add 1 line of headers and 1 line of values to data file '"+file.getName()+"'";
					}
					// 1st data row
					if (rawData[1]!=null) {
						if (ds.properties().hasProperty(P_DATASOURCE_DIM.key())) {
							IntTable it = (IntTable) ds.properties().getPropertyValue(P_DATASOURCE_DIM.key());
							for (int i=0; i<it.size(); i++)
								if (Arrays.asList(rawData[0]).contains(it.getWithFlatIndex(i))) {
									try {
										int index = Integer.valueOf(rawData[1][i]);
									} catch (NumberFormatException e) {
										errorMsg = "Dimension '"+it.getWithFlatIndex(i)
											+"' values in file '"+file.getName()+"' are not integers";
										actionMsg = "Replace values for dimension '"+it.getWithFlatIndex(i)
											+"' in file '"+file.getName()+"' with integers";
									}
							}
						}
					}
					else {
						errorMsg = "Data file '"+file.getName()+"' does not contain enough data";
						actionMsg = "Add 1 line of headers and 1 line of values to data file '"+file.getName()+"'";
					} 
				}
			}
		}
		return this;
	}

}
