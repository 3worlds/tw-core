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

import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.process.AbstractProcess;
import au.edu.anu.twcore.ecosystem.runtime.process.HierarchicalContext;

/**
 * Ancestor for the class doing the user-defined computation
 * 
 * @author Jacques Gignoux - 7 juin 2019
 *
 */
public abstract class TwFunctionAdapter implements TwFunction {
	
	private AbstractProcess myProcess = null;
	protected HierarchicalContext focalContext = null;
	
	public final void setProcess(AbstractProcess process) {
		myProcess = process;
	}
	
	public final AbstractProcess process(){
		return myProcess;
	}

	public void addConsequence(TwFunction function) {
		// do nothing - some descendants have no consequences
	}

	public void setFocalContext(HierarchicalContext context) {
		focalContext = context;
	}
}
