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

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.data.TableNode;
import au.edu.anu.twcore.experiment.DataSource;
import au.edu.anu.twcore.root.World;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.twcore.constants.FileType;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.N_TABLE;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_SIZEDBY;

/**
 * Check that a dim property is present when the variables of the data file require dimensions
 * 
 * @author Jacques Gignoux - 12 nov. 2021
 *
 */
// TODO: if a variable is in a file where other variables request dims, then it must be an identifier
// or have the same dims....
// tested - seems ok
// TODO: move messages to TextTranslations.
public class CheckFileDimQuery extends CheckDataFileQuery {
	
	private Map<String,Integer> tableDims = new HashMap<>();

	@SuppressWarnings({ "unchecked" })
	@Override
	public Queryable submit(Object input) {  // input is a DataSource node
		initInput(input);
		if (input instanceof DataSource) {
			DataSource ds = (DataSource) input;
			String[][] rawData = loadFile(ds);
//			String dstype = (String) ds.properties().getPropertyValue(P_DATASOURCE_SUBCLASS.key());
			File file = ((FileType) ds.properties().getPropertyValue(P_DATASOURCE_FILE.key())).getFile();
			if (file!=null) {
//				// read the 2 first lines of the file
//				LinkedList<String> lines = new LinkedList<String>();
////				String[][] rawData = null;
//				int linesRead = 0;
//				// csv file
//				if (dstype.contains(CsvFileLoader.class.getSimpleName())) {
//					BufferedReader reader = null;
//					try {
//						reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
//					    String line = reader.readLine();
//					    // NB only read two lines: header + 1st data line
//					    while ((line!=null)&&(linesRead<2)) {
//							if (!line.trim().isEmpty()) { // skip empty lines wherever they are
//							    lines.add(line);
//							    linesRead++;
//							}
//							line = reader.readLine();
//					    }
//					    reader.close();
//					} catch (Exception e) {
//						errorMsg = "csv data file '"+file.getName()+"' could not be read";
//						actionMsg = "check and fix format of csv file '"+file.getName()+"'";
//					}
//					rawData = new String[lines.size()][];
//					int i=0;
//					String fieldSeparator = "\t";
//					if (ds.properties().hasProperty(P_DATASOURCE_SEP.key()))
//						fieldSeparator = (String) ds.properties().getPropertyValue(P_DATASOURCE_SEP.key());
//					for (String l:lines) {
//						String[] s = l.split(fieldSeparator);
//						rawData[i] = s;
//						i++;
//					}
//				}
//				// ods file
//				else if (dstype.contains(OdfFileLoader.class.getSimpleName())) {
//					SpreadsheetDocument odf;
//					try {
//						odf = SpreadsheetDocument.loadDocument(new FileInputStream(file));
//						Table table = null;
//						String sheet = (String) ds.properties().getPropertyValue(P_DATASOURCE_SHEET.key());
//						if ((sheet==null)||(sheet.isEmpty()))
//							table = odf.getSheetByIndex(0);	
//						else
//							table = odf.getSheetByName(sheet);
//						// NB only read two lines: header + 1st data line
//						rawData = new String[2][];
//						for (int row=0; row<2; row++) {
//							rawData[row] = new String[table.getColumnCount()];
//							for (int col=0; col<table.getColumnCount(); col++) {
//								rawData[row][col] = table.getCellByPosition(col,row).getStringValue();
//							}
//						}
//					} catch (Exception e) {
//						errorMsg = "ods data file '"+file.getName()+"' could not be read";
//						actionMsg = "check and fix format of ods file '"+file.getName()+"'";
//					}
//				}
				// get the list of all tables declared  in the model so far and their number of dims
				TreeNode root = World.getRoot(ds);
				Collection<TableNode> tables = (Collection<TableNode>) get(root,
					childTree(),
					selectZeroOrMany(hasTheLabel(N_TABLE.label())) );
				for (TableNode table:tables) {
					Collection<Edge> ldim = (Collection<Edge>) get(table.edges(Direction.OUT),
						selectZeroOrMany(hasTheLabel(E_SIZEDBY.label())));
					tableDims.put(table.id(), ldim.size());
				}
				// make sure there are as many dim properties in the datasource  as required by the 
				// table variables found in the file
				if (rawData!=null ) {
					if (rawData[0]!=null) {
						boolean noTableFound = true;
						for (String header:rawData[0]) {
							// for headers matching table names, 
							// 1st case: header matches table name, check that dim
							// property is present and enough values have been set
							if (tableDims.containsKey(header)) {
								noTableFound = false;
								int ndimrequested = tableDims.get(header);
								int ndimfound = 0;
								if (ds.properties().hasProperty(P_DATASOURCE_DIM.key()))
									ndimfound = ((StringTable)ds.properties().getPropertyValue(P_DATASOURCE_DIM.key())).size();
								int missing = ndimrequested-ndimfound;
								// not enough dims
								if (missing>0) {
									if (missing==1) {
										errorMsg = "Missing '"+P_DATASOURCE_DIM.key()
											+"' property in '"+ds.id()+"' to read data file '"+file+"'";
										actionMsg = "Add a '"+P_DATASOURCE_DIM.key()
											+"' property value to '"+ds.id()+"' to read data file '"+file+"'";
									}									
									else {
										errorMsg = "Missing "+missing+" '"+P_DATASOURCE_DIM.key()
											+"' property values in '"+ds.id()+"' to read data file '"+file+"'";
										actionMsg = "Add "+missing+" values to property '"+P_DATASOURCE_DIM.key()
											+"' in '"+ds.id()+"' to read data file '"+file+"'";
									}
								}
								// too many dims
								else if (missing<0) {
									errorMsg = "Too many '"+P_DATASOURCE_DIM.key()
										+"' properties in '"+ds.id()+"' to read data file '"+file+"'";
									actionMsg = "Remove "+(-missing)+" '"+P_DATASOURCE_DIM.key()
										+"' property values from '"+ds.id()+"' to read data file '"+file+"'";
								}
							}
						}
						// if no header matches a table, check that there is no dim property
						if (noTableFound)
							if (ds.properties().hasProperty(P_DATASOURCE_DIM.key())) {
								errorMsg = "Property '"+P_DATASOURCE_DIM.key()
									+"' not needed in '"+ds.id()+"' to read data file '"+file+"'";
								actionMsg = "Remove the '"+P_DATASOURCE_DIM.key()
									+"' property from '"+ds.id()+"' to read data file '"+file+"'";
						}
					}
				}
			}
		}
		return this;
	}

}
