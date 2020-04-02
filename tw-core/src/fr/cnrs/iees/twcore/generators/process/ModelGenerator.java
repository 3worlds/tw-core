package fr.cnrs.iees.twcore.generators.process;

import static fr.ens.biologie.codeGeneration.CodeGenerationUtils.writeFile;
import static fr.ens.biologie.generic.utils.NameUtils.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.project.ProjectPaths;
import fr.cnrs.iees.twcore.generators.TwCodeGenerator;
import fr.cnrs.iees.uit.space.Box;
import fr.cnrs.iees.uit.space.Distance;
import fr.cnrs.iees.uit.space.Point;
import fr.ens.biologie.codeGeneration.MethodGenerator;
import fr.ens.biologie.generic.JavaCode;
import fr.ens.biologie.generic.utils.Logging;

/**
 * A class to generate the skeletton of a user-editable model code
 *
 * @author Jacques Gignoux - 2 avr. 2020
 *
 */
public class ModelGenerator extends TwCodeGenerator implements JavaCode {

	private static Logger log = Logging.getLogger(ModelGenerator.class);
	private String className = null;
	private String modelName = null;
	private String generatedClassName = null;
	private String packageName=null;
	private String packagePath;
	private Class<?> previousModel = null;
	// class comment
	private String classComment = null;
	// all the imports
	private List<String> imports = new LinkedList<String>();
	// all the methods to add to this code
	private Map<String,MethodGenerator> methods = new HashMap<>();
	// method scope
	private static String methodScope = "public static ";

	/**
	 * Constructor able to manage previously generated code
	 *
	 * @param spec speccification for this model
	 * @param previousModel the previousModel class, if any
	 */
	public ModelGenerator(String modelName, String modelDir, Class<?> previousModel) {
		super(null);
		className = validJavaName(wordUpperCaseName(initialUpperCase(modelName)));
		this.modelName = modelDir;
		packageName = ProjectPaths.REMOTECODE.replace(File.separator,".")+"."+modelDir;
		this.previousModel = previousModel;
		packagePath = Project.makeFile(LOCALCODE,validJavaName(wordUpperCaseName(modelDir))).getAbsolutePath();
		if (previousModel!=null) {
			// get all imports from file ?
			Method[] lm = previousModel.getDeclaredMethods();
			for (Method m:lm) {
				String methname = m.getName();
				String returnType = m.getReturnType().getSimpleName();
				// how to get the method statements ???- we need the java file - cant us reflection.
				// any line starting with public static is a method - grab the name and return type, that's
				// all we need
				// if return type not void end of method = return <sthing> ; }
				// if return type void end of method = } matching previous {
				// this assumes valid code, ie not code with compile errors
				// all code between method opening and end is stored in a stringlist
				// once method has its new parameter list and eturn type, statements are added
				// what about imports ? well, we cant do everything!
				MethodGenerator meth = new MethodGenerator(methodScope,returnType,methname);
				methods.put(methname,meth);
			}
		}
		else {
			imports.add("static java.lang.Math.*");
			imports.add("static "+Distance.class.getCanonicalName()+".*");
			imports.add(Point.class.getCanonicalName());
			imports.add(Box.class.getCanonicalName());
		}
	}

	public MethodGenerator getMethod(String name) {
		MethodGenerator result = methods.get(name);
		if (result==null) {
			MethodGenerator meth = new MethodGenerator(methodScope,null,name);
			methods.put(name,meth);
			result = meth;
		}
		return result;
	}

	@Override
	public boolean generateCode() {
		log.info("    generating file " + className + ".java ...");
		File ctGeneratedCodeDir = getModelCodeDir(modelName);
		ctGeneratedCodeDir.mkdirs();
		File file = Project.makeFile(LOCALCODE,modelName, className + ".java");
		writeFile(this, file, className);
		generatedClassName = packageName + "." + className;
		log.info("  done.");
		return true;
	}

	@Override
	public String asText(String indent) {
		String result = "";
		result += "package "+packageName+";\n\n";
		for (String imp:imports)
			result += "import "+imp+";\n";
		if (imports.size()>0)
			result += "\n";
		if (classComment!=null)
			result += classComment+"\n";
		result += "public interface "+className;
		result += " {\n\n"; // 1
		for (MethodGenerator m:methods.values())
			result += m.asText(indent);
//		if (rawMethods!=null)
//			for (String s:rawMethods)
//				result += s+"\n";
		result += "}\n"; // 1
		return result;
	}

	public String generatedClassName() {
		return generatedClassName;
	}


}
