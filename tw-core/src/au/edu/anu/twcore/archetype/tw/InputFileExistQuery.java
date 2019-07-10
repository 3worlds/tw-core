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

import au.edu.anu.rscs.aot.graph.property.Property;
import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.rscs.aot.util.Resources;
import fr.cnrs.iees.twcore.constants.FileType;

/**
 * checks an input file is present in local/models subdir of 3w repo
 * @author gignoux - 24 févr. 2017
 *
 */
public class InputFileExistQuery extends Query{
	
	@Override
	public Query process(Object input) { // input is a property 
		defaultProcess(input);
		Property localItem = (Property) input;
		File s = ((FileType) localItem.getValue()).getFile();
		if (s!=null && s.exists())
			satisfied = true;
		// TODO need to handle jars!!
		if (s!=null && Resources.getFile(s.getName())!=null)
			satisfied = true;
		return this;
	}

	public String toString() {
		return "[" + stateString() + " File must exist ";
	}

}
