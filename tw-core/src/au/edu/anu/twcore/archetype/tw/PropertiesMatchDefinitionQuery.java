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

package au.edu.anu.twcore.archetype.tw;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.edu.anu.rscs.aot.collections.tables.Dimensioner;
import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.collections.tables.Table;
import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.data.Record;
import au.edu.anu.twcore.data.TableNode;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.twcore.constants.DataElementType;

import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

/**
 * A query to check that the properties of a parameterValues or variableValues 
 * node match the definitions of the driver or parameter data
 */

/**
 * @author Ian Davies
 *
 * @date 29 Dec 2019
 */
public class PropertiesMatchDefinitionQuery extends Query {

	private String dataCategory;

	public PropertiesMatchDefinitionQuery(String dataCategory) {
		this.dataCategory = dataCategory;
	}

	public PropertiesMatchDefinitionQuery(StringTable table) {
		super();
		this.dataCategory = (table.getWithFlatIndex(0));
	}

	private String msg;

	@Override
	public Query process(Object input) {
		defaultProcess(input);
		TreeGraphDataNode targetNode = (TreeGraphDataNode) input;
		Collection<TreeGraphDataNode> defs = getDataDefs(targetNode, dataCategory);
		satisfied = true;
		if (defs == null) {
			msg = "No property definitions found.";
			satisfied = false;
			return this;
		}
		SimplePropertyList trgProps = targetNode.properties();
		for (TreeGraphDataNode def : defs) {
			if (!trgProps.hasProperty(def.id())) {
				msg = "Property '" + def.id() + "' is missing.";
				satisfied = false;
				return this;
			}
			if (def.classId().equals(N_FIELD.label())) {
				DataElementType dt = (DataElementType) def.properties().getPropertyValue(P_FIELD_TYPE.key());
				String trgClassName = trgProps.getPropertyClass(def.id()).getName();
				String defClassName = dt.className();
				if (!trgClassName.equals(defClassName)) {
					msg = "Property '" + def.id() + "' should have class '" + defClassName + "' but has '"
							+ trgClassName + "'.";
					satisfied = false;
					return this;
				}
			} else {
				TableNode tblDef = (TableNode) def;
				Dimensioner[] defDims = tblDef.dimensioners();
				Table trgValue = (Table) trgProps.getPropertyValue(def.id());
				Dimensioner[] trgDims = trgValue.getDimensioners();
				if (trgDims.length != defDims.length) {
					msg = "Property '" + def.id() + "' has " + defDims.length + " dimensions but has " + trgDims.length
							+ ".";
					satisfied = false;
					return this;
				}
				for (int i = 0; i < trgDims.length; i++) {
					if (trgDims[i].getLength() != defDims[i].getLength()) {
						msg = "Property '" + def.id() + "' dimension [" + i + "] has length " + trgDims[i].getLength()
								+ " but should be length " + defDims[i].getLength();
						satisfied = false;
						return this;

					}

				}
				Table defValue = tblDef.newInstance();
				if (!trgValue.getClass().equals(defValue.getClass())) {
					msg = "Property '" + def.id() + " is of class '" + trgValue.getClass()
							+ "' but should be of class '" + defValue.getClass() + "'.";
					satisfied = false;
					return this;
				}
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "[" + stateString() + msg + "]";

	}

	/* Public static - available for use by MM for matching purpose */
	@SuppressWarnings("unchecked")
	public static Collection<TreeGraphDataNode> getDataDefs(TreeGraphDataNode node, String dataCategory) {
		// can't allow exceptions to arise here if used from MM
		TreeGraphDataNode parent = (TreeGraphDataNode) node.getParent();
		if (parent == null)
			return null;
		TreeGraphDataNode ct = (TreeGraphDataNode) get(parent.edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(E_INSTANCEOF.label())), endNode());
		if (ct == null)
			return null;
//		TreeGraphDataNode cat = (TreeGraphDataNode) get(ct.edges(Direction.OUT),
//			selectZeroOrOne(hasTheLabel(E_BELONGSTO.label())), endNode());
		List<TreeGraphDataNode> cats = (List<TreeGraphDataNode>) get(ct.edges(Direction.OUT),
			selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())), edgeListEndNodes());
		if (cats.isEmpty())
			return null;
		Set<TreeGraphDataNode> result = new HashSet<>();
		for (TreeGraphDataNode cat:cats) {
			Record rootRecord = (Record) get(cat.edges(Direction.OUT), 
				selectZeroOrOne(hasTheLabel(dataCategory)),
				endNode());
			result.addAll(Record.getLeaves(rootRecord));
		}
		return result;
	}
}