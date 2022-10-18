package fr.cnrs.iees.twcore.generators.data;

import static fr.ens.biologie.codeGeneration.CodeGenerationUtils.writeFile;

import java.io.File;
import java.util.Collection;
import java.util.EnumSet;

import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.twcore.generators.Partition;
import fr.ens.biologie.codeGeneration.EnumGenerator;
import fr.ens.biologie.codeGeneration.MethodGenerator;

import static fr.ens.biologie.generic.utils.NameUtils.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.get;

/**
 * Generates an enum class for a model with all categories extracted from the model tree.
 * 
 * @author Jacques Gignoux - 7 oct. 2022
 *
 */
public class TwCategoryEnumGenerator extends DataClassGenerator {

	/**
	 * This constructor assumes the category list contains the whole hierarchy of all categories 
	 * of a model [= a multi-tree, or forest]
	 * @param modelName
	 * @param spec the structure node (normally)
	 * @param cats
	 */
	public TwCategoryEnumGenerator(String modelName, 
			TreeGraphDataNode spec) {
		super(modelName, spec);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean generateCode(boolean reportErrors) {
		String fname = initialUpperCase(makeModelJavaName(modelName)+"CategoryLabels");
		EnumGenerator eg = new EnumGenerator(packageName,
			"",
			fname,
			Partition.class.getCanonicalName());
		eg.addInterfaceGenericParameter(Partition.class.getSimpleName(),packageName+"."+fname);
		eg.setImport(EnumSet.class.getCanonicalName());
		
		// make enum constants
		eg.setConstant("_world_"); // a root for all category trees		
		// all partition nodes
		Collection<TreeGraphDataNode> ccs = (Collection<TreeGraphDataNode>) get(spec,
			childTree(),
			selectZeroOrMany(hasTheLabel(N_CATEGORYSET.label())));
		// all category nodes
		Collection<TreeGraphDataNode> cc = (Collection<TreeGraphDataNode>) get(spec,
				childTree(),
				selectZeroOrMany(hasTheLabel(N_CATEGORY.label())));
		for (TreeGraphDataNode p:ccs)
			eg.setConstant(p.id());
		for (TreeGraphDataNode cat:cc)
			eg.setConstant(cat.id());
		
		// build the partition() method
		MethodGenerator mg = eg.getMethod("partition");		
		mg.setReturnType("EnumSet<"+fname+">");		
		StringBuilder sb = new StringBuilder();
		sb.append("switch (this) {\n");
		for (TreeGraphDataNode p:ccs) {
			Collection<TreeGraphDataNode> pcats = (Collection<TreeGraphDataNode>) get(p,
				children(),
				selectZeroOrMany(hasTheLabel(N_CATEGORY.label())));
			sb.append("\t\tcase ").append(p.id()).append(":\n");
			sb.append("\t\t\treturn EnumSet.of(");
			int gni = 0;			
			for (TreeGraphDataNode cat:pcats) {
				if (gni<pcats.size()-1)
					sb.append(cat.id()).append(',');
				else
					sb.append(cat.id());
				gni++;
			}
			sb.append(");\n");
		}
		// default: return empty set
		sb.append("\t\tdefault:\n");
		sb.append("\t\t\treturn EnumSet.noneOf(").append(fname).append(".class);\n");
		sb.append("\t\t}"); // end switch block
		mg.setReturnStatement(sb.toString());
		// write java code to source file
		File file = new File(packagePath+File.separator+fname+".java");
		writeFile(eg,file);
		return false;
	}

}
