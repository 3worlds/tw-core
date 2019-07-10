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

import java.util.LinkedList;
import java.util.List;

import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;

/**
 * @author Jacques Gignoux - 4/8/2014
 *
 * interface for user-defined ecological functions deciding to create a number of newborn ComplexSystems.
 * result is a number of descendants to create (as a double - fractional part used as a probability)
 * 
 */
public abstract class CreateOtherDecisionFunction extends TwFunctionAdapter {

    private List<ChangeStateFunction> CSfunctions = 
        	new LinkedList<ChangeStateFunction>();
    private List<ChangeOtherStateFunction> COSfunctions = 
        	new LinkedList<ChangeOtherStateFunction>();
    private List<RelateToDecisionFunction> RTfunctions = 
        	new LinkedList<RelateToDecisionFunction>();
	
    /**
     * 
     * @param t time
     * @param dt time interval
     * @param focal the creator component
     * @param newType the type of the created component
     * @return
     */
	public abstract double nNew(double t,	
		double dt,	
		SystemComponent focal,
		String newType);
	
	public final void addConsequence(TwFunction function) {
		if (ChangeOtherStateFunction.class.isAssignableFrom(function.getClass()))
			COSfunctions.add((ChangeOtherStateFunction) function);
		if (RelateToDecisionFunction.class.isAssignableFrom(function.getClass()))
			RTfunctions.add((RelateToDecisionFunction) function);
		else if (ChangeStateFunction.class.isAssignableFrom(function.getClass()))
			CSfunctions.add((ChangeStateFunction) function);		
	}

	public final List<ChangeStateFunction> getChangeStateConsequences() {
		return CSfunctions;
	}
	
	public final List<ChangeOtherStateFunction> getChangeOtherStateConsequences() {
		return COSfunctions;
	}
	
	public final List<RelateToDecisionFunction> getRelateToDecisionConsequences() {
		return RTfunctions;	
	}
	
}
