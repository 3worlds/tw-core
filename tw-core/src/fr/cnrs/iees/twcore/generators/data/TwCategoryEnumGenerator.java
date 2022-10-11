package fr.cnrs.iees.twcore.generators.data;

import static fr.ens.biologie.codeGeneration.CodeGenerationUtils.writeFile;

import java.io.File;
import java.util.Collection;

import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.twcore.generators.Partition;
import fr.ens.biologie.codeGeneration.EnumGenerator;
import static fr.ens.biologie.generic.utils.NameUtils.*;
/**
 * Generates an enum class for a model with all categories extracted from the model tree.
 * 
 * @author Jacques Gignoux - 7 oct. 2022
 *
 */
public class TwCategoryEnumGenerator extends DataClassGenerator {


	private Collection<TreeGraphDataNode> categories = null;

	/**
	 * This constructor assumes the category list contains the whole hierarchy of all categories 
	 * of a model [= a multi-tree, or forest]
	 * @param modelName
	 * @param spec
	 * @param cats
	 */
	public TwCategoryEnumGenerator(String modelName, 
			TreeGraphDataNode spec, 
			Collection<TreeGraphDataNode> cats) {
		super(modelName, spec);
		categories = cats;
	}

	@Override
	public boolean generateCode(boolean reportErrors) {
		String fname = initialUpperCase(makeModelJavaName(modelName)+"CategoryLabels");
		EnumGenerator eg = new EnumGenerator(packageName,
			"",
			fname,
			Partition.class.getCanonicalName());
		eg.setConstant("_world_"); // a root for all category trees
		for (TreeGraphDataNode cat:categories)
			eg.setConstant(cat.id());
		
		// partition return type must be EnumSet<fname>
		eg.getMethod("partition").setReturnType("EnumSet<"+fname+">");
		
		
		File file = new File(packagePath+File.separator+fname+".java");
		writeFile(eg,file);
		return false;
	}

}
