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

import au.edu.anu.rscs.aot.util.FileUtilities;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.graph.generic.Graph;
import fr.cnrs.iees.graph.io.GraphImporter;
import fr.cnrs.iees.graph.io.impl.OmugiGraphImporter;

/**
 * Author Ian Davies
 *
 * Date 12 Dec. 2018
 */

/**
 * {@code Project} is a singleton static class that contains the directory
 * ({@link java.io.File}) of the project in current use.
 *
 * <p>
 * {@code Project} has methods that parse a 3Worlds project path into
 * <em>Project_name_datetime</em>. An underscore ("_") separates these three
 * parts.
 * <p>
 * 1. 3Worlds project directories always begin with the key word
 * <em>project</em>.
 * <p>
 * 2. The <em>name</em> uses camelCase format and also forbids characters that
 * would produce valid filename. Further, this part must also begin with a lower
 * case (if alpha).
 * <p>
 * The <em>datetime</em> string is the creation time in human-readable form with
 * nanos appended to make the directory unique. Thus directories cannot be made
 * in a fast loop as the directory names will collide.
 * <p>
 * 
 * @author Author Ian Davies
 * 
 *         Date 12 Dec. 2018
 */
public class Project implements ProjectPaths, TWPaths {
	private static final String sep = "_";
	private static final char sepch = '_';

	/*
	 * DateTime format - no blanks - it is effectively a unique id. However,it seems
	 * ":" is a forbidden char in OSX and Windows
	 */
	// private static DateTimeFormatter formatter =
	// DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm:ss:SSS");
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS");

	private static File projectDirectory;

	/**
	 * @param name
	 *            any string
	 * @return The modified name input string. Name will be modified to camelCase
	 *         with any whitespace characters then removed. The first character, if
	 *         alphabetic, will be made lower case to conform with package name
	 *         conventions.
	 * 
	 * @throws TwcoreException
	 *             if a project is open | there are no valid characters | the
	 *             directory cannot be made | directory already exists (if made
	 *             within nano second i.e. within a loop).
	 */
	public static String create(String name) throws TwcoreException {
		String givenName = name;
		if (isOpen())
			throw new TwcoreException(
					"Cannot create project " + name + ". Project is open: " + projectDirectory.getName());

		char[] delimiters = { sepch };
		name = WordUtils.capitalizeFully(name.replaceAll("\\W", sep), delimiters).replaceAll(sep, "");

		if (name.equals(""))
			throw new TwcoreException("Name does not contain any valid characters (" + givenName + ")");

		try {
			File f = new File(name);
			f.getCanonicalFile();
		} catch (Exception e) {
			throw new TwcoreException(name + "is not a valid file name on this system", e);
		}

		// lower case first character
		name = WordUtils.uncapitalize(name);
		String creationDateTime = createDateTime();

		projectDirectory = new File(
				TW_ROOT + File.separator + PROJECT_DIR_PREFIX + sep + name + sep + creationDateTime);
		if (projectDirectory.exists()) {
			File f = projectDirectory;
			close();
			throw new TwcoreException("Project directory already exists: " + f.getAbsolutePath());
		}

		if (!projectDirectory.mkdirs())
			throw new TwcoreException("Unable to create project directory: " + projectDirectory.getAbsolutePath());
		return name;
	}

	/**
	 * Closes the project
	 * 
	 * @throws TwcoreException
	 *             if not open
	 */
	public static void close() throws TwcoreException {
		if (!isOpen())
			throw new TwcoreException("Project is closed.");
		projectDirectory = null;
	}

	/**
	 * @param directory
	 *            full path of 3Worlds project directory
	 * @throws TwcoreException
	 *             when a project is already open | the path is not a valid 3Worlds
	 *             project path.
	 */
	public static void open(File directory) throws TwcoreException {
		if (isOpen())
			throw new TwcoreException(
					"Cannot open an already open project (" + projectDirectory.getAbsolutePath() + ".");
		if (!isValidProjectFile(directory))
			throw new TwcoreException("Invalid project directory name (" + directory.getName() + ".");
		projectDirectory = directory;
	}

	/**
	 * @return projectDirectory
	 * @throws TwcoreException
	 *             if Project is closed.
	 */
	public static File getProjectFile() throws TwcoreException {
		if (!isOpen())
			throw new TwcoreException("Project is closed.");
		return projectDirectory;
	}

	/**
	 * @return Directory path as String
	 * @throws TwcoreException
	 *             if Project is closed
	 */
	public static String getProjectDirectory() throws TwcoreException {
		return getProjectFile().getAbsolutePath();
	}

	/**
	 * @return true if projectDirectory != null
	 */
	public static boolean isOpen() {
		return projectDirectory != null;
	}

