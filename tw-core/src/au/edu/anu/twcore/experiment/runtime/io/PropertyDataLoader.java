package au.edu.anu.twcore.experiment.runtime.io;

import java.util.HashSet;
import java.util.Set;

import au.edu.anu.rscs.aot.AotException;
import au.edu.anu.rscs.aot.collections.tables.BooleanTable;
import au.edu.anu.rscs.aot.collections.tables.ByteTable;
import au.edu.anu.rscs.aot.collections.tables.CharTable;
import au.edu.anu.rscs.aot.collections.tables.DoubleTable;
import au.edu.anu.rscs.aot.collections.tables.FloatTable;
import au.edu.anu.rscs.aot.collections.tables.IntTable;
import au.edu.anu.rscs.aot.collections.tables.LongTable;
import au.edu.anu.rscs.aot.collections.tables.ObjectTable;
import au.edu.anu.rscs.aot.collections.tables.ShortTable;
import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.collections.tables.Table;
import au.edu.anu.twcore.exceptions.TwcoreException;
import au.edu.anu.twcore.experiment.runtime.DataLoader;
import fr.cnrs.iees.properties.SimplePropertyList;

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
 * @see SingleDataLoader
 * @see TableDataLoader
 *
 */
public class PropertyDataLoader implements DataLoader<SimplePropertyList> {

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

	@Override
	public SimplePropertyList load(SimplePropertyList data) {
		for (int row = 0; row < input.length; row++) {
			for (int col = 0; col < input[row].length; col++)
				if (toRead.contains(headers[col])) {
					String header = headers[col];
					if (headers[col].contains(":")) { // recursion in case of nested data structures
						String[] s = headers[col].split(":", 2);
						header = s[0];
						headers[col] = s[1];
					}
					Class<?> dataType = data.getPropertyClass(header);
					if (dataType == null)
						throw new AotException("No datatype found in " + data.getClass().getName()
								+ " for column header " + header + ".");

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
						else if (dataType.equals(ObjectTable.class)) {
							// TODO: load nested objects !!
							throw new TwcoreException("loading of ObjectTable<T> not yet implemented");
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
				} // col
		} // row
		return data;
	}

}
