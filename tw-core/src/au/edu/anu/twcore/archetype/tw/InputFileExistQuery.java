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
 */
public class InputFileExistQuery extends Query implements ProjectPaths {
	
	@Override
	public Query process(Object input) { // input is a property 
		defaultProcess(input);
		Property localItem = (Property) input;
		//String s = (String) localItem.getValue();
		//Ian (24/09/17) its important for
		// property editing to know the type is a File
		// rather than an arbitary string.
//		File s = (File) localItem.getValue();
		File s = ((FileType) localItem.getValue()).getFile();
//		File f = Project.makeFile(PROJECT_MODEL_GRAPHS,s.getPath());
		// JG 7/12/2017: search for file in various locations
//		if (FileFinder.fileLocation(s)!=null)
//			satisfied=true;
		// 16/5/2018 if the file was properly selected with MM, then it must be in .3w/<project> 
		if (s!=null && s.exists())
			satisfied = true;
		// JG 4/4/2018 - to handle jars.
		if (s!=null && ModelRunner.getProjectResource(s.getName())!=null)
			satisfied = true;
		return this;
	}

	public String toString() {
		return "[" + stateString() + " File must exist ";
	}

}
