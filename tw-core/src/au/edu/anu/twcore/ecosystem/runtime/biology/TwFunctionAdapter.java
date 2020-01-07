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
package au.edu.anu.twcore.ecosystem.runtime.biology;

import java.util.Random;

import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.process.AbstractProcess;
import au.edu.anu.twcore.ecosystem.runtime.process.HierarchicalContext;
import au.edu.anu.twcore.rngFactory.RngFactory;
import fr.cnrs.iees.twcore.constants.RngAlgType;
import fr.cnrs.iees.twcore.constants.RngResetType;
import fr.cnrs.iees.twcore.constants.RngSeedSourceType;

/**
 * Ancestor for the class doing the user-defined computation
 * 
 * @author Jacques Gignoux - 7 juin 2019
 *
 */
public abstract class TwFunctionAdapter implements TwFunction {
	
	private AbstractProcess myProcess = null;
	protected HierarchicalContext focalContext = null;
	Random rng = null;

	/**
	 * constructor defining its own random number stream. It's a default stream with
	 * a 'secure' seed, ie impossible to replicate.
	 */
	public TwFunctionAdapter() {
		super();
		if (!RngFactory.exists("default 3wRNG"))
			RngFactory.makeRandom("default 3wRNG", 0, RngResetType.never, 
				RngSeedSourceType.secure, RngAlgType.Pcg32);
		this.rng = RngFactory.getRandom("default 3wRNG");
	}
	
	@Override
	// CAUTION: can be set only once
	// this to prevent end-users to mess up with the internal code
	public final void initProcess(AbstractProcess process) {
		if (myProcess == null)
			myProcess = process;
	}
	
	@Override
	public final AbstractProcess process(){
		return myProcess;
	}

	@Override
	public void addConsequence(TwFunction function) {
		// do nothing - some descendants have no consequences
	}

	@Override
	public void setFocalContext(HierarchicalContext context) {
		focalContext = context;
	}

	@Override
	public final Random rng() {
		return rng;
	}
	
	@Override
	// CAUTION: can be set only once
	// this to prevent end-users to mess up with the internal code
	public final void initRng(Random rng) {
		if (this.rng == null) {
			if (rng==null) {
				if (!RngFactory.exists("default 3wRNG"))
					RngFactory.makeRandom("default 3wRNG", 0, RngResetType.never, 
						RngSeedSourceType.secure, RngAlgType.Pcg32);
				this.rng = RngFactory.getRandom("default 3wRNG");
			}
			else this.rng = rng;
		}
	}
}
