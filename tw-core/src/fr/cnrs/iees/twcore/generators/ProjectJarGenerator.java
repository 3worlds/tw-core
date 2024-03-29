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
package fr.cnrs.iees.twcore.generators;

import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.*;

import org.apache.commons.io.FileUtils;

import au.edu.anu.omhtk.jars.Jars;
import au.edu.anu.aot.errorMessaging.ErrorMessageManager;
import au.edu.anu.omhtk.util.FileUtilities;
import au.edu.anu.twcore.errorMessaging.ModelBuildErrorMsg;
import au.edu.anu.twcore.errorMessaging.ModelBuildErrors;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.userProject.AbstractUPL;
import au.edu.anu.twcore.userProject.UserProjectLink;
import fr.cnrs.iees.omugi.graph.impl.*;
import fr.cnrs.iees.twcore.constants.FileType;

public class ProjectJarGenerator {
	private static String mainClass = null;
	public static final String userCodeRunnerSrc = "UserCodeRunner.java";
	public static final String userCodeRunnerCls = "UserCodeRunner.class";
	public static void setModelRunnerClass(Class<?> klass) {
		mainClass = klass.getName();
	}

	@SuppressWarnings("unchecked")
	public void generate(TreeGraph<TreeGraphDataNode, ALEdge> graph) {
		Set<String> userLibraries = new HashSet<>();
		Set<File> codeFiles = new HashSet<>();
		Set<File> resFiles = new HashSet<>();
		Set<File> dataFiles = new HashSet<>();

		List<TreeGraphDataNode> experiments = (List<TreeGraphDataNode>) get(graph.root().getChildren(),
				selectOneOrMany(hasTheLabel(N_EXPERIMENT.label())));
		for (TreeGraphDataNode experiment : experiments) {
			List<TreeGraphDataNode> dataSources = (List<TreeGraphDataNode>) get(experiment.getChildren(),
					selectZeroOrMany(hasTheLabel(N_DATASOURCE.label())));
			for (TreeGraphDataNode dataSource : dataSources) {
				// TODO property enum yet to be defined for data sources
				File f = ((FileType) dataSource.properties().getPropertyValue(P_DESIGN_FILE.key())).getFile();
				dataFiles.add(f);
			}
		}
		if (UserProjectLink.haveUserProject()) {
			Set<String> libraryExclusions = new HashSet<>();
			libraryExclusions.add(Project.TW_DEP_JAR);
//			libraryExclusions.add(TwPaths.TW_FX_DEP_JAR);
			userLibraries = copyUserLibraries(UserProjectLink.getUserLibraries(libraryExclusions));
			pullAllCodeFiles();
			pullAllResources();
		}
		// make one userCodeJar in root of project
		loadModelCode(codeFiles, resFiles);
		Jars packer = new SimulatorJar(mainClass, dataFiles, codeFiles, resFiles, userLibraries);
//		Jars executable = new SimulatorJar(dataFiles, userCodeJars, userLibraries);
		File executableJarFile = Project.makeFile(Project.getProjectUserName() + ".jar");
		packer.saveJar(executableJarFile);
		// return executableJarFile.getName();

	}

