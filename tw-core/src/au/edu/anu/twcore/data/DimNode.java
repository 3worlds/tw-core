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
import fr.cnrs.iees.omhtk.*;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import fr.cnrs.iees.omugi.collections.tables.Dimensioner;
import au.edu.anu.twcore.InitialisableNode;

/**
 * Class matching the "dimensioner" node label in the 3Worlds configuration
 * tree. Has the "size" property.
 * 
 * @author Jacques Gignoux - 31 mai 2019
 *
 */
public class DimNode extends InitialisableNode implements Singleton<Dimensioner>, Sealable {

	private boolean sealed = false;
	private Dimensioner dimensioner = null;

	/**
	 * @param id       Unique identity of this node.
	 * @param props    Property list for this node.
	 * @param gfactory The graph construction factory
	 */
	public DimNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	/**
	 * @param id       Unique identity of this node.
	 * @param gfactory The graph construction factory
	 */
	public DimNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		if (!sealed) {
			super.initialise();
			sealed = false;
			dimensioner = new Dimensioner((int) properties().getPropertyValue(P_DIMENSIONER_SIZE.key()));
			sealed = true;
		}
	}

	@Override
	public int initRank() {
		return N_DIMENSIONER.initRank();
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
	 * Getter for the dimension size.
	 * 
	 * @return The property value.
	 */
	public int dim() {
		return (int) properties().getPropertyValue(P_DIMENSIONER_SIZE.key());
	}

	@Override
	public Dimensioner getInstance() {
		if (!sealed)
			initialise();
		return dimensioner;
	}

	@Override
	public Sealable seal() {
		sealed = true;
		return this;
	}

	@Override
	public boolean isSealed() {
		return sealed;
	}
}
