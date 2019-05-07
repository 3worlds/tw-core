package au.edu.anu.twcore.archetype.tw;

import java.io.File;

import au.edu.anu.rscs.aot.graph.property.Property;
import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.rscs.aot.util.Resources;
import fr.cnrs.iees.twcore.constants.FileType;

/**
 * checks an input file is present in local/models subdir of 3w repo
 * @author gignoux - 24 févr. 2017
 *
 */
public class InputFileExistQuery extends Query{
	
	@Override
	public Query process(Object input) { // input is a property 
		defaultProcess(input);
		Property localItem = (Property) input;
		File s = ((FileType) localItem.getValue()).getFile();
		if (s!=null && s.exists())
			satisfied = true;
		// TODO need to handle jars!!
		if (s!=null && Resources.getFile(s.getName())!=null)
			satisfied = true;
		return this;
	}

	public String toString() {
		return "[" + stateString() + " File must exist ";
	}

}
