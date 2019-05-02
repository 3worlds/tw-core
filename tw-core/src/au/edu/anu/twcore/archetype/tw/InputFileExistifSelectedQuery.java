package au.edu.anu.twcore.archetype.tw;

import java.io.File;

import au.edu.anu.rscs.aot.graph.property.Property;
import au.edu.anu.rscs.aot.queries.Query;
import fr.ens.biologie.threeWorlds.core.ModelRunner;
import fr.ens.biologie.threeWorlds.resources.core.constants.FileType;
import fr.ens.biologie.threeWorlds.resources.core.constants.ProjectPaths;

/**
 * checks an input file is present in local/models subdir of 3w repo
 * @author gignoux - 24 févr. 2017
 * 
 * Same as InputFileExists but if File is null its statisfied.
 * Used for initialState file where it is valid not to have a file selected 
 * but if the selected file does not exist, we need a warning.
 *
 */
/**
 * @author ian
 *
 */
public class InputFileExistifSelectedQuery extends Query implements ProjectPaths {
	
	@Override
	public Query process(Object input) { // input is a property 
		defaultProcess(input);
		Property localItem = (Property) input;
		File s = ((FileType) localItem.getValue()).getFile();
		if (s==null)
			satisfied = true;
		if (s!=null && s.exists())
			satisfied = true;
		if (s!=null && ModelRunner.getProjectResource(s.getName())!=null)
			satisfied = true;
		return this;
	}

	public String toString() {
		return "[" + stateString() + " File must exist ";
	}

}
