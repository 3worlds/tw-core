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
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.ivy.Ivy;
import org.apache.ivy.core.LogOptions;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.report.ResolveReport;
import org.apache.ivy.core.retrieve.RetrieveOptions;

import au.edu.anu.rscs.aot.util.FileUtilities;
import au.edu.anu.twcore.project.ProjectPaths;
import au.edu.anu.twcore.project.TwPaths;
import fr.ens.biologie.generic.utils.Logging;

/**
 * <p>
 * A class to retrieve all dependencies declared in the ivy.xml of the current
 * application. Code adapted from
 * {@link https://cwiki.apache.org/confluence/display/IVY/Programmatic+use+of+Ivy}
 * </p>
 * 
 * @author Jacques Gignoux - 6 déc. 2017
 *
 */
public class DependencySolver implements ProjectPaths, TwPaths {
	public static final String destPath= TW_ROOT + File.separator + "tmp"; 

	private static Logger log = Logging.getLogger(DependencySolver.class);

	private File ivySettingsXmlFile = null;
	private File dependencyFile = null;

	/**
	 * typically, root should be ~/.3w/lib
	 * 
	 * @param depFile the location of the ivy file (ivy.xml) to search for
	 *                dependencies
	 */
	public DependencySolver(String depFile) {
		super();
		File dir = new File(destPath);
		if (dir.exists())
			try {
				FileUtilities.deleteFileTree(dir);//???
			} catch (IOException e) {
				e.printStackTrace();
			}		
		dir.mkdirs();
		ivySettingsXmlFile = new File(TW_ROOT + File.separator + "tmp" + File.separator + "ivy-settings.xml");
		ivySettingsXmlFile.getParentFile().mkdirs();
		dependencyFile = new File(depFile);
		if (ivySettingsXmlFile.exists())
			ivySettingsXmlFile.delete();
		try {
			ivySettingsXmlFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		buildIvySettingsFile();
	}

	/**
	 * This default ivy settings file is a copy of the default one found in
	 * ~/.ivy2/cache/org.apache.ivy/ivy/jars/ivy-2.2.0.jar
	 */
	private void buildIvySettingsFile() {
		try {
			PrintWriter writer = new PrintWriter(ivySettingsXmlFile);
			writer.println("<ivysettings>");
			writer.println("	<settings defaultResolver=\"default\"/>");
			writer.println(
					"	<property name=\"ivy.shared.default.root\" value=\"${ivy.default.ivy.user.dir}/cache\" override=\"false\"/>");
			writer.println(
					"	<property name=\"ivy.shared.default.ivy.pattern\" value=\"[organisation]/[module]/[revision]/[type]s/[artifact].[ext]\" override=\"false\"/>");
			writer.println(
					"	<property name=\"ivy.shared.default.artifact.pattern\" value=\"[organisation]/[module]/[revision]/[type]s/[artifact].[ext]\" override=\"false\"/>");
			writer.println(
					"	<property name=\"ivy.local.default.root\" value=\"${ivy.default.ivy.user.dir}/local\" override=\"false\"/>");
			writer.println(
					"	<property name=\"ivy.local.default.ivy.pattern\" value=\"[organisation]/[module]/[revision]/[type]s/[artifact].[ext]\" override=\"false\"/>");
			writer.println(
					"	<property name=\"ivy.local.default.artifact.pattern\" value=\"[organisation]/[module]/[revision]/[type]s/[artifact].[ext]\" override=\"false\"/>");
			writer.println("	<resolvers>");
			writer.println("		<ibiblio name=\"public\" m2compatible=\"true\"/>");
			writer.println("		<filesystem name=\"shared\">");
			writer.println(
					"			<ivy pattern=\"${ivy.shared.default.root}/${ivy.shared.default.ivy.pattern}\"/>");
			writer.println(
					"			<artifact pattern=\"${ivy.shared.default.root}/${ivy.shared.default.artifact.pattern}\"/>");
			writer.println("		</filesystem>");
			writer.println("		<filesystem name=\"local\">");
			writer.println("			<ivy pattern=\"${ivy.local.default.root}/${ivy.local.default.ivy.pattern}\"/>");
			writer.println(
					"			<artifact pattern=\"${ivy.local.default.root}/${ivy.local.default.artifact.pattern}\"/>");
			writer.println("		</filesystem>");
			writer.println("		<chain name=\"main\" dual=\"true\">");
			writer.println("			<resolver ref=\"shared\"/>");
			writer.println("			<resolver ref=\"public\"/>");
			writer.println("		</chain>");
			writer.println("		<chain name=\"default\" returnFirst=\"true\" checkmodified=\"true\">");
			writer.println("			<resolver ref=\"local\"/>");
			writer.println("			<resolver ref=\"main\"/>");
			writer.println("		</chain>");
			writer.println("	</resolvers>");
			writer.println("</ivysettings>");
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * this does the hard work. cf.
	 * {@link https://cwiki.apache.org/confluence/display/IVY/Programmatic+use+of+Ivy}
	 */
	@SuppressWarnings("unchecked")
	private void resolveDependencies() {
		Ivy ivy = Ivy.newInstance();
		try {
			ivy.configure(ivySettingsXmlFile);
			ResolveReport resolveReport = ivy.resolve(dependencyFile);
			if (resolveReport.hasError()) {
				List<String> problems = resolveReport.getAllProblemMessages();
				if (problems != null && !problems.isEmpty()) {
					StringBuffer errorMsgs = new StringBuffer();
					for (String problem : problems) {
						errorMsgs.append(problem);
						errorMsgs.append("\n");
					}
					log.warning("Errors encountered during dependency resolution for package [" + "] :");
					log.warning(errorMsgs.toString());
				}
			} else {
				log.fine("Dependencies in file " + dependencyFile + " were successfully resolved");
			}
			ModuleDescriptor md = resolveReport.getModuleDescriptor();
			ModuleRevisionId mRID = md.getModuleRevisionId();
			RetrieveOptions retrieveOptions = new RetrieveOptions();
			// This should be a tmp dir
			File destFolder = new File(destPath);
			String pattern = destFolder + "/[organization]/[module]/[type]/[artifact]-[revision].[ext]";
			retrieveOptions.setDestIvyPattern(pattern);
			retrieveOptions.setLog(LogOptions.LOG_QUIET); // use LOG_DEFAULT to see messages
			int packagesRetrieved;
			packagesRetrieved = ivy.retrieve(mRID, pattern, retrieveOptions);
			log.fine("Retrieved " + packagesRetrieved + " dependencies");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * gets all the jar paths from the ivy cache
	 * 
	 * @param dir  the directory to search for
	 * @param list the list of jars found
	 */
	private void recurseDir(File dir, Collection<String> list) {
		if (dir.isDirectory()) {
			File[] subs = dir.listFiles();
			for (File sub : subs)
				recurseDir(sub, list);
		} else if (dir.getName().endsWith(".jar"))
			list.add(dir.getPath());
	}

	/**
	 * 
	 * @return a collection of all jar pathnames retrieved by the ivy dependency
	 *         solver
	 */
	public Collection<String> getJars() {
		File dir = new File(destPath);
		resolveDependencies();
		Collection<String> jarPaths = new LinkedList<>();
		recurseDir(dir, jarPaths);
		return jarPaths;
	}

}
