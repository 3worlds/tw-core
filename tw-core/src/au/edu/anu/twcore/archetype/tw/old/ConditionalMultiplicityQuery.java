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
package au.edu.anu.twcore.archetype.tw.old;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.old.queries.Query;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.graph.TreeNode;

import static au.edu.anu.rscs.aot.old.queries.base.SequenceQuery.get;

import java.util.Collection;

import static au.edu.anu.rscs.aot.old.queries.CoreQueries.*;


/**
 * Checks that a multiplicity is 0 if a certain condition is met, 1 otherwise.
 * At the moment only applicable to properties and child nodes, but could be
 * adapted to other cases by adding parameters or putting syntax in.
 *
 * @author J. Gignoux - 14 juil. 2020
 *
 */
@Deprecated
public class ConditionalMultiplicityQuery extends Query {

	private String property = null;
	private String nodeLabel = null;
	private int nMin = 0;

	public ConditionalMultiplicityQuery(StringTable args, Integer nMin) {
		super();
		property = args.getWithFlatIndex(0);
		nodeLabel = args.getWithFlatIndex(1);
		this.nMin = nMin;
	}

	public ConditionalMultiplicityQuery(Integer nMin, StringTable args) {
		this(args,nMin);
	}

	@Override
	public Query process(Object input) { // input is a node
		defaultProcess(input);
		TreeNode localItem = (TreeNode) input;
		ReadOnlyDataHolder rodh = (ReadOnlyDataHolder) input;
		Collection<?> l = (Collection<?>) get(localItem.getChildren(),
			selectZeroOrMany(hasTheLabel(nodeLabel)));
		satisfied = ((l.size()>=nMin) && (rodh.properties().hasProperty(property))) ||
			(l.size()<nMin);
//		satisfied = ((l.size()>=nMin) && (rodh.properties().hasProperty(property))) ||
//				((l.size()<nMin) && (!rodh.properties().hasProperty(property)));
		return this;
	}

}
