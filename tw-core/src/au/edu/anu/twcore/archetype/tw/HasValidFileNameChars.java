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
//		String tmpdir = System.getProperty("java.io.tmpdir");
		String fileName = (String) localItem.getValue();
//		File file = null;
//		if (tmpdir != null)
//			file = new File(tmpdir + File.separator + fileName);
//		else
//			file = new File(fileName);
		try {
			FileWriter fw = new FileWriter(fileName,StandardCharsets.UTF_8);
			fw.close();
			new File(fileName).delete();
		} catch (IOException e) {
			String[] msgs = TextTranslations.getHasValidFileNameChars(fileName);
			actionMsg = msgs[0];
			errorMsg = msgs[1];
		}
		return this;
	}

}
