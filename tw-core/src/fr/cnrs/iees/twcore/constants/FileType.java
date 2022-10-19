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

package fr.cnrs.iees.twcore.constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import au.edu.anu.twcore.project.Project;
import fr.cnrs.iees.io.parsing.CloneableProperty;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;
import fr.cnrs.iees.omhtk.utils.Duple;

/**
 * Author Ian Davies
 *
 * Date 28 Jan. 2019
 */
public class FileType implements CloneableProperty{
	private String relativePath = "";
	@Override
	public Object clone() {
		FileType clone = new FileType();
		clone.relativePath= relativePath;
		return clone;
	}

	public String getRelativePath() {
		return relativePath;
	}
	//dataSourceFileSpec
	//designFilePropertySpec
	// dataSinkSpec
	// problme is these depend on the sub-arch
	public List<Duple<String,String>> getExtenstions(){
		List<Duple<String,String>> result = new ArrayList<>();
		result.add(new Duple<String,String>("All files","*.*"));
		return result;
	}

	@Override
	public String toString() {
		return relativePath;
	}

	public void setRelativePath(String newValue) {
		relativePath = newValue;
	}

	public File getFile() {
		if (!Project.isOpen())
			throw new IllegalStateException("Project must be open.");
		if (relativePath.equals(""))
			return null;
		else
			return Project.makeFile(relativePath);
	}

	public static FileType valueOf(String string) {
		FileType result = new FileType();
		result.setRelativePath(string);
		return result;
	}

	public static FileType defaultValue() {
		return new FileType();
	}

	static {
		ValidPropertyTypes.recordPropertyType(FileType.class.getSimpleName(), FileType.class.getName(), defaultValue());
	}

}
