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
package au.edu.anu.twcore.ecosystem.runtime.stop;

import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;

/**
 * An abstract ancestor for all stopping conditions that involve checking a property value,
 * ie conditions not based on time.
 * @author gignoux - 7 mars 2017
 *
 */
public abstract class PropertyStoppingCondition extends AbstractStoppingCondition {

	protected String pname = null;
	private ReadOnlyPropertyList plist = null;
	
	public PropertyStoppingCondition(String stopVariable, 
			ReadOnlyPropertyList system) {
		super();
		pname = stopVariable;
		plist = system;
		if (plist==null)
			throw new TwcoreException("This stopping condition requires a non-null system to track");
	}

	protected double getVariable() {
		if (plist==null) {
			// TODO! find the property list in which to search !
			throw new TwcoreException("This stopping condition requires a non-null system to track");
		}
		if (plist.getPropertyClass(pname).equals(Double.class))
			return (double) plist.getPropertyValue(pname);
		else if (plist.getPropertyClass(pname).equals(Integer.class))
			return 1.0*(int) plist.getPropertyValue(pname);
		else if (plist.getPropertyClass(pname).equals(Boolean.class))
			return ((boolean) plist.getPropertyValue(pname))?1.0:0.0;
		else if (plist.getPropertyClass(pname).equals(Long.class))
			return 1.0*(long) plist.getPropertyValue(pname);
		else if (plist.getPropertyClass(pname).equals(Float.class))
			return (float) plist.getPropertyValue(pname);
		else if (plist.getPropertyClass(pname).equals(Short.class))
			return 1.0*(short) plist.getPropertyValue(pname);
		else if (plist.getPropertyClass(pname).equals(Byte.class))
			return 1.0*(byte) plist.getPropertyValue(pname);
		throw new TwcoreException("The stopping property type is not compatible with doubles");
	}
	
}
