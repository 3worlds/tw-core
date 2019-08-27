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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import au.edu.anu.rscs.aot.util.FileUtilities;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.identity.IdentityScope;
import fr.cnrs.iees.identity.impl.LocalScope;
import fr.cnrs.iees.io.GraphFileFormats;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.text.WordUtils;

/**
 * Author Ian Davies
 *
 * Date 12 Dec. 2018
 */

/**
 * {@code Project} is a singleton static class containing the directory path
 * ({@link java.io.File}) of the 3Worlds project in current use. All project
 * directories reside within the 3Worlds root directory “home/.3w”. The class
 * contains helper methods for creating files relative to this project.
 * <p>
 * The name of all 3Worlds projects comprise three parts separated by an
 * underscore:
 * <p>
 * 1) the keyword “project”;
 * <p>
 * 2) a user supplied name. This name is reformatted as camel caps for use as:
 * i) a package name for generated java classes; ii) the name of the
 * configuration file; and, iii) the name of the root node in the project
 * configuration file.
 * <p>
 * 3) A date and time string "yyyy-MM-dd-HH-mm-ss-SSS"
 * <p>
 * Project paths are intended to be unique and an exception will be thrown if
 * the system finds identical directories. This situation could only arise if,
 * for some reason, projects were created programmatically (in a rapid loop) or
 * by editing directory names.
 * <p>
 * 
 * @author Author Ian Davies
 * 
 *         Date 12 Dec. 2018
 */
// tested OK with version 0.1.1 on 21/5/2019
public class Project implements ProjectPaths, TwPaths {
	/*
	 * DateTime format - no blanks - it is effectively a unique id. Avoid ":" as it
	 * is a forbidden char in OSX and Windows
	 */
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS");
	private static File projectDirectory;
	private static final String sep = "_";
	private static final char sepch = '_';
	private static final String klassName = Project.class.getName();
	private static Logger log = Logger.getLogger(klassName);
	private static final IdentityScope pScope = new LocalScope("Projects");
	// important that this comes last here
	static {
		log.setLevel(Level.FINE);
		initialiseScope();
	}

	public static String proposeId(String proposedId) {
		return pScope.newId(false, proposedId).id();
	}

	// prevent instantiation
	private Project() {
	};


	/**
	 * @param name any string.
	 * @return The modified name input string. Name will be modified to camelCase
	 *         with any whitespace characters then removed. The first character, if
	 *         alphabetic, will be made lower case to conform with package name
	 *         conventions.
	 * 
	 * @throws TwcoreException if a project is open | there are no valid characters
	 *                         | the directory cannot be made | directory already
	 *                         exists | duplicate directories exist.
	 */
	public static String create(String name) {
		log.entering(klassName, "create");

		checkUniqueness();
		if (isOpen()) {
			String msg = "Cannot create project " + name + ". Project is open: " + projectDirectory.getName();
			log.severe(msg);
			throw new TwcoreException(msg);
		}

		try {
			File f = new File(name);
			f.getCanonicalFile();
		} catch (Exception e) {
			String msg = name + "is not a valid file name on this system";
			log.severe(msg);
			throw new TwcoreException(msg, e);
		}

		String creationDateTime = createDateTime();

		projectDirectory = new File(
				TW_ROOT + File.separator + PROJECT_DIR_PREFIX + sep + name + sep + creationDateTime);
		if (projectDirectory.exists()) {
			File f = projectDirectory;
			close();
			String msg = "Project directory already exists: " + f.getAbsolutePath();
			log.severe(msg);
			throw new TwcoreException(msg);
		}
		if (!projectDirectory.mkdirs()) {
			String msg = "Unable to create project directory: " + projectDirectory.getAbsolutePath();
			log.severe(msg);
			throw new TwcoreException(msg);
		}
		pScope.newId(true, projectDirectory.getName().split(sep)[1]);
		log.exiting(klassName, "create");
		return name;
	}

	public static String formatName(String name) {
		char[] delimiters = { sepch };
		String result = WordUtils.capitalizeFully(name.replaceAll("\\W", sep), delimiters).replaceAll(sep, "");
		result = WordUtils.uncapitalize(result);
		return result;
	}
	/**
	 * Closes the project
	 * 
	 * @throws TwcoreException if not open
	 */
	public static void close() {
		log.entering(klassName, "Close");
		if (!isOpen())
			throw new TwcoreException("Project is closed.");
		projectDirectory = null;
		log.exiting(klassName, "Close");
	}

