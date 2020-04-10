
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import au.edu.anu.rscs.aot.errorMessaging.ErrorList;
import au.edu.anu.rscs.aot.util.FileUtilities;
import au.edu.anu.twcore.errorMessaging.ModelBuildErrorMsg;
import au.edu.anu.twcore.errorMessaging.ModelBuildErrors;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.project.ProjectPaths;
import fr.cnrs.iees.twcore.generators.ProjectJarGenerator;
import fr.ens.biologie.generic.utils.Logging;

/**
 * @author Ian Davies
 *
 * @date 29 Sep 2019
 */
public abstract class AbstractUPL implements IUserProjectLink {
	private File modelFile;
	private List<File> dataFiles;
	private List<File> functionFiles;
	private List<File> initialiserFiles;
	public static String extOrig = ".orig";
	private static Logger log = Logging.getLogger(AbstractUPL.class);
	private String userCodeRunnerStr = "public class UserCodeRunner {\n" + //
			"\n" + //
			"// example: String[] args1 = {\"0\", \"<projectPath>\", \"OFF\",\"au.edu.anu.twuifx.widgets.SimpleControlWidget:INFO\"};\n"
			+ "		public static void main(String[] args) {\n" + //
			"			String[] args1 = {\"0\", \"<projectPath>\"};\n" + //
			"			au.edu.anu.twuifx.mr.MRmain.main(args1);\n" + //
			"\n" + //
			"		}\n" + //
			"\n" + //
			"	}";

	private String ppph = "<projectPath>";

	public AbstractUPL() {
		modelFile = null;
		dataFiles = new ArrayList<>();
		functionFiles = new ArrayList<>();
		initialiserFiles = new ArrayList<>();
	}

	@Override
	public void clearFiles() {
		modelFile = null;
		dataFiles.clear();
		functionFiles.clear();
		initialiserFiles.clear();
	}

