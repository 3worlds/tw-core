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

	public OdfFileLoader(String idsp, String idst, String idsc, String idsr, String idmd, int[] dimCols,
			Set<String> columnsToRead, InputStream input, String sheet) {
		super(idsp, idst, idsc, idsr, idmd, dimCols, columnsToRead, input);
		this.sheet = sheet;
	}

	@Override
	protected String[][] loadFromFile() {
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
