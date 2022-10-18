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
package au.edu.anu.twcore.ecosystem.structure;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.get;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.edu.anu.rscs.aot.collections.tables.DoubleTable;
import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.space.SpaceOrganiser;
import au.edu.anu.twcore.ecosystem.runtime.system.RelationContainer;

/**
 * Class matching the "ecosystem/structure" node label in the 3Worlds configuration tree.
 * May have properties regarding space overlaying (required only if >1 space).
 *
 * @author Jacques Gignoux - 27 mai 2019
 *
 */
public class Structure
		extends InitialisableNode
		implements Sealable {

	public LimitedEdition<SpaceOrganiser> spaceOrganiser = new LimitedEdition<>() {
		@Override
		public SpaceOrganiser getInstance(int id) {
			if (!sealed)
				initialise();
			if (!spaceOrgs.containsKey(id))
				spaceOrgs.put(id,makeSpaceOrganiser(id));
			return spaceOrgs.get(id);
		}
	};

	public LimitedEdition<Map<String,RelationContainer>> relationContainers = new LimitedEdition<>() {
		@Override
		public Map<String,RelationContainer> getInstance(int id) {
			if (!sealed)
				initialise();
			if (!relconts.containsKey(id))
				relconts.put(id,makeRelationContainers(id));
			return relconts.get(id);
		}
	};

	private boolean sealed = false;
	private Map<Integer,Map<String,RelationContainer>> relconts = new HashMap<>();
	private Map<Integer,SpaceOrganiser> spaceOrgs = new HashMap<>();

	// default constructor
	public Structure(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public Structure(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
		seal();
	}

	@Override
	public int initRank() {
		return N_STRUCTURE.initRank();
	}

	@SuppressWarnings("unchecked")
	private Map<String,RelationContainer> makeRelationContainers(int id) {
		List<RelationType> lrt = (List<RelationType>) get(getChildren(),
			selectZeroOrMany(hasTheLabel(N_RELATIONTYPE.label())));
		Map<String,RelationContainer> result = new HashMap<>();
		for (RelationType rt:lrt)
			result.put(rt.id(),rt.getInstance(id));
		return result;
	}

	@SuppressWarnings({ "unchecked" })
	private SpaceOrganiser makeSpaceOrganiser(int id) {
		List<SpaceNode> ls = (List<SpaceNode>) get(getChildren(),
			selectZeroOrMany(hasTheLabel(N_SPACE.label())));
		if (ls.isEmpty())
			return null;
		else if (ls.size()==1) {
			return new SpaceOrganiser(ls.get(0).getInstance(id));
		}
		else {
			// get the table of points (caution - this must be a 2D table)
			DoubleTable points = (DoubleTable) properties().getPropertyValue(P_SPACES_FIXED_POINTS.key());
			SpaceOrganiser spo = new SpaceOrganiser(points);
			for (SpaceNode sp:ls)
				spo.addSpace(sp.getInstance(id));
			spo.seal();
			return spo;
		}
	}

//	@Override
//	public Map<String, RelationContainer> getInstance(int id) {
//		if (!sealed)
//			initialise();
//		if (!relconts.containsKey(id))
//			relconts.put(id,makeRelationContainers(id));
//		return relconts.get(id);
//	}

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