	@Override
	public void addModelFile(File f) {
		modelFile = f;
		log.info(f.getAbsolutePath());
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

	private void writeUserCodeRunner() {
		File ucrFile = new File(
				this.srcRoot().getAbsolutePath() + File.separator + ProjectJarGenerator.userCodeRunnerSrc);
		if (!ucrFile.exists()) {
			String ucrStr = userCodeRunnerStr;
			String contents = ucrStr.replace(ppph, Project.getProjectFile().getName());
			BufferedWriter outfile;
			try {
				outfile = new BufferedWriter(new FileWriter(ucrFile));
				outfile.write(contents);
				outfile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Pushes files from the ThreeWorlds code generator (LOCAL) to an associated
	 * user java project (REMOTE) (if one exists).
	 *
	 * There are three types of java files generated: Data, Function and
	 * Initialiser. The rules for pushing and overwriting are as follows:
	 *
	 * Data: LOCAL ALWAYS overwrites REMOTE. It is not intended that remote projects
	 * modify this code.
	 *
	 * Function: LOCAL NEVER overwrites REMOTE. This is the rule UNLESS the Function
	 * class has changed. As Function templates all differ, a change in function
	 * class requires that a new java template file be created. To avoid losing
	 * work, old files are backed up as *.orig<n> so the developers can move their
	 * code into the new class as they see fit.
	 *
	 * Initialiser: LOCAL NEVER overwrites REMOTE.
	 *
	 * This method also creates a main class (UserCodeRunner.java) in the default
	 * package to launch their model from the IDE. This allows break-points to be
	 * inserted in the developed code for debugging purposes.
	 */
	// NEW (8/4/2020 JG: there is now a 4th file type, ModelFile
	// new organisaion: only class files are pushed for function and data files
	// the only java file pushed is the model file
	@Override
	public void pushFiles() {
		String remoteSrcPath = this.srcRoot().getAbsolutePath() + File.separator + ProjectPaths.REMOTECODE;
		String remoteClsPath = this.classRoot().getAbsolutePath() + File.separator + ProjectPaths.REMOTECODE;
		String localPath = Project.makeFile(ProjectPaths.LOCALCODE).getAbsolutePath();
		writeUserCodeRunner();
		log.info(localPath + "-> [" + remoteSrcPath + "," + remoteClsPath + "]");
		pushModelFile(localPath,remoteSrcPath, remoteClsPath);
		pushDataFiles(localPath, remoteSrcPath, remoteClsPath);
		pushFunctionFiles(localPath, remoteSrcPath, remoteClsPath);
		pushInitialiserFiles(localPath, remoteSrcPath, remoteClsPath);
		// pushInnerClasses(localPath,remoteSrcPath, remoteClsPath);
	}

	// experimental - always overwrites
	private void pushModelFile(String localPath, String remoteSrcPath, String remoteClsPath) {
		File localClsFile = new File(modelFile.getAbsolutePath().replace(".java", ".class"));
		File remoteSrcFile = new File(modelFile.getAbsolutePath().replace(localPath, remoteSrcPath));
		File remoteClsFile = new File(
				modelFile.getAbsolutePath().replace(localPath, remoteClsPath).replace(".java", ".class"));
		FileUtilities.copyFileReplace(modelFile, remoteSrcFile);
		FileUtilities.copyFileReplace(localClsFile, remoteClsFile);
	}

	// Always overwrite
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

	// NEW version which always overwrites and only copies class files
	public void pushFunctionFiles(String localPath, String remoteSrcPath, String remoteClsPath) {
		for (File localSrcFile : functionFiles) {
			File localClsFile = new File(localSrcFile.getAbsolutePath().replace(".java", ".class"));
			File remoteSrcFile = new File(localSrcFile.getAbsolutePath().replace(localPath, remoteSrcPath));
			File remoteClsFile = new File(
					localSrcFile.getAbsolutePath().replace(localPath, remoteClsPath).replace(".java", ".class"));
			FileUtilities.copyFileReplace(localSrcFile, remoteSrcFile);
			FileUtilities.copyFileReplace(localClsFile, remoteClsFile);
		}
	}


// NEW JG: code kept for recycling
	// Never overwrite unless a class change is detected. In this case backup old
	// file as a text file first.
//	public void pushFunctionFiles(String localPath, String remoteSrcPath, String remoteClsPath) {
//		for (File localSrcFile : functionFiles) {
//			File localClsFile = new File(localSrcFile.getAbsolutePath().replace(".java", ".class"));
//			File remoteSrcFile = new File(localSrcFile.getAbsolutePath().replace(localPath, remoteSrcPath));
//			File remoteClsFile = new File(
//					localSrcFile.getAbsolutePath().replace(localPath, remoteClsPath).replace(".java", ".class"));
//			// Don't overwrite. This is a user editable file
//			if (!remoteSrcFile.exists()) {
//				FileUtilities.copyFileReplace(localSrcFile, remoteSrcFile);
//				FileUtilities.copyFileReplace(localClsFile, remoteClsFile);
//			} else {
//				/*
//				 * ... unless the function class has changed. If it has changed, backup the old
//				 * user file with a unique name before overwriting.
//				 */
//				String localAncestorClass = getAncestorName(localSrcFile);
//				String remoteAncestorClass = getAncestorName(remoteSrcFile);
//				if (!localAncestorClass.equals(remoteAncestorClass)) {
//					// Prepare a backup file
//					File backup = createUniqueBackUp(
//							new File(remoteSrcFile.getAbsolutePath().replace(".java", extOrig + "0")));
//					remoteSrcFile.renameTo(backup);
//					FileUtilities.copyFileReplace(localSrcFile, remoteSrcFile);
//					FileUtilities.copyFileReplace(localClsFile, remoteClsFile);
//					ErrorList.add(new ModelBuildErrorMsg(ModelBuildErrors.PROCESS_CLASS_CHANGE, localAncestorClass,
//							remoteAncestorClass, localSrcFile));
//				}
//			}
//		}
//	}

//	public void pushInnerClasses(String localPath, String remoteSrcPath, String remoteClsPath) {
//		// find any .class files that have no matching .java file
//		File jDir = new File(remoteSrcPath);
//		File[] jFiles = jDir.listFiles(new FilenameFilter() {
//
//			@Override
//			public boolean accept(File dir, String name) {
//				return name.endsWith(".java");
//			}
//
//		});
//		File cDir = new File(remoteClsPath);
//		File[] cFiles = cDir.listFiles(new FilenameFilter() {
//
//			@Override
//			public boolean accept(File dir, String name) {
//				return name.endsWith(".class");
//			}
//
//		});
//		List<File> srcFiles = new ArrayList<>();
//		List<File> clsFiles = new ArrayList<>();
//		List<File> innFiles = new ArrayList<>();
//		for (File f:jFiles)
//			srcFiles.add(f);
//		for (File f:cFiles)
//			clsFiles.add(f);
//
//
//
//	}
	// Never overwrite.
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

	@Override
	public File classForSource(File srcFile) {
		return new File(srcFile.getAbsolutePath().replace(srcRoot().getAbsolutePath(), classRoot().getAbsolutePath())
				.replace(".java", ".class"));
	}

	@Override
	public File sourceForClass(File clsFile) {
		return new File(clsFile.getAbsolutePath().replace(srcRoot().getAbsolutePath(), classRoot().getAbsolutePath())
				.replace(".class", ".java"));
	}

}
