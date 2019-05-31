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

package fr.cnrs.iees.twcore.generators.resources;

import static fr.cnrs.iees.twcore.generators.Comments.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Column;
import org.odftoolkit.simple.table.Table;

/**
 * <h1>A utility class to generate core 3worlds components.</h1>
 * 
 * @author Jacques Gignoux - 07-06-2018
 * 
 *         <p>
 *         Use this class to generate simultaneously (hence, consistently):
 *         </p>
 *         <ul>
 *         <li>enum class for properties of the 3w configuration graph</li>
 *         <li>matching asciidoc documentation</li>
 *         <li>matching archetype snippets</li>
 *         </ul>
 *         <p>
 *         The specification of the generated items is in the
 *         <code>enumConstants.ods</code> file. The generated artifacts will go
 *         into the
 *         <code>fr.ens.biologie.threeWorlds.resources.core.constants</code>
 *         package for enum classes and into the
 *         <code>fr.ens.biologie.threeWorlds.doc</code> package for
 *         documentation files.
 *         </p>
 *
 */
public class Generator {

	// versioning
	private static Date now = null;

	// constants package
	// constants dir
	// arch dir
	// SPEC_FILE

	// packages, dirs and files
	private static final String SPEC_FILENAME = "enumConstants.ods";
	// TODO awaiting new graph and archetype def.
//	private static final String ARCH_FILENAME = "archetypeTree.ods";

	private static final String PROJECT_DIR = System.getProperty("user.dir");
	private static final String SRC_DIR = PROJECT_DIR + File.separator + "src";
	private static final String RES_DIR = SRC_DIR + File.separator
			+ "fr.cnrs.iees.twcore.generators.resources".replace('.', File.separatorChar);
	private static final String CONSTANTS_PACKAGE = "fr.cnrs.iees.twcore.constants";
	private static final String CONSTANTS_DIR = SRC_DIR + File.separator
			+ CONSTANTS_PACKAGE.replace('.', File.separatorChar);
//	private static final String ARCHETYPES_DIR = SRC_DIR + File.separator
//			+ "fr.cnrs.iees.twcore.archetypes".replace('.', File.separatorChar);
	private static final String ENUM_FILE = RES_DIR + File.separator + SPEC_FILENAME;
//	private static final String ARCH_FILE = RES_DIR + File.separator + ARCH_FILENAME;
	private static final String DOC_DIR = PROJECT_DIR + File.separator + "doc";
//	private static final String INDENT = "  ";
//	static {
//		System.out.println(PROJECT_DIR);
//		System.out.println(SRC_DIR);
//		System.out.println(RES_DIR);
//		System.out.println(DOC_DIR);
//		System.out.println(CONSTANTS_DIR);
//		System.out.println(ARCHETYPES_DIR);
//	    System.out.println(ARCH_FILE);
//		System.out.println(ENUM_FILE);
//	}

	// row indexing in ods file
	private static int HEADERS = 0;
	private static int TYPES = 1;

	// column index expected from ods file for node sheets
	private static int PROPNAME = 0;
	private static int PROPTYPE = 1;
	private static int PROPLIST = 2;
//	private static int PROPMULT = 3;
	private static int PROPDESC = 4;
	private static int PROPCODE = 5;
	private static int PROPIMPORT = 6;

	// column index expected from ods file for enum sheets
	private static int VALUE = 0;
	private static int DESC = 1;

