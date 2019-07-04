package fr.cnrs.iees.twcore.generators;

import java.io.File;

import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.project.ProjectPaths;
import fr.cnrs.iees.graph.Node;
import fr.ens.biologie.generic.JavaGenerator;
import fr.ens.biologie.generic.utils.NameUtils;


/**
 * An ancestor class to setup the directory hierarchy for Java code generators and compilers.
 * Assumes specification are passed to the constructor as a AotNode - descendants will know how to use
 * this AotNode.
 * 
 * Hierarchy is:
 * 
 * .3w
 * --project_[name]_[uid] 
 * ----distributed				ProjectPaths.PROJECT_DISTRIBUTED
 * ------jars					ProjectPaths.PROJECT_JARS
 * ------graphs					ProjectPaths.PROJECT_GRAPHS
 * ------files					ProjectPaths.PROJECT_FILES
 * --------[model name]
 * ----------data
 * ----------code
 * --------[model name]
 * ----------data
 * ----------code
 * ----local					ProjectPaths.PROJECT_LOCAL
 * ------hardware				ProjectPaths.PROJECT_HARDWARE_GRAPHS
 * ------userInterfaces			ProjectPaths.PROJECT_USER_INTERFACE_GRAPHS
 * ------logs					ProjectPaths.PROJECT_LOGS
 * ------recordings				ProjectPaths.PROJECT_RECORDINGS
 * 
 * <p>3Worlds: component threeWorlds</p>
 * @author Jacques Gignoux - 23 nov. 2016
 *
 */
public abstract class TwCodeGenerator 
	implements ProjectPaths, ThreeWorldsProjectPaths, JavaGenerator {
	
	protected Node spec = null;
	
	protected TwCodeGenerator(Node spec) {
		super();
		this.spec = spec;
	}
	
	protected String makeModelJavaName(String modelName) {
		return NameUtils.wordUpperCaseName(modelName);
	}
	
	protected File getModelCodeDir(String modelRoot) {
		File result = Project.makeFile(makeModelJavaName(modelRoot),THREE_WORLDS_CODE);
		return result;
	}

	protected File getModelDataDir(String modelRoot) {
//		return Project.makeFile(PROJECT_FILES,makeModelJavaName(modelRoot),THREE_WORLDS_DATA);
		return Project.makeFile(makeModelJavaName(modelRoot),THREE_WORLDS_DATA);
	}
}
