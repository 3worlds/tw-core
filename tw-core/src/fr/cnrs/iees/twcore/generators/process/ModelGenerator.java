package fr.cnrs.iees.twcore.generators.process;

import static fr.ens.biologie.codeGeneration.CodeGenerationUtils.*;
import static fr.ens.biologie.generic.utils.NameUtils.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;


import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bouncycastle.util.Strings;

import au.edu.anu.twcore.data.FieldNode;
import au.edu.anu.twcore.data.Record;
import au.edu.anu.twcore.data.TableNode;
import au.edu.anu.twcore.ecosystem.dynamics.ProcessNode;
import au.edu.anu.twcore.ecosystem.runtime.process.ComponentProcess;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemData;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.project.ProjectPaths;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;
import fr.cnrs.iees.twcore.generators.TwCodeGenerator;
import fr.cnrs.iees.uit.space.Box;
import fr.cnrs.iees.uit.space.Distance;
import fr.cnrs.iees.uit.space.Point;
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
	private Map<String,ModelMethodGenerator> methods = new HashMap<>();
	// method scope
	private static String methodScope = "public static";
	// set to make sure there are no two fields with the same name
	private Set<String> replicateNames = new HashSet<>();

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
				// TODO get the code from previous version.
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
				ModelMethodGenerator meth = new ModelMethodGenerator(methodScope,returnType,methname);
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

	private void addRecordFields(ModelMethodGenerator method, Record rec) {
		for (TreeNode tn:rec.getChildren()) {
			if (tn instanceof FieldNode) {
				FieldNode f = (FieldNode) tn;
				if (replicateNames.contains(f.id())) {
					log.warning(()->"Replicated field name ("+f.id()+") in function "+method.name());
				}
				else {
					StringBuilder comment = new StringBuilder();
					if (f.properties().hasProperty(P_FIELD_DESCRIPTION.key()))
						if (f.properties().getPropertyValue(P_FIELD_DESCRIPTION.key())!=null)
							comment.append(f.properties().getPropertyValue(P_FIELD_DESCRIPTION.key()));
						else
							comment.append(f.id());
					else
						comment.append(f.id());
					if (f.properties().hasProperty(P_FIELD_UNITS.key()))
						if (f.properties().getPropertyValue(P_FIELD_UNITS.key())!=null)
							if (!f.properties().getPropertyValue(P_FIELD_UNITS.key()).toString().isEmpty())
								comment.append(" (")
									.append(f.properties().getPropertyValue(P_FIELD_UNITS.key()))
									.append(")");
					if (f.properties().hasProperty(P_FIELD_PREC.key()))
						if (f.properties().getPropertyValue(P_FIELD_PREC.key())!=null)
							comment.append(" ± ")
								.append(f.properties().getPropertyValue(P_FIELD_PREC.key()));
					if (f.properties().hasProperty(P_FIELD_INTERVAL.key()))
						if (f.properties().getPropertyValue(P_FIELD_INTERVAL.key())!=null)
							comment.append(" ")
								.append(f.properties().getPropertyValue(P_FIELD_INTERVAL.key()));
					if (f.properties().hasProperty(P_FIELD_RANGE.key()))
						if (f.properties().getPropertyValue(P_FIELD_RANGE.key())!=null)
							comment.append(" [")
								.append(f.properties().getPropertyValue(P_FIELD_RANGE.key()))
								.append(']');
					String type = f.properties().getPropertyValue(P_FIELD_TYPE.key()).toString();
					if (ValidPropertyTypes.isPrimitiveType(type)) {
						if (type.equals("Integer"))
							type = "int";
						else
							type = Strings.toLowerCase(type.substring(0,1)) + type.substring(1);
					}
					method.addArgument(f.id(),type,comment.toString());
					replicateNames.add(f.id());
				}
			}
			else if (tn instanceof TableNode) {
				// TODO: tables! + recursion with records !
			}
		}
	}

	@SuppressWarnings("unchecked")
	public ModelMethodGenerator setMethod(TreeGraphDataNode function) {
		TwFunctionTypes ftype = (TwFunctionTypes) function.properties().getPropertyValue(P_FUNCTIONTYPE.key());
		String fname = function.id();
		ModelMethodGenerator method = methods.get(fname);
		// if method does not exist, create it and set its header, return type and return statement
		if (method==null) {
			String mname  = Strings.toLowerCase(fname.substring(0,1)) + fname.substring(1);
			method = new ModelMethodGenerator(methodScope,ftype.returnType(),mname);
			methods.put(fname,method);
			if (!ftype.returnType().equals("void"))
				method.setReturnStatement("return "+zero(ftype.returnType()));
			method.clearArguments();
		}
		// in all cases, retrieve all the parameters and parameter types
		replicateNames.clear();
		//always present parameters
		method.addArgument("t","double","current time");
		method.addArgument("dt","double","current time step");

		// methods applying to ComponentProceses:
		for (TwFunctionTypes tft:ComponentProcess.compatibleFunctionTypes)
			if (tft.equals(ftype)) {
				// focal arguments
				// TODO: groups etc.
				method.addArgument("limits","Box" ,"space limits");
				method.addArgument("age", "double", "focal cpt. age");
				method.addArgument("birthDate", "double", "focal cpt. creation time");
				TreeNode fp = function.getParent();
				if (fp instanceof ProcessNode) { // must be this or a consequence
					ProcessNode pn = (ProcessNode) fp;
					// must be categories
					List<Category> cats = (List<Category>) get(pn.edges(Direction.OUT),
						selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())),
						edgeListEndNodes());
					for (Category cat:cats) {
						Record rec = (Record) get(cat.edges(Direction.OUT),
							selectZeroOrOne(hasTheLabel(E_PARAMETERS.label())),
							endNode());
						if (rec!=null)
							addRecordFields(method,rec);
						rec = (Record) get(cat.edges(Direction.OUT),
							selectZeroOrOne(hasTheLabel(E_LTCONSTANTS.label())),
							endNode());
						if (rec!=null)
							addRecordFields(method,rec);
						rec = (Record) get(cat.edges(Direction.OUT),
							selectZeroOrOne(hasTheLabel(E_DRIVERS.label())),
							endNode());
						if (rec!=null)
							addRecordFields(method,rec);
						rec = (Record) get(cat.edges(Direction.OUT),
							selectZeroOrOne(hasTheLabel(E_DECORATORS.label())),
							endNode());
						if (rec!=null)
							addRecordFields(method,rec);

						// TODO: Decorators and next maybe read/write
					}
				}
				else { // must be a consequence

				}
				method.addArgument("location", "Point", "focal cpt. location");
				break;
		}


		// other arguments (optional: test on function)


		return method;
	}

//	public MethodGenerator getMethod(String name) {
//		MethodGenerator result = methods.get(name);
//		if (result==null) {
//			MethodGenerator meth = new MethodGenerator(methodScope,null,name);
//			methods.put(name,meth);
//			result = meth;
//		}
//		return result;
//	}

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
		for (ModelMethodGenerator m:methods.values())
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
