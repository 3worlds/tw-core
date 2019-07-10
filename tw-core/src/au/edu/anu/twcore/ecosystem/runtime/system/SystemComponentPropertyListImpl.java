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
package au.edu.anu.twcore.ecosystem.runtime.system;

import static au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent.*;

import java.util.Map;
import java.util.Set;

import au.edu.anu.twcore.data.runtime.TwData;
import fr.cnrs.iees.properties.PropertyListSetters;
import fr.cnrs.iees.properties.SimplePropertyList;

/**
 * <p>
 * A 3worlds specific propertyList for all data contained in SystemComponent.
 * </p>
 * <ul>
 * <li>parameters : read only, shared among many systems, not changing over a
 * simulation</li>
 * <li>automatic variables (age, birthdate, species - maybe demographic stage) :
 * read only, owned by the system, changing internally during a simulation</li>
 * <li>'primary' state variables (or 'drivers') : read/write, owned by the
 * system, changing over a simulation and driving the change. Two copies to
 * avoid user error: either current and next, or current and delta (different
 * update methods).</li>
 * <li>'secondary' state variables (or 'decorators') : read/write, changing over
 * a simulation as a result of change in primary state variables</li>
 * </ul>
 * <p>
 * You must always have one driver. No Delta. Next state style.
 * </p>
 * <p>
 * NB from AOT, only the current state is visible
 * </p>
 *
 * 
 * @author gignoux - 20 févr. 2017<br/>
 * 
 * 
 */
public class SystemComponentPropertyListImpl implements SimplePropertyList {

	// 3Worlds side
	/** automatic state variables */
	private SystemData autoState = null;
	/** state variables that drive the dynamics */
	private TwData[] drivers = null;
	/** state variables that undergo the dynamics */
	private TwData decorators = null;

	// AOT side
	// NB: only current state is visible
	/** map of TwData indices by keys */
	private Map<String, Integer> propertyMap = null;
	/** array of TwData */
	private TwData[] properties = null;
	protected static int DRIVERS = 0;
	protected static int AUTO = 1;
	protected static int DECO = 2;

	/**
	 * Constructor
	 * 
	 * @param parameters
	 * @param variables
	 * @param depth
	 *            the depth in past (could be zero)
	 * @param propertyMap
	 */
	protected SystemComponentPropertyListImpl(TwData driverVariables, 
			TwData decoratorVariables, 
			int depth,
			Map<String, Integer> propertyMap) {
		super();
		// 3worlds side
		this.autoState = new SystemData();
		drivers = new TwData[depth];
		if (driverVariables == null)
			for (int i = 0; i < drivers.length; i++)
				drivers[i] = null;
		else
			for (int i = 0; i < drivers.length; i++)
				drivers[i] = driverVariables.clone();
		if (decoratorVariables != null)
			decorators = decoratorVariables.clone();
		// graph side - a flat propertyList
		this.propertyMap = propertyMap; // shared (save memory space)
		properties = new TwData[3];
		properties[DRIVERS] = drivers[CURRENT];
		properties[AUTO] = autoState;
		properties[DECO] = decorators;
	}

	// Graph Side - slow access but as any node.
	// ======================================================

	@Override
	public PropertyListSetters setProperty(String key, Object value) {
		properties[propertyMap.get(key)].setProperty(key, value);
		return this;
	}

	@Override
	public Object getPropertyValue(String key) {
		return properties[propertyMap.get(key)].getPropertyValue(key);
	}

	@Override
	public boolean hasProperty(String key) {
		return properties[propertyMap.get(key)].hasProperty(key);
	}

	@Override
	public Set<String> getKeysAsSet() {
		return propertyMap.keySet();	}

	@Override
	public int size() {
		return propertyMap.size();
	}

	protected SystemComponentPropertyListImpl cloneStructure() {
		SystemComponentPropertyListImpl result = new SystemComponentPropertyListImpl(/* parameters, */
				drivers[0], decorators, drivers.length, propertyMap);
		return result;
	}

	@Override
	public SimplePropertyList clone() {
		SystemComponentPropertyListImpl result = cloneStructure();
		if (autoState != null)
			result.autoState.setProperties(autoState);
		if (decorators != null)
			result.decorators.setProperties(decorators);
		for (int i = 0; i < drivers.length; i++)
			if (result.drivers[i] != null) //Ian 15/2/2018 if category has not TwData then these are null
				result.drivers[i].setProperties(drivers[i]);
		return result;
	}

	@Override
	public final SystemComponentPropertyListImpl fillWith(Object value) {
		for (int i = 0; i < properties.length; i++)
			properties[i].fillWith(value);
		return this;
	}
	
	// 3Worlds Side - for user & simulator interaction.
	// ======================================================

	protected SystemData auto() {
		return autoState;
	}

	protected TwData decorators() {
		return decorators;
	}

	protected TwData[] drivers() {
		return drivers;
	}
	
}
