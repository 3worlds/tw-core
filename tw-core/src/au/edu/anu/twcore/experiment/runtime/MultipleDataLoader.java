package au.edu.anu.twcore.experiment.runtime;

import java.util.Map;

import fr.cnrs.iees.properties.SimplePropertyList;

/**
 * <p>A MultipleDataLoader is a class that loads data from a <em>single</em> source (usually 
 * a file) into <em>multiple</em> 3Worlds internal objects of the {@linkplain SimplePropertyList} 
 * hierarchy (usually {@linkplain TwData}).
 * The results are grouped in a map of objects by String identifiers. In other words:</p>
 * 
 * 1 file <---> 1 MultipleDataLoader <---> 1 Map<String,SimplePropertyList descendant> ---> <em>n</em> TwData
 * 
 * <p>A new MultipleDataLoader class should be defined for any new file format
 * used to input data into 3Worlds. The implementing class must link to the data source in its constructor.
 * cf as an example {@linkplain TableDataLoader}.</p>
 * <p>The identification of separate TwData items in the data files assumes the
 * existence of some unique identifier field in the data, that must be specified in
 * the configuration graph structure.</p>
 *
 * 
 * @author Jacques Gignoux - 13/2/2012 refactored 23/2/2017 7/12/2017 9/10/2019
 * @see TableDataLoader
 * @see PropertyDataLoader
 * @see TwData
 * @see SimplePropertyList
 * 
 */
public interface MultipleDataLoader<T extends SimplePropertyList> {

    /** loads data into a Map of TwData (or other SimplePropertyList implementation, specified by T)
     * indexed by String identifier (typically names of nodes).
     * @param dataModel - a T instance that will be cloned as many times as necessary to receive all 
     * data contained in the file. Clones are then put in the Map argument.
     **/
    public void load(Map<String,T> result, T dataModel);    
	
}
