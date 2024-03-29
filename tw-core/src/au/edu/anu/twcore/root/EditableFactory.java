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
package au.edu.anu.twcore.root;

import fr.cnrs.iees.omugi.graph.*;

/**
 * This interface provides a means to completely remove element ids when edition
 * a graph.
 * 
 * @author Ian Davies - 16 Aug 2019
 */
public interface EditableFactory {
	/* */
	/**
	 * Removes any traces of a {@linkplain Node} from a factory: its id along with the ids of any
	 * {@linkplain Edge}s
	 * 
	 * @param node The {@link Node} to remove.
	 */
	public void expungeNode(Node node);

	/**
	 * Removes edge id from a factory
	 * 
	 * @param edge The {@link Edge} to remove.
	 */
	public void expungeEdge(Edge edge);

}
