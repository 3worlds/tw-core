package fr.cnrs.iees.twcore.generators.data;

import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.properties.ExtendablePropertyList;
import fr.cnrs.iees.twcore.constants.DataElementType;
import fr.ens.biologie.codeGeneration.ClassGenerator;
import fr.ens.biologie.codeGeneration.MethodGenerator;
import fr.ens.biologie.generic.utils.Logging;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static fr.ens.biologie.codeGeneration.CodeGenerationUtils.writeFile;
import static fr.ens.biologie.generic.utils.NameUtils.*;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import au.edu.anu.rscs.aot.collections.tables.IntTable;
import au.edu.anu.rscs.aot.collections.tables.Table;

import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.generators.TwComments.*;
import static au.edu.anu.twcore.DefaultStrings.*;

/**
 * 
 * @author Jacques Gignoux - 1 févr. 2021
 *
 */
public class TwDataInterfaceGenerator extends DataClassGenerator {
	
	private static Logger log = Logging.getLogger(TwDataInterfaceGenerator.class);
	private static Map<String,String> edgeLabels = new HashMap<>();
	private static Map<String,String> prefixes = new HashMap<>();
	static {
		edgeLabels.put(P_DRIVERCLASS.key(), E_DRIVERS.label());
		edgeLabels.put(P_DECORATORCLASS.key(), E_DECORATORS.label());
		edgeLabels.put(P_CONSTANTCLASS.key(), E_CONSTANTS.label());
		prefixes.put(P_DRIVERCLASS.key(), "Drv");
		prefixes.put(P_DECORATORCLASS.key(), "Dec");
		prefixes.put(P_CONSTANTCLASS.key(), "Cnt");
	}

	public TwDataInterfaceGenerator(String modelName,TreeGraphDataNode spec) {
		super(modelName,spec);
	}
	
	@SuppressWarnings("unchecked")
	private void generateInterface(TreeGraphDataNode recSpec, String className, String classComment) {
		String[] comment = new String[1];
		comment[0] = classComment;
		ClassGenerator cg = new ClassGenerator(packageName,comment(comment),className,true,null,null);
		Collection<TreeGraphDataNode> fields = (Collection<TreeGraphDataNode>) get(recSpec.getChildren(), 
			selectZeroOrMany(hasTheLabel(N_FIELD.label())));
		for (TreeGraphDataNode field:fields) {
			String fname = field.id();
			String ftype = null;
			DataElementType det = (DataElementType) field.properties().getPropertyValue(P_FIELD_TYPE.key());
			if (det.asPrimitive()==null)
				ftype = det.name();
			else
				ftype = det.asPrimitive();
			// specific getter
			MethodGenerator m = new MethodGenerator("public",true,ftype,fname);
			cg.setMethod("get"+fname, m);
			// the usual setter
			m = new MethodGenerator("public",true,"void",fname,ftype);
			cg.setMethod("set"+fname, m);
		}
		Collection<TreeGraphDataNode> tables = (Collection<TreeGraphDataNode>) get(recSpec.getChildren(), 
			selectZeroOrMany(hasTheLabel(N_TABLE.label())));
		for (TreeGraphDataNode table:tables) {
			String tname = table.id();
			String ttype = null;
			String tpack = null;
			if (table.properties().hasProperty(P_DATAELEMENTTYPE.key())) {
				DataElementType det = (DataElementType) table.properties().getPropertyValue(P_DATAELEMENTTYPE.key());
				String t = det.name();
				if (t.equals("Integer")) {
					ttype = IntTable.class.getSimpleName();
					tpack = IntTable.class.getCanonicalName();
				}
				else {
					ttype = t+"Table";
					tpack = Table.class.getPackageName()+"."+ttype;
				}
			}
			else {
//				TreeGraphDataNode childRec = (TreeGraphDataNode) get(table,
//					children(),
//					selectOne(hasTheLabel(N_RECORD.label())));
//				ttype = validJavaName(initialUpperCase(wordUpperCaseName(childRec.id())));
				// TODO: improve this
				ttype = Table.class.getSimpleName();
				tpack = Table.class.getCanonicalName();
			}
			// specific getter
			MethodGenerator m = new MethodGenerator("public",true,ttype,tname);
			cg.setMethod("get"+tname, m);
			// no setter
			// import for the table class
			cg.setImport(tpack);
		}
		log.info("    generating file "+className+".java ...");
		File file = new File(packagePath+File.separator+className+".java");
		writeFile(cg,file);
		log.info("  ...done.");
	}
	
	// helper method for below
	private boolean setDataClassName(ExtendablePropertyList catProps, String dataGroup) {
		boolean changed = false;
		if (!catProps.hasProperty(dataGroup)) {
			catProps.addProperty(dataGroup,null);
			changed = true;
		}
		String oldClassName = (String) catProps.getPropertyValue(dataGroup);
		String newClassName = null;
		TreeGraphDataNode recSpec = (TreeGraphDataNode) get(spec.edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(edgeLabels.get(dataGroup))),
			endNode());
		if (recSpec!=null) {
			newClassName = validJavaName(initialUpperCase(wordUpperCaseName(spec.id()))) 
				+ defaultPrefix + prefixes.get(dataGroup);
			String classComment = "Data interface for "+edgeLabels.get(dataGroup)+" of category "+spec.id();
			generateInterface(recSpec,newClassName,classComment);
		}
		if (oldClassName!=null) {
			if (!oldClassName.equals(newClassName)) {
				catProps.setProperty(dataGroup, newClassName);
				changed = true;
			}
		}
		else if (newClassName!=null)
			if (!newClassName.equals(oldClassName)) {
				catProps.setProperty(dataGroup, newClassName);
				changed=true;
			}
		return changed;
	}
	
	@Override
	public boolean generateCode(boolean reportErrors) {
		ExtendablePropertyList catProps = (ExtendablePropertyList) spec.properties();
		boolean cd1, cd2, cd3;
		cd1 = setDataClassName(catProps,P_DRIVERCLASS.key());
		cd2 = setDataClassName(catProps,P_DECORATORCLASS.key());
		cd3 = setDataClassName(catProps,P_CONSTANTCLASS.key());
		return (cd1||cd2||cd3);
	}

}
