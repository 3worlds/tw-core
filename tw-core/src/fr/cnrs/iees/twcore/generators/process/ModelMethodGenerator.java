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
package fr.cnrs.iees.twcore.generators.process;

import static fr.ens.biologie.codeGeneration.Comments.*;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import fr.ens.biologie.codeGeneration.MethodGenerator;

/**
 * A descendant of MethodGenerator with a different formatting of methods
 *
 * @author gignoux
 *
 */
public class ModelMethodGenerator extends MethodGenerator {

	private String methodComment = null;
	private String[] argComments = null;

	private List<String> rawCode = new LinkedList<>();

	public ModelMethodGenerator(Method method) {
		super(method);
		insertCodeInsertionComment = false;
	}

	public ModelMethodGenerator(MethodGenerator mgen) {
		super(mgen);
		insertCodeInsertionComment = false;
		argComments = new String[argNames.length];
		Arrays.fill(argComments, "");
	}

	public ModelMethodGenerator(String scope, String returnType, String name, String... argTypes) {
		super(scope, returnType, name, argTypes);
		insertCodeInsertionComment = true;
		argComments = new String[argTypes.length];
		for (int i=0; i<argComments.length; i++)
			argComments[i] = "";
	}

	public void setRawCode(List<String> code) {
		rawCode.addAll(code);
	}

	public void setRawCode(String... code) {
		for (String s:code)
			rawCode.add(s);
	}

	/**
	 * Use this only to add arguments to the existing ones - with caution.
	 * @param name
	 * @param type
	 * @param comment
	 * @return
	 */
	public MethodGenerator addArgument(//TwFunctionArguments arg,
//			ConfigurationEdgeLabels dataGroup,
			String name,
			String type,
			String comment) {
		if (argNames==null) {
			argNames = new String[1];
			argTypes = new String[1];
			argComments = new String[1];
		}
		else {
			argNames = Arrays.copyOf(argNames,argNames.length+1);
			argTypes = Arrays.copyOf(argTypes,argTypes.length+1);
			argComments = Arrays.copyOf(argComments,argComments.length+1);
		}
		argNames[argNames.length-1] = name;
		argTypes[argTypes.length-1] = type;
		argComments[argComments.length-1] = comment;
//		argumentGroups.get(arg).add(new Tuple<>(dataGroup,name,type));
		return this;
	}

//	/**
//	 * Use this to set / add arguments. If the argument exists in the argument list, nothing happens.
//	 * If the argument does not exist, it is added at the end of the list.
//	 * CAUTION: when replacing default argument names (v1,v2,v3...) the new names must be
//	 * passed in the proper order (no check) for replacment to take place.
//	 *
//	 * @param name
//	 * @param type
//	 * @param comment
//	 * @return
//	 */
//	public MethodGenerator setArgument(String name,
//			String type,
//			String comment) {
//		int i=0;
//		while ((i<argNames.length) && (!isDefaultName(argNames[i])))
//			i++;
//		if (i<argNames.length) {
//			argNames[i] = name;
//			argTypes[i] = type;
//			argComments[i] = comment;
//		}
//		Set<String> argset = new HashSet<>();
//		argset.addAll(Arrays.asList(argNames));
//		if (!argset.contains(name))
//			addArgument(name,type,comment);
//		return this;
//	}
//
//	private boolean isDefaultName(String name) {
//		try {
//			Integer.parseInt(name.substring(1).trim());
//		}
//		catch (NumberFormatException e) {
//			return false;
//		}
//		return (name.substring(0,1).equals("v"));
//	}
//
//	public String getArgumentName(int i) {
//		return argNames[i];
//	}

	public void setMethodComment(String comment) {
		methodComment = comment;
	}

	/**
	 * Use this method to cleanup argument list (after initialisation with a Method for example).
	 * @return
	 */
	public MethodGenerator clearArguments() {
		argTypes = null;
		argNames = null;
		return this;
	}

	@Override
	public String asText(String indent) {
		String result = "";
		if (override!=null)	result += indent + override + "\n";
		// place javadoc comment here
		if (methodComment!=null)
			result += methodComment;
		if (returnType==null) // constructors only
			result += indent + scope + " " + name + "(";
		else
			result += indent + scope + " " + returnType + " " + name + "(\n";
		if (argTypes!=null)
		for (int i=0; i< argTypes.length; i++) {
			String pLine = indent+indent+argTypes[i]+" "+argNames[i];
			if (i==argTypes.length-1)
				pLine += ") {";
			else
				pLine += ",";
			int commentStart = Math.max(40, pLine.length()+1);
			result += pLine+" ".repeat(commentStart-pLine.length())+"// ";
			result += argComments[i];
			result += "\n";
		}

		if (insertCodeInsertionComment) {
			String[] cmt = Arrays.copyOf(startCodeInsertion,1);
			cmt[0] = name + " " + cmt[0];
			result += indent+singleLineComment(cmt);
		}
		if (rawCode.isEmpty())
			for (String s:statements)
				result += indent+indent+s+";\n";
		else
			for (String s:rawCode)
				result += s+"\n";
		if (returnType==null) ;
		else if (returnType.equals("void")) ;
		else if (rawCode.isEmpty())
			result += indent+indent+returnStatement+";\n";
		if (insertCodeInsertionComment) {
			String[] cmt = Arrays.copyOf(endCodeInsertion,1);
			cmt[0] = name + " " + cmt[0];
			result += indent+singleLineComment(cmt);
		}
		result += indent+"}\n\n";
		return result;
	}

//	public Map<TwFunctionArguments,List<Tuple<ConfigurationEdgeLabels,String,String>>> callerArguments () {
//		return argumentGroups;
//	}


}
