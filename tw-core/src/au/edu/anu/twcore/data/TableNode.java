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
import fr.cnrs.iees.properties.ExtendablePropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.twcore.constants.DataElementType;
import fr.cnrs.iees.omhtk.Sealable;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.*;

import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.*;
import au.edu.anu.omugi.collections.tables.*;
import au.edu.anu.twcore.InitialisableNode;

/**
 * Class matching the "table" node label in the 3Worlds configuration tree.
 * <p>
 * Has the "type" property and an edge to a dimensioner. It is a factory for
 * tables of primitive types only because tables of records are much more
 * complex.
 * 
 * @author Jacques Gignoux - 31 mai 2019
 *
 */
public class TableNode extends InitialisableNode implements Sealable {

	private boolean sealed = false;
	private Dimensioner[] dims = null;
	private Table tableTemplate = null;

	/**
	 * @param id       Unique identity of this node.
	 * @param props    Property list for this node.
	 * @param gfactory The graph construction factory
	 */
	public TableNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	/**
	 * @param id       Unique identity of this node.
	 * @param gfactory The graph construction factory
	 */
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
			SortedMap<Integer, DimNode> ld = new TreeMap<>();
			for (SizedByEdge sbe : lsbe) {
				int ix = (int) sbe.properties().getPropertyValue(P_DIMENSIONER_RANK.key());
				DimNode dn = (DimNode) sbe.endNode();
				ld.put(ix, dn);
			}
			dims = new Dimensioner[ld.size()];
			int i = 0;
			for (int j : ld.keySet())
				dims[i++] = ld.get(j).getInstance();
			// make template for easy cloning
			// tables of primitives
			if (properties().hasProperty(P_DATAELEMENTTYPE.key())) {
				DataElementType dataType = (DataElementType) properties().getPropertyValue(P_DATAELEMENTTYPE.key());
				switch (dataType) {
				case Byte:
					tableTemplate = new ByteTable(dims);
					break;
				case Char:
					tableTemplate = new CharTable(dims);
					break;
				case Short:
					tableTemplate = new ShortTable(dims);
					break;
				case Integer:
					tableTemplate = new IntTable(dims);
					break;
				case Long:
					tableTemplate = new LongTable(dims);
					break;
				case Float:
					tableTemplate = new FloatTable(dims);
					break;
				case Double:
					tableTemplate = new DoubleTable(dims);
					break;
				case Boolean:
					tableTemplate = new BooleanTable(dims);
					break;
				case String:
					tableTemplate = new StringTable(dims);
					break;
				default:
					;
				}
			}
			// tables of records - use tables of propertylists
			// ok because no table can be root of a data hierarchy
			// TODO: only works with 1 level of nesting, ie record within table
			else if (properties().hasProperty(P_TWDATACLASS.key())) {
				Record record = (Record) get(getChildren(), selectZeroOrOne(hasTheLabel(N_RECORD.label())));
				List<FieldNode> fields = (List<FieldNode>) get(record.getChildren(),
						selectOneOrMany(hasTheLabel(N_FIELD.label())));
				ExtendablePropertyList props = new ExtendablePropertyListImpl();
				for (FieldNode field : fields) {
					DataElementType dataType = (DataElementType) field.properties()
							.getPropertyValue(P_FIELD_TYPE.key());
					switch (dataType) {
					case Byte:
						Byte b = 0;
						props.addProperty(field.id(), b);
						break;
					case Char:
						Character c = 0;
						props.addProperty(field.id(), c);
						break;
					case Short:
						Short s = 0;
						props.addProperty(field.id(), s);
						break;
					case Integer:
						Integer ii = 0;
						props.addProperty(field.id(), ii);
						break;
					case Long:
						Long l = 0L;
						props.addProperty(field.id(), l);
						break;
					case Float:
						Float f = 0.0F;
						props.addProperty(field.id(), f);
						break;
					case Double:
						Double d = 0.0;
						props.addProperty(field.id(), d);
						break;
					case Boolean:
						Boolean bo = false;
						props.addProperty(field.id(), bo);
						break;
					case String:
						String ss = "";
						props.addProperty(field.id(), ss);
						break;
					default:
						;
					}

				}
				tableTemplate = new ObjectTable<SimplePropertyList>(dims);
//				tableTemplate.fillWith(props.clone()); // NO! this is copying the SAME prop in every cell !
				for (int k = 0; k < tableTemplate.size(); k++)
					((ObjectTable<SimplePropertyList>) tableTemplate).setWithFlatIndex(props.clone(), k);
//				// FLAW: this cannot work as the class is unavailable in this code.
//				// There is no way to instantiate a template from here.
//				Class<?> tclass;
//				try {
//					tclass = Class.forName((String)properties().getPropertyValue(P_TWDATACLASS.key()));
//					tableTemplate = (Table) tclass.getConstructor().newInstance();
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
			sealed = true;
		}
	}

//	private Table getNestedDataStructure() {
//		Table result = null;
//		return result;
//	}

	@Override
	public int initRank() {
		return N_TABLE.initRank();
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
	 * Returns an instance of the table dimensions by initialising the table.
	 * 
	 * @return The table dimensions.
	 */
	public Dimensioner[] dimensioners() {
		if (!sealed)
			initialise();
		return dims;
	}

	// only to use as a template.
	/**
	 * Returns an instance of the table to act as a template.
	 * 
	 * @return Table instance.
	 */
	public Table templateInstance() {
		if (!sealed)
			initialise();
		return tableTemplate;
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
