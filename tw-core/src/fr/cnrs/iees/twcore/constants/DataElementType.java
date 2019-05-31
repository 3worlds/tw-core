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
* along with this program. If not, see <http://www.gnu.org/licenses/>.
* 

* 3Worlds - A software for the simulation of ecosystems
* 
* by:  Jacques Gignoux - jacques.gignoux@upmc.fr
*      Ian D. Davies   - ian.davies@anu.edu.au
*      Shayne R. Flint - shayne.flint@anu.edu.au
* 
* http:// ???
* 

* CAUTION: generated code - do not modify
* generated by Generator on Sun Dec 30 09:13:24 AEDT 2018
*/
package fr.cnrs.iees.twcore.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// NOTE (JG): this class is redundant with ValidPropertyTypes in omugi. It should be replaced
// by the former (in which new property types should be added)
@Deprecated
public enum DataElementType {

// Double: a single precision floating point number (4 10^-38^ to 3.4 10^38^) with 15 significant digits
	Double ("java.lang.Double"),

// Integer: an integer [-2147483648; 2147483647]
	Integer ("java.lang.Integer"),

// Long: a long integer [-9223372036854775808; 9223372036854775807]
	Long ("java.lang.Long"),

// Float: a single precision floating point number (4 10^-38^ to 3.4 10^38^) with 6 significant digits
	Float ("java.lang.Float"),

// Boolean: a logical value {true, false}
	Boolean ("java.lang.Boolean"),

// String: a text string
	String ("java.lang.String"),

// Short: a short integer [-32768; 32767]
	Short ("java.lang.Short"),

// Char: a character value (16-bit Unicode = UTF16, i.e. 65535 different values)
	Char ("java.lang.Char"),

// Byte: a very, very short integer [-128 ; 127]
	Byte ("java.lang.Byte"),

// Object: anything else *TODO: should we keep this? it’s probably useless*
	Object ("java.lang.Object");
	
	private final String className;

	private DataElementType(String className) {
		this.className = className;
	}

	public String className() {
		return className;
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

}

