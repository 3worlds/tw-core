package fr.cnrs.iees.twcore.generators.process;

import static fr.ens.biologie.codeGeneration.CodeGenerationUtils.*;
import static fr.ens.biologie.generic.utils.NameUtils.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.bouncycastle.util.Strings;

import au.edu.anu.twcore.data.FieldNode;
import au.edu.anu.twcore.data.Record;
import au.edu.anu.twcore.data.TableNode;
import au.edu.anu.twcore.ecosystem.dynamics.ProcessNode;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.process.AbstractRelationProcess;
import au.edu.anu.twcore.ecosystem.runtime.process.ComponentProcess;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.project.ProjectPaths;
import au.edu.anu.twcore.userProject.UserProjectLink;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;
import fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels;
import fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames;
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
	// class comment
	private String classComment = null;
	// all the imports
	private Set<String> imports = new HashSet<String>();
	// all the methods to add to this code
	private Map<String,ModelMethodGenerator> methods = new HashMap<>();
	// method scope
	private static String methodScope = "public static";
	// set to make sure there are no two fields with the same name
	private Set<String> replicateNames = new HashSet<>();
//	// a map of the argument names grouped by role for Twfunction code generation
//	private EnumMap<ArgumentGroups,List<Duple<String,String>>> argumentGroups = new EnumMap<>(ArgumentGroups.class);
	// store all the generated data class matchings to categories
	private Map<Set<Category>,generatedData> dataClassNames = new HashMap<>();
	private Map<String,List<String>> snippets =new HashMap<>();

	private class generatedData {
		private String drivers;
		private String decorators;
		private String parameters;
		private String lifetimeConstants;
	}

	/**
	 * Constructor able to manage previously generated code
	 *
	 */
	@SuppressWarnings("unchecked")	// graph root,   ecology.id()
	public ModelGenerator(TreeGraphNode root3w, String modelDir) {
		super(null);
		className = validJavaName(wordUpperCaseName(initialUpperCase(root3w.id())));
		modelName = modelDir;
		packageName = ProjectPaths.REMOTECODE.replace(File.separator,".")+"."+modelDir;
		//check if a file was already generated for this model in user project
		// if yes, extract all user code as snippets.
		packagePath = Project.makeFile(LOCALCODE,validJavaName(wordUpperCaseName(modelDir))).getAbsolutePath();
		String previousModel = UserProjectLink.srcRoot()+File.separator+
			ProjectPaths.REMOTECODE+File.separator+
			validJavaName(wordUpperCaseName(modelDir))+File.separator+
			className+".java";
		if (Files.exists(Path.of(previousModel))) {
			File previousFile = new File(previousModel);
			List<String> lines = new LinkedList<>();
			try {
				lines = Files.readAllLines(previousFile.toPath());
			} catch (IOException e) {
				log.severe(()->"File "+previousFile+" could not be read - file regenerated instead");
			}
			String record = null;
			for (String line:lines) {
				if (line.contains("import"))
					imports.add(line.substring(line.indexOf("import")+6,line.indexOf(';')).strip());
				if (line.contains("END CODE INSERTION ZONE"))
					record = null;
				if (record!=null) {
					if (snippets.get(record)==null)
						snippets.put(record,new LinkedList<>());
					snippets.get(record).add(line);
				}
				if (line.contains("INSERT YOUR CODE BELOW THIS LINE"))
					record = line.substring(line.indexOf("//")+2,line.indexOf('*')).strip();
			}
			// debugging
			for (String s:snippets.keySet()) {
				System.out.println("Snippet for method'"+s+"': ");
				for (String ss: snippets.get(s))
					System.out.println("\t"+ss);
			}
		}
		else {
			imports.add("static java.lang.Math.*");
			imports.add("static "+Distance.class.getCanonicalName()+".*");
			imports.add(Point.class.getCanonicalName());
			imports.add(Box.class.getCanonicalName());
		}
		List<TreeGraphDataNode> cpt = (List<TreeGraphDataNode>) get(root3w, children(),selectOne(hasTheLabel(N_SYSTEM.label())),
			children(),selectOne(hasTheLabel(N_STRUCTURE.label())),
			children(),selectOneOrMany(hasTheLabel(N_COMPONENTTYPE.label())));
		for (TreeGraphDataNode tn:cpt) {
			generatedData cl = new generatedData();
			if (tn.properties().hasProperty(P_PARAMETERCLASS.key()))
				if (tn.properties().getPropertyValue(P_PARAMETERCLASS.key())!=null) {
					String s = (String) tn.properties().getPropertyValue(P_PARAMETERCLASS.key());
					if (s.isEmpty())
						cl.parameters = null;
					else
						cl.parameters = s;
			}
			if (tn.properties().hasProperty(P_LTCONSTANTCLASS.key()))
				if (tn.properties().getPropertyValue(P_LTCONSTANTCLASS.key())!=null) {
					String s = (String) tn.properties().getPropertyValue(P_LTCONSTANTCLASS.key());
					if (s.isEmpty())
						cl.lifetimeConstants = null;
					else
						cl.lifetimeConstants = s;
			}
			if (tn.properties().hasProperty(P_DRIVERCLASS.key()))
				if (tn.properties().getPropertyValue(P_DRIVERCLASS.key())!=null) {
					String s = (String) tn.properties().getPropertyValue(P_DRIVERCLASS.key());
					if (s.isEmpty())
						cl.drivers = null;
					else
						cl.drivers = s;
			}
			if (tn.properties().hasProperty(P_DECORATORCLASS.key()))
				if (tn.properties().getPropertyValue(P_DECORATORCLASS.key())!=null) {
					String s = (String) tn.properties().getPropertyValue(P_DECORATORCLASS.key());
					if (s.isEmpty())
						cl.decorators = null;
					else
						cl.decorators = s;
			}
			// this because ComponentType is unsealed at that time
			SortedSet<Category> categories = new TreeSet<>();	 // caution: sorted set !
			Collection<Category> nl = (Collection<Category>) get(tn.edges(Direction.OUT),
				selectOneOrMany(hasTheLabel(E_BELONGSTO.label())),
				edgeListEndNodes());
			categories.addAll(((Categorized<SystemComponent>)tn).getSuperCategories(nl));
			dataClassNames.put(categories,cl);
		}
	}

	// returns the generated data class that contains a given field name
	// this works because fields and tables all have unique names
	public String containingClass(String field) {
		for (Set<Category> set: dataClassNames.keySet()) {
			for (Category c:set) {
				TreeGraphDataNode rec = (TreeGraphDataNode) get(c.edges(Direction.OUT),
					selectZeroOrOne(hasTheLabel(E_DRIVERS.label())),
					endNode());
				if (rec!=null) {
					for (TreeNode t:rec.getChildren())
						// this works for tables and fields
						if (field.equals(t.id()))
							return dataClassNames.get(set).drivers;
				}
				rec = (TreeGraphDataNode) get(c.edges(Direction.OUT),
					selectZeroOrOne(hasTheLabel(E_DECORATORS.label())),
					endNode());
				if (rec!=null) {
					for (TreeNode t:rec.getChildren())
						if (field.equals(t.id()))
							return dataClassNames.get(set).decorators;
				}
				rec = (TreeGraphDataNode) get(c.edges(Direction.OUT),
					selectZeroOrOne(hasTheLabel(E_LTCONSTANTS.label())),
					endNode());
				if (rec!=null) {
					for (TreeNode t:rec.getChildren())
						if (field.equals(t.id()))
							return dataClassNames.get(set).lifetimeConstants;
				}
				rec = (TreeGraphDataNode) get(c.edges(Direction.OUT),
					selectZeroOrOne(hasTheLabel(E_PARAMETERS.label())),
					endNode());
				if (rec!=null) {
					for (TreeNode t:rec.getChildren())
						// this works for tables and fields
						if (field.equals(t.id()))
							return dataClassNames.get(set).parameters;
				}
			}
		}
		return null;
	}


	public ModelMethodGenerator method(String name) {
		return methods.get(name);
	}

	// prepare comments to explain arguments to end user
	private String argComment(TreeGraphDataNode f,
			ConfigurationPropertyNames descro,
			ConfigurationPropertyNames units,
			ConfigurationPropertyNames prec,
			ConfigurationPropertyNames interval,
			ConfigurationPropertyNames range) {
		StringBuilder comment = new StringBuilder();
		if (f.properties().hasProperty(descro.key()))
			if (f.properties().getPropertyValue(descro.key())!=null)
				comment.append(f.properties().getPropertyValue(descro.key()));
			else
				comment.append(f.id());
		else
			comment.append(f.id());
		if (f.properties().hasProperty(units.key()))
			if (f.properties().getPropertyValue(units.key())!=null)
				if (!f.properties().getPropertyValue(units.key()).toString().isEmpty())
					comment.append(" (")
						.append(f.properties().getPropertyValue(units.key()))
						.append(")");
		if (f.properties().hasProperty(prec.key()))
			if (f.properties().getPropertyValue(prec.key())!=null)
				comment.append(" ± ")
					.append(f.properties().getPropertyValue(prec.key()));
		if (f.properties().hasProperty(interval.key()))
			if (f.properties().getPropertyValue(interval.key())!=null)
				comment.append(" ")
					.append(f.properties().getPropertyValue(interval.key()));
		if (f.properties().hasProperty(range.key()))
			if (f.properties().getPropertyValue(range.key())!=null)
				comment.append(" [")
					.append(f.properties().getPropertyValue(range.key()))
					.append(']');
		return comment.toString();
	}

	private String simpleType(String className) {
		String[] ss = className.split("\\.");
		return ss[ss.length-1];
	}

	class memberInfo implements Comparable<memberInfo> {
		String name;
		String type;
		String fullType;
		String comment;
		boolean isTable;
		@Override
		public int compareTo(memberInfo m) {
			return name.compareTo(m.name);
		}
	}

	// gets all the fields and tables of a give category set. Returns them in always the same order
	protected Map<ConfigurationEdgeLabels,SortedSet<memberInfo>> getAllMembers(Collection<Category> cats) {
		Map<ConfigurationEdgeLabels,SortedSet<memberInfo>> result = new HashMap<>();
		for (Category cat:cats)
			for (ConfigurationEdgeLabels cel:
				EnumSet.of(E_PARAMETERS,E_LTCONSTANTS,E_DECORATORS,E_DRIVERS)) {
			Record rec = (Record) get(cat.edges(Direction.OUT),
				selectZeroOrOne(hasTheLabel(cel.label())),
				endNode());
			if (rec!=null) {
				for (TreeNode tn:rec.getChildren()) {
					memberInfo mb = new memberInfo();
					if (tn instanceof FieldNode) {
						FieldNode f = (FieldNode) tn;
						mb.isTable = false;
						mb.name = validJavaName(wordUpperCaseName(f.id()));
						mb.comment = argComment(f,P_FIELD_DESCRIPTION,P_FIELD_UNITS,P_FIELD_PREC,
							P_FIELD_INTERVAL,P_FIELD_RANGE);
						mb.type = f.properties().getPropertyValue(P_FIELD_TYPE.key()).toString();
						if (ValidPropertyTypes.isPrimitiveType(mb.type)) {
							if (mb.type.equals("Integer"))
								mb.type = "int";
							else
								mb.type = Strings.toLowerCase(mb.type.substring(0,1)) + mb.type.substring(1);
						}
						mb.fullType = ValidPropertyTypes.getJavaClassName(mb.type);
					}
					else if (tn instanceof TableNode) {
						TableNode t = (TableNode) tn;
						mb.isTable = true;
						mb.name = validJavaName(wordUpperCaseName(t.id()));
						mb.comment = argComment(t,P_TABLE_DESCRIPTION,P_TABLE_UNITS,P_TABLE_PREC,
							P_TABLE_INTERVAL,P_TABLE_RANGE);
						// Add table dimensions to comment
						// table of primitive types
						if (t.properties().hasProperty(P_DATAELEMENTTYPE.key())) {
							mb.type = t.properties().getPropertyValue(P_DATAELEMENTTYPE.key()).toString();
							if (ValidPropertyTypes.isPrimitiveType(mb.type)) {
								if (mb.type.equals("Integer"))
									mb.type = "IntTable";
								else
									mb.type += "Table";
							}
							mb.fullType = ValidPropertyTypes.getJavaClassName(mb.type);
						}
						// table of user-defined records
						else {
							mb.type = validJavaName(initialUpperCase(wordUpperCaseName(t.id())));
							mb.fullType = null;
						}

					}
					if (result.get(cel)==null)
						result.put(cel,new TreeSet<>());
					result.get(cel).add(mb);
				}
			}
			else
				result.put(cel,new TreeSet<>()); // empty
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	protected SortedSet<Category> findCategories(TreeGraphDataNode function, boolean focal) {
		TwFunctionTypes ftype = (TwFunctionTypes) function.properties().getPropertyValue(P_FUNCTIONTYPE.key());
		TreeNode fp = function.getParent();
		SortedSet<Category> cats = new TreeSet<>();
		// function parent is a process
		if (fp instanceof ProcessNode) {
			ProcessNode pn = (ProcessNode) fp;
			if (focal)
				for (TwFunctionTypes tft:ComponentProcess.compatibleFunctionTypes)
					if (tft.equals(ftype)) {
					cats.addAll((Collection<Category>) get(pn.edges(Direction.OUT),
						selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())),
						edgeListEndNodes()));
					return cats;
			}
			for (TwFunctionTypes tft:AbstractRelationProcess.compatibleFunctionTypes)
				if (tft.equals(ftype)) {
					RelationType relnode = (RelationType) get(pn.edges(Direction.OUT),
						selectZeroOrOne(hasTheLabel(E_APPLIESTO.label())),
						endNode());
					if (focal) {
						cats.addAll((Collection<Category>) get(relnode.edges(Direction.OUT),
							selectOneOrMany(hasTheLabel(E_FROMCATEGORY.label())),
							edgeListEndNodes()));
						return cats;
					}
					else {
						cats.addAll((Collection<Category>) get(relnode.edges(Direction.OUT),
							selectOneOrMany(hasTheLabel(E_TOCATEGORY.label())),
							edgeListEndNodes()));
						return cats;
					}
			}
		}
		// function parent is a function
		else {
			// TODO: categories of consequence functions ??? good luck!
			// if createOther --> lifeCycle
			// if deleteOther --> returnTo relation
			// if changeCategory --> life cycle
			// there may be more than one set in each case --> multiple method generation
		}
		return cats;
	}

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
			if (snippets.containsKey(mname))
				method.setRawCode(snippets.get(mname));
		}
		// retrieve all the parameters and parameter types
		replicateNames.clear();
		EnumSet<ArgumentGroups> argSet = EnumSet.noneOf(ArgumentGroups.class);
		argSet.addAll(ftype.readOnlyArguments());
		argSet.addAll(ftype.writeableArguments());
		Map<ConfigurationEdgeLabels, SortedSet<memberInfo>> membersForFocal =
			getAllMembers(findCategories(function,true));
		Map<ConfigurationEdgeLabels, SortedSet<memberInfo>> membersForOther =
			getAllMembers(findCategories(function,false));
		for (ArgumentGroups arg:argSet) {
			switch (arg) {
			case t:
			case dt:
				method.addArgument(arg,arg.name(),arg.type(),arg.description());
				replicateNames.add(arg.name());
				break;
			case limits:
				method.addArgument(arg,arg.name(),simpleType(arg.type()),arg.description());
				replicateNames.add(arg.name());
				break;
			case ecosystemPar:
			case lifeCyclePar:
				// TODO: groups etc.
				break;
			case ecosystemPop:
			case lifeCyclePop:
			case groupPop:
			case otherGroupPop:
				// TODO: groups etc.
				break;
			case focalAuto:
			case otherAuto:
				method.addArgument(arg,"age", "double", arg.description()+"age");
				replicateNames.add("age");
				method.addArgument(arg,"birthDate", "double", arg.description()+"birth date");
				replicateNames.add("birthDate");
				break;
			case groupPar:
				for (memberInfo mb:membersForFocal.get(E_PARAMETERS)) {
					if (mb.fullType!=null)
						imports.add(mb.fullType);
					method.addArgument(arg,mb.name,mb.type,arg.description()+mb.comment);
					replicateNames.add(mb.name);
				}
				break;
			case focalLtc:
				for (memberInfo mb:membersForFocal.get(E_LTCONSTANTS)) {
					if (mb.fullType!=null)
						imports.add(mb.fullType);
					method.addArgument(arg,mb.name,mb.type,arg.description()+mb.comment);
					replicateNames.add(mb.name);
				}
				break;
			case focalDrv:
				for (memberInfo mb:membersForFocal.get(E_DRIVERS)) {
					if (mb.fullType!=null)
						imports.add(mb.fullType);
					method.addArgument(arg,mb.name,mb.type,arg.description()+mb.comment);
					replicateNames.add(mb.name);
				}
				break;
			case focalDec:
				for (memberInfo mb:membersForFocal.get(E_DECORATORS)) {
					if (mb.fullType!=null)
						imports.add(mb.fullType);
					method.addArgument(arg,mb.name,mb.type,arg.description()+mb.comment);
					replicateNames.add(mb.name);
				}
				// TODO:if readwrite, do otherwise
				break;
			case otherGroupPar:
				for (memberInfo mb:membersForOther.get(E_PARAMETERS)) {
					if (mb.fullType!=null)
						imports.add(mb.fullType);
					method.addArgument(arg,mb.name,mb.type,arg.description()+mb.comment);
					replicateNames.add(mb.name);
				}
				break;
			case otherLtc:
				for (memberInfo mb:membersForOther.get(E_LTCONSTANTS)) {
					if (!ValidPropertyTypes.isPrimitiveType(mb.type))
						imports.add(ValidPropertyTypes.getJavaClassName(mb.type));
					method.addArgument(arg,mb.name,mb.type,arg.description()+mb.comment);
					replicateNames.add(mb.name);
				}
				break;
			case otherDrv:
				for (memberInfo mb:membersForOther.get(E_DRIVERS)) {
					if (mb.fullType!=null)
						imports.add(mb.fullType);
					method.addArgument(arg,mb.name,mb.type,arg.description()+mb.comment);
					replicateNames.add(mb.name);
				}
				break;
			case otherDec:
				for (memberInfo mb:membersForOther.get(E_DECORATORS)) {
					if (mb.fullType!=null)
						imports.add(mb.fullType);
					method.addArgument(arg,mb.name,mb.type,arg.description()+mb.comment);
					replicateNames.add(mb.name);
				}
				// TODO:if readwrite, do otherwise
				break;
			case focalLoc:
			case otherLoc:
				method.addArgument(arg,arg.name(),simpleType(arg.type()),arg.description());
				replicateNames.add(arg.name());
				break;
			case nextFocalDrv:
				String argt = function.id()+"."+ initialUpperCase(arg.name());
//						Strings.toUpperCase(arg.name().substring(0,1))+arg.name().substring(1);
				method.addArgument(arg,arg.name(),argt,arg.description());
				replicateNames.add(arg.name());
				break;
			case nextFocalLoc:
				method.addArgument(arg,arg.name(),arg.type(),arg.description());
				replicateNames.add(arg.name());
				break;
			}
		}
		return method; // contains the arguments in the order in which they should be called
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
		for (ModelMethodGenerator m:methods.values())
			result += m.asText(indent);
//		if (rawMethods!=null)
//			for (String s:rawMethods)
//				result += s+"\n";
		result += "}\n"; // 1
		return result;
	}

	public String className() {
		return className;
	}

	public String generatedClassName() {
		return generatedClassName;
	}

	public File getFile() {
		String name = generatedClassName.replace(this.packageName+".", "");
		String path = packagePath+File.separator+name;
		return new File(path+".java");
	}

}
