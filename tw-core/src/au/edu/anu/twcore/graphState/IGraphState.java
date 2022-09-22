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

/**
 ** 
 * Interface to specify actions to manage changes in the configuration graph
 * (restructuring of the graph nodes and edges or changes to property values or
 * their addition or removal). A controller, managing a particular UI
 * implementation (e.g javafx) will listen to an implementation of this
 * interface by implementing {@link IGraphStateListener}.
 * 
 * @author Ian Davies - May 6, 2019
 */
public interface IGraphState {
	/**
	 * @return true if the graph has changed since the time clear() was called.
	 */
	public boolean changed();

	/**
	 * Set the state of the graph to true.
	 */
	public void setChanged();

	/**
	 * Set changed state to false.
	 */
	public void clear();

	/**
	 * Add an {@link IGraphStateListener}.
	 * 
	 * @param l {@link IGraphStateListener}.
	 */
	public void addListener(IGraphStateListener l);

	/**
	 * Inform all register {@link IGraphStateListener}s of a state change.
	 */
	public void onChange();

}