	/**
	 * @param directory full path of 3Worlds project directory
	 * @throws TwcoreException when a project is already open | the path is not a
	 *                         valid 3Worlds project path.
	 */
	public static void open(File directory) {
		log.entering(klassName, "Open");
		if (isOpen()) {
			String msg = "Cannot open an already open project (" + projectDirectory.getAbsolutePath() + ".";
			log.severe(msg);
			throw new TwcoreException(msg);
		}
		if (!isValidProjectFile(directory)) {
			String msg = "Invalid project directory name (" + directory.getName() + ").";
			log.severe(msg);
			throw new TwcoreException(msg);
		}
		projectDirectory = directory;
		log.exiting(klassName, "Open");
	}

	/**
	 * @return projectDirectory
	 * @throws TwcoreException if Project is closed.
	 */
	public static File getProjectFile() {
		if (!isOpen())
			throw new TwcoreException("Project is closed.");
		return projectDirectory;
	}

	/**
	 * @return Directory path as String
	 * @throws TwcoreException if Project is closed
	 */
	public static String getProjectDirectory() {
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
	 * @throws TwcoreException if Project is closed
	 */
	public static String getProjectUserName() {
		if (!isOpen())
			throw new TwcoreException("Project is closed.");
		String[] items = parseProjectName(projectDirectory);
		return items[1];
	}

	/**
	 * @return
	 * @throws TwcoreException
	 */
	public static String getDisplayName() {
		return extractDisplayName(projectDirectory);
	}

	/**
	 * @return String containing the unique date and time of project creation.
	 * @throws TwcoreException if project is not open
	 */
	public static String getProjectDateTime() {
		if (!isOpen())
			throw new TwcoreException("Project is closed.");
		return extractDateTime(projectDirectory);
	}

	/**
	 * @param pathElements
	 * @return new file with path elements appended to project path.
	 * @throws TwcoreException if project is not open
	 */
	public static File makeFile(String... pathElements) {
		if (!isOpen())
			throw new TwcoreException("Cannot make file from a closed Project");
		String s1 = projectDirectory.getAbsolutePath();
		String s2 = FileUtilities.makePath(pathElements);
		String s3 = FileUtilities.makePath(s1, s2);
		return new File(s3);
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	public static File makeProjectPreferencesFile() {
		return makeFile(TwPaths.TW_PREF);
	}

	public static File makeRuntimePreferencesFile() {
		return makeFile(ProjectPaths.RUNTIME, TwPaths.TW_PREF);
	}

	/**
	 * TODO
	 * 
	 * @return array of all valid 3Worlds projects
	 */
	public static File[] getAllProjectPaths() {
		String repos = TW_ROOT;
		File folder = new File(repos);
		if (!folder.exists())
			folder.mkdirs();
		File[] result = folder.listFiles(new ProjectFilter());
		// sort by date
		Arrays.parallelSort(result, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				long m1 = o1.lastModified();
				long m2 = o2.lastModified();
				if (m1 > m2)
					return 1;
				else if (m1 < m2)
					return -1;
				else
					return 0;
			}

		});
		return result;
	}

	/**
	 * TODO Used to filter directories
	 * 
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

	public static File makeConfigurationFile() {
		String name = Project.getProjectUserName();
		// Its a string of several extensions
		return Project.makeFile(name + GraphFileFormats.TOMUGI.extension().split(" ")[0]);
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	public static File makeLayoutFile() {
		return makeFile(TwPaths.TW_LAYOUT + GraphFileFormats.TOMUGI.extension().split(" ")[0]);
	}

// used for menu creation
	public static String extractDisplayName(File directory) {
		String[] items = parseProjectName(directory);
		return items[1] + "(" + items[2] + ")";
	}

	public static String extractDateTime(File directory) {
		String[] items = parseProjectName(directory);
		return items[2];
	}
	/**
	 * @param directories array of 3Worlds directories
	 * @return array of display name strings as {@code Name(Date)}
	 * @throws TwcoreException if any of the directories are invalid.
	 */
	public static String[] extractDisplayNames(File[] directories) {
		String[] result = new String[directories.length];
		for (int i = 0; i < directories.length; i++)
			result[i] = extractDisplayName(directories[i]);
		return result;
	}

	// ------------------ private
	private static String[] parseProjectName(File file) {
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

	private static String createDateTime() {
		LocalDateTime currentDate = LocalDateTime.now(ZoneOffset.UTC);
		String res = currentDate.format(formatter);
		return res;
	}

	private static void checkUniqueness() {
		File[] files = getAllProjectPaths();
		List<String> ul = new ArrayList<>();
		for (File f : files) {
			if (ul.contains(f.getName()))
				throw new TwcoreException("Identical project directories found: " + f.getName());
			ul.add(f.getName());
		}
	}

	private static void initialiseScope() {
		File[] dirs = getAllProjectPaths();
		for (File dir : dirs) {
			String name = dir.getName();
			pScope.newId(true, name.split(sep)[1]);
		}
	}

}
