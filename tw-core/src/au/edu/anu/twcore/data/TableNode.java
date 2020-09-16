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
import fr.cnrs.iees.twcore.constants.DataElementType;
import fr.ens.biologie.generic.Factory;
import fr.ens.biologie.generic.Sealable;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.*;
import au.edu.anu.rscs.aot.collections.tables.*;
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
		// TODO (16/9/2020): get rid of the Factory interface, it's useless and dangerous here
		implements Factory<Table>, Sealable {

	private boolean sealed = false;
	private Dimensioner[] dims = null;
	private DataElementType dataType = null;
	
	public TableNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}
	
	public TableNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		if (!sealed) {
			super.initialise();
			// sort dimensioner by rank order - NB ranks must be different, but they do not
			// have to follow each other
			List<SizedByEdge> lsbe = (List<SizedByEdge>) get(this.edges(Direction.OUT),
				selectOneOrMany(hasTheLabel(E_SIZEDBY.label())));
			SortedMap<Integer,DimNode> ld = new TreeMap<>();
			for (SizedByEdge sbe:lsbe) {
				int ix = (int) sbe.properties().getPropertyValue(P_DIMENSIONER_RANK.key());
				DimNode dn = (DimNode) sbe.endNode();
				ld.put(ix,dn);
			}
			dims = new Dimensioner[ld.size()];
			int i=0;
			for (int j:ld.keySet())
				dims[i++] = ld.get(j).getInstance();
			// get data type
			if (properties().hasProperty(P_DATAELEMENTTYPE.key()))
				dataType = (DataElementType) properties().getPropertyValue(P_DATAELEMENTTYPE.key());
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
	
	public Dimensioner[] dimensioners() {
		if (!sealed)
			initialise();
		return dims;
	}

	@Override
	@Deprecated // we dont want to see this anymore - it must go.
	public Table newInstance() {
		if (!sealed)
			initialise();
		switch (dataType) {
			case Byte:
				return new ByteTable(dims);
			case Char:
				return new CharTable(dims);
			case Short:
				return new ShortTable(dims);
			case Integer:
				return new IntTable(dims);
			case Long:
				return new LongTable(dims);
			case Float:
				return new FloatTable(dims);
			case Double:
				return new DoubleTable(dims);
			case Boolean:
				return new BooleanTable(dims);
			case String:
				return new StringTable(dims);
			default:;
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
