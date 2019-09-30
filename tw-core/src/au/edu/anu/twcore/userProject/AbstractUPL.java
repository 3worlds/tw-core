
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

package au.edu.anu.twcore.userProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import au.edu.anu.rscs.aot.util.FileUtilities;
import au.edu.anu.twcore.errorMessaging.ComplianceManager;
import au.edu.anu.twcore.errorMessaging.codeGenerator.ProcessClassChangeErr;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.project.ProjectPaths;
import fr.ens.biologie.generic.utils.Logging;

/**
 * @author Ian Davies
 *
 * @date 29 Sep 2019
 */
public abstract class AbstractUPL implements IUserProjectLink {
	private List<File> dataFiles;
	private List<File> functionFiles;
	private List<File> initialiserFiles;
	private static String extOrig = ".orig";
	private static Logger log = Logging.getLogger(AbstractUPL.class);

	public AbstractUPL() {
		dataFiles = new ArrayList<>();
		functionFiles = new ArrayList<>();
		initialiserFiles = new ArrayList<>();
	}

	@Override
	public void clearFiles() {
		dataFiles.clear();
		functionFiles.clear();
		initialiserFiles.clear();
	}

	@Override
	public void addDataFile(File f) {
		dataFiles.add(f);
		log.info(f.getAbsolutePath());
	}

	@Override
	public void addFunctionFile(File f) {
		functionFiles.add(f);
		log.info(f.getAbsolutePath());
	}

	@Override
	public void addInitialiserFile(File f) {
		initialiserFiles.add(f);
		log.info(f.getAbsolutePath());
	}

	@Override
	public void pushFiles() {
		String remoteSrcPath = this.srcRoot().getAbsolutePath();
		String remoteClsPath = this.classRoot().getAbsolutePath();
		String localPath = Project.makeFile(ProjectPaths.CODE).getAbsolutePath();
		log.info(localPath + "-> [" + remoteSrcPath + "," + remoteClsPath + "]");
		pushDataFiles(localPath, remoteSrcPath, remoteClsPath);
		pushFunctionFiles(localPath, remoteSrcPath, remoteClsPath);
		pushInitialiserFiles(localPath, remoteSrcPath, remoteClsPath);
	}

	private void pushDataFiles(String localPath, String remoteSrcPath, String remoteClsPath) {
		for (File localSrcFile : dataFiles) {
			File localClsFile = new File(localSrcFile.getAbsolutePath().replace(".java", ".class"));
			File remoteSrcFile = new File(localSrcFile.getAbsolutePath().replace(localPath, remoteSrcPath));
			File remoteClsFile = new File(
					localSrcFile.getAbsolutePath().replace(localPath, remoteClsPath).replace(".java", ".class"));
			FileUtilities.copyFileReplace(localSrcFile, remoteSrcFile);
			FileUtilities.copyFileReplace(localClsFile, remoteClsFile);
		}
	}

	public void pushFunctionFiles(String localPath, String remoteSrcPath, String remoteClsPath) {
		for (File localSrcFile : functionFiles) {
			File localClsFile = new File(localSrcFile.getAbsolutePath().replace(".java", ".class"));
			File remoteSrcFile = new File(localSrcFile.getAbsolutePath().replace(localPath, remoteSrcPath));
			File remoteClsFile = new File(
					localSrcFile.getAbsolutePath().replace(localPath, remoteClsPath).replace(".java", ".class"));
			// Don't overwrite. This is a user editable file
			if (!remoteSrcFile.exists()) {
				FileUtilities.copyFileReplace(localSrcFile, remoteSrcFile);
				FileUtilities.copyFileReplace(localClsFile, remoteClsFile);
			} else {
				/*
				 * ... unless the function class has changed. If it has changed, backup the old
				 * user file with a unique name before overwriting.
				 */
				String localAncestorClass = getAncestorName(localSrcFile);
				String remoteAncestorClass = getAncestorName(remoteSrcFile);
				if (!localAncestorClass.equals(remoteAncestorClass)) {
					// Prepare a backup file
					File backup = createUniqueBackUp(
							new File(remoteSrcFile.getAbsolutePath().replace(".java", extOrig + "0")));
					remoteSrcFile.renameTo(backup);
					FileUtilities.copyFileReplace(localSrcFile, remoteSrcFile);
					FileUtilities.copyFileReplace(localClsFile, remoteClsFile);
					ComplianceManager.add(
							new ProcessClassChangeErr(localAncestorClass, remoteAncestorClass, localSrcFile.getName()));

				}
			}
		}
	}

	public void pushInitialiserFiles(String localPath, String remoteSrcPath, String remoteClsPath) {
		for (File inSrcFile : initialiserFiles) {
			File inClsFile = new File(inSrcFile.getAbsolutePath().replace(".java", ".class"));
			File remoteClsFile = new File(
					inSrcFile.getAbsolutePath().replace(localPath, remoteClsPath).replace(".java", ".class"));
			File remoteSrcFile = new File(inSrcFile.getAbsolutePath().replace(localPath, remoteSrcPath));
			// Don't overwrite. This is a user editable file.
			if (!remoteSrcFile.exists()) {
				FileUtilities.copyFileReplace(inSrcFile, remoteSrcFile);
				FileUtilities.copyFileReplace(inClsFile, remoteClsFile);
			}
		}
	}

	private static String getAncestorName(File f) {
		String result = "";
		try {
			BufferedReader infile = new BufferedReader(new FileReader(f));
			String line = infile.readLine();
			while (line != null) {
				if (line.contains("extends")) {
					String[] tokens = line.split(" ");
					result = getAncestorToken(tokens);
					result = result.trim();
					infile.close();
					return result;
				}
				line = infile.readLine();
			}
			infile.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static String getAncestorToken(String[] tokens) {
		for (int i = 0; i < tokens.length - 2; i++) {
			String token = tokens[i];
			if (token.equals("extends"))
				return tokens[i + 1];
		}
		return null;
	}

	private static File createUniqueBackUp(File file) {
		if (!file.exists()) {
			return file;
		} else {
			String name = file.getName();
			String ext = name.substring(name.indexOf("."), name.length());
			String sCount = ext.replace(extOrig, "");
			int count = Integer.parseInt(sCount) + 1;
			String newExt = extOrig + count;
			String newName = name.replace(ext, newExt);
			File newFile = new File(file.getParent() + File.separator + newName);
			return createUniqueBackUp(newFile);
		}
	}

}
