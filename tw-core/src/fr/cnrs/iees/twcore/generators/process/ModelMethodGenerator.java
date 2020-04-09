package fr.cnrs.iees.twcore.generators.process;

import static fr.ens.biologie.codeGeneration.Comments.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import fr.ens.biologie.codeGeneration.MethodGenerator;
import fr.ens.biologie.generic.utils.Duple;

/**
 * A descendant of MethodGenerator with a different formatting of methods
 *
 * @author gignoux
 *
 */
public class ModelMethodGenerator extends MethodGenerator {

	private String[] argComments = null;
	// a map of the argument names grouped by role for Twfunction code generation
	private EnumMap<ArgumentGroups,List<Duple<String,String>>> argumentGroups =
		new EnumMap<>(ArgumentGroups.class);

	public ModelMethodGenerator(Method method) {
		super(method);
		insertCodeInsertionComment = false;
		for (ArgumentGroups a:ArgumentGroups.values())
			argumentGroups.put(a,new LinkedList<>());
	}

	public ModelMethodGenerator(String scope, String returnType, String name, String... argTypes) {
		super(scope, returnType, name, argTypes);
		insertCodeInsertionComment = true;
		for (ArgumentGroups a:ArgumentGroups.values())
			argumentGroups.put(a,new LinkedList<>());
	}

	/**
	 * Use this only to add arguments to the existing ones - with caution.
	 * @param name
	 * @param type
	 * @return
	 */
	public MethodGenerator addArgument(ArgumentGroups grp, String name, String type, String comment) {
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
		argumentGroups.get(grp).add(new Duple<>(name,type));
		return this;
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

		if (insertCodeInsertionComment)
			result += indent+singleLineComment(startCodeInsertion);
		for (String s:statements) {
			result += indent+indent+s+";\n";
		}
		if (returnType==null) ;
		else if (returnType.equals("void")) ;
		else result += indent+indent+returnStatement+";\n";
		if (insertCodeInsertionComment)
			result += indent+singleLineComment(endCodeInsertion);
		result += indent+"}\n\n";
		return result;
	}

	public Map<ArgumentGroups,List<Duple<String,String>>> callerArguments () {
		return this.argumentGroups;
	}

}