	/**
	 * @return The project name part of the project directory. This is used as a
	 *         java package name in generated java files for this project. It is
	 *         also used as the name of the 3Worlds configuration graph root
	 * @throws TwcoreException
	 *             if Project is closed
	 */
	public static String getProjectName() throws TwcoreException {
		String[] items = parseProjectName(projectDirectory);
		return items[1];
	}

	/**
	 * @param directory
	 *            whose path is to be parsed.
	 * @return Project path without the "Project" prefix and date/time in parts.
	 * @throws TwcoreException
	 *             if the directory string will not parse correctly
	 */
	public static String extractDisplayName(File directory) throws TwcoreException {
		String[] items = parseProjectName(directory);
		return items[1] + "(" + items[2] + ")";
	}

	public static String getDisplayName() throws TwcoreException {
		return extractDisplayName(projectDirectory);
	}

	/**
	 * @param directories
	 *            array of 3Worlds directories
	 * @return array of display name strings as {@code Name(Date)}
	 * @throws TwcoreException
	 *             if any of the directories are invalid.
	 */
	public static String[] extractDisplayNames(File[] directories) throws TwcoreException {
		String[] result = new String[directories.length];
		for (int i = 0; i < directories.length; i++)
			result[i] = extractDisplayName(directories[i]);
		return result;
	}

	/**
	 * @param directory
	 *            containing the date time string
	 * @return returns the date and time string from the given 3Worlds directory.
	 * @throws TwcoreException
	 *             if not a valid 3Worlds directory
	 */
	public static String extractDateTime(File directory) throws TwcoreException {
		String[] items = parseProjectName(directory);
		return items[2];
	}

	/**
	 * @return String containing the unique date and time of project creation.
	 * @throws TwcoreException
	 *             if project is not open
	 */
	public static String getProjectDateTime() throws TwcoreException {
		if (!isOpen())
			throw new TwcoreException("Project is closed.");
		return extractDateTime(projectDirectory);
	}

	/**
	 * @param pathElements
	 * @return new file with path elements appended to project path.
	 * @throws TwcoreException
	 *             if project is not open
	 */
	public static File makeFile(String... pathElements) throws TwcoreException {
		if (!isOpen())
			throw new TwcoreException("Cannot make file from a closed Project");
		String s1 = projectDirectory.getAbsolutePath();
		String s2 = FileUtilities.makePath(pathElements);
		String s3 = FileUtilities.makePath(s1, s2);
		return new File(s3);
	}

	public static File makePreferencesFile() {
		return makeFile(TWPaths.TW_PREF);
	}

	public static File makeLayoutFile() {
		return makeFile(TWPaths.TW_LAYOUT);
	}

	/**
	 * @return array of all valid 3Worlds projects
	 */
	public static File[] getAllProjectPaths() {
		String repos = TW_ROOT;
		File folder = new File(repos);
		return folder.listFiles(new ProjectFilter());
	}

	/**
	 * @param directory
	 * @return true if this directory is a valid 3Worlds project directory
	 */
	public static boolean isValidProjectFile(File directory) {
		if (!directory.exists())
			return false;
		if (!directory.isDirectory())
			return false;
		String name = directory.getName();
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
		if (!WordUtils.uncapitalize(items[1]).equals(items[1]))
			return false;
		return true;
	}

	private static String createDateTime() {
		LocalDateTime currentDate = LocalDateTime.now(ZoneOffset.UTC);
		String res = currentDate.format(formatter);
		return res;
	}

	private static String[] parseProjectName(File file) throws TwcoreException {
		String name = file.getName();
		String[] items = name.split(sep);
		if (!(items.length == 3))
			throw new TwcoreException(name + " is not a project name. Must have Project_<name>_<dateTime>");
		if (!items[0].equals(PROJECT_DIR_PREFIX))
			throw new TwcoreException(name + " is not a project name. Must start with key work 'Project'");
		try {
			LocalDateTime.parse(items[2], formatter);
		} catch (Exception e) {
			throw new TwcoreException(name + " is not a project name. Must contain valid Date");
		}
		if (!WordUtils.uncapitalize(items[1]).equals(items[1]))
			throw new TwcoreException("First character of " + items[1] + " must be lower case");

		return items;
	}

	private static File makeConfigurationFile()  throws TwcoreException {
		String name = Project.getProjectName();
		return Project.makeFile(name + ".twg");
	}

	public static Graph<?, ?> newConfiguration() throws TwcoreException {
//		File file = Project.makeConfigurationFile();
		// Graph g =
		return null;
	}
	
	
	public static Graph<?, ?> newLayout()  throws TwcoreException {
//		File file = Project.makeLayoutFile();
		// Graph
		return null;
		
	}


	public static Graph<?, ?> loadConfiguration() {
		File file = Project.makeConfigurationFile();
		return new OmugiGraphImporter(file).getGraph();
	}

	public static Graph<?, ?> loadLayout() {
		File file = Project.makeLayoutFile();
		return new OmugiGraphImporter(file).getGraph();
	}

}
