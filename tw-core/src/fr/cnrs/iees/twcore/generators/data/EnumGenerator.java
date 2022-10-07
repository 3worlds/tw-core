package fr.cnrs.iees.twcore.generators.data;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.ens.biologie.codeGeneration.MethodGenerator;
import fr.ens.biologie.generic.JavaCode;

/**
 * TO MOVE TO OMHTK when it works
 * MOVE management of inherited methods to an ancestor common to ClassGenerator and EnumGenerator
 * 
 * @author Jacques Gignoux - 7 oct. 2022
 *
 */
public class EnumGenerator implements JavaCode {

	private String name;
	private String packageName;
	private Set<String> imports = new HashSet<String>();
	private String classComment = null;
	private Set<String> interfaces = new HashSet<String>();
	private Set<String> constants = new HashSet<String>();
	private Map<String,MethodGenerator> methods = new HashMap<String,MethodGenerator>();
	private List<String> methodsToOverride = new ArrayList<>();
	
	// MOVE TO AN ABSTRACT ANCESTOR
	private void recordAncestorMethods(Class<?> c) {
		Method[] lm = c.getDeclaredMethods();
		for (Method m:lm) {
			if (!methods.containsKey(m.getName()))
			if (Modifier.isAbstract(m.getModifiers()) ||
				(methodsToOverride.contains(m.getName()))) {
				methods.put(m.getName(), new MethodGenerator(m));
			}
		}
	}
	
	// MOVE TO AN ABSTRACT ANCESTOR
	private String stripTemplate(String className) {
		String result = className;
		if (className.indexOf('<')>-1)
			result = className.substring(0, className.indexOf('<'));
		return result;
	}

	// MOVE TO AN ABSTRACT ANCESTOR
	private String stripPackageFromClassName(String fullClassName) {
		String[] sc = fullClassName.split("\\.");
		return sc[sc.length-1];
	}

	public EnumGenerator (String packageName,
		String classComment,
		String name,
		String... interfaces) {
		this.packageName = packageName;
		this.classComment = classComment;
		this.name = name;
		for (String s:interfaces) {
			try { recordAncestorMethods(Class.forName(s)); }
			catch (ClassNotFoundException e) {}
			// an interface name with no package is assumed to be in the same package, hence no import
			if (s.contains(".")) 
				imports.add(stripTemplate(s));
			this.interfaces.remove(s);
			this.interfaces.add(stripPackageFromClassName(s));
		}
	}

	@Override
	public String asText(String indent) {
		StringBuilder result = new StringBuilder();
		// class headers
		result.append("package ").append(packageName).append(";\n\n");
		for (String imp:imports)
			result.append("import ").append(imp).append(";\n");
		if (imports.size()>0)
			result.append("\n");
		if (classComment!=null)
			result.append(classComment).append("\n");
		result.append("public enum ").append(name);
		if (!interfaces.isEmpty())
			result.append(" implements ");
		String[] ifs = interfaces.toArray(new String[interfaces.size()]);
		for (int i=0; i<ifs.length; i++)
			if (i<ifs.length-1) result.append(' ').append(ifs[i]).append(", ");
			else result.append(' ').append(ifs[i]);
		result.append(" {\n\n"); // 1
		// Enum fields
		for (String constant:constants)
			result.append(indent).append(constant).append(",\n");
		result.append(indent).append(";\n\n");
		// methods, if any (no constructors at the moment)
		for (MethodGenerator m:methods.values())
			result.append(m.asText(indent));
		// class footer
		result.append("}\n"); // 1
		return result.toString();
	}
	
	public EnumGenerator setConstant(String constant) {
		constants.add(constant);
		return this;
	}

}
