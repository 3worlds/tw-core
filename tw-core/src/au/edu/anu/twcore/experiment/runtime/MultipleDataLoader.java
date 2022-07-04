/**************************************************************************
 *  TW-CORE - 3Worlds Core classes and methods                            *
 *                                                                        *
 *  Copyright 2018: Shayne Flint, Jacques Gignoux & Ian D. Davies         *
 *       shayne.flint@anu.edu.au                                          * 
 *       jacques.gignoux@upmc.fr                                          *
 *       ian.davies@anu.edu.au                                            * 
 *                                                                        *
 *  TW-CORE is a library of the principle components required by 3W       *
 *                                                                        *
 **************************************************************************                                       
 *  This file is part of TW-CORE (3Worlds Core).                          *
 *                                                                        *
 *  TW-CORE is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  TW-CORE is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *                         
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with TW-CORE.                                                   *
 *  If not, see <https://www.gnu.org/licenses/gpl.html>                   *
 *                                                                        *
 **************************************************************************/
package au.edu.anu.twcore.experiment.runtime;

import java.util.Map;

import fr.cnrs.iees.properties.SimplePropertyList;
import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.experiment.runtime.io.*;

/**
 * <p>A MultipleDataLoader is a class that loads data from a <em>single</em> source (usually 
 * a file) into <em>multiple</em> 3Worlds internal objects of the {@linkplain SimplePropertyList} 
 * hierarchy (usually {@linkplain TwData}).
 * The results are grouped in a map of objects by String identifiers. In other words:</p>
 * 
 * 1 file {@literal<--->} 1 MultipleDataLoader {@literal <--->} 1 Map{@literal <}String,SimplePropertyList descendant{@literal >} ---{@literal >} <em>n</em> TwData
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
     * 
     * This will only read properties matching the dataModel argument
     * 
     * @param dataModel - a T instance that will be cloned as many times as necessary to receive all 
     * data contained in the file. Clones are then put in the Map argument.
     **/
    public void load(Map<DataIdentifier,T> result, T dataModel);    
    
    /**
     * Loads data into a Map of propertyLists. It uses internally an ExtendablePropertyList and
     * will read all data found in the files, creating properties on demand.
     * 
     * This will read everything and put them in propertylists.
     * 
     * @param result
     */
    public void load(Map<DataIdentifier,T> result);    
	
}
