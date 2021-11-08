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

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import au.edu.anu.twcore.experiment.runtime.DataIdentifier;
import au.edu.anu.twcore.experiment.runtime.MultipleDataLoader;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.ens.biologie.generic.utils.Logging;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

/**
 * <p>Abstract ancestor for data loader dealing with data organized in tables (eg csv 
 * or odt data loaders). This data loader assumes that specific variables must appear
 * in a table format depending in the file they come from:</p>
 * <ol>
 * <li>a species identifier (property idSpecies)</li>
 * <li>a stage identifier (property idStage)</li>
 * <li>a SystemComponent (or individual) identifier (property idComponent)</li>
 * <li>a SystemRelation (or relation) identifier (property idRelation) [NOT YET IMPLEMENTED in simulator]</li>
 * <li>a series of dimension variables (property idDims)</li>
 * </ol>
 * <p>All other variables are assumed to be read as such (ie, they play no specific role
 * in the file scanning. The data file is assumed to contain the variable names 
 * (eg as column headers in a spreadsheet format - or row headers, wy not !)</p>
 * 
 * <p>Conditions apply on the abovelisted properties:</p>
 * <ul>
 * <li>(1) must be present, all other ones are optional.</li>
 * <li>if (3) is present then (2) must be present</li>
 * <li>all properties are names (Strings) except idDims which must be a StringTable</li>
 * </ul>
 * <p>Data organisation rules:</p>
 * <ol>
 * <li>if only (1) is present, an instance of TwData is produced per species</li>
 * <li>if (1) and (2) are present, an instance of TwData is produced per stage[in]species </li>
 * <li>if (1), (2) and (3) are present, an instance of TwData is produced per systemComponent[in]stage[in]species</li>
 * </ol>
 * <p>Uses {@link PropertyDataLoader} to read data chunks</p>
 * <p>results are returned in a map of String * TwData. Since more than one variable is
 * given to identify the TwData, the string is a concatenation of the names separated by a separator</p>
 * 
 * @author Jacques Gignoux - 31 mai 2017
 *
 */
