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

package au.edu.anu.twcore.archetype;

import java.util.Map;

import au.edu.anu.rscs.aot.graph.AotGraph;
import au.edu.anu.rscs.aot.graph.AotNode;
import au.edu.anu.rscs.aot.util.IntegerRange;

/**
 * Author Ian Davies
 *
 * Date 10 Jan. 2019
 */

/*
 * These are the basic public methods required of an archetype implementation.
 * 
 * NOTE: There are two uses: CHECKING compliance and BUILDING a configuration
 * file. Queries are not executed when BUILDING.
 * 
 * 
 */
// Develop in this library but move to tw-core later - saves time!!
public interface Specifier {

	/* True if the archetype is a valid archetype */
	public boolean complies();

	/*
	 * runs all checks against the given node . Nodes without an spec can't be
	 * checked. I'm avoiding this at the moment : complies(AotGraph graph)
	 */
	public boolean complies(AotNode node, AotNode nodeSpec);

	/* get specification of a given node. If null, it can't be checked. */
	public AotNode getSpecificationOf(AotNode node);

	/*
	 * label, spec of all potential children of a parent with this label and class.
	 */
	public Map<String, AotNode> getPossibleChildrenOf(String parentLabel, AotNode parentSpec, String parentClass);

	/*
	 * label, spec of all potential neighbours (not children) of a node with this
	 * label and class.
	 */
	public Map<String, AotNode> getPossibleNeighboursOf(String parentLabel, AotNode parentSpec, String parentClass);

	/*
	 * Items in the object table of these constraints.
	 * 
	 * Need to know this so we can check which option the user has selected in the
	 * configuration being constructed.
	 */
	public String[] getEdgeXorPropertyOptions(AotNode nodeSpec);

	public String[] getNodeXorNodeOptions(AotNode nodeSpec);

	public String[] getPropertyXorPropertyOptions(AotNode nodeSpec);

	/* Get the upper value of the multiplicity of this property specification */
	//public int getUpperMultiplicity(AotNode propertySpec);
	public IntegerRange getMultiplicity(AotNode nodeSpec,String propertyLabel);

	/* True if node name must begin with upper case letter */
	public boolean nameStartsWithUpperCase(AotNode nodeSpec);

	public AotNode getPropertySpec(String label);

	public String getLabel(AotNode spec);


	// TODO more to come...

}
