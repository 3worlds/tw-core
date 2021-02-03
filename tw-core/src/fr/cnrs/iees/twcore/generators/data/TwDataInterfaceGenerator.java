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
import java.util.logging.Logger;

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
	private static String constants =  "Cnt";
	private static String decorators = "Dec";
	private static String drivers =  "Drv";

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
			log.info("    generating file "+className+".java ...");
			File file = new File(packagePath+File.separator+className+".java");
			writeFile(cg,file,className);
			log.info("  ...done.");
		}
	}
	
	@Override
	public boolean generateCode() {
		boolean result = false;
		String classComment = "";
		String className = "";
		// prepare Category node to receive names for generated classes
		ExtendablePropertyList catProps = (ExtendablePropertyList) spec.properties();
		if (!catProps.hasProperty(P_DRIVERCLASS.key())) {
			catProps.addProperty(P_DRIVERCLASS.key(),null);
			result = true;
		}
		if (!catProps.hasProperty(P_DECORATORCLASS.key())) {
			catProps.addProperty(P_DECORATORCLASS.key(),null);
			result = true;
		}
		if (!catProps.hasProperty(P_CONSTANTCLASS.key())) {
			catProps.addProperty(P_CONSTANTCLASS.key(),null);
			result = true;
		}
//		GraphState.setChanged();
		TreeGraphDataNode recSpec = (TreeGraphDataNode) get(spec.edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(E_DRIVERS.label())),
			endNode());
		if (recSpec!=null) {
			className = validJavaName(initialUpperCase(wordUpperCaseName(spec.id()))) 
				+ defaultPrefix + drivers;
			classComment = "Data interface for "+E_DRIVERS.label()+" of category "+spec.id();
			generateInterface(recSpec,className,classComment);
			if (!className.equals(catProps.getPropertyValue(P_DRIVERCLASS.key()))) {
				catProps.setProperty(P_DRIVERCLASS.key(),className);
				result = true;
			}
		}
		recSpec = (TreeGraphDataNode) get(spec.edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(E_DECORATORS.label())),
			endNode());
		if (recSpec!=null) {
			className = validJavaName(initialUpperCase(wordUpperCaseName(spec.id()))) 
				+ defaultPrefix + decorators;
			classComment = "Data interface for "+E_DECORATORS.label()+" of category "+spec.id();
			generateInterface(recSpec,className,classComment);
			if (!className.equals(catProps.getPropertyValue(P_DECORATORCLASS.key()))) {
				catProps.setProperty(P_DECORATORCLASS.key(),className);
				result = true;
			}
		}
		recSpec = (TreeGraphDataNode) get(spec.edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(E_CONSTANTS.label())),
			endNode());
		if (recSpec!=null) {
			className = validJavaName(initialUpperCase(wordUpperCaseName(spec.id()))) 
				+ defaultPrefix + constants;
			classComment = "Data interface for "+E_CONSTANTS.label()+" of category "+spec.id();
			generateInterface(recSpec,className,classComment);
			if (!className.equals(catProps.getPropertyValue(P_CONSTANTCLASS.key()))) {
				catProps.setProperty(P_CONSTANTCLASS.key(),className);
				result = true;
			}
		}
		
		// TODO: complete code (ignore tables for the moment)
		return result;
	}

}
