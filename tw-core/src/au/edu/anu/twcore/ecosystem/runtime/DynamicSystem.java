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

import au.edu.anu.twcore.data.runtime.TwData;

/**
 * Objects that have state variables that can be modified along time should implement
 * this interface.
 * <p>3Worlds: component threeWorlds</p>
 * @author Jacques Gignoux - 27 sept. 2012
 * updated 20/2/2017
 *
 */
public interface DynamicSystem {
	/** returns the current (t) state variables of this system */
    public TwData currentState();
	/** returns the future (t+1) state variables of this system */    
    public TwData nextState();
	/** returns the previous (t-1) state variables of this system */
    public TwData previousState();
    /** returns the previous (t-stepsBack) state variables of this system */
    public TwData previousState(int stepsBack);	
    /** returns a state with -1 = next, 0 = current, 1 = past[0], 2 = past[1], etc */
    public TwData state(int stepIndex);
    /** goes back in time one step */
	public void stepBackward();
	/** goes back in time nSteps steps */
    public void stepBackward(int nSteps);
    /** goes forward in time one step */
    public void stepForward();
//	see one day if this is useful:    
//    /** goes forward in time nSteps steps */
//    public void stepForward(int nSteps);    
    /**
     * extrapolate data to a hypothetical next state at a given time based on the past evolution of states, recorded
     * within this object
     * @param time the time to which to extrapolate. Must be larger than any already recorded time.
     */
    public void extrapolateState(long time);	
    /** 
     * interpolate a state between two already computed states given an intermediate time.
     * @param time the time at which data must be interpolated
     */
    public void interpolateState(long time);
}
