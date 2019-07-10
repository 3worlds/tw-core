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

/**
 * 3Worlds projects' directory structure - these dirs appear under
 * ProjectPaths.PROJECT_FILES/[ModelName]/
 * 
 * @author Jacques Gignoux	- 23/4/2015
 *
 */
@Deprecated // use TWPaths instead.
public interface ThreeWorldsProjectPaths {
	
	/** the directory for all generated code */
	public static final String THREE_WORLDS_CODE = "code";
	/** the directory for all user-specific data (eg csv files and others stuff) */
	public static final String THREE_WORLDS_DATA = "data";
	
	/** the package where default input files can be found - to use with Resource.getInputStream(name,package) */	
	public static final String THREE_WORLDS_DEFAULTS = "fr.ens.biologie.threeWorlds.resources.defaults";

}
