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
package au.edu.anu.twcore.ecosystem.runtime;

import java.util.List;
import java.util.Random;

import au.edu.anu.twcore.ecosystem.runtime.process.AbstractProcess;
import au.edu.anu.twcore.ecosystem.runtime.process.HierarchicalContext;

/**
 * Ancestor for the class doing the user-defined computation
 * 
 * @author Jacques Gignoux - 7 juin 2019
 *
 */
public interface TwFunction {
	
	/**
	 * Connects a function to its process, only once (at construction time).
	 * This function is not meant to be used by end-users.
	 * 
	 * @param process the process
	 */
	public void initProcess(AbstractProcess process);
	
	public AbstractProcess process();

	public void addConsequence(TwFunction function);
	
	public void setFocalContext(HierarchicalContext context);
	
	public default List<? extends TwFunction> getConsequences() {
		return null;
	}

	public Random rng();
	
	/**
	 * Connects a function to its random number generator, only once (at construction time)
	 * This function is not meant to be used by end-users.
	 *  
	 * @param rng the random number generator
	 */
	public void initRng(Random rng);
}
