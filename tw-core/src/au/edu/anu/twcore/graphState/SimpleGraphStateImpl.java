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
package au.edu.anu.twcore.graphState;

import java.util.ArrayList;
import java.util.List;

import au.edu.anu.twcore.graphState.IGraphState;

/**
 * @author Ian Davies
 *
 * @date 1 Aug 2019
 */
public class SimpleGraphStateImpl implements IGraphState{

	private boolean changed = false;
	private List<IGraphStateListener> listeners = new ArrayList<>();
	@Override
	public boolean hasChanged() {
		return changed;
	}

	@Override
	public void setChanged(boolean state) {
		changed = state;
	}

	@Override
	public void addListener(IGraphStateListener l) {
		listeners.add(l);
	}

	@Override
	public void onChange() {
		for (IGraphStateListener l: listeners)
			l.onStateChange(changed);	
	}

}
