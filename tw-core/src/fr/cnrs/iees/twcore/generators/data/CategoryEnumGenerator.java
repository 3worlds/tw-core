package fr.cnrs.iees.twcore.generators.data;

import static fr.ens.biologie.codeGeneration.CodeGenerationUtils.writeFile;

import java.io.File;
import java.util.Collection;

import fr.cnrs.iees.graph.impl.TreeGraphDataNode;

/**
 * Generates an enum class for a model with all categories extracted from the model tree.
 * 
 * @author Jacques Gignoux - 7 oct. 2022
 *
 */
public class CategoryEnumGenerator extends DataClassGenerator {


	private Collection<TreeGraphDataNode> categories = null;

	/**
	 * This constructor assumes the category list contains the whole hierarchy of all categories 
	 * of a model [= a multi-tree, or forest]
	 * @param modelName
	 * @param spec
	 * @param cats
	 */
	public CategoryEnumGenerator(String modelName, 
			TreeGraphDataNode spec, 
			Collection<TreeGraphDataNode> cats) {
		super(modelName, spec);
		categories = cats;
	}

	@Override
	public boolean generateCode(boolean reportErrors) {
		String fname = makeModelJavaName(modelName)+"CategoryLabels";
		EnumGenerator eg = new EnumGenerator("package",
			"comment",
			fname,
			"Partition");
		for (TreeGraphDataNode cat:categories)
			eg.setConstant(cat.id());
		File file = new File(packagePath+File.separator+fname+".java");
		writeFile(eg,file);
		return false;
	}

}
