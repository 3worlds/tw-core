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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.properties.SimplePropertyList;

/**
 * <p>A data loader to load a <em>csv</em> ("comma separated values") file into a 
 * collection of (identical) {@link SimplePropertyList} objects. csv files are commonly used to export data
 * from spreadsheet software such as OpenOffice-Calc.</p>
 * <p>As csv is not a standardized format, we impose some restrictions on the format usable
 * by this loader:</p>
 * <ul>
 * <li>The file must not contain any missing value or structural empty cells.</li>
 * <li>Empty lines are permitted (they are skipped).</li>
 * <li>Default field delimiter is tabulation ("\t"), but any other character or character sequence is possible.</li>
 * <li>Text data must not be quoted.</li>
 * <li>The first data line of the file must contain column headers. They must match
 * field names as defined in the 3Worlds specification dsl file.</li>
 *
 * <p>Since we do not allow for empty cells in the file, this means that complex {@link SimplePropertyList}
 * constructs may have to be loaded from different files. In particular, a different file
 * per set of table dimensions should be used. The match between different files is based
 * on the TwData object identifiers they contain. For example, a {@link TwData}
 * structured this way: </p>
 * 
 * {@code double x;}<br/>
 * {@code double y;}<br/>
 * {@code MyDataTable z;}<br/>
 * 
 * <p>with {@code MyDataTable} being a 2D table of 2*3 Integers, could be read from 2 files
 * structured in this way:</p>
 * first file for x and y:<br/>
 * <table>
 * <tr><td>id</td><td>x</td><td>y</td></tr>
 * <tr><td>A</td><td>1.0</td><td>2.54</td></tr>
 * <tr><td>B</td><td>12.0</td><td>3.84</td></tr>
 * </table>
 * ...<br/><br/>
 * second file for z:<br/>
 *  <table>
 * <tr><td>id</td><td>dim_1</td><td>dim_2</td><td>z</td></tr>
 * <tr><td>A</td><td>0</td><td>0</td><td>3879</td></tr>
 * <tr><td>A</td><td>0</td><td>1</td><td>4556</td></tr>
 * <tr><td>A</td><td>0</td><td>2</td><td>589</td></tr>
 * <tr><td>A</td><td>1</td><td>0</td><td>2143</td></tr>
 * <tr><td>A</td><td>1</td><td>1</td><td>5486</td></tr>
 * <tr><td>A</td><td>1</td><td>2</td><td>5511</td></tr>
 * <tr><td>B</td><td>0</td><td>0</td><td>865</td></tr>
 * <tr><td>B</td><td>0</td><td>1</td><td>455</td></tr>
 * <tr><td>B</td><td>0</td><td>2</td><td>5895</td></tr>
 * <tr><td>B</td><td>1</td><td>0</td><td>2871</td></tr>
 * <tr><td>B</td><td>1</td><td>1</td><td>849</td></tr>
 * <tr><td>B</td><td>1</td><td>2</td><td>151</td></tr>
 * </table>
 * ...<br/><br/>
 * <p>Although not universal - this is impossible - , this csv data loader should be usable for most data originating
 * from spreadsheets or softwares such as R.</p>
 * 
 * @author Jacques Gignoux - 2/6/2017 refactored 9/10/2019
 * @see TableDataLoader
 *
 */
public class CsvFileLoader extends TableDataLoader {
	
	public static String defaultCsvSeparator = "\t"; 

	/**the field separator used in this particular csv file */
	private String fieldSeparator;

	public CsvFileLoader(String idsp, String idst, String idsc, String idsr, String idmd, String[] dimCols,
			Set<String> columnsToRead, Map<String,Object> colTemplates, InputStream input, String separator) {
		super(idsp, idst, idsc, idsr, idmd, dimCols, columnsToRead, colTemplates, input,separator);
	}

	@Override
	protected String[][] loadFromFile(Object...pars) {
		fieldSeparator = (String) pars[0];
		String[][] rawData;
		BufferedReader reader = null;
		LinkedList<String> lines = new LinkedList<String>();
		try {
		    reader = new BufferedReader(new InputStreamReader(input,StandardCharsets.UTF_8));
		    String line = reader.readLine();
		    while (line!=null) {
				if (!line.trim().isEmpty()) // skip empty lines wherever they are
				    lines.add(line);
				line = reader.readLine();
		    }
		    reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		    throw new TwcoreException("I/O error with csv file reader",e);
		}
		String topline = lines.getFirst();
		if (topline!=null) {
			rawData = new String[lines.size()][];
			int i=0;
			for (String line:lines) {
				String[] s = line.split(fieldSeparator);
				rawData[i] = s;
				i++;
			}
			return rawData;
			}
		return null;
	}


}
