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
package au.edu.anu.twcore.data.runtime;

import java.util.List;
import java.util.Set;

import fr.cnrs.iees.omugi.graph.property.Property;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimpleWriteProtectablePropertyList;

/**
 * an ancestor class for 3Worlds data structures, meant to be used as a PropertyList in some 
 * Nodes 
 * 
 * @author gignoux - 16 févr. 2017
 *
 */
public abstract class TwData 
		implements SimpleWriteProtectablePropertyList {
	
	// PropertyListGetters  methods
	private boolean readOnly = false;
	
	@Override
	public final TwData setProperty(Property property) {
		setProperty(property.getKey(),property.getValue());
		return this;
	}

	@Override
	public abstract TwData setProperty(String key, Object value);

	@Override
	public final TwData setProperties(ReadOnlyPropertyList propertyList) {
		for (String key:propertyList.getKeysAsSet())
			setProperty(key,propertyList.getPropertyValue(key));
		return this;
	}

	@Override
	public final TwData setProperties(List<String> keys, List<Object> values) {
		for (String key:keys)
			setProperty(key,values.iterator().next());
		return this;
	}

	@Override
	public final TwData setProperties(String[] keys, Object[] values) {
		for (int i=0; i<keys.length; i++)
			setProperty(keys[i],values[i]);
		return this;
	}

	@Override
	public final Property getProperty(String key) {
		return new Property(key, getPropertyValue(key));	
	}

	@Override
	public abstract Object getPropertyValue(String key);
	
	@Override
	public abstract boolean hasProperty(String key);

	@Override
	public abstract String propertyToString(String key);

	@Override
	public String getPropertyClassName(String key) {
		return getPropertyClass(key).getName();
	}

	@Override
	public abstract Class<?> getPropertyClass(String key);
	
	@Override
	public abstract Set<String> getKeysAsSet();

	@Override
	public abstract String[] getKeysAsArray();

	// DataContainer  methods
	//

	@Override
	public abstract TwData clone();

	protected abstract TwData cloneStructure();

	@Override
	public abstract TwData clear();

	// does nothing: what could we do ???
	@Override
	public final TwData fillWith(Object value) {
		return this;
	}

	@Override
	public abstract int size();

	@Override
	public final boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public final TwData writeEnable() {
		readOnly = false;
		return this;
	}

	@Override
	public final TwData writeDisable() {
		readOnly = true;
		return this;
	}
	
   @Override
    public String toString() {
	   StringBuilder sb = new StringBuilder(1024);
	   sb.append("(");
        for (String key:getKeysAsSet())
        	sb.append(propertyToString(key)).append(" ");
        sb.append(")");
        return sb.toString();
   }

	@Override
	public boolean hasTheSamePropertiesAs(ReadOnlyPropertyList list) {
		if (size()==list.size())
			if (getKeysAsSet().equals(list.getKeysAsSet())) {
				for (String key:getKeysAsSet()) 
					if (!getPropertyClass(key).equals(list.getPropertyClass(key)))
						return false;
				return true;
			}
		return false;
	}
//	
//	@Override 
//	public String toToken() {
//		StringBuilder sb = new StringBuilder(1024);
//		sb.append(START_BLOCK_DELIMITER[RECORD]);
//		String[] keys = getKeysAsArray();
//		for (int i=0; i<keys.length; i++) {
//			sb.append(propertyToString(keys[i]));
//			if (i<keys.length-1) 
//				sb.append(ITEM_DELIMITER[RECORD]);
//		}
//	    sb.append(END_BLOCK_DELIMITER[RECORD]);
//	    return sb.toString();
//	}

}
