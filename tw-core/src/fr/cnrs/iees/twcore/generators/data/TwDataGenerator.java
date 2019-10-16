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

import java.io.File;
import au.edu.anu.rscs.aot.collections.tables.Dimensioner;
import au.edu.anu.rscs.aot.collections.tables.ObjectTable;
import au.edu.anu.rscs.aot.collections.tables.Table;
import au.edu.anu.twcore.data.runtime.TwData;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.ens.biologie.codeGeneration.ClassGenerator;
import fr.ens.biologie.codeGeneration.MethodGenerator;
import static fr.ens.biologie.codeGeneration.CodeGenerationUtils.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

/**
 * A code generator to make descendants of {@link TwData}
 * 
 * <p>3Worlds: component threeWorlds</p>
 * @author Jacques Gignoux - 23 déc. 2014
 * 
 */
public class TwDataGenerator
	extends HierarchicalDataGenerator {
		
	private static final String[] predefinedTableTypes = {"BooleanTable", "ByteTable", "CharTable",
		"DoubleTable", "FloatTable", "IntTable", "LongTable", "ShortTable", "StringTable"};

	public TwDataGenerator(String modelName,TreeGraphDataNode spec) {	
		super(modelName,spec);
	}
	
	@Override
	protected ClassGenerator getRecordClassGenerator(String className,String comment) {
		return new ClassGenerator(packageName, comment, className,TwData.class.getCanonicalName());
//			"au.edu.anu.rscs.aot.graph.properties.SimplePropertyList"); // doesnt seem required ??
	}

	@Override
	protected ClassGenerator getTableClassGenerator(String className, String contentType, String comment) {
		return new ClassGenerator(packageName, comment, className,
			ObjectTable.class.getPackageName()+".ObjectTable<"+contentType+">");
	}
	public File getFile() {
		String name = className.replace(this.packageName+".", "");
		String path = packagePath+File.separator+name;
		return new File(path+".java");
	}
	@Override
	protected void headerCode(ClassGenerator cg, String className) {
		// imports
		cg.setImport(SimplePropertyList.class.getCanonicalName());
		cg.setImport(Table.class.getCanonicalName());	
		cg.setImport("java.util.Set");
		cg.setImport("java.util.HashSet");
		cg.setImport(TwData.class.getCanonicalName());
		// constructors
		cg.setConstructor();
		cg.getConstructor("constructor1").setStatement("super()");
		// inherited methods
		cg.getMethod("setProperty").setReturnStatement("return this");
		cg.getMethod("cloneStructure").setStatement(className+" result = new "+className+"()");
		cg.getMethod("cloneStructure").setReturnStatement("return result");
		cg.getMethod("clone").setReturnStatement("return clone");
		cg.getMethod("clone").setStatement(className+" clone = ("+className+") cloneStructure()");
		cg.getMethod("clear").setReturnStatement("return this");
		cg.getMethod("hasProperty").setReturnStatement("return false");
		cg.getMethod("getKeysAsSet").setReturnType("Set<String>");
		cg.getMethod("getKeysAsSet").setStatement("Set<String> result = new HashSet<String>()");
		cg.getMethod("getKeysAsSet").setReturnStatement("return result");
	}

	@Override
	protected void fieldCode(ClassGenerator cg, String fname, String ftype) {
		// fields
		cg.setField(fname, ftype, null);
		// specific getters
		MethodGenerator m = new MethodGenerator("public",ftype,fname);
		m.setReturnStatement("return "+fname);
		cg.setMethod("get"+fname, m);
		// generic methods inherited from SimplePropertyList 
		cg.getMethod("getPropertyValue").setStatement("if (v0.equals(\""+fname+"\")) return "+fname);
		cg.getMethod("getKeysAsSet").setStatement("result.add(\""+fname+"\")");
	}

	@Override
	protected void tableFieldCode(ClassGenerator cg, String fname, String ftype) {
		cg.getMethod("clear").setStatement(fname+".clear()");
//		cg.getMethod("clone").setStatement("for (int i=0; i<"+fname+".getFlatSize(); i++) clone."+fname+".setWithFlatIndex("+fname+"().getWithFlatIndex(i),i)");
		if (isPredefinedTableType(ftype))
			cg.getMethod("clone").setStatement("clone."+fname+" = "+fname+".clone()");	
		else
			cg.getMethod("clone").setStatement("clone."+fname+" = ("+ftype+") "+fname+".clone()");		
		cg.getMethod("hasProperty").setStatement("if (v0.equals(\""+fname+"\")) return true");
		cg.getMethod("propertyToString").setStatement("if (v0.equals(\""+fname+"\")) return "+fname+".toString()");
		cg.getMethod("setProperty").setStatement("if (v0.equals(\""+fname+"\")) "+fname+".copy((Table)v1)");
		cg.getMethod("getPropertyClass").setStatement("if (v0.equals(\""+fname+"\")) return "+ftype+".class");
	}

	@Override
	protected void primitiveFieldCode(ClassGenerator cg, String fname,
			String ftype) {
		cg.getMethod("clear").setStatement(fname+ " = "+zero(ftype));
		cg.getMethod("clone").setStatement("clone."+fname+" = "+fname);
		cg.getMethod("hasProperty").setStatement("if (v0.equals(\""+fname+"\")) return true");
		cg.getMethod("propertyToString").setStatement("if (v0.equals(\""+fname+"\")) return String.valueOf("+fname+")");
		// specific setters - only for primitive types !
		MethodGenerator m = new MethodGenerator("public","void",fname,ftype);
		m.setStatement("if (!isReadOnly()) "+fname+" = v0");
		cg.setMethod("set"+fname, m);
		cg.getMethod("setProperty").setStatement("if (v0.equals(\""+fname+"\")) "+fname+" = ("+ftype+") v1");
		cg.getMethod("getPropertyClass").setStatement("if (v0.equals(\""+fname+"\")) return "+ftype+".class");
	}

	@Override
	protected void finalCode(ClassGenerator cg) {
		cg.getMethod("size").setReturnStatement("return "+cg.nfields());
		String[] ff = new String[cg.nfields()]; 
		ff = cg.fields().toArray(ff);
		String s="";
		for (int i=0; i<ff.length-1; i++) s+="\""+ff[i]+"\",";
		if (ff.length==0)
			System.out.println(cg.getClassName()+ " has no members!!");
		s+="\""+ff[ff.length-1]+"\"";			
		cg.getMethod("getKeysAsArray").setStatement("String[] result = {"+s+"}");
		cg.getMethod("getKeysAsArray").setReturnStatement("return result");
	}

	private boolean isPredefinedTableType(String ftype) {
		for (int i=0; i<predefinedTableTypes.length; i++)
			if (ftype.equals(predefinedTableTypes[i]))
				return true;
		return false;
	}
	
	@Override
	protected void tableInitCode(ClassGenerator cg, String fname, String ftype,Iterable<TreeGraphDataNode> dimList) {
		String dims ="";
		cg.setImport(Dimensioner.class.getCanonicalName());
		if (isPredefinedTableType(ftype)) {
			for (TreeGraphDataNode dim:dimList) {
				if (dim.properties().hasProperty(P_DIMENSIONER_SIZE.key())) 
					dims += "new Dimensioner("+dim.properties().getPropertyValue(P_DIMENSIONER_SIZE.key())+"),";
			}
			dims = dims.substring(0, dims.length()-1);
		}
		cg.getConstructor("constructor1").setStatement(fname+" = new "+ftype+"("+dims+")"); 
	}

	// this is called only to generate ObjectTable descendants
	// these Tables must implement clone() and clonestructure() with the proper return types,
	// otherwise there is a cast exception when using the ancestor clone() method.
	@Override
	protected void tableCode(ClassGenerator cg, String ftype, String contentType, Iterable<TreeGraphDataNode> dimList) {
		cg.setImport(packageName+"."+contentType);
		cg.setImport(Dimensioner.class.getCanonicalName());
		cg.setConstructor();
		String s = "super(";
		for (TreeGraphDataNode dim:dimList) {
			if (dim.properties().hasProperty(P_DIMENSIONER_SIZE.key())) 
				s += "new Dimensioner("+dim.properties().getPropertyValue(P_DIMENSIONER_SIZE.key())+"),";
		}
		s = s.substring(0, s.length()-1);
		s +=")";
		cg.getConstructor("constructor1").setStatement(s);
		// since clone() is not abstract in ancestor, we must redeclare it here
		MethodGenerator m = new MethodGenerator("public",ftype,"clone");
		cg.setMethod("clone", m);
		cg.getMethod("clone").setReturnType(ftype);
		cg.getMethod("clone").setStatement(ftype+" result = cloneStructure()");
		cg.getMethod("clone").setStatement("for (int i=0; i<flatSize; i++)");
		cg.getMethod("clone").setStatement("\tresult.setWithFlatIndex(getWithFlatIndex(i),i);");
		cg.getMethod("clone").setReturnStatement("return result");
		// since cloneStructure() is not abstract in ancestor, we must redeclare it here
		m = new MethodGenerator("public",ftype,"cloneStructure");
		cg.setMethod("cloneStructure", m);
		cg.getMethod("cloneStructure").setReturnType(ftype);
		cg.getMethod("cloneStructure").setReturnStatement("return new "+ftype+"()");
	}

	
}
