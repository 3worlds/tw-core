
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
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import au.edu.anu.rscs.aot.errorMessaging.ErrorMessageManager;
import au.edu.anu.rscs.aot.util.FileUtilities;
import au.edu.anu.twcore.errorMessaging.ModelBuildErrorMsg;
import au.edu.anu.twcore.errorMessaging.ModelBuildErrors;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.project.ProjectPaths;
import fr.cnrs.iees.identity.IdentityScope;
import fr.cnrs.iees.identity.impl.LocalScope;
import fr.cnrs.iees.twcore.generators.ProjectJarGenerator;
import fr.ens.biologie.codeGeneration.Comments;
import static au.edu.anu.rscs.aot.util.StringUtils.ELLIPSIS;

/**
 * @author Ian Davies
 *
 * @date 29 Sep 2019
 */
public abstract class AbstractUPL implements IUserProjectLink {
	public static String extOrig = ".orig";
	//private File remoteModelFile;

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
		super();
	}

	@Override
	public void pushCompiledTree(File root, File srcModel) {

		/**
		 * If this is first time, create the UserCodeRunner.java file. We do this only
		 * once to avoid overwriting user edits. This means, if the java project is used
		 * by more than one 3w project, UserCodeRunner will attempt to run the wrong
		 * project.
		 */
		writeUserCodeRunner();

		// Obtain a complete list of all local files.
		List<File> srcCurrentLocalFiles = getFileTree(root, "java");
		List<File> clsCurrentLocalFiles = getFileTree(root, "class");

		// Get local and remote paths as strings to use as find/replace
		String remoteSrcPath = this.srcRoot().getAbsolutePath() + File.separator + ProjectPaths.CODE;
		String remoteClsPath = this.classRoot().getAbsolutePath() + File.separator + ProjectPaths.CODE;
		String localPath = root.getAbsolutePath() + File.separator + ProjectPaths.CODE;

		/**
		 * The Main Model Class (MMC) is the only file that is edited by both MM and
		 * IDE. Therefore, it must only be pushed if it has been changed by MM (with
		 * backup if it already exists in the IDE). If the MMC is edited in IDE (and any
		 * other files created and edited there), it is expected the edits of the MMC
		 * (i.e these can only be snippets) and any other java files, are imported by
		 * the user to 3w. Thus, the 'import' menu option in MM MUST also import any
		 * user edited/created classes (cf import snippets).
		 * 
		 * TODO: Check what happens if there is some dependence upon data files when run
		 * from:
		 * 
		 * 1) MM
		 * 
		 * 2) MR
		 * 
		 * 3) UserCodeRunner
		 * 
		 */
		// push all files except the MMC
		File clsModel = null;
		for (File f : srcCurrentLocalFiles)
			if (!f.equals(srcModel))
				copyOver(f, localPath, remoteSrcPath);
			else
				clsModel = new File(f.getAbsolutePath().replace(".java", ".class"));
		for (File f : clsCurrentLocalFiles)
			if (!f.equals(clsModel))
				copyOver(f, localPath, remoteClsPath);

		// Push the MMC if required.
		updateModelFile(srcModel, clsModel, localPath, remoteSrcPath, remoteClsPath);

	}

	@Override
	public List<String> pullDependentTree(File mainModelClass) {
		// TODO: This assumes only one model main class and therefore one system node since this file is within the system.id dir!
		List<File> fileList = getFileTree(new File(mainModelClass.getParentFile().getParent()), "java");
		String genStr = mainModelClass.getParent() + File.separator + ProjectPaths.GENERATED;
		String modStr = mainModelClass.getAbsolutePath();

		List<File> remoteFiles = new ArrayList<>();
		for (File f : fileList)
			if (!f.getAbsolutePath().contains(genStr))
				if (!f.getAbsolutePath().contains(modStr))
					remoteFiles.add(f);

		List<String> result = new ArrayList<>();
		String remotePath = mainModelClass.getParentFile().getParent();
		String localPath = Project.makeFile(ProjectPaths.LOCALJAVACODE).getAbsolutePath();
		for (File remoteFile : remoteFiles) {
			File toFile = copyOver(remoteFile, remotePath, localPath);
			String s = remoteFile.getAbsolutePath().replace(remotePath, ELLIPSIS) + " -> "
					+ toFile.getAbsolutePath().replace(localPath, ELLIPSIS);
			result.add(s);
		}
		return result;
	}

	private void updateModelFile(File localSrc, File localCls, String localPath, String remoteSrcPath,
			String remoteClsPath) {
		File reomteSrc = new File(localSrc.getAbsolutePath().replace(localPath, remoteSrcPath));
		File remoteCls = new File(localCls.getAbsolutePath().replace(localPath, remoteClsPath));
		// first time
		if (!reomteSrc.exists()) {
			FileUtilities.copyFileReplace(localSrc, reomteSrc);
			FileUtilities.copyFileReplace(localCls, remoteCls);
		} else if (fileHasChanged(localSrc, reomteSrc)) {
			backupFile(reomteSrc);
			FileUtilities.copyFileReplace(localSrc, reomteSrc);
			FileUtilities.copyFileReplace(localCls, remoteCls);
			ErrorMessageManager.dispatch(new ModelBuildErrorMsg(ModelBuildErrors.MODEL_FILE_BACKUP, localSrc));
		}
		//remoteModelFile = remoteSrc;
	}

	private static boolean dump = false;

	@Override
	public Map<String, List<String>> getSnippets(File mainModelClass) {
		return SnippetReader.readSnippetsFromFile(mainModelClass);
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
			if (line.contains(Comments.codeInsertBegin) || line.contains(Comments.importInsertBegin)) {
				sb.append(line);// keep this bit at least
				skip = true;
			} else if (line.contains(Comments.codeInsertEnd) || line.contains(Comments.importInsertEnd)) {
				skip = false;
				sb.append(line);// keep this bit at least
			} else if (!skip) {// skip user code
				if (!line.contains("import ")) { // skip generated imports
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

//	private void deleteRemoteFile(File localFile, String localPath, String remotePath) {
//		File remoteFile = new File(localFile.getAbsolutePath().replace(localPath, remotePath));
//		if (remoteFile.exists())
//			remoteFile.delete();
//	}

//	private static void pushFile(File localFile, String localPath, String remotePath) {
//		File remoteFile = new File(localFile.getAbsolutePath().replace(localPath, remotePath));
//		FileUtilities.copyFileReplace(localFile, remoteFile);
//	}
//	private static void pullFile(File remoteFile, String remotePath, String localPath) {
//		File localFile = new File (remoteFile.getAbsolutePath().replace(remotePath,localPath));
//		FileUtilities.copyFileReplace(remoteFile,localFile);
//	}
	private static File copyOver(File fromFile, String from, String to) {
		File toFile = new File(fromFile.getAbsolutePath().replace(from, to));
		FileUtilities.copyFileReplace(fromFile, toFile);
		return toFile;
	}

	private static List<File> getFileTree(File root, String... ext) {
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
