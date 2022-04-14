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

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;

import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.twcore.experiment.DataSource;
import au.edu.anu.twcore.experiment.runtime.io.CsvFileLoader;
import au.edu.anu.twcore.experiment.runtime.io.OdfFileLoader;
import fr.cnrs.iees.twcore.constants.FileType;

/**
 * An abstract ancestor for queries that require to read a data file to perform checks on it.
 * NOTE: heavy check, loads whole file. Slow.
 * 
 * @author Jacques Gignoux - 13 déc. 2021
 *
 */
public abstract class CheckDataFileQuery extends QueryAdaptor {

	protected String[][] loadFile(DataSource ds) {
		String[][] rawData = null;
		String dstype = (String) ds.properties().getPropertyValue(P_DATASOURCE_SUBCLASS.key());
		File file = ((FileType) ds.properties().getPropertyValue(P_DATASOURCE_FILE.key())).getFile();
		if (file!=null) {
			LinkedList<String> lines = new LinkedList<String>();
			// csv file
			if (dstype.contains(CsvFileLoader.class.getSimpleName())) {
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),StandardCharsets.UTF_8));
				    String line = reader.readLine();
				    while (line!=null) {
						if (!line.trim().isEmpty()) { // skip empty lines wherever they are
						    lines.add(line);
						}
						line = reader.readLine();
				    }
				    reader.close();
				} catch (Exception e) {
					errorMsg = "csv data file '"+file.getName()+"' could not be read";
					actionMsg = "check and fix format of csv file '"+file.getName()+"'";
				}
				if (lines.isEmpty())
					rawData = null;
				else
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
					if ((odf.getSheetCount()>1)&&(((sheet==null)||(sheet.isEmpty())))) {
						errorMsg = "sheet '"+ sheet + "' not found in ods data file '"+file.getName()+"'";
						actionMsg = "check and fix sheet name for ods file '"+file.getName()+"'";
						return null;
					}
					else if ((sheet==null)||(sheet.isEmpty()))
						table = odf.getSheetByIndex(0);	
					else
						table = odf.getSheetByName(sheet);	
					if (table!=null) {
						rawData = new String[2][];
						for (int row=0; row<2; row++) {
							rawData[row] = new String[table.getColumnCount()];
							for (int col=0; col<table.getColumnCount(); col++) {
								rawData[row][col] = table.getCellByPosition(col,row).getStringValue();
							}
						}
					}
					else {
						errorMsg = "sheet '"+ sheet + "' not found in ods data file '"+file.getName()+"'";
						actionMsg = "check and fix sheet name for ods file '"+file.getName()+"'";
						return null;
					}
				} catch (Exception e) {
					errorMsg = "ods data file '"+file.getName()+"' could not be read";
					actionMsg = "check and fix format of ods file '"+file.getName()+"'";
					return null;
				}
			}
		}
		return rawData;
	}
	
	
}
