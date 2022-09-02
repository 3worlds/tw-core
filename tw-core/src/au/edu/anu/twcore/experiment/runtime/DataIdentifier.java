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

import java.util.Collection;

import au.edu.anu.twcore.data.runtime.DataLabel;

/**
 * A class to generate temporary identifier for data loaded from files. 
 * 
 * @author Jacques Gignoux - 5 nov. 2021
 *
 */
public class DataIdentifier extends DataLabel {
	
	private static int cindex = 0;
	private static int gindex = 1;
	private static int lcindex = 2;

	public DataIdentifier(String componentId, String groupId, String lifeCycleId) {
		super(componentId,groupId,lifeCycleId);
	}
	
	public DataIdentifier(String... labelParts) {
		super();
		if (labelParts.length==3)
			for (String lab : labelParts)
				label.add(lab);
		else
			throw new IllegalArgumentException("A DataIdentifier must have three label parts");
	}

	public DataIdentifier(Collection<String> labelParts) {
		super();
		if (labelParts.size()==3)
			for (String lab : labelParts)
				label.add(lab);
		else
			throw new IllegalArgumentException("A DataIdentifier must have three label parts");
	}

	public String componentId() {
		return label.get(cindex);
	}
	
	public void setComponentId(String id) {
		label.set(cindex,id);
	}
	
	public String groupId() {
		return label.get(gindex);
	}
	
	public String lifeCycleId() {
		return label.get(lcindex);
	}
	
	public boolean isEmpty() {
		if (((componentId()==null)||(componentId().isEmpty()))	&&
			((groupId()==null)	  ||(groupId().isEmpty()))		&&
			((lifeCycleId()==null)||(lifeCycleId().isEmpty()))	)
			return true;
		return false;
	}
	
}
