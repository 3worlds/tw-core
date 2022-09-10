/**************************************************************************
 *  TW-APPS - Applications used by 3Worlds                                *
 *                                                                        *
 *  Copyright 2018: Jacques Gignoux & Ian D. Davies                       *
 *       jacques.gignoux@upmc.fr                                          *
 *       ian.davies@anu.edu.au                                            * 
 *                                                                        *
 *  TW-APPS contains ModelMaker and ModelRunner, programs used to         *
 *  construct and run 3Worlds configuration graphs. All code herein is    *
 *  independent of UI implementation.                                     *
 *                                                                        *
 **************************************************************************                                       
 *  This file is part of TW-APPS (3Worlds applications).                  *
 *                                                                        *
 *  TW-APPS is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  TW-APPS is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *                         
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with TW-APPS.                                                   *
 *  If not, see <https://www.gnu.org/licenses/gpl.html>                   *
  **************************************************************************/

package fr.cnrs.iees.twcore.generators;

import java.io.File;
import java.util.Set;

import au.edu.anu.omhtk.jars.Jars;
import au.edu.anu.twcore.project.Project;

/**
 * @author Ian Davies - 25 Aug 2019
 */
public class SimulatorJar extends Jars{
	public SimulatorJar(String mainClass,Set<File> dataFiles, Set<File> codeFiles, Set<File> resFiles,Set<String> userLibraries) {
//		 This jar user code, data and resources (i.e. user specific jars).
		// set ModelRunner as main class: could be called germane ha ha.
		setMainClass(mainClass);
		// data files
		String prjDir = Project.getProjectDirectory()+File.separator;
		for (File s : dataFiles) {
			String fileName = s.getAbsolutePath();
			String resourceName = fileName.replace(prjDir, "");
			resourceName = resourceName.replace(s.getName(), "");
			resourceName = resourceName.replace("\\", Jars.separator);
			if (resourceName.endsWith(Jars.separator))
				resourceName = resourceName.substring(0, resourceName.length() - 1);
			addFile(s.getAbsolutePath(), resourceName);
		}
		// code files
		String codeRoot = Project.makeFile(Project.LOCAL_JAVA).getAbsolutePath();
		for (File file : codeFiles) {// both java and class files
			String fileName = file.getAbsolutePath();
			String jarDirectory = file.getAbsolutePath().replace(codeRoot, "");
			jarDirectory = jarDirectory.replace(file.getName(), "");
			jarDirectory = formatJarDirectory(jarDirectory);
			addFile(fileName, jarDirectory);
		}
		// resources
		String resRoot = Project.makeFile(Project.LOCAL_JAVA_RES).getAbsolutePath();
		for (File file : resFiles) {
			String fileName = file.getAbsolutePath();
			String jarDirectory = file.getAbsolutePath().replace(resRoot, "");
			jarDirectory = jarDirectory.replaceAll(file.getName(), "");
			jarDirectory = formatJarDirectory(jarDirectory);
			addFile(fileName, jarDirectory);
		}

		// dependencies
		addDependencyOnJar(".." + Jars.separator + Project.TW_DEP_JAR);
//		addDependencyOnJar(".." + Jars.separator + TwPaths.TW_FX_DEP_JAR);
		for (String userLibrary : userLibraries)
			addDependencyOnJar(userLibrary);
	}
	private String formatJarDirectory(String s) {
		String r = s.replace("\\", Jars.separator);
		if (r.endsWith("/"))
			r = r.substring(0, r.lastIndexOf("/"));
		return r;
	}

}
