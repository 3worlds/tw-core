
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import au.edu.anu.rscs.aot.errorMessaging.ErrorList;
import au.edu.anu.rscs.aot.util.FileUtilities;
import au.edu.anu.twcore.errorMessaging.ModelBuildErrorMsg;
import au.edu.anu.twcore.errorMessaging.ModelBuildErrors;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.project.ProjectPaths;
import fr.cnrs.iees.identity.IdentityScope;
import fr.cnrs.iees.identity.impl.LocalScope;
import fr.cnrs.iees.twcore.generators.ProjectJarGenerator;
import fr.ens.biologie.codeGeneration.Comments;

/**
 * @author Ian Davies
 *
 * @date 29 Sep 2019
 */
public abstract class AbstractUPL implements IUserProjectLink {
	private List<File> srcPrev;
	private List<File> clsPrev;
	public static String extOrig = ".orig";
	private File remoteModelFile;

	// private static Logger log = Logging.getLogger(AbstractUPL.class);
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
		srcPrev = new ArrayList<>();
		clsPrev = new ArrayList<>();
	}

	@Override
	public void pushCompiledTree(File root, File srcModel) {

		writeUserCodeRunner();

		List<File> srcFiles = getFileTree(root, "java");
		List<File> clsFiles = getFileTree(root, "class");

		String remoteSrcPath = this.srcRoot().getAbsolutePath() + File.separator + ProjectPaths.CODE;
		String remoteClsPath = this.classRoot().getAbsolutePath() + File.separator + ProjectPaths.CODE;
		String localPath = root.getAbsolutePath() + File.separator + ProjectPaths.CODE;

		for (File f : srcPrev)
			if (!srcFiles.contains(f))
				deleteRemoteFile(f, localPath, remoteSrcPath);
		for (File f : clsPrev)
			if (!clsPrev.contains(f))
				deleteRemoteFile(f, localPath, remoteSrcPath);

		// handle modelFile

		File clsModel = null;
		for (File f : srcFiles)
			if (!f.equals(srcModel))
				pushFile(f, localPath, remoteSrcPath);
			else
				clsModel = new File(f.getAbsolutePath().replace(".java", ".class"));
		for (File f : clsFiles)
			if (!f.equals(clsModel))
				pushFile(f, localPath, remoteClsPath);

		updateModelFile(srcModel, clsModel, localPath, remoteSrcPath, remoteClsPath);

		srcPrev.clear();
		clsPrev.clear();

		srcPrev.addAll(srcFiles);
		clsPrev.addAll(clsFiles);
	}

	private void updateModelFile(File localSrc, File localCls, String localPath, String remoteSrcPath,
			String remoteClsPath) {
		File remoteSrc = new File(localSrc.getAbsolutePath().replace(localPath, remoteSrcPath));
		File remoteCls = new File(localCls.getAbsolutePath().replace(localPath, remoteClsPath));
		// first time
		if (!remoteSrc.exists()) {
			FileUtilities.copyFileReplace(localSrc, remoteSrc);
			FileUtilities.copyFileReplace(localCls, remoteCls);
		} else if (fileHasChanged(localSrc, remoteSrc)) {
			backupFile(remoteSrc);
			FileUtilities.copyFileReplace(localSrc, remoteSrc);
			FileUtilities.copyFileReplace(localCls, remoteCls);
			ErrorList.add(new ModelBuildErrorMsg(ModelBuildErrors.MODEL_FILE_BACKUP, localSrc));
		}
		remoteModelFile = remoteSrc;
	}

	private static boolean dump = false;

	@Override
	public Map<String, List<String>> getSnippets() {
		boolean startMethod = false;
		boolean startRead = false;
		String key = null;
		Map<String, List<String>> result = new HashMap<>();
		try {
			List<String> lines = Files.readAllLines(remoteModelFile.toPath());
			for (String line : lines) {
				// stop
				if (line.contains(Comments.endCodeInsert)) {
					startMethod = false;
					startRead = false;
					key = null;
				}
				// read
				if (key != null && startRead) {
					List<String> codeLines = result.get(key);
					if (codeLines == null)
						codeLines = new ArrayList<>();
					codeLines.add(line);
					result.put(key, codeLines);
				}

				// method found?
				String tmp = line.trim();
				String[] parts = tmp.split("\\W+");
				if (parts.length > 2) {
					if (parts[0].equals("public") && parts[1].equals("static") && !startMethod) {
						startMethod = true;
						key = parts[parts.length-1];
					}
				}
				// start read
				if (line.contains(Comments.beginCodeInsert) && startMethod) {
					startRead = true;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private boolean fileHasChanged(File localSrc, File remoteSrc) {
		// strips all but inline comments
		String stripCommentRegex = "(?s)/\\*.*?\\*/";
		List<String> localLines = null;
		List<String> remoteLines = null;
		try {
			localLines = Files.readAllLines(localSrc.toPath());
			remoteLines = Files.readAllLines(remoteSrc.toPath());
			String strLocal = stripLines(localLines);
			String strRemote = stripLines(remoteLines);
			strLocal = strLocal.replaceAll(stripCommentRegex, "");
			strLocal = StringUtils.deleteWhitespace(strLocal);
			strRemote = strRemote.replaceAll(stripCommentRegex, "");
			strRemote = StringUtils.deleteWhitespace(strRemote);
			boolean same = strLocal.equals(strRemote);
			if (!same && dump) {// for debugging. TODO: Could be used as a msg for this circumstance
				if (strLocal.length() != strRemote.length()) {
					System.out.println("Local:\t" + strLocal.length());
					System.out.println("Remote:\t" + strRemote.length());
				}
				int loc = Integer.MAX_VALUE;
				for (int i = 0; i < Math.min(strRemote.length(), strLocal.length()); i++) {
					if (strRemote.charAt(i) != strLocal.charAt(i))
						loc = Math.min(loc, i);
				}
				System.out.println(strRemote);
				System.out.println(strLocal);
				StringBuilder caret = new StringBuilder();
				if (loc < Integer.MAX_VALUE)
					for (int i = 0; i < loc; i++)
						caret.append(" ");
				caret.append("^");
				System.out.println(caret.toString());
			}

			return !same;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	/*-
	 * Strip:
	 * inline comments
	 * blank lines,
	 * import statements
	 * user code inserts
	 */

	private String stripLines(List<String> lines) {
		StringBuilder sb = new StringBuilder();
		boolean skip = false;
		for (String line : lines) {
			line = line.trim();
			if (line.contains(Comments.beginCodeInsert)) {
				sb.append(line);// keep this bit at least
				skip = true;
			} else if (line.contains(Comments.endCodeInsert)) {
				skip = false;
				sb.append(line);// keep this bit at least
			} else if (!skip) {// skip user code
				if (!line.contains("import ")) { // skip imports
					String[] parts = line.split("//"); // look for inline comments and take the first part.
					line = parts[0].trim();
					if (!line.isBlank())
						sb.append(line);
				}
			}
		}

		return sb.toString();
	}

	private void backupFile(File remoteSrc) {
		// we could just save the snippets but user may like to see what has changed.
		String backupRoot = "_" + remoteSrc.getName().replace(".java", "") + "_";
		File[] files = remoteSrc.getParentFile().listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(backupRoot);
			}

		});
		IdentityScope scope = new LocalScope("BACKUP");
		for (File f : files) {
			String name = f.getName().replace(".txt", "");
			scope.newId(true, name);
		}
		String nextName = scope.newId(true, backupRoot + "1").id();
		nextName = nextName + ".txt";
		File backup = new File(remoteSrc.getAbsolutePath().replace(remoteSrc.getName(), nextName));
		FileUtilities.copyFileReplace(remoteSrc, backup);
	}

	private void deleteRemoteFile(File localFile, String localPath, String remotePath) {
		File remoteFile = new File(localFile.getAbsolutePath().replace(localPath, remotePath));
		if (remoteFile.exists())
			remoteFile.delete();
	}

	private void pushFile(File localFile, String localPath, String remotePath) {
		File remoteFile = new File(localFile.getAbsolutePath().replace(localPath, remotePath));
		FileUtilities.copyFileReplace(localFile, remoteFile);
	}

	private List<File> getFileTree(File root, String... ext) {
		List<File> files = new ArrayList<File>();
		for (File f : FileUtils.listFiles(root, ext, true))
			files.add(f);
		return files;
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
