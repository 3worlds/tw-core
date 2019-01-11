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

import au.edu.anu.rscs.aot.graph.AotNode;
import au.edu.anu.rscs.aot.util.IntegerRange;

public class SpecifierAdapter implements Specifier{

	@Override
	public boolean complies() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean complies(AotNode node, AotNode nodeSpec) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AotNode getSpecificationOf(AotNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, AotNode> getPossibleChildrenOf(String parentLabel, AotNode parentSpec, String parentClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, AotNode> getPossibleNeighboursOf(String parentLabel, AotNode parentSpec, String parentClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getEdgeXorPropertyOptions(AotNode nodeSpec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getNodeXorNodeOptions(AotNode nodeSpec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getPropertyXorPropertyOptions(AotNode nodeSpec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IntegerRange getMultiplicity(AotNode nodeSpec, String propertyLabel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean nameStartsWithUpperCase(AotNode nodeSpec) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AotNode getPropertySpec(String label) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabel(AotNode spec) {
		// TODO Auto-generated method stub
		return null;
	}

}
