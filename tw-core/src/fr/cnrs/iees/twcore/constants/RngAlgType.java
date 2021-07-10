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
 * generated by CentralResourceGenerator on Sat Jul 10 07:29:49 AEST 2021
*/
package fr.cnrs.iees.twcore.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;

public enum RngAlgType {

// PCG32: Implementation of a permuted congruential generator with 32-bit output (https://en.wikipedia.org/wiki/Permuted_congruential_generator[PGC32]); fast (56% faster than `JAVA`) and good quality
	PCG32 ("Implementation of a permuted congruential generator with 32-bit output (https://en.wikipedia.org/wiki/Permuted_congruential_generator[PGC32]); fast (56% faster than `JAVA`) and good quality"),

// JAVA: The default Java random number generator of `java.util.Random`; medium speed, poor quality
	JAVA ("The default Java random number generator of `java.util.Random`; medium speed, poor quality"),

// XSRANDOM: Implementation of a Xorshift random number generator as found http://demesos.blogspot.com/2011/09/replacing-java-random-generator.html[here]; very fast (76% faster than `JAVA`), medium quality
	XSRANDOM ("Implementation of a Xorshift random number generator as found http://demesos.blogspot.com/2011/09/replacing-java-random-generator.html[here]; very fast (76% faster than `JAVA`), medium quality");
	
	private final String description;

	private RngAlgType(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}

	public static String[] toStrings() {
		String[] result = new String[RngAlgType.values().length];
		for (RngAlgType s: RngAlgType.values())
			result[s.ordinal()] = s.name();
		Arrays.sort(result);
		return result;
	}

	public static Set<String> keySet() {
		Set<String> result = new HashSet<String>();
		for (RngAlgType e: RngAlgType.values())
			result.add(e.toString());
		return result;
	}

	public static RngAlgType defaultValue() {
		return PCG32;
	}

	static {
		ValidPropertyTypes.recordPropertyType(RngAlgType.class.getSimpleName(), 
		RngAlgType.class.getName(),defaultValue());
	}

}

