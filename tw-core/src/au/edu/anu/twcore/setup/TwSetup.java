/**
 * 
 */
package au.edu.anu.twcore.setup;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;

import au.edu.anu.omhtk.jars.Jars;
import au.edu.anu.twcore.jars.DependencySolver;
import au.edu.anu.twcore.jars.ThreeWorldsJar;
import au.edu.anu.twcore.project.ProjectPaths;
import au.edu.anu.twcore.project.TwPaths;


/**
 * A class to properly generate the .3w repo with the necessary dependencies from the 3Worlds 
 * eclipse project.
 * 
 * @author Ian Davies
 * @date 10 Dec. 2017
 */
public class TwSetup implements ProjectPaths, TwPaths {
	
	// please only increment these numbers (with caution). Never decrement.
	public static final String VERSION_MAJOR = "0";
	public static final String VERSION_MINOR = "0";
	public static final String VERSION_MICRO = "1";
	
	public static final String TW_DEP_JAR = "tw-dep.jar";
	// NB these two names cannot be extracted from the classes because the classes are
	// in tw-uifx.
	private static final String MODELMAKER_CLASS = "";
	private static final String MODELRUNNER_CLASS = "";
	
	private static void deleteFileTree(File dir) throws IOException {
		Path root = dir.toPath();
		Files.walk(root)
				.sorted(Comparator.reverseOrder())
				.map(Path::toFile)
				.forEach(File::delete);
		assertFalse("Directory still exists", Files.exists(root));
	}
	
	/**
	 * <p>For end users: generates a Jar with ModelMaker ready to run.
	 * This jar is just a manifest with:
	 * <ul>
	 * <li>tw-dep.jar</li>
	 * </ul>
	 * assuming these two jars reside in the same directory as ModelMaker.jar
	 * </p>
	 */
	public static void packModelMaker() {
		ThreeWorldsJar packer = new ThreeWorldsJar(VERSION_MAJOR,VERSION_MINOR,VERSION_MICRO);
		packer.setMainClass(MODELMAKER_CLASS);
		packer.addDependencyOnJar(TW_ROOT+Jars.separator+TW_DEP_JAR);
		File outFile = jarFile("modelMaker.jar");
		packer.saveJar(outFile);
		outFile.setExecutable(true, false);
	}
	
	private static File jarFile(String filename) {
		File file = new File(USER_ROOT+File.separator+filename);
		if (file.exists()) 
			file.delete();
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	/**
	 * <p>For end users: generates a Jar with ModelMaker ready to run.
	 * This jar is just a manifest with:
	 * <ul>
	 * <li>tw-dep.jar</li>
	 * </ul>
	 * assuming these two jars reside in the same directory as ModelMaker.jar
	 * </p>
	 */
	public static void packModelRunner() {
		ThreeWorldsJar packer = new ThreeWorldsJar(VERSION_MAJOR,VERSION_MINOR,VERSION_MICRO);
		packer.addDependencyOnJar(TW_ROOT+Jars.separator+TW_DEP_JAR);
		packer.setMainClass(MODELRUNNER_CLASS);
		File outFile = jarFile("modelRunner.jar");
		packer.saveJar(outFile);
		outFile.setExecutable(true, false);
	}
	
	/**
	 * gets all the dependencies of 3worlds.
	 * CAUTION: this is done by scanning the .ivy2 directory. So it is good practice, before
	 * running this method, to completely regenerate the .ivy2 cache so that all files are
	 * clean.
	 * In the general use-case, 3w developers would probably send this jar file to end-users so
	 * that they do not have to manage the library mess. Or ?
	 */
	public static void pack3wDependencies() {
		// get all dependencies of all 3w libraries
		// and pack them in a single jar
		// TODO: the information comes from the ivy.xml file of every 3Worlds library
		String ivyFile = System.getProperty("user.dir")+File.separator+"scripts"+File.separator+"ivy.xml";
		DependencySolver solver = new DependencySolver(ivyFile);
		Collection<String> result = solver.getJars();
		ThreeWorldsJar packer = new ThreeWorldsJar(VERSION_MAJOR,VERSION_MINOR,VERSION_MICRO);
		// no main class
		for (String s:result)
			packer.addJar(s);
		File outFile = jarFile(TW_DEP_JAR);
		packer.saveJar(outFile);
		// not executable	
	}
	
	/**
	 * gets all the 3Worlds classes from all its libraries.
	 */
	public static void pack3wLibraries() {
		// here is how to get the workspace directory:
//		ResourcesPlugin.getWorkspace();
//		/home/gignoux/git/tw-core/tw-core
	}

	/**
	 * Creates the .3w directory and the dependency jar for 3Worlds if they do not exist
	 * @param args
	 */
	public static void main(String... args) throws IOException {
		// 1) create /.3w
		//       		|-->/lib
		System.out.println("Setting up 3Worlds environment:");
		System.out.println("Creating the .3w directory");
		File f = new File(USER_ROOT + File.separator + TW);
		f.mkdirs();
		// 2) place jar of ivy dependencies in /.3w
		System.out.println("Installing required libraries");
		pack3wDependencies();
		// 3) clean up: delete /.3w/lib
		deleteFileTree(f);
		// 4) make jar of 3worlds
		System.out.println("Installing the 3Worlds ModelMaker");
		packModelMaker();
		System.out.println("FINISHED");
	}
}
