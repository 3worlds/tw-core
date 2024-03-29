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
package au.edu.anu.twcore.data;

import fr.cnrs.iees.omugi.graph.GraphFactory;
import fr.cnrs.iees.omugi.identity.Identity;
import fr.cnrs.iees.omugi.properties.SimplePropertyList;
import fr.cnrs.iees.omugi.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.twcore.constants.DataElementType;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import au.edu.anu.twcore.InitialisableNode;

/**
 * Class matching the "field" node label in the 3Worlds configuration tree. Has
 * the "type" property.
 * 
 * @author Jacques Gignoux - 31 mai 2019
 *
 */
public class FieldNode extends InitialisableNode {

	/**
	 * @param id       Unique identity of this node.
	 * @param props    Property list for this node.
	 * @param gfactory The graph construction factory
	 */
	public FieldNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	/**
	 * @param id       Unique identity of this node.
	 * @param gfactory The graph construction factory
	 */
	public FieldNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_FIELD.initRank();
	}

	/**
	 * Getter for the label (classId).
	 * 
	 * @return classId of this class.
	 */
	public String name() {
		return classId();
	}

	/**
	 * Getter for the Type of this field.
	 * 
	 * @return The property value.
	 */
	public String type() {
		return (String) properties().getPropertyValue(P_FIELD_TYPE.key());
	}

	/**
	 * Default instance of this Field type.
	 * 
	 * @return Initialised instance of field type.
	 */
	public Object newInstance() {
		DataElementType dt = (DataElementType) properties().getPropertyValue(P_FIELD_TYPE.key());
		switch (dt) {
		case Byte:
			return Byte.valueOf((byte) 0);
		case Char:
			return Character.valueOf('0');
		case Short:
			return Short.valueOf((short) 0);
		case Integer:
			return Integer.valueOf(0);
		case Long:
			return Long.valueOf(0L);
		case Float:
			return Float.valueOf(0.0F);
		case Double:
			return Double.valueOf(0.0);
		case Boolean:
			return Boolean.valueOf(false);
		case String:
			return "";
		default:
			throw new IllegalArgumentException("Unable to instantiate " + dt);
		}
	}

}
