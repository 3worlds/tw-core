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
import java.util.Set;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;

import au.edu.anu.twcore.exceptions.TwcoreException;

/**
 * 
 * @author Jacques Gignoux - 2/6/2017 refactored 10 oct. 2019
 *
 */
public class OdfFileLoader extends TableDataLoader {

	/**the sheet to read from this specific odf spreadsheet */
	private String sheet = null;

	public OdfFileLoader(String idsp, String idst, String idsc, String idsr, String idmd, String[] dimCols,
			Set<String> columnsToRead, InputStream input, String sheet) {
		super(idsp, idst, idsc, idsr, idmd, dimCols, columnsToRead, input,sheet);		
	}

	@Override
	protected String[][] loadFromFile(Object...pars) {
		this.sheet = (String) pars[0];
		String[][] rawData = null;
		try {
			SpreadsheetDocument odf = SpreadsheetDocument.loadDocument(input);
			Table table = null;
			if ((sheet==null)||(sheet.isEmpty()))
				table = odf.getSheetByIndex(0);	
			else
				table = odf.getSheetByName(sheet);						
			rawData = new String[table.getRowCount()][];
			for (int row=0; row<table.getRowCount(); row++) {
				rawData[row] = new String[table.getColumnCount()];
				for (int col=0; col<table.getColumnCount(); col++) {
					rawData[row][col] = table.getCellByPosition(col,row).getStringValue();
				}
			}
			return rawData;			
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new TwcoreException("I/O error with odf file reader",e1);
		}
	}

}