	// column index for archetype tree
//	private static int AR_HASNODE = 0;
//	private static int AR_PARENT = 1;
//	private static int AR_CHILD = 2;
//	private static int AR_MULT = 3;
//	private static int AR_DESC = 4;
//	private static int AR_CLASS = 5;

//=====================================================================================
	private static void generateNodeDoc(Table nodeSheet) {
		System.out.println("generating documentation for " + nodeSheet.getTableName());
		StringBuilder output = new StringBuilder();
		List<Column> cols = nodeSheet.getColumnList();

		// header comments
		output.append("// 3Worlds documentation for node ").append(nodeSheet.getTableName()).append('\n');
		output.append(singleLineComment(generated));
		output.append("// ").append("generated by ").append(Generator.class.getSimpleName()).append(" on ").append(now)
				.append('\n').append('\n');

		output.append("_Properties for_ `").append(nodeSheet.getTableName()).append("`:\n\n");
		output.append("[horizontal]\n");
		for (int i = TYPES + 1; i < cols.get(PROPNAME).getCellCount(); i++) {
			String propName = cols.get(PROPNAME).getCellByIndex(i).getStringValue();
			String propType = cols.get(PROPTYPE).getCellByIndex(i).getStringValue();
			String propDesc = cols.get(PROPDESC).getCellByIndex(i).getStringValue();
			output.append('`').append(propName).append("`:: ").append(propDesc).append('\n');
			if (!propType.contains(".")) // this is an enum - include subdoc file
				output.append("+\n****\n").append("include::ArchetypeDoc-").append(nodeSheet.getTableName()).append("-")
						.append(propType).append(".adoc[]\n****\n");
			output.append('\n');
		}

		// output file
		File out = new File(DOC_DIR + File.separator + "ArchetypeDoc-" + nodeSheet.getTableName() + ".adoc");
		try {
			PrintStream outp = new PrintStream(out);
			outp.println(output.toString());
			outp.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

//=====================================================================================
//	private static void generateNodeArchetype(Table nodeSheet) {
//		System.out.println("generating archetype snippet for " + nodeSheet.getTableName());
//		StringBuilder output = new StringBuilder();
//
//		// header comments
//		output.append("// 3Worlds archetype snippet for node ").append(nodeSheet.getTableName()).append('\n');
//		output.append(singleLineComment(editableCode));
//		output.append("// some tweaking needed, indicated by '*****'\n");
//		output.append("// ").append("generated by ").append(Generator.class.getSimpleName()).append(" on ").append(now)
//				.append('\n').append('\n');
//
//		// node spec
//		output.append("hasNode ").append(nodeSheet.getTableName()).append("Spec\n");
//		output.append(INDENT).append("reference: \"*****").append(nodeSheet.getTableName()).append(":\"").append('\n');
//		output.append(INDENT).append("multiplicity: *****").append('\n');
//		List<Column> cols = nodeSheet.getColumnList();
//
//		// property specs
//		for (int i = TYPES + 1; i < cols.get(PROPNAME).getCellCount(); i++) {
//			String propName = cols.get(PROPNAME).getCellByIndex(i).getStringValue();
//			output.append(INDENT).append("hasProperty ").append(propName).append('\n');
//			String propType = cols.get(PROPTYPE).getCellByIndex(i).getStringValue();
//			String propList = cols.get(PROPLIST).getCellByIndex(i).getStringValue();
//			String type = null;
//			if (propList.equals("no")) {
//				try {
//					Class.forName(propType);
//					type = propType; // raw java types
//				} catch (ClassNotFoundException e) {
//					type = CONSTANTS_PACKAGE + "." + propType; // enums
//				}
//				if (type != null)
//					output.append(INDENT).append(INDENT).append("type: \"").append(type).append("\"\n");
//			} else if (propList.equals("yes")) {
//				type = null;
//				if (propType.contains("Integer"))
//					type = "au.edu.anu.rscs.aot.collections.tables.IntTable";
//				else if (propType.contains("Long"))
//					type = "au.edu.anu.rscs.aot.collections.tables.LongTable";
//				else if (propType.contains("Double"))
//					type = "au.edu.anu.rscs.aot.collections.tables.DoubleTable";
//				else if (propType.contains("String"))
//					type = "au.edu.anu.rscs.aot.collections.tables.StringTable";
//				else if (propType.contains("Boolean"))
//					type = "au.edu.anu.rscs.aot.collections.tables.BooleanTable";
//				else if (propType.contains("Float"))
//					type = "au.edu.anu.rscs.aot.collections.tables.FloatTable";
//				else if (propType.contains("Char"))
//					type = "au.edu.anu.rscs.aot.collections.tables.CharTable";
//				else if (propType.contains("Byte"))
//					type = "au.edu.anu.rscs.aot.collections.tables.ByteTable";
//				else if (propType.contains("Short"))
//					type = "au.edu.anu.rscs.aot.collections.tables.ShortTable";
//				else {
//					if (propType.contains("."))
//						type = "au.edu.anu.rscs.aot.collections.tables.ObjectTable<" + propType + ">";
//					else
//						type = CONSTANT_DIR.replace('/', '.') + "." + propType + "Table"; // enum tables
//				}
//				if (type != null)
//					output.append(INDENT).append(INDENT).append("type: \"").append(type).append("\"\n");
//			}
//			String propMult = cols.get(PROPMULT).getCellByIndex(i).getStringValue();
//			if (propMult.equals("no"))
//				output.append(INDENT).append(INDENT).append("multiplicity: 1..1").append('\n');
//			else if (propMult.equals("yes"))
//				output.append(INDENT).append(INDENT).append("multiplicity: 0..1").append('\n');
//			// TODO: constraint queries
//		}
//
//		output.append("\n// end generated block\n");
//		// output file
//		File out = new File(ARCHETYPE_DIR + File.separator + "3WA-snippet-"
//				+ nodeSheet.getTableName() + ".dsl");
//		try {
//			PrintStream outp = new PrintStream(out);
//			outp.println(output.toString());
//			outp.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//	}

//=====================================================================================
	private static void generateEnumCode(Table enumSheet, Map<String, String> extraCode,
			Map<String, String> extraImports) {
		System.out.println("generating java code for " + enumSheet.getTableName());
		String propName = enumSheet.getTableName().split("\\.")[1];
		StringBuilder output = new StringBuilder();
		List<Column> cols = enumSheet.getColumnList();

		// header comments
		String[] moreComments = { "generated by " + Generator.class.getSimpleName() + " on " + now };
		output.append(comment(license, general, generated, moreComments));
		// header code
		output.append("package ").append(CONSTANTS_PACKAGE).append(";\n\n");
		output.append("import java.util.Arrays;\n");
		output.append("import java.util.HashSet;\n");
		output.append("import java.util.Set;\n");
		output.append("import fr.cnrs.iees.io.parsing.ValidPropertyTypes;\n\n");
		// import snippets, if any
		String extraI = extraImports.get(propName);
		if (extraI != null)
			output.append(extraI).append('\n');

		output.append("public enum ").append(propName).append(" {\n\n");

		// enum values
		int extraFieldCount = 0;
		for (int i = 0; i < enumSheet.getColumnCount(); i++)
			if (!" value description dependency option ".contains(cols.get(i).getCellByIndex(HEADERS).getStringValue()))
				extraFieldCount++;
		int[] extraFields = new int[extraFieldCount];
		if (extraFieldCount > 0) {
			int count = 0;
			for (int i = 0; i < enumSheet.getColumnCount(); i++)
				if (!" value description dependency option "
						.contains(cols.get(i).getCellByIndex(HEADERS).getStringValue()))
					extraFields[count++] = i;
		}
		for (int i = TYPES + 1; i < enumSheet.getRowCount(); i++) {
			output.append("// ").append(cols.get(VALUE).getCellByIndex(i).getStringValue()).append(": ")
					.append(cols.get(DESC).getCellByIndex(i).getStringValue()).append('\n');
			output.append('\t').append(cols.get(VALUE).getCellByIndex(i).getStringValue());
			if (extraFieldCount > 0) {
				output.append(" (");
				for (int j = 0; j < extraFields.length; j++) {
					String extraFieldType = cols.get(extraFields[j]).getCellByIndex(TYPES).getStringValue();
					// NB: at the moment, only understand simple types: primitives, Strings and
					// enums
					if (extraFieldType.equals("String"))
						output.append('"');
					output.append(cols.get(extraFields[j]).getCellByIndex(i).getStringValue());
					if (extraFieldType.equals("String"))
						output.append('"');
					if (j < extraFields.length - 1)
						output.append(", ");
				}
				output.append(")");
			}
			if (i == enumSheet.getRowCount() - 1)
				output.append(";\n");
			else
				output.append(",\n\n");
		}
		output.append('\t').append("\n");

		// extra field initialisation
		if (extraFieldCount > 0) {
			// fields
			for (int j = 0; j < extraFields.length; j++) {
				String extraFieldType = cols.get(extraFields[j]).getCellByIndex(TYPES).getStringValue();
				output.append("\tprivate final ").append(extraFieldType).append(" ")
						.append(cols.get(extraFields[j]).getCellByIndex(HEADERS).getStringValue()).append(";\n");
			}
			output.append('\n');
			// constructor
			output.append("\tprivate ").append(propName).append('(');
			for (int j = 0; j < extraFields.length; j++) {
				String extraFieldType = cols.get(extraFields[j]).getCellByIndex(TYPES).getStringValue();
				output.append(extraFieldType).append(" ")
						.append(cols.get(extraFields[j]).getCellByIndex(HEADERS).getStringValue());
				if (j < extraFields.length - 1)
					output.append(", ");
			}
			output.append(") {\n");
			for (int j = 0; j < extraFields.length; j++)
				output.append("\t\tthis.").append(cols.get(extraFields[j]).getCellByIndex(HEADERS).getStringValue())
						.append(" = ").append(cols.get(extraFields[j]).getCellByIndex(HEADERS).getStringValue())
						.append(";\n");
			output.append("\t}\n\n");
			// getters
			for (int j = 0; j < extraFields.length; j++) {
				String extraFieldType = cols.get(extraFields[j]).getCellByIndex(TYPES).getStringValue();
				output.append("\tpublic ").append(extraFieldType).append(" ")
						.append(cols.get(extraFields[j]).getCellByIndex(HEADERS).getStringValue()).append("() {\n");
				output.append("\t\treturn ").append(cols.get(extraFields[j]).getCellByIndex(HEADERS).getStringValue())
						.append(";\n");
				output.append("\t}\n\n");
			}
		}

		// enum methods
		output.append("\tpublic static String[] toStrings() {\n");
		output.append("\t\tString[] result = new String[").append(propName).append(".values().length];\n");
		output.append("\t\tfor (").append(propName).append(" s: ").append(propName).append(".values())\n");
		output.append("\t\t\tresult[s.ordinal()] = s.name();\n");
		output.append("\t\tArrays.sort(result);\n");
		output.append("\t\treturn result;\n");
		output.append("\t}\n\n");

		output.append("\tpublic static Set<String> keySet() {\n");
		output.append("\t\tSet<String> result = new HashSet<String>();\n");
		output.append("\t\tfor (").append(propName).append(" e: ").append(propName).append(".values())\n");
		output.append("\t\t\tresult.add(e.toString());\n");
		output.append("\t\treturn result;\n");
		output.append("\t}\n\n");

		// defaultValue method - the default is the first value of the enum
		output.append("\tpublic static ").append(propName).append(" defaultValue() {\n");
		output.append("\t\treturn ").append(cols.get(VALUE).getCellByIndex(2).getStringValue()).append(";\n");
		output.append("\t}\n\n");

		// static initialisation block - to record this enum type as a valid property for parsers
		output.append("\tstatic {\n"); 
		output.append("\t\tValidPropertyTypes.recordPropertyType(").append(propName).append(".class.getSimpleName(), \n"); 
		output.append("\t\t").append(propName).append(".class.getName(),defaultValue());\n"); 
		output.append("\t}\n\n"); 
		
		// code snippets, if any
		String extra = extraCode.get(propName);
		if (extra != null)
			output.append(extra).append('\n');

		// footer code
		output.append("}\n");

		// output file
		File out = new File(CONSTANTS_DIR + File.separator + propName + ".java");
		try {
			PrintStream outp = new PrintStream(out);
			outp.println(output.toString());
			outp.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

//=====================================================================================
	private static void generateEnumDoc(Table nodeSheet, Table enumSheet) {
		System.out.println("generating documentation for " + enumSheet.getTableName());
		String propType = enumSheet.getTableName().split("\\.")[1];
		StringBuilder output = new StringBuilder();
		List<Column> cols = enumSheet.getColumnList();

		// header comments
		output.append("// 3Worlds documentation for property ").append(enumSheet.getTableName()).append('\n');
		output.append(singleLineComment(generated));
		output.append("// ").append("generated by ").append(Generator.class.getSimpleName()).append(" on ").append(now)
				.append('\n').append('\n');

		output.append("_possible values_:\n\n[horizontal]\n");
		for (int i = TYPES + 1; i < cols.get(VALUE).getCellCount(); i++) {
			String valueName = cols.get(VALUE).getCellByIndex(i).getStringValue();
			output.append('`').append(valueName).append("`:: ");
			String descro = cols.get(DESC).getCellByIndex(i).getStringValue();
			output.append(descro);
			if (i == TYPES + 1)
				output.append(" (default value)");
			output.append('\n');
		}

		// output file
		File out = new File(
				DOC_DIR + File.separator + "ArchetypeDoc-" + nodeSheet.getTableName() + "-" + propType + ".adoc");
		try {
			PrintStream outp = new PrintStream(out);
			outp.println(output.toString());
			outp.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

//=====================================================================================
	private static void generateEnumSetCode(Table nodeSheet, Table enumSheet) {
		String propType = enumSheet.getTableName().split("\\.")[1];
		for (int i = TYPES + 1; i < nodeSheet.getColumnByIndex(PROPTYPE).getCellCount(); i++) {
			if (nodeSheet.getColumnByIndex(PROPTYPE).getCellByIndex(i).getStringValue().equals(propType))
				if (nodeSheet.getColumnByIndex(PROPLIST).getCellByIndex(i).getStringValue().equals("yes")) {
					System.out.println("generating EnumTable code for " + enumSheet.getTableName());
					StringBuilder output = new StringBuilder();

					// header comments
					String[] moreComments = { "generated by " + Generator.class.getSimpleName() + " on " + now };
					output.append(comment(general, generated, moreComments));

					// header code
					output.append("package ").append(CONSTANTS_PACKAGE).append(";\n\n");
					output.append("import java.util.Collection;\n");
					output.append("import java.util.EnumSet;\n\n");
					output.append("public class ").append(propType).append("Set {\n\n");
					// fields
					output.append("\tprivate EnumSet<").append(propType).append("> values = null;\n\n");
					// constructors
					output.append("\t/** constructor from list of enum values */\n");
					output.append("\tpublic ").append(propType).append("Set(Collection<").append(propType)
							.append("> ag) {\n");
					output.append("\t\tsuper();\n");
					output.append("\t\tvalues = EnumSet.copyOf(ag);\n");
					output.append("\t}\n\n");
					output.append("\t/** constructor for an empty Set */\n");
					output.append("\tpublic ").append(propType).append("Set() {\n");
					output.append("\t\tsuper();\n");
					output.append("\t\tvalues = EnumSet.noneOf(").append(propType).append(".class);\n");
					output.append("\t}\n\n");
					output.append("\t/** constructor from single enum value */\n");
					output.append("\tpublic ").append(propType).append("Set(").append(propType).append(" sa) {\n");
					output.append("\t\tsuper();\n");
					output.append("\t\tvalues = EnumSet.of(sa);\n");
					output.append("\t}\n\n");
					// valueOf
					output.append("\tpublic static ").append(propType).append("Set valueOf(String value) {\n");
					output.append("\t\tString ss = value.substring(1,value.indexOf('}'));\n");
					output.append("\t\tString [] sl = ss.split(\",\");\n");
					output.append("\t\t").append(propType).append("Set e = new ").append(propType).append("Set();\n");
					output.append("\t\tfor (String s:sl)\n");
					output.append("\t\t\te.values.add(").append(propType).append(".valueOf(").append(propType)
							.append(".class,s.trim()));\n");
					output.append("\t\treturn e;\n");
					output.append("\t}\n\n");
					// getter for enumSet
					output.append("\tpublic EnumSet<").append(propType).append("> values() {\n");
					output.append("\t\treturn values;\n");
					output.append("\t}\n\n");
					// equals
					output.append("\t@Override\n");
					output.append("\tpublic boolean equals(Object o) {\n");
					output.append("\t\treturn values.equals(o);\n");
					output.append("\t}\n\n");
					// toString
					output.append("\t@Override\n");
					output.append("\tpublic String toString() {\n");
					output.append("\t\tStringBuilder sb = new StringBuilder();\n");
					output.append("\t\tsb.append('{');\n");
					output.append("\t\tint n = values.size();\n");
					output.append("\t\tint i=0;\n");
					output.append("\t\tfor (").append(propType).append(" sa:values) {\n");
					output.append("\t\t\tsb.append(sa);\n");
					output.append("\t\t\ti++;\n");
					output.append("\t\t\tif (i<n) sb.append(',');\n");
					output.append("\t\t}\n");
					output.append("\t\tsb.append('}');\n");
					output.append("\t\treturn sb.toString();\n");
					output.append("\t}\n\n");

					// footer code
					output.append("}\n");

					// output file
					File out = new File(CONSTANTS_DIR + File.separator + propType + "Set.java");
					try {
						PrintStream outp = new PrintStream(out);
						outp.println(output.toString());
						outp.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				} // big if
		}
	}

	// =====================================================================================
//	private static void generateArchetype(Table sheet) {
//		// headers on 1st line: | hasNode | parent | child | multiplicity | description
//		// | class |
//		for (int i = 1; i < sheet.getRowCount(); i++)
//			if (sheet.getCellByPosition(AR_HASNODE, i).getStringValue().length() > 0) {
//				System.out.println("generating Archetype node specifications ");
//				StringBuilder output = new StringBuilder();
//				String ctIndent = "";
//				output.append(ctIndent).append("hasNode ")
//						.append(sheet.getCellByPosition(AR_HASNODE, i).getStringValue()).append("\n");
//				ctIndent += INDENT;
//				output.append(ctIndent).append("reference: \"")
//						.append(sheet.getCellByPosition(AR_PARENT, i).getStringValue()).append(":/")
//						.append(sheet.getCellByPosition(AR_CHILD, i).getStringValue()).append(":\"\n");
//				output.append(ctIndent).append("multiplicity: ")
//						.append(sheet.getCellByPosition(AR_MULT, i).getStringValue()).append("\n");
//				output.append(ctIndent).append("hasProperty name\n").append(ctIndent).append(INDENT)
//						.append("type: String\n").append(ctIndent).append(INDENT).append("multiplicity: 1..1")
//						.append("\n");
//				if (sheet.getCellByPosition(AR_CLASS, i).getStringValue().length() > 0) {
//					output.append(ctIndent).append("hasProperty class\n").append(ctIndent).append(INDENT)
//							.append("type: String\n").append(ctIndent).append(INDENT).append("multiplicity: 1..1")
//							.append("\n");
//					output.append(ctIndent).append(INDENT).append("mustSatisfyQuery\n").append(ctIndent).append(INDENT)
//							.append(INDENT)
//							.append("className: fr.ens.biologie.threeWorlds.ui.configuration.archetype3w.IsInValueSetQuery\n")
//							.append(ctIndent).append(INDENT).append(INDENT).append("values: {\"")
//							.append(sheet.getCellByPosition(AR_CLASS, i).getStringValue()).append("\"}\n");
//				}
//				System.out.println(output.toString());
//			}
//	}
//
	// =====================================================================================
	// get the code snippets for enums.
	private static void getExtraCode(Table sheet, Map<String, String> extraCode) {
		List<Column> cols = sheet.getColumnList();
		for (int i = TYPES + 1; i < sheet.getRowCount(); i++) {
			String propType = cols.get(PROPTYPE).getCellByIndex(i).getStringValue();
			String code = cols.get(PROPCODE).getCellByIndex(i).getStringValue();
			if (code != null)
				if (!code.isEmpty())
					extraCode.put(propType, code);
		}
	}

//	// =====================================================================================
	// get the import snippets for enums.
	private static void getExtraImports(Table sheet, Map<String, String> extraCode) {
		List<Column> cols = sheet.getColumnList();
		for (int i = TYPES + 1; i < sheet.getRowCount(); i++) {
			String propType = cols.get(PROPTYPE).getCellByIndex(i).getStringValue();
			String code = cols.get(PROPIMPORT).getCellByIndex(i).getStringValue();
			if (code != null)
				if (!code.isEmpty())
					extraCode.put(propType, code);
		}
	}

//=====================================================================================
	public static void main(String[] args) {
		now = new Date();
		System.out.println("Starting 3Worlds core component generation at " + now + "...");
		try {
			// process archetypeTree.ods
//			File archspecs = new File(ARCH_FILE);
//			SpreadsheetDocument odf2 = SpreadsheetDocument.loadDocument(archspecs);
//			Table treesheet = odf2.getSheetByIndex(0);
//			generateArchetype(treesheet);

			// process enumConstants.ods
			File specs = new File(ENUM_FILE);
			SpreadsheetDocument odf = SpreadsheetDocument.loadDocument(specs);
			Map<String, String> extraCode = new HashMap<>();
			Map<String, String> extraImports = new HashMap<>();

			// first, select all sheets that do not have a "." in their name
			// these are "Node" sheets, i.e. their content describes a list of properties
			// applying to a node
			for (int i = 0; i < odf.getSheetCount(); i++) {
				Table sheet = odf.getSheetByIndex(i);
				if (!sheet.getTableName().equals("Instructions")) // ignore instructions sheet
					if (sheet.getTableName().indexOf('.') < 0) {
						getExtraCode(sheet, extraCode);
						getExtraImports(sheet, extraImports);
						generateNodeDoc(sheet);
//						generateNodeArchetype(sheet);
					}
			}
			// second, select all other sheets
			// these are enum-type property sheets
			for (int i = 0; i < odf.getSheetCount(); i++) {
				Table sheet = odf.getSheetByIndex(i);
				if (sheet.getTableName().indexOf('.') > 0) {
					Table nodeSheet = odf.getSheetByName(sheet.getTableName().split("\\.")[0]);
					generateEnumCode(sheet, extraCode, extraImports);
					generateEnumDoc(nodeSheet, sheet);
					generateEnumSetCode(nodeSheet, sheet);
				}
			}
		} catch (Exception e) {
			System.out.println("3Worlds core component generation failed");
			e.printStackTrace();
		}
		System.out.println("3Worlds core component generation finished at " + new Date());
	}

}