public abstract class TableDataLoader 
		implements MultipleDataLoader<SimplePropertyList> {
	
	private static Logger log = Logging.getLogger(TableDataLoader.class);
	
	/** the separator used to build hierarchical identifiers eg species:stage:individual*/
	private static String SEP = ":";
	/** the list of variable names as found in the file */
	private String[] headers = null;
	/** the list of variable names to read from the file, if provided*/
	private Set<String> columnsToRead = new HashSet<String>();
	/** the table of raw data as extracted from the file */
	private String[/*line*/][/*column*/] rawData;
	/** the identifier column names, sorted by target (species, stage, component, relation, variable) */
	private Map<String,Integer> idcols = new HashMap<String,Integer>();
	
	private int[] dimCols = null;
	
	protected InputStream input = null;
	
	public TableDataLoader (String idsp,String idst,String idsc,String idsr,String idmd,
			String[] dimColss, Set<String> columnsToReadd,InputStream inputt,Object...extraPars) {
		// if a list of variable names is given, then only these ones will be read
		this.columnsToRead = columnsToReadd;
		if (dimColss.length>0)
			this.dimCols = new int[dimColss.length];
		this.input = inputt;
		// now read the file (in descendants)
		rawData = loadFromFile(extraPars);
		if (rawData==null)
			log.severe("No data could be read from resource "+input);
		else {
			// by definition, in a table the header line is the first line:
			headers = rawData[0];
			// if no names were given, it means all variables (excluding dims) are 
			// to be read except the identifiers which deserve a special treatment
			boolean fillIt = columnsToRead.isEmpty();
			for (int i=0; i<headers.length; i++) {
				String var = headers[i];
				boolean dimFound = false;
				if (dimCols!=null)
					for (int j=0; j<dimCols.length; j++)
						if (dimColss[j].equals(var)) {
							dimCols[j]=i;
							dimFound = true;	
						}
				if (!dimFound)
					if (var.equals(idsp)) idcols.put(P_DATASOURCE_IDLC.key(),i);
					else if (var.equals(idst)) idcols.put(P_DATASOURCE_IDGROUP.key(),i);
					else if (var.equals(idsc)) idcols.put(P_DATASOURCE_IDCOMPONENT.key(),i);
					else if (var.equals(idsr)) idcols.put(P_DATASOURCE_IDRELATION.key(),i);
					else if (var.equals(idmd)) idcols.put(P_DATASOURCE_IDVAR.key(),i);
					else if (fillIt) columnsToRead.add(var);
			}
			// warnings: id columns requested but not found in file.
			if (idsc!=null)
				if (!idsc.isBlank() && (!idcols.containsKey(P_DATASOURCE_IDCOMPONENT.key())))
					log.warning(()->"Component Identifier '"+idsc+"' not found in data source");
		}
	}
	
	protected abstract String[/*line*/][/*column*/] loadFromFile(Object...pars);
	
	private DataIdentifier getLineIds(String[] dataLine) {
		String s1="",s2="",s3="";
		if (idcols.containsKey(P_DATASOURCE_IDLC.key()))
			s3 = dataLine[idcols.get(P_DATASOURCE_IDLC.key())];
		if (idcols.containsKey(P_DATASOURCE_IDGROUP.key()))
			s2 = dataLine[idcols.get(P_DATASOURCE_IDGROUP.key())];
		if (idcols.containsKey(P_DATASOURCE_IDCOMPONENT.key()))
			s1 = dataLine[idcols.get(P_DATASOURCE_IDCOMPONENT.key())];
		return new DataIdentifier(s1,s2,s3);
	}
	
	@Override
	public final void load(Map<DataIdentifier, SimplePropertyList> result, SimplePropertyList dataModel) {
//		LinkedList<String[]> dataChunk = new LinkedList<String[]>();
		// start at 1 because rawData[0] is the headers line
		for (int i=1; i<rawData.length; i++) {
			// get item unique id
			DataIdentifier id = getLineIds(rawData[i]);
			// if empty id
			if (id.componentId().isEmpty()) {
				// case 1: no dimension column declared in data, every line is a different id
				if (dimCols==null)
					id.setComponentId(String.valueOf(i));
				// case 2: dimensions declared, then all lines are assumed to belong to the same
				// component, with id 0 as there is no way to know the id.
				else
					id.setComponentId("0");
			}
			SimplePropertyList data = null;
			if (result.containsKey(id)) 
				data = result.get(id);
			else
				data = dataModel.clone();
			PropertyDataLoader dataLoader = new PropertyDataLoader(
			rawData[i],
		    headers,
		    columnsToRead,
		    dimCols);
			dataLoader.load(data);
			result.put(id,data);
		}
	}

    /** utility for descendants */
	protected SimplePropertyList find(String id,
			Map<String,SimplePropertyList> list,
			SimplePropertyList dataModel) {
    	SimplePropertyList data = null;
    	if (list.containsKey(id))
    		data = list.get(id);
    	else {
    		data = (SimplePropertyList) dataModel.clone();
    		list.put(id, data);
    	}
    	return data;
    }

    /**
     * Results are returned in a map with String identifiers.
     * These identifiers may be compound (eg species:stage:individual).
     * This methods extract the hierarchical levels from the identifier name
     * @param ids
     * @return
     */
	public static final String[] extractIds(String ids) {
		return ids.split(SEP);
	}
	
    /**
     * Results are returned in a map with String identifiers.
     * These identifiers may be compound (eg species:stage:individual).
     * This methods produces a compound identifier from a list of Strings given to it
     * @param ids
     * @return
     */
	public static final String collateIds(String... ids) {
		String s = ids[0];
		for (int i=1; i<ids.length; i++)
			s+= SEP+ids[i];
		return s;
	}

	public final String[] headers() {
		return headers;
	}
	
}
