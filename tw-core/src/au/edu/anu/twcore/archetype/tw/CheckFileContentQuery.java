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
import org.assertj.core.util.Arrays;
import fr.cnrs.iees.omugi.collections.tables.*;
import au.edu.anu.qgraph.queries.Queryable;
import au.edu.anu.twcore.experiment.DataSource;
import fr.cnrs.iees.twcore.constants.FileType;

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

/**
 * Check that the content of a data file contains column headers required by a data source.
 * 
 * @author Jacques Gignoux - 8 nov. 2021
 *
 */
// TODO: move messages to TextTranslations.
public class CheckFileContentQuery extends CheckDataFileQuery {

	@SuppressWarnings("unused")
	@Override
	public Queryable submit(Object input) {  // input is a DataSource node
		initInput(input);
		if (input instanceof DataSource) {
			DataSource ds = (DataSource) input;
			String[][] rawData = loadFile(ds);
			File file = ((FileType) ds.properties().getPropertyValue(P_DATASOURCE_FILE.key())).getFile();
			if (file!=null) {
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
							StringTable it = (StringTable) ds.properties().getPropertyValue(P_DATASOURCE_DIM.key());
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
							StringTable it = (StringTable) ds.properties().getPropertyValue(P_DATASOURCE_DIM.key());
							for (int i=0; i<it.size(); i++)
								for (int j=0; j<rawData[0].length; j++)
									if (rawData[0][j].equals(it.getWithFlatIndex(i))) {
										try {
											int index = Integer.valueOf(rawData[1][j]);
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
