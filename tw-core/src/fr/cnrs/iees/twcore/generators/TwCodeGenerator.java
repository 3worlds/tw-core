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
package fr.cnrs.iees.twcore.generators;

import java.io.File;

import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.project.ProjectPaths;
import au.edu.anu.twcore.project.TwPaths;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.ens.biologie.generic.JavaGenerator;
import fr.ens.biologie.generic.utils.NameUtils;


/**
 * An ancestor class to setup the directory hierarchy for Java code generators and compilers.
 * Assumes specification are passed to the constructor as a AotNode - descendants will know how to use
 * this AotNode.
 * 
 * Hierarchy is:
 * 
 * .3w
 * --project_[name]_[uid] 
 * ----distributed				ProjectPaths.PROJECT_DISTRIBUTED
 * ------jars					ProjectPaths.PROJECT_JARS
 * ------graphs					ProjectPaths.PROJECT_GRAPHS
 * ------files					ProjectPaths.PROJECT_FILES
 * --------[model name]
 * ----------data
 * ----------code
 * --------[model name]
 * ----------data
 * ----------code
 * ----local					ProjectPaths.PROJECT_LOCAL
 * ------hardware				ProjectPaths.PROJECT_HARDWARE_GRAPHS
 * ------userInterfaces			ProjectPaths.PROJECT_USER_INTERFACE_GRAPHS
 * ------logs					ProjectPaths.PROJECT_LOGS
 * ------recordings				ProjectPaths.PROJECT_RECORDINGS
 * 
 * I THINK THE ABOVE IS ALL CRAP NOW -IDD
 * 
 * <p>3Worlds: component threeWorlds</p>
 * @author Jacques Gignoux - 23 nov. 2016
 *
 */
public abstract class TwCodeGenerator 
	implements ProjectPaths, TwPaths, JavaGenerator {
	
	protected TreeGraphDataNode spec = null;
	
	protected TwCodeGenerator(TreeGraphDataNode spec) {
		super();
		this.spec = spec;
	}
	
	protected String makeModelJavaName(String modelName) {
		return NameUtils.wordUpperCaseName(modelName);
	}
	
	protected File getModelCodeDir(String modelRoot) {
		//File result = Project.makeFile(makeModelJavaName(modelRoot),TW_CODE);
		File result = Project.makeFile(ProjectPaths.CODE,makeModelJavaName(modelRoot));
		return result;
	}

	protected File getModelDataDir(String modelRoot) {
		//File result = Project.makeFile(makeModelJavaName(modelRoot),TW_DATA);
		return Project.makeFile(ProjectPaths.RES,makeModelJavaName(modelRoot));
	}
}
