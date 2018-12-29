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
* generated by Generator on Sun Dec 30 07:03:28 AEDT 2018
*/
package fr.cnrs.iees.twcore.generators.resources.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum SnippetLocation {

// inFunctionBody: snippet is added in the body of the relevant method of the  `Function` class (e.g. in the `changeState()` method for a `ChangeStateFunction`, etc.)
	inFunctionBody,

// inClassBody: snippet is added in `Function` class as private methods
	inClassBody;
	
	public static String[] toStrings() {
		String[] result = new String[SnippetLocation.values().length];
		for (SnippetLocation s: SnippetLocation.values())
			result[s.ordinal()] = s.name();
		Arrays.sort(result);
		return result;
	}

	public static Set<String> keySet() {
		Set<String> result = new HashSet<String>();
		for (SnippetLocation e: SnippetLocation.values())
			result.add(e.toString());
		return result;
	}

	public static SnippetLocation defaultValue() {
		return inFunctionBody;
	}

}

