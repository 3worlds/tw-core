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
package au.edu.anu.twcore.ecosystem.runtime.system;

import java.util.Collection;

import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.rvgrid.observer.Observer;

/**
 *
 * @author J. Gignoux - 18 févr. 2021
 *
 */
public interface DynamicGraphObserver<N extends Node,E extends Edge>
		extends Observer {

	public default void onEdgeAdded(E e) {}
	
	public default void onEdgesAdded(Collection<E> es) {
		for (E e:es) onEdgeAdded(e);
	}	
	
	public default void onEdgeRemoved(E e) {}
	
	public default void onEdgesRemoved(Collection<E> es) {
		for (E e:es) onEdgeRemoved(e);
	}
	
	public default void onEdgeChanged(E e) {}
	
	public default void onEdgesChanged(Collection<E> es) {
		for (E e:es) onEdgeChanged(e);
	}

	
	public default void onNodeAdded(N n) {}
	
	public default void onNodesAdded(Collection<N> ns) {
		for (N n:ns) onNodeAdded(n);
	}
	
	public default void onNodeRemoved(N n) {}
	
	public default void onNodesRemoved(Collection<N> ns) {
		for (N n:ns) onNodeRemoved(n);
	}
	
	public default void onNodeChanged(N n) {}
	
	public default void onNodesChanged(Collection<N> ns) {
		for (N n:ns) onNodeChanged(n);
	}

}
