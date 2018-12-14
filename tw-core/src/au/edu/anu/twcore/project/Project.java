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

import org.apache.commons.text.WordUtils;

import au.edu.anu.rscs.aot.OmhtkException;
import au.edu.anu.rscs.aot.util.FileUtilities;

/**
 * Author Ian Davies
 *
 * Date 12 Dec. 2018
 */
public class Project implements ProjectPaths, TWPaths {
	private static final String sep = "_";
	private static final char sepch = '_';
	/* Just the name - nothing else */
	private static String projectName;
	/* Date time string in human-readable format */
	private static String creationDateTime;

	/* Uid format - no blanks */
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm:ss:SSS");

	private static File projectFile;

	private static boolean open;

	static {
		close();
	}

	public static String create(String name) {
		open = true;
		char[] delimiters = { sepch };

		name = WordUtils.capitalizeFully(name.replaceAll("\\W", sep), delimiters).replaceAll(sep, "");
		creationDateTime = createDateTime();
		projectFile = new File(
				TW_ROOT + File.separator + PROJECT_DIR_PREFIX + sep + projectName + sep + creationDateTime);
		if (!projectFile.mkdirs())
			throw new OmhtkException("Unable to create project directory: " + projectFile.getAbsolutePath());
		return projectName;
	}

	public static File getProjectFile() {
		return projectFile;
	}

	private static String createDateTime() {
		LocalDateTime currentDate = LocalDateTime.now(ZoneOffset.UTC);
		String res = currentDate.format(formatter);
		return res;
	}

	public static void close() {
		open = false;
		projectName = null;
		creationDateTime = null;
		projectFile = null;
	}

	public static boolean isOpen() {
		return open;
	}

	private static String[] parseProjectName(File file) {
		String name = file.getName();
		String[] items = name.split(sep);
		if (!(items.length == 3))
			throw new OmhtkException(name + " is not a project name. Must have Project_<name>_<dateTime>");
		if (!items[0].equals(PROJECT_DIR_PREFIX))
			throw new OmhtkException(name + " is not a project name. Must start with key work 'Project'");
		try {
			LocalDateTime.parse(items[2], formatter);
		} catch (Exception e) {
			throw new OmhtkException(name + " is not a project name. Must contain valid Date");
		}
		return items;
	}

	public static boolean validProjectFile(File file) {
		if (!file.exists())
			return false;
		String name = file.getName();
		String[] items = name.split(sep);
		if (!(items.length == 3))
			return false;
		if (!items[0].equals(PROJECT_DIR_PREFIX))
			return false;
		try {
			LocalDateTime.parse(items[2], formatter);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static String getProjectName() {
		if (open) {
			String[] items = parseProjectName(projectFile);
			return items[1];
		}
		return null;
	}

	public static String getProjectDirectory() {
		if (open)
			return projectFile.getAbsolutePath();
		return null;
	}

	public static String getDisplayName(File file) {
		String[] items = parseProjectName(file);
		return items[1] + "(" + items[2] + ")";
	}

	public static String[] extractDisplayNames(File[] files) {
		String[] result = new String[files.length];
		for (int i = 0; i < files.length; i++)
			result[i] = getDisplayName(files[i]);
		return result;
	}

	public static File makeFile(String... pathElements) {
		if (!open)
			throw new OmhtkException("Cannot make file from a closed Project");
		String s1 = projectFile.getAbsolutePath();
		String s2 = FileUtilities.makePath(pathElements);
		String s3 = FileUtilities.makePath(s1, s2);
		return new File(s3);
	}

	public static String getProjectDateTime() {
		return creationDateTime;
	}

	public static File[] getProjectPaths() {
		String repos = TW_ROOT;
		File folder = new File(repos);
		return folder.listFiles(new ProjectFilter());
	}
}
