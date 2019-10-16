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
package fr.cnrs.iees.twcore.generators.data;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.ens.biologie.generic.utils.NameUtils.*;
import static fr.cnrs.iees.twcore.generators.TwComments.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static fr.ens.biologie.codeGeneration.CodeGenerationUtils.*;
import java.io.File;
import java.util.logging.Logger;

import au.edu.anu.rscs.aot.collections.tables.Dimensioner;
import au.edu.anu.rscs.aot.collections.tables.IntTable;
import au.edu.anu.rscs.aot.collections.tables.Table;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.project.ProjectPaths;
import au.edu.anu.twcore.userProject.UserProjectLink;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.twcore.constants.DataElementType;
import fr.cnrs.iees.twcore.generators.TwCodeGenerator;
import fr.ens.biologie.codeGeneration.ClassGenerator;
//import fr.ens.biologie.codeGeneration.JavaCompiler;
import fr.ens.biologie.generic.utils.Logging;

/**
 * <p>Implements the recursive generation of nested record and table data classes.
 * Details of code generated are left to descendants.</p>
 *
 *
 *
 *javac -sourcepath ~/.3w/project_test1_2019-10-15-11-05-36-408/local/java/code/my_ecosystem/*.java -classpath ~/.3w/tw-dep.jar 
 *
 * @author Jacques Gignoux - dec. 2014
 */
