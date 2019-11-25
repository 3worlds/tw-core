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
 * Same as InputFileExists but if File is null its statisfied.
 * Used for initialState file where it is valid not to have a file selected 
 * but if the selected file does not exist, we need a warning.
 *
 */

/**
 * @author Ian Davies
 *
 * @date 7 May 2019
 */
public class InputFileExistifSelectedQuery extends Query {
	File s;
	Property localItem;
	@Override
	public Query process(Object input) { // input is a property 
		defaultProcess(input);
		localItem = (Property) input;
		FileType ft = (FileType) localItem.getValue();
		s = ft.getFile();
		if (s==null)
			satisfied = true;
		if (s!=null && s.exists())
			satisfied = true;
		// TODO need to handle jars!!
		if (s!=null && Resources.getFile(s.getName())!=null)
			satisfied = true;
		return this;
	}

	public String toString() {
		return "[" + stateString() + "||File for property '"+localItem.getKey()+"' must exist if selected.||";
	}

}
