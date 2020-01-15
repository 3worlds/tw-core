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
package au.edu.anu.twcore.ecosystem.runtime.process;

import au.edu.anu.twcore.data.runtime.TwData;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;

/**
 * A class to pass context data between a Process and its functions
 * 
 * @author Jacques Gignoux - 19 sept. 2019
 *
 */
public class HierarchicalContext {

	public TwData ecosystemParameters = null;
	public TwData ecosystemVariables = null;
	public ReadOnlyPropertyList ecosystemPopulationData = null;
	public String ecosystemName = null;
	public TwData lifeCycleParameters = null;
	public TwData lifeCycleVariables = null;
	public ReadOnlyPropertyList lifeCyclePopulationData = null;
	public String lifeCycleName = null;
	public TwData groupParameters = null;
	public TwData groupVariables = null;
	public ReadOnlyPropertyList groupPopulationData = null;
	public String groupName = null;
	
	public HierarchicalContext() {
		super();
	}
	
	void clear() {
		ecosystemParameters = null;
		ecosystemVariables = null;
		ecosystemPopulationData = null;
		ecosystemName = null;
		lifeCycleParameters = null;
		lifeCycleVariables = null;
		lifeCyclePopulationData = null;
		lifeCycleName = null;
		groupParameters = null;
		groupVariables = null;
		groupPopulationData = null;
		groupName = null;
	}

}
