package fr.cnrs.iees.twcore.generators;

import static au.edu.anu.rscs.aot.queries.CoreQueries.hasTheLabel;
import static au.edu.anu.rscs.aot.queries.CoreQueries.selectOneOrMany;
import static au.edu.anu.rscs.aot.queries.CoreQueries.selectZeroOrMany;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.N_DATASOURCE;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.N_EXPERIMENT;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_DESIGN_FILE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import au.edu.anu.omhtk.jars.Jars;
import au.edu.anu.rscs.aot.util.FileUtilities;
import au.edu.anu.twcore.errorMessaging.ComplianceManager;
import au.edu.anu.twcore.errorMessaging.deploy.DeployClassFileMissing;
import au.edu.anu.twcore.errorMessaging.deploy.DeployClassOutOfDate;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.project.ProjectPaths;
import au.edu.anu.twcore.project.TwPaths;
import au.edu.anu.twcore.userProject.UserProjectLink;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.twcore.constants.FileType;

public class ProjectJarGenerator {
	public static String mainClass = "au.edu.anu.twuifx.mr.Main";

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
			libraryExclusions.add(TwPaths.TW_DEP_JAR);
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

	private Set<String> copyUserLibraries(File[] fJars) {
		/**
		 * Copy any libraries used by the Java project to the targetDir. These can then
		 * be referenced in the simulator.jar
		 */

		File targetDir = Project.makeFile(ProjectPaths.LIB);
		targetDir.mkdirs();
		Set<String> result = new HashSet<>();
		String relativePath = "." + targetDir.getAbsolutePath().replace(Project.makeFile().getAbsolutePath(), "");
		if (fJars == null)
			return result;
		for (File fJar : fJars) {
			File outPath = new File(targetDir.getAbsolutePath() + File.separator + fJar.getName());
			try {
				Files.copy(fJar.toPath(), outPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
				String entry = relativePath + "/" + outPath.getName();
				result.add(entry.replace("\\", "/"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;

	}

	private void pullAllCodeFiles() {
		File fDstRoot = Project.makeFile(ProjectPaths.CODE);
		String[] extensions = new String[] { "java" };
		List<File> remoteSrcFiles = (List<File>) FileUtils.listFiles(UserProjectLink.srcRoot(), extensions, true);
		for (File remoteSrcFile : remoteSrcFiles) {
			if (!remoteSrcFile.getName().equals("UserCodeRunner.java")) {
				File remoteClsFile = UserProjectLink.classForSource(remoteSrcFile);
				File fDstJava = swapDirectory(remoteSrcFile, UserProjectLink.srcRoot(), fDstRoot);
				File fDstClass = swapDirectory(remoteClsFile, UserProjectLink.classRoot(), fDstRoot);
				if (!remoteClsFile.exists())
					ComplianceManager.add(new DeployClassFileMissing(remoteClsFile, remoteSrcFile));
				else {
					try {
						FileTime ftSrc = Files.getLastModifiedTime(remoteSrcFile.toPath());
						FileTime ftCls = Files.getLastModifiedTime(remoteClsFile.toPath());
						Long ageJava = ftSrc.toMillis();
						Long ageClass = ftCls.toMillis();
						if (ageJava > ageClass)
							ComplianceManager.add(new DeployClassOutOfDate(remoteSrcFile, remoteClsFile, ftSrc, ftCls));
					} catch (IOException e) {
						e.printStackTrace();
					}
					FileUtilities.copyFileReplace(remoteSrcFile, fDstJava);
					FileUtilities.copyFileReplace(remoteClsFile, fDstClass);
				}
			}
		}
	}

	private void pullAllResources() {
		File fdstRoot = Project.makeFile(Project.RES);
		File fSrcRoot = UserProjectLink.srcRoot();
		List<File> files = (List<File>) FileUtils.listFiles(fSrcRoot, null, true);
		for (File srcFile : files) {
			String name = srcFile.getName();
			if (!(name.endsWith("java") || name.endsWith("class"))) {
				File dstFile = swapDirectory(srcFile, UserProjectLink.srcRoot(), fdstRoot);
				dstFile.mkdirs();
				FileUtilities.copyFileReplace(srcFile, dstFile);
			}
		}
	}

	private static File swapDirectory(File file, File from, File to) {
		File result = new File(file.getAbsolutePath().replace(from.getAbsolutePath(), to.getAbsolutePath()));
		return result;
	}

	private void loadModelCode(Set<File> srcFiles, Set<File> resFiles) {
		File srcRoot = Project.makeFile(ProjectPaths.CODE);
		File resRoot = Project.makeFile(ProjectPaths.RES);
		if (srcRoot.exists())
			srcFiles.addAll(FileUtils.listFiles(srcRoot, null, true));
		if (resRoot.exists())
			resFiles.addAll(FileUtils.listFiles(resRoot, null, true));
	}

}