// NB to fix possible problems in the future: there was a bug due to the one-by-one compiling
// of java classes. eg, this command:
// javac ./Subtable.java -classpath ~/.3w/tw-dep.jar
// fails because dependent classes are not compiled, where as this command:
// javac *.java -classpath ~/.3w/tw-dep.jar
// works, because javac manages the dependencies of all classes in the directory
// ALL the java compiling code has been moved to CodeGenerator.generate() 
public abstract class HierarchicalDataGenerator 
	extends TwCodeGenerator
	implements ProjectPaths {
	
	private static Logger log = Logging.getLogger(HierarchicalDataGenerator.class); 
	
	/** the name of the class to generate */
	protected String className = null;
	/** the name of the package in which the class will be generated (ie with "." as separators) */
	protected String packageName = null;
	/** the directory name matching package name */
	protected String packagePath = null;
	/** the model name (matching the ecology node name */
	protected String modelName = null;		
	
	private boolean hadErrors = false;

	protected abstract ClassGenerator getRecordClassGenerator(String className,String comment);
	
	protected abstract ClassGenerator getTableClassGenerator(String className, String contentType,String comment);
	
	protected HierarchicalDataGenerator(String modelName,TreeGraphDataNode spec) {
		super(spec);
		this.modelName = modelName;
		packageName =REMOTECODE+"."+ validJavaName(wordUpperCaseName(modelName));	
		packagePath = Project.makeFile(LOCALCODE,validJavaName(wordUpperCaseName(modelName))).getAbsolutePath();
	}
	
	@SuppressWarnings("unchecked")
	private final String generateRecordCode(TreeGraphDataNode spec) {
		String cn = null;
		if (spec.properties().hasProperty("generatedClassName"))
			cn = (String)spec.properties().getPropertyValue("generatedClassName");
		else
			cn = spec.id();
		cn = validJavaName(initialUpperCase(wordUpperCaseName(cn)));
		log.info("Generating data class '"+cn+"'");
		String comment = comment(general,classComment(cn),generatedCode(false,modelName, ""));		
		ClassGenerator cg = getRecordClassGenerator(cn,comment);
		headerCode(cg,cn);
		Iterable<TreeNode> childrenList = null;
		// CAUTION: now specs are defined either with child nodes or with specific edges
		if (spec.hasChildren())
			childrenList = (Iterable<TreeNode>) spec.getChildren();
		else
			childrenList = (Iterable<TreeNode>) get(spec.edges(Direction.OUT),
				selectZeroOrMany(hasProperty("type","forCodeGeneration")),
				edgeListEndNodes());
		for (TreeNode ff:childrenList) {
			TreeGraphDataNode f = (TreeGraphDataNode) ff;
			String fname = validJavaName(wordUpperCaseName(f.id()));
			String ftype = null;
			if (f.properties().hasProperty(P_FIELDTYPE.key())) {
				DataElementType det = (DataElementType)f.properties().getPropertyValue(P_FIELDTYPE.key());
				ftype = det.name();
			}
			if (f.classId().equals(N_TABLE.label())) {
				ftype = generateTableCode((TreeGraphDataNode) f,cg);
				tableFieldCode(cg,fname,ftype);
			}
			else {
				primitiveFieldCode(cg,fname,ftype);
			}
			fieldCode(cg,fname,ftype);			
		}
		finalCode(cg);
		log.info("    generating file "+cn+".java ...");
		File file = new File(packagePath+File.separator+cn+".java");
		writeFile(cg,file,cn);
		UserProjectLink.addDataFile(file);
		log.info("  ...done.");
		return cn;
	}
	
	protected abstract void headerCode(ClassGenerator cg, String className);
	protected abstract void fieldCode(ClassGenerator cg,String fname,String ftype);
	protected abstract void tableFieldCode(ClassGenerator cg,String fname,String ftype);
	protected abstract void primitiveFieldCode(ClassGenerator cg,String fname,String ftype);
	protected abstract void finalCode(ClassGenerator cg);

	/**
	 * 
	 * @param spec
	 * @return the type of the generated Table
	 */
	@SuppressWarnings("unchecked")
	private final String generateTableCode(TreeGraphDataNode spec, ClassGenerator parentCG) {
		String ftype = "";
		String fpack = "";
		String fname = validJavaName(wordUpperCaseName(spec.id()));
		Iterable<TreeGraphDataNode> dims = (Iterable<TreeGraphDataNode>) get(spec,
			outEdges(),
			edgeListEndNodes(),
			selectOneOrMany(hasTheLabel("dimensioner")));
		if (spec.properties().hasProperty(P_DATAELEMENTTYPE.key())) {
			DataElementType tet = (DataElementType) spec.properties().getPropertyValue(P_DATAELEMENTTYPE.key());
			String t = tet.name();
			if (t.equals("Integer")) { 
				ftype = IntTable.class.getSimpleName();
				fpack = IntTable.class.getCanonicalName();
			}
			else {
				ftype = t+"Table";
				fpack = Table.class.getPackageName()+"."+ftype;
			}
		}
		else { // this must be a record - superclass will be generated from the record name, but doesnt exist yet !
			// a table is never the root of a data definition, so there should not be problems here
			TreeGraphDataNode rec = (TreeGraphDataNode) get(spec, 
				children(), 
				selectOne(hasTheLabel(N_RECORD.label())));
			generateRecordCode(rec);
			ftype = validJavaName(initialUpperCase(wordUpperCaseName(spec.id())));
			log.info("    generating file "+ftype+".java ...");
			fpack = packageName+"."+ftype;
			String contentType = validJavaName(initialUpperCase(wordUpperCaseName(rec.id())));
			String comment = comment(general,classComment(fname),generatedCode(false,modelName, ""));		
			ClassGenerator cg = getTableClassGenerator(ftype,contentType,comment);	
			tableCode(cg,ftype,contentType,dims);
			File file = new File(packagePath+File.separator+ftype+".java");
			writeFile(cg,file,ftype);
			UserProjectLink.addDataFile(file);
			log.info("  ...done.");
		}
		if (parentCG!=null) {
			parentCG.setImport(fpack);
			parentCG.setImport(Dimensioner.class.getCanonicalName());
			tableInitCode(parentCG,fname,ftype,dims);
		}		
		return ftype;
	}

	protected abstract void tableInitCode(ClassGenerator cg,String fname,String ftype,Iterable<TreeGraphDataNode> dimList);
	protected abstract void tableCode(ClassGenerator cg,String ftype,String contentType,Iterable<TreeGraphDataNode> dimList);

	@Override
	public final boolean generateCode() {		
		if (spec.classId().equals(N_RECORD.label())) 
			className = packageName+"."+generateRecordCode(spec);
		else if (spec.classId().equals(N_TABLE.label())) 
			className = packageName+"."+generateTableCode(spec,null);
		return hadErrors;
	}
	
	public final String generatedClassName() {
		return className;
	}

}
