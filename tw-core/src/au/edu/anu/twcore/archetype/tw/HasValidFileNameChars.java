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
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import au.edu.anu.rscs.aot.graph.property.Property;
import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.TextTranslations;

/**
 * @author Ian Davies - 19 Dec 2021
 */
/**
 * Checks that chars in a string can form a valid file name under the current OS
 * Input is a property
 */
public class HasValidFileNameChars extends QueryAdaptor {

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		Property localItem = (Property) input;
		String fileName = (String) localItem.getValue();
		if (!isValid(fileName)) {
			String[] msgs = TextTranslations.getHasValidFileNameChars(fileName);
			actionMsg = msgs[0];
			errorMsg = msgs[1];
		}
		return this;
	}

	// https://www.baeldung.com/java-validate-filename
	private static final Character[] INVALID_WINDOWS_SPECIFIC_CHARS = { '"', '*', '<', '>', '?', '|' };
	private static final Character[] INVALID_UNIX_SPECIFIC_CHARS = { '\000','/' };

	private static boolean isValid(String fileName) {
		if (fileName == null || fileName.isEmpty() || fileName.length() > 255) {
			return false;
		}
		return Arrays.stream(getInvalidCharsByOS()).noneMatch(ch -> fileName.contains(ch.toString()));
	}

	private static Character[] getInvalidCharsByOS() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			return INVALID_WINDOWS_SPECIFIC_CHARS;
		} else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
			return INVALID_UNIX_SPECIFIC_CHARS;
		} else {
			return new Character[] {};
		}
	}

}
