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

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.twcore.constants.RngAlgType;
import fr.cnrs.iees.twcore.constants.RngResetType;
import fr.cnrs.iees.twcore.constants.RngSeedSourceType;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.rngFactory.RngFactory;
import au.edu.anu.twcore.rngFactory.RngFactory.Generator;

/**
 * Class matching the "RngNode" node label in the 3Worlds configuration tree.
 * Has the "rng" property.
 * 
 * @author Ian Davies - 13 Dec 2019
 */
public class RngNode extends InitialisableNode implements LimitedEdition<Random>, Sealable {

	private static char sep = ':';
	private boolean sealed = false;
//	private Random rng = null;
	private Map<Integer, Random> rngs = new HashMap<>();
	private RngAlgType alg;
	private RngSeedSourceType seedSrc;
	private RngResetType reset;
	private Integer tableIndex;

	public RngNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public RngNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		if (!sealed) {
			super.initialise();
			sealed = false;
			alg = (RngAlgType) properties().getPropertyValue(P_RNGALG.key());
			seedSrc = (RngSeedSourceType) properties().getPropertyValue(P_RNGSEEDSOURCE.key());
			reset = (RngResetType) properties().getPropertyValue(P_RNGRESETIME.key());
			tableIndex = (Integer) properties().getPropertyValue(P_RNGTABLEINDEX.key());
			sealed = true;
		}
	}

	@Override
	public int initRank() {
		return N_DIMENSIONER.initRank();
	}

	public String name() {
		return classId();
	}

	public int dim() {
		return (int) properties().getPropertyValue(P_DIMENSIONER_SIZE.key());
	}

	@Override
	public Random getInstance(int id) {
		if (!sealed)
			initialise();
		if (!rngs.containsKey(id)) {
			String key = new StringBuilder().append(id()).append(sep).append(id).toString();
			Random rng = null;
			Generator gen = RngFactory.find(key);
			if (gen != null)// should be an error otherwise this is sharing an rng with something else
				// rng = gen.getRandom();
				throw new IllegalArgumentException("A random number generator called '" + key + "' already exists.");
			else {
				gen = RngFactory.newInstance(key, tableIndex, reset, seedSrc, alg);
				rng = gen.getRandom();
			}
			rngs.put(id, rng);
			/**
			 * ok so we have duplicate management of uniqueness. To avoid this and relieve
			 * the RngFactory of checking uniquness, other uses of the factory must be
			 * altered. cf dataTrackerD0 and TwFunctionAdapter.
			 */
		}
		return rngs.get(id);
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
