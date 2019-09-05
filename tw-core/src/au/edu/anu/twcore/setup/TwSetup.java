/**
 * 
 */
package au.edu.anu.twcore.setup;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
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
 * CAUTION: before running this class, it is strongly recommended to completely clean the
 * ivy cache by removing the local and cache subdirectories and regenerating them
 * using the 3w libraries build.xml scripts in the proper order (ie omhtk > omugi > qgraph > aot
 * > tw-core > tw-apps > tw-uifx)
 * 
 * @author Ian Davies
 * @date 10 Dec. 2017
 */
public class TwSetup implements ProjectPaths, TwPaths {
	
	// please only increment these numbers (with caution). Never decrement.
	public static final String VERSION_MAJOR = "0";
	public static final String VERSION_MINOR = "0";
	public static final String VERSION_MICRO = "1";
	
	public static final String MODELRUNNER_JAR = "modelRunner.jar";
	public static final String MODELMAKER_JAR = "modelMaker.jar";
	// this is supposed to return the root dir of all 3worlds libraries, e.g. /home/gignoux/git
	public static final String CODEROOT = Path.of(System.getProperty("user.dir")).getParent().getParent().toString();
	// NB these two names cannot be extracted from the classes because the classes are
	// in tw-uifx.
	private static final String MODELMAKER_CLASS = "au.edu.anu.twuifx.mm.Main";
	private static final String MODELRUNNER_CLASS = "au.edu.anu.twuifx.mr.Main";
	
