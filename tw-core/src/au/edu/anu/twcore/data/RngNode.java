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
import fr.ens.biologie.generic.Sealable;
import fr.ens.biologie.generic.Singleton;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.Random;

import au.edu.anu.omhtk.rng.Pcg32;
import au.edu.anu.omhtk.rng.XSRandom;
import au.edu.anu.rscs.aot.collections.tables.Dimensioner;
import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.rngFactory.RngFactory;

/**
 * Class matching the "RngNode" node label in the 3Worlds configuration tree.
 * Has the "rng" property.
  */
/**
 * @author Ian Davies
 *
 * @date 13 Dec 2019
 */
public class RngNode extends InitialisableNode implements Singleton<Random>, Sealable {

	private boolean sealed = false;
	private Random rng = null;

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
			RngAlgType alg = (RngAlgType) properties().getPropertyValue(P_RNGALG.key());
			RngSeedSourceType seedSrc = (RngSeedSourceType) properties().getPropertyValue(P_RNGSEEDSOURCE.key());
			RngResetType reset = (RngResetType) properties().getPropertyValue(P_RNGRESETIME.key());
			Integer tableIndex = (Integer) properties().getPropertyValue(P_RNGTABLEINDEX.key());
			String key = id();
			if (RngFactory.exists(key))
				rng = RngFactory.getRandom(key);
			else {
				RngFactory.makeRandom(key, tableIndex, reset, seedSrc, alg);
				rng = RngFactory.getRandom(key);
			}

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
	public Random getInstance() {
		if (!sealed)
			initialise();
		return rng;
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
