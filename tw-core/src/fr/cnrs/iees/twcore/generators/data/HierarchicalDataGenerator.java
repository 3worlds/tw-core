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

import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.omhtk.utils.NameUtils.*;
import static fr.cnrs.iees.twcore.generators.TwComments.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static fr.cnrs.iees.omhtk.codeGeneration.CodeGenerationUtils.*;
import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;

import au.edu.anu.omugi.collections.tables.Dimensioner;
import au.edu.anu.omugi.collections.tables.IntTable;
import au.edu.anu.omugi.collections.tables.Table;
import au.edu.anu.omugi.graph.property.Property;
import au.edu.anu.twcore.ecosystem.runtime.space.LocationData;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.ALDataEdge;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.properties.ExtendablePropertyList;
import fr.cnrs.iees.twcore.constants.DataElementType;
import fr.cnrs.iees.omhtk.codeGeneration.ClassGenerator;
//import fr.ens.biologie.codeGeneration.JavaCompiler;
import fr.cnrs.iees.omhtk.utils.Logging;

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
// javac ./Subtable.java -classpath ~/3w/tw.jar
// fails because dependent classes are not compiled, where as this command:
// javac *.java -classpath ~/3w/tw.jar
// works, because javac manages the dependencies of all classes in the directory
// ALL the java compiling code has been moved to CodeGenerator.generate()
public abstract class HierarchicalDataGenerator
	extends DataClassGenerator {

	private static Logger log = Logging.getLogger(HierarchicalDataGenerator.class);

	/** the name of the class to generate */
	protected String className = null;

	private boolean hadErrors = false;

	protected abstract ClassGenerator getRecordClassGenerator(String className,String comment,Set<String> locatedMethods);

	protected abstract ClassGenerator getTableClassGenerator(String className, String contentType,String comment);

	protected HierarchicalDataGenerator(String modelName,TreeGraphDataNode spec) {
		super(modelName,spec);
	}

	@SuppressWarnings("unchecked")
	private final String generateRecordCode(TreeGraphDataNode spec) {
		String cnn = null;
		if (spec.properties().hasProperty(P_TWDATACLASS.key()))
			cnn = (String)spec.properties().getPropertyValue(P_TWDATACLASS.key());
		else {
			cnn = spec.id();
			((ExtendablePropertyList)spec.properties()).addProperty(
				new Property(P_TWDATACLASS.key(),cnn) );
		}
		String cn = validJavaName(initialUpperCase(wordUpperCaseName(cnn)));
		log.info("Generating data class '"+cn+"'");
		String comment = comment(general,classComment(cn),generatedCode(false,modelName, ""));
		Iterable<TreeNode> childrenList = null;
		// CAUTION: now specs are defined either with child nodes or with specific edges
		//JG: Is this still true?
		if (spec.hasChildren())
			childrenList = (Iterable<TreeNode>) spec.getChildren();
		else
			//JG: is this case ever reached?
			childrenList = (Iterable<TreeNode>) get(spec.edges(Direction.OUT),
				selectZeroOrMany(hasProperty("type","forCodeGeneration")),
				edgeListEndNodes());
		// get the fields that are used as space coordinates
		List<ALDataEdge> coordEdges = (List<ALDataEdge>) get(childrenList,
			selectZeroOrMany(hasTheLabel(N_FIELD.label())),
			nodeListInEdges(),
			selectZeroOrMany(hasTheLabel(E_COORDMAPPING.label())));
		SortedMap<Integer,Node> rankedCoordFields = new TreeMap<>();
		for (ALDataEdge ce:coordEdges)
			rankedCoordFields.put((Integer)ce.properties().getPropertyValue(P_SPACE_COORD_RANK.key()),
				ce.endNode());
		List<Node> coordinateFields = new ArrayList<>();
		// all this to make sure coordinates come in the proper order and are numbered 0,1,2,3 etc.
		for (Node n:rankedCoordFields.values())
			coordinateFields.add(n);
		// instantiate class generator
		Set<String> locatedMethods = null;
		if (!coordinateFields.isEmpty()) {
			locatedMethods = new HashSet<>();
			for (Method m:LocationData.class.getMethods())
				locatedMethods.add(m.getName());
//			locatedMethods.add("coordinates");
//			locatedMethods.add("coordinate");
//			locatedMethods.add("asPoint");
//			locatedMethods.add("setCoordinates");
		}
		ClassGenerator cg = getRecordClassGenerator(cn,comment,locatedMethods);
		// generate imports and inherited methods
		headerCode(cg,cn);
		// generate field code
		for (TreeNode ff:childrenList) {
			TreeGraphDataNode f = (TreeGraphDataNode) ff;
			String fname = validJavaName(wordUpperCaseName(f.id()));
			String ftype = null;
			if (f.properties().hasProperty(P_FIELD_TYPE.key())) {
				DataElementType det = (DataElementType)f.properties().getPropertyValue(P_FIELD_TYPE.key());
				if (det.asPrimitive()==null)
					ftype = det.name();
				else
					ftype = det.asPrimitive();
			}
			// generate field code specific to table types
			if (f.classId().equals(N_TABLE.label())) {
				ftype = generateTableCode((TreeGraphDataNode) f,cg);
				tableFieldCode(cg,fname,ftype);
			}
			// generate field code for other ("plain") types
			else
				primitiveFieldCode(cg,fname,ftype,coordinateFields.indexOf(f)+1,coordinateFields.size());
			// generate specific accessors for fields used as spatial coordinates
			if (coordinateFields.contains(f))
				fieldCode(cg,fname,ftype,coordinateFields.indexOf(f)+1,coordinateFields.size());
			// generate common field-specific methods and code
			else
				fieldCode(cg,fname,ftype,-1,-1);
		}
		finalCode(cg);
		log.info("    generating file "+cn+".java ...");
		File file = new File(packagePath+File.separator+cn+".java");
		writeFile(cg,file);
		//UserProjectLink.addDataFile(file);
		log.info("  ...done.");
		return cn;
	}

	protected abstract void headerCode(ClassGenerator cg, String className);
	/**
	 * This method generates the code for data fields that is common to all data field types.
	 *
	 * @param cg the class generator instance
	 * @param fname the field name
	 * @param ftype the field type (as a member of the {@linkplain DataElementType} enum)
	 * @param coordRank the space coordinate rank of this field (-1 if this field is not used as a coordinate)
	 * @param coordSize the space dimension (-1 if no space)
	 */
	protected abstract void fieldCode(ClassGenerator cg,String fname,String ftype, int coordRank,int coordSize);
	protected abstract void tableFieldCode(ClassGenerator cg,String fname,String ftype);
	protected abstract void primitiveFieldCode(ClassGenerator cg,String fname,String ftype, int coordRank,int coordSize);
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
		List<ALDataEdge> edims = (List<ALDataEdge>) get(spec,
			outEdges(),
			selectOneOrMany(hasTheLabel(E_SIZEDBY.label())));
		// CAUTION: It's the edge that has the rank property not the dim node - fixed 25/7/2021 IDD
		// But does this now take account of a nested hierarchy??
		SortedMap<Integer,TreeGraphDataNode> sortedDims = new TreeMap<>();
		for (ALDataEdge e:edims) 
			sortedDims.put(
					(Integer)e.properties().getPropertyValue(P_DIMENSIONER_RANK.key()),
					(TreeGraphDataNode) e.endNode());
		// get the table element type
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
			if (spec.properties().hasProperty(P_TWDATACLASS.key()))
				spec.properties().setProperty(P_TWDATACLASS.key(),ftype);
			else
				((ExtendablePropertyList)spec.properties()).addProperty(
					new Property(P_TWDATACLASS.key(),ftype) );
			log.info("    generating file "+ftype+".java ...");
			fpack = packageName+"."+ftype;
			String contentType = validJavaName(initialUpperCase(wordUpperCaseName(rec.id())));
			String comment = comment(general,classComment(fname),generatedCode(false,modelName, ""));
			ClassGenerator cg = getTableClassGenerator(ftype,contentType,comment);
			tableCode(cg,ftype,contentType,sortedDims.values());		
			//tableCode(cg,ftype,contentType,dims);
			File file = new File(packagePath+File.separator+ftype+".java");
			writeFile(cg,file);
			//UserProjectLink.addDataFile(file);
			log.info("  ...done.");
		}
		if (parentCG!=null) {
			parentCG.setImport(fpack);
			parentCG.setImport(Dimensioner.class.getCanonicalName());
//			tableInitCode(parentCG,fname,ftype,dims);
			tableInitCode(parentCG,fname,ftype,sortedDims.values());
		}
		return ftype;
	}

	protected abstract void tableInitCode(ClassGenerator cg,String fname,String ftype,Iterable<TreeGraphDataNode> dimList);
	protected abstract void tableCode(ClassGenerator cg,String ftype,String contentType,Iterable<TreeGraphDataNode> dimList);

	@Override
	public final boolean generateCode(boolean reportErrors) {
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
