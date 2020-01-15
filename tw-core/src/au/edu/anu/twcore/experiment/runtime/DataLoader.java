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

import fr.cnrs.iees.properties.SimplePropertyList;

/**
 * <p>A DataLoader is a class that loads data from a <em>single</em> source (usually
 * a StringList) into a <em>single</em> 3Worlds data storage object of the {@linkplain SimplePropertyList} class.
 * The data source is usually a chunk of a file extracted by a MultipleDataLoader. In other 
 * words:</p>
 * 
 * 1 file chunk <---> 1 DataLoader <---> 1 SimplePropertyList (actually {@linkplain TwData})
 * 
 * <p>A new DataLoader class should be defined for any new file format
 * used to input data into 3Worlds. The descendant class must link to the data source in its constructor.
 * The class is meant to be used with a {@linkplain MultipleDataLoader}.
 * cf as an implementation example {@linkplain CsvDataLoader}.</p>
 * <p>The T parameter defines the return type of the load() method - in 3Worlds it is usually TwData,
 * but any other implementation of SimplePropertyList could be used. This means it is possible to use
 * for example the {@link CsvDataLoader} to load node properties from a csv file.</p> 
 * 
 * @see MultipleDataLoader
 * @see CsvDataLoader
 * @see SimplePropertyList
 * 
 * @author Jacques Gignoux - 12/2/2012 refactored 23/2/2017 refactored 9 oct. 2019
 *
 */
public interface DataLoader<T extends SimplePropertyList> {

	  /**
     * Load data from a source defined in its constructor (eg a file or a string or stringlist)
     * and returns the same object filled with data.
     * 
     * @param data - the {@linkplain SimplePropertyList} object in which all the data will be loaded
     * @return data, with all the data loaded inside.
     */
	public T load(SimplePropertyList data);
	
}
