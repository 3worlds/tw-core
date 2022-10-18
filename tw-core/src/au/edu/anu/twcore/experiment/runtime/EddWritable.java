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

import java.util.List;
import java.util.Map;

import au.edu.anu.omugi.graph.property.Property;
import au.edu.anu.twcore.experiment.ExpFactor;
import au.edu.anu.twcore.experiment.Experiment;
import au.edu.anu.twcore.experiment.runtime.ExperimentDesignDetails;

/**
 * 
 * Writable interface for {@link ExperimentDesignDetails} implementation.
 * <p>
 * Only the {@link Experiment} has access to this interface in order to construct the
 * various collections of data.
 * 
 * @author Ian Davies - 13 Jul 2022
 * 
 * 
 */
public interface EddWritable {
	/**
	 * Returns an editable map of experiment factors ({@link ExpFactor}). The map
	 * 'key' is the alias name of the factor, that is the edge Id between the
	 * Treatment node and the Field being manipulated in the configuration graph.
	 * 
	 * @return editable map of experiment factors ({@link ExpFactor}).
	 */
	public Map<String, ExpFactor> factors();

	/**
	 * Returns an editable list of properties: one list for each simulator. Each
	 * simulation sets its property (constants) values to the properties in the
	 * list after initialization.
	 * 
	 * @return Editable list of Properties and their values.
	 */
	public List<List<Property>> treatments();

	/**
	 * Returns an editable list of simulator constants. This list is built by the
	 * simulator when first initialized. The values may be subsequently changed by
	 * the experiment treatments following initialization.
	 * 
	 * @return Editable list of baseline values for these simulators.
	 */
	public Map<String, Object> baseline();

}
