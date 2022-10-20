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
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

import au.edu.anu.omhtk.util.FileUtilities;
import fr.cnrs.iees.omugi.io.GraphFileFormats;
import fr.cnrs.iees.omhtk.utils.Logging;

import java.util.logging.Logger;

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
 * @author  Ian Davies - Date 12 Dec. 2018
 */
// tested OK with version 0.1.1 on 21/5/2019
public class Project {
	// There are two types of methods: those that operate
	// only on an open project and those that can operate on a given 3w project
	// directory file. So we should start with File[] getAllProjectFiles and decide
	// what public methods can do with this: basically returning the short name(s),
	// date(s) or display name(s). Those operating on open projects must throw and
	// exception if the given project is not open
	/*
	 * DateTime format - no blanks - it is effectively a unique id. Avoid ":" as it
	 * is a forbidden char in OSX and Windows
	 */
	/**
	 * User's home directory name.
	 */
	public static final String USER_ROOT 		= System.getProperty("user.home");
	/**
	 * 3Worlds home directory name. The default is home/<name>/3w. To change this,
	 * add a environment setting for the user's account using 'TW_HOME' as the
	 * setting key.
	 */
	public static final String TW_HOME 			= get3wHome();
	/**
	 * The OS specific 3Worlds jar file name.
	 */
	public static final String TW_DEP_JAR 		= getDepJarName();
	/**
	 * Prefix for all 3Worlds projects.
	 */
	public static final String PROJECT_DIR_PREFIX = "project";
	/**
	 * Name of the local directory within a 3Worlds project (possibly redundant
	 * now).
	 */
	public static final String LOCAL 			= "local";
	public static final String JAVA  			= "java";
	/**
	 * Name of the Java directory within the Local directory.
	 */
	public static final String LOCAL_JAVA 		= LOCAL + File.separator + JAVA;
	/**
	 * Package name of the Java directory within the Local directory.
	 */
	public static final String LOCAL_JAVA_PKG 	= LOCAL + "." + JAVA;
	/**
	 * User linked project jar files. It will be empty if there is no linked project
	 */
	public static String LOCAL_JAVA_LIB 		= LOCAL_JAVA + File.separator + "lib";

	/**
	 * Name of root directory for Java code generation
	 */
	public static final String CODE 			= "code";

	/**
	 * Generated code for a project regardless of the existence of a linked user
	 * project. The sub dir is organised as system.id() which themselves don't need
	 * further subdirs (i.e /code
	 */
	public static final String LOCAL_JAVA_CODE 	= LOCAL_JAVA + File.separator + CODE;

	/**
	 * Data files for a project regardless of the existence of a linked user
	 * project.
	 */
	public static final String LOCAL_JAVA_RES 	= LOCAL_JAVA + File.separator + "res";

	/**
	 * root of runtime model. Has runtime preferences and any data files, startup
	 * files and generated filrs
	 */
	public static final String RUNTIME 			= LOCAL + File.separator + "runTime";

	public static final String LOGS 			= LOCAL + File.separator + "logs";

	/** for generated 'glue' code, ie code the user does not edit. */
	public static final String GENERATED 		= "generated";

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS");
	private static final String sep = "_";
	private static final String klassName = Project.class.getName();
	private static final Logger log = Logging.getLogger(Project.class);

	/** Project is open if this is not null */
	private static File projectDirectory = null;

	// prevent instantiation
	private Project() {
	};

