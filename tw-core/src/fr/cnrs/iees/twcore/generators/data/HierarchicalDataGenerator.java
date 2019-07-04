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

import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.project.ProjectPaths;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.twcore.constants.DataElementType;
import fr.cnrs.iees.twcore.generators.TwCodeGenerator;
import fr.ens.biologie.codeGeneration.ClassGenerator;
import fr.ens.biologie.codeGeneration.JavaCompiler;

/**
 * <p>Implements the recursive generation of nested record and table data classes.
 * Details of code generated are left to descendants.</p>
 *
 * @author Jacques Gignoux - dec. 2014
 */
public abstract class HierarchicalDataGenerator 
	extends TwCodeGenerator
	implements ProjectPaths {
	
	private Logger log = Logger.getLogger(HierarchicalDataGenerator.class.getName()); 
	
	/** the name of the class to generate */
	protected String className = null;
	/** the name of the package in which the class will be generated (ie with "." as separators) */
	protected String packageName = null;
	/** the directory name matching package name */
	protected String packagePath = null;
	/** the model name (matching the ecology node name */
	protected String modelName = null;		
	/** the compiler used to compile the generated classes */
	private JavaCompiler compiler = new JavaCompiler();
	
	private File rootDir = Project.makeFile();  // TODO check the root dir is OK
	
	private boolean hadErrors = false;

	protected abstract ClassGenerator getRecordClassGenerator(String className,String comment);
	
	protected abstract ClassGenerator getTableClassGenerator(String className, String contentType,String comment);
	
	protected HierarchicalDataGenerator(String modelName,TreeGraphDataNode spec) {
		super(spec);
		this.modelName = modelName;
		packageName = validJavaName(wordUpperCaseName(modelName))+"."+TW_CODE;	
		packagePath = Project.makeFile(validJavaName(wordUpperCaseName(modelName)),TW_CODE).getAbsolutePath();
	}
	
	private final String generateRecordCode(TreeGraphDataNode spec) {
		String cn = validJavaName(initialUpperCase(wordUpperCaseName(spec.id())));
		String comment = comment(general,classComment(cn),generatedCode(false,modelName, ""));		
		ClassGenerator cg = getRecordClassGenerator(cn,comment);
		headerCode(cg,cn);
		for (TreeNode ff:spec.getChildren()) {
			TreeGraphDataNode f = (TreeGraphDataNode) ff;
			String fname = validJavaName(wordUpperCaseName(f.id()));
			String ftype = null;
			if (f.properties().hasProperty(P_DATAELEMENTTYPE.toString())) {
				DataElementType det = (DataElementType)f.properties().getPropertyValue(P_DATAELEMENTTYPE.toString());
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
		hadErrors = hadErrors | compiler.compileCode(file,rootDir);

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
		if (spec.properties().hasProperty(P_DATAELEMENTTYPE.toString())) {
			DataElementType tet = (DataElementType) spec.properties().getPropertyValue(P_DATAELEMENTTYPE.toString());
			String t = tet.name();
			if (t.equals("Integer")) { 
				ftype = "IntTable";
				fpack = "au.edu.anu.rscs.aot.collections.tables.IntTable";
			}
			else {
				ftype = t+"Table";
				fpack = "au.edu.anu.rscs.aot.collections.tables."+t+"Table";
			}
			// no code to generate here ! just use a predefined table ! 
			log.info("    generating reference to "+fpack+" ...");
//			spec.setProperty("class", fpack);
			log.info("  ...done.");
		}
		else { // this must be a record - superclass will be generated from the record name, but doesnt exist yet !			
			TreeGraphDataNode rec = (TreeGraphDataNode) get(spec, 
				children(), 
				selectOne(hasTheLabel(N_RECORD.toString())));
			generateRecordCode(rec);
			ftype = validJavaName(initialUpperCase(wordUpperCaseName(spec.id())));
			log.info("    generating file "+ftype+".java ...");
			fpack = packageName+"."+ftype;
			String contentType = validJavaName(initialUpperCase(wordUpperCaseName(rec.id())));
			String comment = comment(general,classComment(fname),generatedCode(false,modelName, ""));		
			ClassGenerator cg = getTableClassGenerator(ftype,contentType,comment);	
			tableCode(cg,ftype,contentType,dims);
//			File file = Project.makeFile(PROJECT_FILES,
//				validJavaName(wordUpperCaseName(modelName))+File.separator+THREE_WORLDS_CODE,
//				ftype+".java");
			File file = new File(packagePath+File.separator+ftype+".java");
			writeFile(cg,file,ftype);
			hadErrors = hadErrors | compiler.compileCode(file,rootDir);

//			spec.setProperty("class", packageName+"."+ftype);
			log.info("  ...done.");
		}
		if (parentCG!=null) {
			parentCG.setImport(fpack);
			parentCG.setImport("au.edu.anu.rscs.aot.collections.tables.Dimensioner");
			tableInitCode(parentCG,fname,ftype,dims);
		}		
		return ftype;
	}

	protected abstract void tableInitCode(ClassGenerator cg,String fname,String ftype,Iterable<TreeGraphDataNode> dimList);
	protected abstract void tableCode(ClassGenerator cg,String ftype,String contentType,Iterable<TreeGraphDataNode> dimList);

	@Override
	public final boolean generateCode() {		
		if (spec.classId().equals(N_RECORD.toString())) 
			className = packageName+"."+generateRecordCode(spec);
		else if (spec.classId().equals(N_TABLE.toString())) 
			className = packageName+"."+generateTableCode(spec,null);
		return hadErrors;
	}
	
	public final String generatedClassName() {
		return className;
	}

}
