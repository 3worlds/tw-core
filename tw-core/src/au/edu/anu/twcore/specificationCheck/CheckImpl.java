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

package au.edu.anu.twcore.specificationCheck;

import au.edu.anu.twcore.archetype.TWA;
import fr.cnrs.iees.graph.impl.TreeGraph;

/**
 * Author Ian Davies
 *
 * Date 22 Jan. 2019
 * 
 * NB (JG): this is a bit redundant with the method I wrote in TwArchetype - maybe 
 * we dont need this class. I suggest you arrange it as you prefer.
 * Currently, all the checks are performed by class Archetypes (in aot)
 * class TwArchetype provides a check against the 3w archetype through its
 * checkSpecification() method.
 * 
 */
// TODO not too sure if this is the way to go??
public class CheckImpl implements Checkable{
	private TreeGraph<?,?> graph;// graph to check
	// we will need the appropriate archetype for this graph (archetypeArcheytpe or twArchetype)!
	public CheckImpl(TreeGraph<?,?> graph) {
		this.graph=graph;
	}

	@Override
	public boolean validateGraph() {
		return TWA.checkSpecifications(graph).iterator().hasNext();
	}

}