	/**
	 * @param name any string.
	 * @return The modified name input string. Name will be modified to camelCase
	 *         with any whitespace characters then removed. The first character, if
	 *         alphabetic, will be made lower case to conform with package name
	 *         conventions.
	 *         <p>
	 *         Throws IllegalStateException if a project is open | there are no
	 *         valid characters | the directory cannot be made | directory already
	 *         exists | duplicate directories exist.
	 */
	public static String create(String name) {
		log.entering(klassName, "create");

		checkUniqueness();
		if (isOpen())
			throw new IllegalStateException(
					"Cannot create project " + name + ". Project is open: " + projectDirectory.getName());
		try {
			File f = new File(name);
			f.getCanonicalFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String creationDateTime = createDateTime();

		projectDirectory = new File(
				TW_HOME + File.separator + PROJECT_DIR_PREFIX + sep + name + sep + creationDateTime);
		if (projectDirectory.exists()) {
			File f = projectDirectory;
			close();
			throw new IllegalStateException("Project directory already exists: " + f.getAbsolutePath());
		}
		if (!projectDirectory.mkdirs())
			throw new IllegalStateException(
					"Unable to create project directory: " + projectDirectory.getAbsolutePath());

		log.exiting(klassName, "create");
		return name;
	}

	/**
	 * Closes the project
	 * <p>
	 * Throws IllegalStateException if not open
	 */
	public static void close() {
		log.entering(klassName, "Close");
		if (!isOpen())
			throw new IllegalStateException("Project is closed.");
		projectDirectory = null;
		log.exiting(klassName, "Close");
	}

	/**
	 * @param directory full path of 3Worlds project directory
	 *                  <p>
	 *                  Throws IllegalStateException if: a project is already open |
	 *                  the path is not a valid 3Worlds project path.
	 */
	public static void open(File directory) {
		log.entering(klassName, "Open");
		if (isOpen()) {
			String msg = "Cannot open an already open project (" + projectDirectory.getAbsolutePath() + ".";
			log.severe(msg);
			throw new IllegalStateException(msg);
		}
		if (!isValidProjectFile(directory)) {
			String msg = "Invalid project directory name (" + directory.getName() + ").";
			log.severe(msg);
			throw new IllegalStateException(msg);
		}
		projectDirectory = directory;
		log.exiting(klassName, "Open");
	}

	/**
	 * @return projectDirectory
	 * @throws IllegalStateException if Project is closed.
	 */
	public static File getProjectFile() {
		if (!isOpen())
			throw new IllegalStateException("Project is closed.");
		return projectDirectory;
	}

	/**
	 * @return Directory path as String
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
	 *         also used as the name of the 3Worlds configuration graph root.
	 *         <p>
	 *         Throws IllegalStateException if Project is closed
	 */
	public static String getProjectUserName() {
		if (!isOpen())
			throw new IllegalStateException("Project is closed.");
		String[] items = parseProjectName(projectDirectory);
		return items[1];
	}

	/**
	 * @return The name of the project in human readable format.
	 */
	public static String getDisplayName() {
		return extractDisplayName(projectDirectory);
	}

	/**
	 * @return String containing the unique date and time of project creation.
	 *         <p>
	 *         Throws IllegalStateException if project is not open
	 */
	public static String getProjectDateTime() {
		if (!isOpen())
			throw new IllegalStateException("Project is closed.");
		return extractDateTime(projectDirectory);
	}

	/**
	 * @param pathElements
	 * @return new file with path elements appended to project path.
	 *         <p>
	 *         Throws IllegalStateException if project is not open
	 */
	public static File makeFile(String... pathElements) {
		if (!isOpen())
			throw new IllegalStateException("Cannot make file from a closed Project");
		String s1 = projectDirectory.getAbsolutePath();
		String s2 = FileUtilities.makePath(pathElements);
		String s3 = FileUtilities.makePath(s1, s2);
		return new File(s3);
	}

	/**
	 * 
	 * @return The file path of the ModelMaker preferences file.
	 */
	public static File makeProjectPreferencesFile() {
		return makeFile("MM.xml");
	}

	/**
	 * @return The file path of the ModelRunner preferences file.
	 */
	public static File makeRuntimePreferencesFile() {
		return makeFile(RUNTIME, "MR.xml");
	}

	/**
	 * @return array of all valid 3Worlds projects
	 */
	public static File[] getAllProjectPaths() {
		String repos = TW_HOME;
		File folder = new File(repos);
		if (!folder.exists())
			folder.mkdirs();
		File[] result = folder.listFiles(new ProjectFilter());
		// sort by file name
		Arrays.sort(result, (f1, f2) -> f1.getName().compareTo(f2.getName()));
		return result;
	}

	/**
	 * Test for a valid 3Worlds project directory. It must have three parts
	 * separated with '_', starting with project and ending with a valid time
	 * format.
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
		return true;
	}

	/**
	 * @return a project file path with the correct extension for TreeGraphs.
	 */
	public static File makeConfigurationFile() {
		String name = Project.getProjectUserName();
		// Its a string of several extensions
		return Project.makeFile(name + GraphFileFormats.TGOMUGI.extension().split(" ")[0]);
	}

	/**
	 * 
	 * @return Path to a layout file which contains information about the visual
	 *         appearence of the configuration graph in ModelMaker.
	 */
	public static File makeLayoutFile() {
		return makeFile(".layout" + GraphFileFormats.TGOMUGI.extension().split(" ")[0]);
	}

// used for menu creation
	/**
	 * @param directory
	 * @return The project name without the "project" prefix
	 */
	public static String extractDisplayName(File directory) {
		String[] items = parseProjectName(directory);
		return items[1] + " (" + items[2] + ")";
	}

	/**
	 * @param directory
	 * @return The date part of the full project name.
	 */
	public static String extractDateTime(File directory) {
		String[] items = parseProjectName(directory);
		return items[2];
	}

	/**
	 * @param directories array of 3Worlds directories
	 * @return array of display name strings as {@code Name(Date)}
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
		if (!(items.length == 3)) {
			throw new IllegalArgumentException(
					name + " is not a project name. Must have Project_<name>_<dateTime>. Found: "
							+ Arrays.deepToString(items));
		}
		if (!items[0].equals(PROJECT_DIR_PREFIX))
			throw new IllegalArgumentException(name + " is not a project name. Must start with key work 'Project'");
		try {
			LocalDateTime.parse(items[2], formatter);
		} catch (Exception e) {
			throw new IllegalArgumentException(name + " is not a project name. Must contain valid Date");
		}

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
				throw new IllegalStateException("Identical project directories found: " + f.getName());
			ul.add(f.getName());
		}
	}

	public static Iterable<String> getAllProjectNames() {
		Set<String> result = new HashSet<>();
		File[] dirs = getAllProjectPaths();
		for (File dir : dirs) {
			String name = dir.getName();
			result.add(name.split(sep)[1]);
		}
		return result;
	}

	private static String getDepJarName() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("mac"))
			return "tw-mac.jar";
		if (os.contains("win"))
			return "tw-win.jar";
		return "tw-linux.jar";

	}

	private static String get3wHome() {
		// sudo -H gedit /etc/environment
		// TW_HOME="/home/<name>/3w" or whatever you like including other drive.
		String result = System.getenv().get("TW_HOME");
		if (result == null) {
			result = System.getProperty("user.home") + File.separator + "3w";
			File f = new File(result);
			if (!f.exists())
				f.mkdirs();
		} else {
			File f = new File(result);
			if (!f.exists())
				if (!f.mkdirs())
					throw new IllegalStateException("Could not create 3Worlds home directory: " + f + "\n"
							+ "Check that the environment path for 'TW_HOME' is a valid path name with R/W access.");
		}
		return result;

	}
}