	private static File buildTwApplicationIvyFile() {
		String ivyFile = "<ivy-module version=\"2.0\"\n" + 
				"		xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + 
				"		xmlns:m=\"http://maven.apache.org/POM/4.0.0\"\n" + 
				"		xsi:noNamespaceSchemaLocation=\"http://ant.apache.org/ivy/schemas/ivy.xsd\">\n" + 
				"\n" + 
				"	<info	organisation=\"fr.cnrs.iees.tw-core\"\n" + 
				"			module=\"tw-core\"\n" + 
				"			revision=\"0.1.7\"\n" + 
				"			status=\"integration\">\n" + 
				"		<license name=\"gpl3\" url=\"https://www.gnu.org/licenses/gpl-3.0.txt\"/>\n" + 
				"		<description>This module brings together all components required for 3Worlds including archetype</description>\n" + 
				"	</info>\n" + 
				"\n" + 
				"	<configurations>\n" + 
				"		<conf name=\"java library\"/>\n" + 
				"	</configurations>\n" + 
				"\n" + 
				"	<publications>\n" + 
				"		<artifact type=\"jar\" ext=\"jar\">\n" + 
				"			<conf name=\"java library\"/>\n" + 
				"		</artifact>\n" + 
				"	</publications>\n" + 
				"\n" + 
				"	<dependencies>\n" + 
				"		<dependency org=\"au.edu.anu.tw-uifx\" name=\"tw-uifx\" rev=\"[0.1.4,)\"/>\n" + 
				"	</dependencies>\n" + 
				"\n" + 
				"</ivy-module>\n" + 
				"";
		File outf = new File("bidon.xml");
		PrintWriter writer;
		try {
			writer = new PrintWriter(outf);
			writer.print(ivyFile);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return outf;
	}
	
	public static void deleteFileTree(File dir) throws IOException {
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
		String filename = MODELMAKER_JAR;
		System.out.print("Packing 3Worlds model maker into "+filename+"...");
		packer.setMainClass(MODELMAKER_CLASS);
		packer.addDependencyOnJar(TW+Jars.separator+TW_DEP_JAR);
		File outFile = jarFile(filename);
		packer.saveJar(outFile);
		outFile.setExecutable(true, false);
		System.out.println("done");
	}
	
	private static File jarFile(String filename) {
//		File file = new File(USER_ROOT+File.separator+filename);
		File file = new File(TW_ROOT+File.separator+filename);
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
	 * </ul>//		for (String s:getProjectDependencies("tw-uifx")) 
//			packer.addJar(s);

	 * assuming these two jars reside in the same directory as ModelMaker.jar
	 * </p>
	 */
	public static void packModelRunner() {
		ThreeWorldsJar packer = new ThreeWorldsJar(VERSION_MAJOR,VERSION_MINOR,VERSION_MICRO);
		String filename = MODELRUNNER_JAR;
		System.out.println("Packing 3worlds model runner into "+filename+"...");
		packer.addDependencyOnJar(TW+Jars.separator+TW_DEP_JAR);
		packer.setMainClass(MODELRUNNER_CLASS);
		File outFile = jarFile(filename);
		packer.saveJar(outFile);
		outFile.setExecutable(true, false);
		System.out.println("done");
	}
	
	private static Collection<String> getProjectDependencies(String projectName) {
		System.out.println("getting dependencies for library "+projectName);
		String ivyFile = CODEROOT+File.separator
				+projectName+File.separator
				+projectName+File.separator  // yes, twice. eg user.dir = /home/gignoux/git/tw-core/tw-core
				+"scripts"+File.separator+"ivy.xml";
		DependencySolver solver = new DependencySolver(ivyFile);
		Collection<String> result = solver.getJars();
		return result;
	}
	
	/**
	 * gets all the dependencies of 3worlds and pack them in a single jar.
	 * Uses the ivy.xml file of each library to get the dependencies.
	 * 
	 * In the general use-case, 3w developers would probably send this jar file to end-users so
	 * that they do not have to manage the library mess. Or ?
	 */
	public static void pack3wDependencies() {
		ThreeWorldsJar packer = new ThreeWorldsJar(VERSION_MAJOR,VERSION_MINOR,VERSION_MICRO);
		String filename = TW_DEP_JAR;
		System.out.println("Packing 3worlds dependencies into "+filename+":");
		System.out.println("- assuming the 3Worlds code root directory is "+CODEROOT);
		System.out.println("  (if this is wrong, edit the CODEROOT constant in TwSetup.java and re-run)");
		// get all dependencies of all 3w libraries
		// and pack them in a single jar
		// this puts in everything since tw-uifx depends on all libraries
		for (String s: new DependencySolver(buildTwApplicationIvyFile().toString()).getJars()) {
			packer.addJar(s);
			if (s.contains("au.") || s.contains("fr."))
				System.out.println("LOCAL: "+s);
		}
		// except the code of tw-core. Why ??? Is this because we are in this project ?
		// Well, then:
		packer.addPackageTree("au.edu.anu");
		packer.addPackageTree("fr.ens.biologie");
		packer.addPackageTree("fr.cnrs.iees");
		System.out.println("PACKING: fr.cnrs.iees.twcore");
		packer.addPackageTree("fr.cnrs.iees.twcore");
		System.out.println("PACKING: au.edu.anu.twcore");
		packer.addPackageTree("au.edu.anu.twcore");
//		for (String s:getProjectDependencies("uit")) packer.addJar(s);
//		for (String s:getProjectDependencies("ymuit")) packer.addJar(s);
		File outFile = jarFile(filename);
		packer.saveJar(outFile);
		// no main class
		// not executable	
		System.out.println("...done");
	}

// useless - can only get classes from local project	
//	/**
//	 * gets all the 3Worlds classes from all its libraries.
//	 */
//	public static void pack3wLibraries() {
//		ThreeWorldsJar packer = new ThreeWorldsJar(VERSION_MAJOR,VERSION_MINOR,VERSION_MICRO);
//		String filename = TW_LIB_JAR+"."+packer.getVersion()+EXT;
//		System.out.println("Packing 3worlds libraries into "+filename+":");
//		// TODO: make sure it doesnt get the test and versioning code?
//		// this only finds classes of the current library
//		packer.addPackageTree("au.edu.anu");
//		packer.addPackageTree("fr.ens.biologie");
//		packer.addPackageTree("fr.cnrs.iees");
//		File outFile = jarFile(filename);
//		packer.saveJar(outFile);
//		System.out.println("...done");
//	}

	/**
	 * Creates the .3w directory and the dependency jar for 3Worlds if they do not exist
	 * @param args
	 */
//	public static void main(String... args) throws IOException { : NB String... can cause problems with finding main signature! Should be String[]
		public static void main(String[] args) throws IOException {
		// 1) create /.3w
		//       		|-->/lib
		System.out.println("Setting up 3Worlds environment:");
		System.out.println("Creating the .3w directory");
//		File f = new File(USER_ROOT + File.separator + TW + File.separator +"lib");
//		f.mkdirs();
		// 2) place jar of ivy dependencies in /.3w
		System.out.println("Installing required libraries");
		pack3wDependencies();
//		pack3wLibraries();
		// 3) clean up: delete /.3w/lib
//		deleteFileTree(f); // ???
		// 4) make jar of 3worlds
		System.out.println("Skipping ModelMaker,jar");
//		packModelMaker();
		System.out.println("Skipping ModelMaker,jar");
//		packModelRunner();
		deleteFileTree( new File(DependencySolver.destPath));
		System.out.println("FINISHED");
		// 5) zip ModelMaker = .3w dir for distribution to end users
		// do it from the system - it's easier !
		// eg on linux: zip 3w.zip modelMaker.jar .3w/threeWorlds.jar .3w/tw-dep.jar

	}
}
