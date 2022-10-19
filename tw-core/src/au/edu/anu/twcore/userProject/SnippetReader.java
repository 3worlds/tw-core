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
package au.edu.anu.twcore.userProject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.cnrs.iees.omhtk.codeGeneration.Comments;

/**
 * @author Ian Davies -  12 July 2021
 */
public class SnippetReader {
	private SnippetReader() {
	};

	public static Map<String, List<String>> readSnippetsFromFile(File f) {
		boolean startMethod = false;
		boolean startRead = false;
		boolean importing = false;
		String key = null;
		Map<String, List<String>> result = new HashMap<>();
		char c[] = f.getName().replace(".java", "").toCharArray();
		c[0] = Character.toLowerCase(c[0]);
		String rootName = new String(c);
		try {
			List<String> lines = Files.readAllLines(f.toPath(),StandardCharsets.UTF_8);
			for (String line : lines) {
				// stop
				if (line.contains(Comments.importInsertEnd)) {
					if (!result.containsKey(key)) {
						//must have a blank line otherwise clearing the imports does nothing
						List<String> importLines = new ArrayList<>();
						importLines.add("");
						result.put(key, importLines);
					}
					importing = false;
					key = null;
				}
				if (importing) {
					// Split by ";" in case someone's playing silly buggers and puts more than one
					// import statement per line.
					String[] parts = line.split(";");
					for (String part : parts) {
						if (part.contains("import")) {
							String importStr = part.replace("import", "").trim();
							List<String> importLines = result.get(key);
							if (importLines == null)
								importLines = new ArrayList<>();
							importLines.add(importStr);
							result.put(key, importLines);
						}
					}
				}

				if (line.contains(Comments.codeInsertEnd)) {
					if (!result.containsKey(key)) {
						//must have a blank line otherwise clearing the method does nothing
						List<String> importLines = new ArrayList<>();
						importLines.add("");
						result.put(key, importLines);
					}
					startMethod = false;
					startRead = false;
					key = null;
				}
				// read
				if (key != null && startRead) {
					List<String> codeLines = result.get(key);
					if (codeLines == null)
						codeLines = new ArrayList<>();
					codeLines.add(line);
					result.put(key, codeLines);
				}

				// method found?
				String tmp = line.trim();
				String[] parts = tmp.split("\\W+");
				if (parts.length > 3) {
					if (parts[0].equals("public") && parts[1].equals("static") && !startMethod) {
						startMethod = true;
						key = parts[3];
					}
				}
				// start read
				if (line.contains(Comments.codeInsertBegin) && startMethod) {
					startRead = true;
				}
				if (line.contains(Comments.importInsertBegin)) {
					key = rootName;
					importing = true;
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

}
