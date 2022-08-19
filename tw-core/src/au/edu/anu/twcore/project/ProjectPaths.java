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

package au.edu.anu.twcore.project;

import java.io.File;

/**
 * Author Ian Davies
 *
 * Date 12 Dec. 2018
 */

// Don't add anything here on speculation. Wait until it's needed.
public interface ProjectPaths {
	public static String PROJECT_DIR_PREFIX /*	*/ = "project";
	public static String LOCAL /*				*/ = "local";
	public static String JAVA /*				*/ = "java";
	public static String LOCALJAVA /*			*/ = LOCAL + File.separator + JAVA;
	public static String LOCALJAVA_PKG/*		*/ = LOCAL+"."+JAVA;
	/* dir root for generated code */
	public static String CODE/*					*/ = "code";

/*
	 * generated code for a project regardless of the existence of a linked user
	 * project. The sub dir is organised as system.id() which themselves don't need
	 * further subdirs (i.e /code
	 */
	public static String LOCALJAVACODE /*                */ = LOCALJAVA + File.separator + CODE;

	/*
	 * User linked project jar files. It will be empty if there is no linked project
	 */
	public static String LOCALJAVALIB /*                 */ = LOCALJAVA + File.separator + "lib";
	/*
	 * data files for a project regardless of the existence of a linked user
	 * project. I don't think it has any subDirs
	 */
	public static String LOCALJAVARES /*                 */ = LOCALJAVA + File.separator + "res";

	/*
	 * root of runtime model. Has runtime preferences and any data files, startup
	 * files and generated filrs
	 */
	public static String RUNTIME /*             */ = LOCAL + File.separator + "runTime";

	public static String LOGS /*                  */=LOCAL + File.separator + "logs";
	
	// for generated 'glue' code, ie the one the user doesnt want to know about
	public static String GENERATED = "generated";
}
