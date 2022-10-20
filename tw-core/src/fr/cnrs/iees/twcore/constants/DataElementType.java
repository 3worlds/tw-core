/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *                    *** 3Worlds - A software for the simulation of ecosystems ***
 *                    *                                                           *
 *                    *        by:  Jacques Gignoux - jacques.gignoux@upmc.fr     *
 *                    *             Ian D. Davies   - ian.davies@anu.edu.au       *
 *                    *             Shayne R. Flint - shayne.flint@anu.edu.au     *
 *                    *                                                           *
 *                    *         http:// ???                                       *
 *                    *                                                           *
 *                    *************************************************************
 * CAUTION: generated code - do not modify
 * generated by CentralResourceGenerator on Wed Jan 26 09:47:44 AEDT 2022
*/
package fr.cnrs.iees.twcore.constants;

import java.util.*;

import fr.cnrs.iees.omugi.io.parsing.ValidPropertyTypes;

public enum DataElementType {

// Double: a double precision floating point number (4.9406564584124654 10^-324 to 1.7976931348623157 10^308, symmetric for negative numbers) with 16 significant digits
	Double ("a double precision floating point number (4.9406564584124654 10^-324 to 1.7976931348623157 10^308, symmetric for negative numbers) with 16 significant digits",
		"java.lang.Double",
		"double"),

// Integer: an integer [-2147483648; 2147483647]
	Integer ("an integer [-2147483648; 2147483647]",
		"java.lang.Integer",
		"int"),

// Long: a long integer [-9223372036854775808; 9223372036854775807]
	Long ("a long integer [-9223372036854775808; 9223372036854775807]",
		"java.lang.Long",
		"long"),

// Float: a single precision floating point number (1.40239846 10^-45 to 3.40282347 10^38, symmetric for negative numbers) with 8 significant digits
	Float ("a single precision floating point number (1.40239846 10^-45 to 3.40282347 10^38, symmetric for negative numbers) with 8 significant digits",
		"java.lang.Float",
		"float"),

// Boolean: a logical value {true, false}
	Boolean ("a logical value {true, false}",
		"java.lang.Boolean",
		"boolean"),

// String: a text string
	String ("a text string",
		"java.lang.String",
		"String"),

// Short: a short integer [-32768; 32767]
	Short ("a short integer [-32768; 32767]",
		"java.lang.Short",
		"short"),

// Char: a character value (16-bit Unicode = UTF16, i.e. 65535 different values)
	Char ("a character value (16-bit Unicode = UTF16, i.e. 65535 different values)",
		"java.lang.Char",
		"char"),

// Byte: a very, very short integer [-128 ; 127]
	Byte ("a very, very short integer [-128 ; 127]",
		"java.lang.Byte",
		"byte"),

// Object: anything else *TODO: should we keep this? it’s probably useless*
	Object ("anything else *TODO: should we keep this? it’s probably useless*",
		"java.lang.Object",
		"Object");
	
	private final String description;
	private final String className;
	private final String asPrimitive;

	private DataElementType(String description, String className, String asPrimitive) {
		this.description = description;
		this.className = className;
		this.asPrimitive = asPrimitive;
	}

	public String description() {
		return description;
	}

	public String className() {
		return className;
	}

	public String asPrimitive() {
		return asPrimitive;
	}

	public static String[] toStrings() {
		String[] result = new String[DataElementType.values().length];
		for (DataElementType s: DataElementType.values())
			result[s.ordinal()] = s.name();
		Arrays.sort(result);
		return result;
	}

	public static Set<String> keySet() {
		Set<String> result = new HashSet<String>();
		for (DataElementType e: DataElementType.values())
			result.add(e.toString());
		return result;
	}

	public static DataElementType defaultValue() {
		return Double;
	}

	static {
		ValidPropertyTypes.recordPropertyType(DataElementType.class.getSimpleName(), 
		DataElementType.class.getName(),defaultValue());
	}

    public boolean isNumeric() {
        switch (this) {
        case String:
        case Object:
        case Boolean:
        case Char:
            return false;
        case Byte:
        case Double:
        case Float:
        case Integer:
        case Long:
        case Short:
            return true;
        default:
            return false;
        }
    }

}

