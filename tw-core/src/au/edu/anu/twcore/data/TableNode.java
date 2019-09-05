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

import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.Factory;
import fr.ens.biologie.generic.Sealable;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.List;

import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.*;
import static fr.cnrs.iees.io.parsing.ValidPropertyTypes.*;

import au.edu.anu.rscs.aot.collections.tables.*;
import au.edu.anu.rscs.aot.util.StringUtils;
import au.edu.anu.twcore.InitialisableNode;

/**
 * Class matching the "table" node label in the 3Worlds configuration tree.
 * Has the "type" property and an edge to a dimensioner.
 * Is a factory for tables of primitive types only (because tables of records are much more complex,
 * they depend on category groupings).
 * 
 * @author Jacques Gignoux - 31 mai 2019
 *
 */
public class TableNode 
		extends InitialisableNode 
		implements Factory<Table>, Sealable {

	private boolean sealed = false;
	private Dimensioner[] dims = null;
	private String dataType = null;
	
	public TableNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}
	
	public TableNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);		super.initialise();

	}


	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		if (!sealed) {
			super.initialise();
			List<DimNode> d = (List<DimNode>) get(this.edges(Direction.OUT),
				selectOneOrMany(hasTheLabel(E_SIZEDBY.label())),
				edgeListEndNodes());
			dims = new Dimensioner[d.size()];
			for (int i=0; i<dims.length; i++)
				dims[i] = d.get(i).getInstance();
			if (properties().hasProperty(P_FIELD_TYPE.key()))
				if (isPrimitiveType((String) properties().getPropertyValue(P_FIELD_TYPE.key())))
					dataType = StringUtils.cap((String)properties().getPropertyValue(P_FIELD_TYPE.key()));
			sealed = true;
		}
	}

	@Override
	public int initRank() {
		return N_TABLE.initRank();
	}

	public String name() {
		return classId();
	}

	@Override
	public Table newInstance() {
		if (!sealed)
			initialise();
		switch (dataType) {
			case "Byte":
				return new ByteTable(dims);
			case "Char":
				return new CharTable(dims);
			case "Short":
				return new ShortTable(dims);
			case "Integer":
				return new IntTable(dims);
			case "Long":
				return new LongTable(dims);
			case "Float":
				return new FloatTable(dims);
			case "Double":
				return new DoubleTable(dims);
			case "Boolean":
				return new BooleanTable(dims);
			case "String":
				return new StringTable(dims);
		}
		return null;
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
