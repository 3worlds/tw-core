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
package fr.cnrs.iees.twcore.generators.data;

import static fr.ens.biologie.generic.utils.NameUtils.validJavaName;
import static fr.ens.biologie.generic.utils.NameUtils.wordUpperCaseName;

import au.edu.anu.twcore.project.ProjectPaths;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.twcore.generators.TwCodeGenerator;

/**
 * 
 * @author Jacques Gignoux - 1 févr. 2021
 *
 */
public abstract class DataClassGenerator 
		extends TwCodeGenerator
		implements ProjectPaths{

	/** the model name (matching the ecology node name */
	protected String modelName = null;
	/** the name of the package in which the class will be generated (ie with "." as separators) */
	protected String packageName = null;
	/** the directory name matching package name */
	protected String packagePath = null;
	
	protected DataClassGenerator(String modelName,TreeGraphDataNode spec) {
		super(spec);
		this.modelName = modelName;
		packageName =CODE+"."+ validJavaName(wordUpperCaseName(modelName))+"."+GENERATED;
//		packagePath = Project.makeFile(LOCALJAVACODE,validJavaName(wordUpperCaseName(modelName))).getAbsolutePath();
		packagePath = getModelGlueCodeDir(modelName).getAbsolutePath();
	}

}