	private Set<String> copyUserLibraries(File[] remoteJarFiles) {
		/**
		 * Copy any libraries used by the Java project to the targetDir. These can then
		 * be referenced in the simulator.jar
		 */

		File localDir = Project.makeFile(Project.LOCAL_JAVA_LIB);
		localDir.mkdirs();
		Set<String> result = new HashSet<>();
		String relativePath = "." + localDir.getAbsolutePath().replace(Project.makeFile().getAbsolutePath(), "");
		if (remoteJarFiles == null)
			return result;
		for (File remoteJarFile : remoteJarFiles) {
			File localJarFile = new File(localDir.getAbsolutePath() + File.separator + remoteJarFile.getName());
			try {
				Files.copy(remoteJarFile.toPath(), localJarFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				String entry = relativePath + "/" + localJarFile.getName();
				result.add(entry.replace("\\", "/"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;

	}

	private void pullAllCodeFiles() {
		File localDir = Project.makeFile(Project.LOCAL_JAVA);
		String[] srcExtensions = new String[] { "java" };
		List<File> remoteSrcFiles = (List<File>) FileUtils.listFiles(UserProjectLink.srcRoot(), srcExtensions, true);
		for (File remoteSrcFile : remoteSrcFiles) {
			if (!remoteSrcFile.getName().equals(userCodeRunnerSrc)) {
				File remoteClsFile = UserProjectLink.classForSource(remoteSrcFile);
				File localSrcFile = replaceParentPath(remoteSrcFile, UserProjectLink.srcRoot(), localDir);
				File localClsFile = replaceParentPath(remoteClsFile, UserProjectLink.classRoot(), localDir);
				if (!remoteClsFile.exists())
					ErrorMessageManager.dispatch(new ModelBuildErrorMsg(ModelBuildErrors.DEPLOY_CLASS_MISSING, remoteClsFile,
							remoteSrcFile));
				else {
					try {
						FileTime ftSrc = Files.getLastModifiedTime(remoteSrcFile.toPath());
						FileTime ftCls = Files.getLastModifiedTime(remoteClsFile.toPath());
						Long ageJava = ftSrc.toMillis();
						Long ageClass = ftCls.toMillis();
						if (ageJava > ageClass)
							ErrorMessageManager.dispatch(new ModelBuildErrorMsg(ModelBuildErrors.DEPLOY_CLASS_OUTOFDATE, remoteSrcFile,
									remoteClsFile, ftSrc, ftCls));
					} catch (IOException e) {
						e.printStackTrace();
					}
					FileUtilities.copyFileReplace(remoteSrcFile, localSrcFile);
					FileUtilities.copyFileReplace(remoteClsFile, localClsFile);
				}
			}
		}
		// any class files not already copied must be inner classes
		String[] clsExtensions = new String[] { "class" };
		List<File> remoteClsFiles = (List<File>) FileUtils.listFiles(UserProjectLink.classRoot(), clsExtensions, true);
		for (File remoteClsFile : remoteClsFiles) {
			if (!remoteClsFile.getName().equals(userCodeRunnerCls)) {
				File localClsFile = replaceParentPath(remoteClsFile, UserProjectLink.classRoot(), localDir);
				if (!localClsFile.exists()) {
					String name = remoteClsFile.getName();
					if (name.contains("$"))// just to be sure and also excludes the codeRunner
						FileUtilities.copyFileReplace(remoteClsFile, localClsFile);
				}
			}
		}

	}

	private void pullAllResources() {
		File localDir = Project.makeFile(Project.LOCAL_JAVA_RES);
		List<File> remoteFiles = (List<File>) FileUtils.listFiles(UserProjectLink.srcRoot(), null, true);
		for (File remoteFile : remoteFiles) {
			String name = remoteFile.getName();
			if (!(name.endsWith("java") || name.endsWith("class") || name.contains(AbstractUPL.extOrig))) {
				File localResFile = replaceParentPath(remoteFile, UserProjectLink.srcRoot(), localDir);
				localResFile.mkdirs();
				FileUtilities.copyFileReplace(remoteFile, localResFile);
			}
		}
	}

	private static File replaceParentPath(File file, File from, File to) {
		File result = new File(file.getAbsolutePath().replace(from.getAbsolutePath(), to.getAbsolutePath()));
		return result;
	}

	private void loadModelCode(Set<File> srcFiles, Set<File> resFiles) {
		File srcRoot = Project.makeFile(Project.LOCAL_JAVA_CODE);
		File resRoot = Project.makeFile(Project.LOCAL_JAVA_RES);
		if (srcRoot.exists())
			srcFiles.addAll(FileUtils.listFiles(srcRoot, null, true));
		if (resRoot.exists())
			resFiles.addAll(FileUtils.listFiles(resRoot, null, true));
	}

}
