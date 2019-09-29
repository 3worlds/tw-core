
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

import au.edu.anu.rscs.aot.util.FileUtilities;
import au.edu.anu.twcore.errorMessaging.ComplianceManager;
import au.edu.anu.twcore.errorMessaging.codeGenerator.ProcessClassChangeErr;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.project.ProjectPaths;

/**
 * @author Ian Davies
 *
 * @date 29 Sep 2019
 */
public abstract class AbstractUPL implements IUserProjectLink {
	private List<File> dataFiles = new ArrayList<>();
	private List<File> functionFiles = new ArrayList<>();
	private List<File> initialiserFiles = new ArrayList<>();

	public enum CodeGenTypes {
		DATA, FUNCTION, INITIALISER;
	}

	@Override
	public void clearFiles() {
		dataFiles.clear();
		functionFiles.clear();
		initialiserFiles.clear();
	}

	public void addJavaFile(CodeGenTypes t, File f) {
		switch (t) {
		case DATA: {
			dataFiles.add(f);
			dataFiles.add(new File(f.getAbsolutePath().replace(".java", ".class")));
			break;
		}
		case FUNCTION: {
			functionFiles.add(f);
			break;
		}
		default: {
			initialiserFiles.add(f);
		}
		}
	}

	@Override
	public void pushFiles() {
		pushDataFiles();
		pushFunctionFiles();
		pushInitialiserFiles();
	}

	private void pushDataFiles() {
		String codePath = projectRoot().getAbsolutePath();
		String root = Project.makeFile(ProjectPaths.CODE).getAbsolutePath();
		for (File infile : dataFiles) {
			File outfile = null;
			if (infile.getAbsolutePath().endsWith(".java"))
				outfile = new File(
						infile.getAbsolutePath().replace(root, codePath + File.separator + SRC + File.separator));
			else
				outfile = new File(infile.getAbsolutePath().replace(root, codePath + File.separator + BIN));

			new File(outfile.getParent()).mkdirs();
			FileUtilities.copyFileReplace(infile, outfile);
		}
	}

	public void pushFunctionFiles() {
		for (File infile : functionFiles) {
			File javaProjectFile = makeJavaProjectPair(codePath, rootPackage, infile);
			File backup = new File(javaProjectFile.getAbsolutePath().replace(".java", extOrig + "0"));
			if (!javaProjectFile.exists()) {
				FileUtilities.copyFileReplace(infile, javaProjectFile);
				File modelClass = new File(infile.getAbsolutePath().replace(".java", ".class"));
				File prjClass = makeClassProjectPair(codePath, modelClass);
				FileUtilities.copyFileReplace(modelClass, prjClass);
			} else {
				String newAncestorClass = getAncestorName(infile);
				String oldAncestorClass = getAncestorName(javaProjectFile);
				if (!newAncestorClass.equals(oldAncestorClass)) {
					backup = createUniqueBackUp(backup);
					javaProjectFile.renameTo(backup);
					FileUtilities.copyFileReplace(infile, javaProjectFile);
					File modelClass = new File(infile.getAbsolutePath().replace(".java", ".class"));
					File prjClass = makeClassProjectPair(codePath, modelClass);
					FileUtilities.copyFileReplace(modelClass, prjClass);

					ComplianceManager
							.add(new ProcessClassChangeErr(newAncestorClass, oldAncestorClass, infile.getName()));

				}
			}
		}
	}

	public void pushInitialiserFiles() {
		for (File infile : initialiserFiles) {
			File prjJava = makeJavaProjectPair(codePath, rootPackage, infile);
			if (!prjJava.exists()) {
				File modelClass = new File(infile.getAbsolutePath().replace(".java", ".class"));
				File prjClass = makeClassProjectPair(codePath, modelClass);
				FileUtilities.copyFileReplace(infile, prjJava);
				FileUtilities.copyFileReplace(modelClass, prjClass);
			}
		}
	}

	private static String extOrig = ".orig";

	private static File makeJavaProjectPair(String codePath, String rootPackage, File template) {
		String root = Project.makeFile(ProjectPaths.CODE).getAbsolutePath();
		File result = new File(
				template.getAbsolutePath().replace(root, codePath + File.separator + SRC + File.separator));
		return result;
	}

	private static File makeClassProjectPair(String codePath, File template) {
		String root = Project.makeFile(ProjectPaths.CODE).getAbsolutePath();
		File result = new File(template.getAbsolutePath().replace(root, codePath + File.separator + BIN));
		return result;
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

	public  void show() {
		System.out.println(CodeGenTypes.DATA);
		for (File f : dataFiles) {
			System.out.println(f.getName());
		}
		System.out.println(CodeGenTypes.FUNCTION);
		for (File f : functionFiles) {
			System.out.println(f.getName());
		}
		System.out.println(CodeGenTypes.INITIALISER);
		for (File f : initialiserFiles) {
			System.out.println(f.getName());
		}

	}

}
