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

package au.edu.anu.twcore.jars;

import java.io.File;
import java.util.Set;

import au.edu.anu.omhtk.jars.Jars;

/**
 * Author Ian Davies
 *
 * Date 19 Dec. 2018
 * 
 * Re-implementation of work by Jacques Gignoux. 
 */

/**
 * Generates a Jar containing the current simulator to be deployed according to
 * experiment
 * 
 * @author Jacques Gignoux - 30/11/2017
 *         <p>
 *         Refactoring of jars to help with debugging a user model. Now this jar
 *         is just a manifest with:
 *         <ul>
 *         <li>tw-dep.jar</li>
 *         <li>threeWorlds.jar</li>
 *         <li>data.jar</li>
 *         <li>&lt;userCode&gt;.jar</li>
 *         </ul>
 *         </p>
 */
public class SimulatorJar extends Jars {
	public static final String SimulatorJar = "simulator.jar";

	// This jar contains only a manifest.
	public SimulatorJar(Set<File> dataFiles, Set<File> userCodeJars, Set<String> userLibraries) {
		super();
		setMainClass("au.edu.anu.twuifx.mr.MrLauncher");
		addDependencyOnJar(String.join("", "..", Jars.separator, ThreeWorldsJar.ThreeWorldsJar));
//		addDependencyOnJar(".." + Jar.separator + TwDepJar.TW_DEP_JAR);
//		addDependencyOnJar("data.jar");
//		addDependencyOnJar(".." + Jar.separator + ThreeWorldsJar.TW_JAR);
//		for (String userLibrary : userLibraries)
//			addDependencyOnJar(userLibrary);
//		for (File f : userCodeJars)
//			addDependencyOnJar(f.getName());

	}

}
