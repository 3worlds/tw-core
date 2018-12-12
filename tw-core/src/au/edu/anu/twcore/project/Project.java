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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Author Ian Davies
 *
 * Date 12 Dec. 2018
 */
public class Project implements ProjectPaths, TWPaths {
	private static final String sep = "_";
	/* Just the name - nothing else */
	private static String projectName;
	/* Date time string in human-readable format */
	private static String projectUid;

	private static File projectFile;
	
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");

	private static boolean open;

	static {
		close();
	}

	public static void create(String name) {
		open = true;
		projectName = name;
		projectUid = createUid();
		projectFile = new File(TW_ROOT + File.separator + PROJECT_DIR_PREFIX + sep + projectName + sep + projectUid);
		System.out.println(projectFile.getAbsolutePath());
		projectFile.mkdirs();
	}

	private static String createUid() {
		LocalDateTime currentDate = LocalDateTime.now(ZoneOffset.UTC);	
		String res = currentDate.format(formatter);
		LocalDateTime cd = LocalDateTime.parse(res, formatter);
		return res;
	}

	public static void close() {
		open = false;
		projectName = null;
		projectUid = null;
		projectFile = null;
	}

	public static boolean isOpen() {
		return open;
	}
public static void main(String[] args) {
	Project.create("CRAP");
	
}
}
