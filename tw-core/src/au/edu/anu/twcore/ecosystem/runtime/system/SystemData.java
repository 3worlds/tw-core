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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
/**
 * A Data object to contain automatic ComplexSystem variables, i.e. those variables which
 * are common to any SystemComponent.
 * <p>3Worlds: component threeWorlds</p>
 * @author Jacques Gignoux - 28 sept. 2012
 * refactored 17/2/2017
 *
 */
public class SystemData extends TwData {

	/** the name of this system  - for compatibility with graph */
	private String name = "";
	/** the age of the system since its creation, in mainTimer units */
	private long age = 0L;
	/** the birth date of the system, in mainTimer units */
	private long birthDate = 0L;


	private static String[] keyArray = {"name","age","birthDate"};
	protected static Set<String> keySet = new HashSet<String>(Arrays.asList(keyArray));

	public SystemData() {
		super();
	}

	// quick getters and setters
	//

	public long age() {
		return age;
	}

	public void age(long value) {
		if (isReadOnly()) throw new TwcoreException("attempt to write to read-only data");
		age = value;
	}

	public long birthDate() {
		return birthDate;
	}

	public void birthDate(long value) {
		if (isReadOnly()) throw new TwcoreException("attempt to write to read-only data");
		birthDate = value;
	}

	public String name() {
		return name;
	}

	public void name(String value) {
		if (isReadOnly()) throw new TwcoreException("attempt to write to read-only data");
		name = value;
	}

	// generic methods inherited from TwData

	@Override
	public SystemData setProperty(String key, Object value) {
		if (key.equals("age")) age = (Long)value;
		if (key.equals("birthDate")) birthDate = (Long)value;
		if (key.equals("name")) name = (String)value;
		return this;
	}

	@Override
	public Object getPropertyValue(String key) {
		if (key.equals("age")) return age;
		else if (key.equals("birthDate")) return birthDate;
		else if (key.equals("name")) return name;
		return null;
	}

	@Override
	public SystemData clear() {
		age = 0L;
		birthDate = 0L;
		name = "";
		return this;
	}

	@Override
	public SystemData cloneStructure() {
		SystemData result = new SystemData();
		return result;
	}

	@Override
	public String[] getKeysAsArray() {
		return keyArray;
	}

	@Override
	public SystemData clone() {
		SystemData clone = cloneStructure();
		clone.age = age; // this is nonsense !
		clone.birthDate = birthDate; // this is nonsense too !
		clone.name = name;
		return clone;
	}

   @Override
    public String toString() {
        String s="(";
        s += "name="+name+" ";
        s += "age="+age+" ";
        s += "birthDate="+birthDate;
        s += ")";
        return s;
    }

	@Override
	public boolean hasProperty(String key) {
		if (key.equals("age")|key.equals("birthDate")|key.equals("name"))
			return true;
		return false;
	}

	@Override
	public String propertyToString(String key) {
		if (key.equals("age")) return String.valueOf(age);
		else if (key.equals("birthDate")) return String.valueOf(birthDate);
		else if (key.equals("name")) return name;
		return null;
	}

	@Override
	public String getPropertyClassName(String key) {
		return getPropertyClass(key).getName();
	}

	@Override
	public Set<String> getKeysAsSet() {
		return keySet;
	}

	@Override
	public int size() {
		return 5;
	}

	@Override
	public Class<?> getPropertyClass(String key) {
		if (key.equals("age")) return Long.class;
		else if (key.equals("birthDate")) return Long.class;
		else if (key.equals("name")) return String.class;
		return null;
	}

	@Override
	public boolean hasTheSamePropertiesAs(ReadOnlyPropertyList list) {
		if (keySet.equals(list.getKeysAsSet())) {
			for (String key:keySet)
				if (!list.getPropertyClass(key).equals(getPropertyClass(key)))
					return false;
			return true;
		}
		return false;
	}

}
