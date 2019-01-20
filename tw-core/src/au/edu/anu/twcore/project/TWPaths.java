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

import fr.cnrs.iees.io.GraphFileFormats;

/**
 * Author Ian Davies
 *
 * Date Dec 12, 2018
 */
// Don't add anything here on speculation. Wait until it's needed.
public interface TWPaths {
	public static String USER_ROOT/*       */ = System.getProperty("user.home");
	public static String TW /*             */ = ".3w";
	public static String TW_ROOT /*        */ = USER_ROOT + File.separator + TW;
	public static String TW_DEP_JAR /*     */ = "tw-dep.jar";
	public static String TW_PREF /*        */ = "MM.xml";
	public static String TW_LAYOUT /*      */ = "Layout"+GraphFileFormats.AOT.extension().split(" ")[1];
}
