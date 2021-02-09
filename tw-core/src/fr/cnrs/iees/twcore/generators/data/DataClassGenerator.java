package fr.cnrs.iees.twcore.generators.data;

import static fr.ens.biologie.generic.utils.NameUtils.validJavaName;
import static fr.ens.biologie.generic.utils.NameUtils.wordUpperCaseName;

import au.edu.anu.twcore.project.ProjectPaths;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.twcore.generators.TwCodeGenerator;

/**
 * 
 * @author Jacques Gignoux - 1 févr. 2021
 *
 */
public abstract class DataClassGenerator 
		extends TwCodeGenerator
		implements ProjectPaths{

	/** the model name (matching the ecology node name */
	protected String modelName = null;
	/** the name of the package in which the class will be generated (ie with "." as separators) */
	protected String packageName = null;
	/** the directory name matching package name */
	protected String packagePath = null;
	
	protected DataClassGenerator(String modelName,TreeGraphDataNode spec) {
		super(spec);
		this.modelName = modelName;
		packageName =CODE+"."+ validJavaName(wordUpperCaseName(modelName))+"."+GENERATED;
//		packagePath = Project.makeFile(LOCALJAVACODE,validJavaName(wordUpperCaseName(modelName))).getAbsolutePath();
		packagePath = getModelGlueCodeDir(modelName).getAbsolutePath();
	}

}
