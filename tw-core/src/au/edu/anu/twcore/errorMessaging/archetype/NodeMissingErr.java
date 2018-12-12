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

package au.edu.anu.twcore.errorMessaging.archetype;

import au.edu.anu.twcore.errorMessaging.ErrorMessageAdaptor;

/**
 * Author Ian Davies
 *
 * Date Dec 12, 2018
 */
public class NodeMissingErr extends ErrorMessageAdaptor{
//	private IntegerRange expectedRange;
	private int foundCount;
//	private AotNode spec;
	private String reference;

public NodeMissingErr(/*String reference, IntegerRange expectedRange, int foundCount, AotNode spec,
			AotGraph currentGraph*/) {
//	this.reference = reference;
//	this.expectedRange = expectedRange;
//	this.foundCount = foundCount;
//	this.spec = spec;
//	String parent = ArchetypeHelper.getParentLabelFromReference(reference);
//	String[] parents;
//	if (parent.equals(""))
//		parents = ArchetypeHelper.getParentLabels(spec);
//	else {
//		parents = new String[1];
//		parents[0] = parent;
//	}
//	msg1 = "Missing Node: Add node " + formatParentChildString(reference, spec) + ".";
//	msg2 = msg1 + " Expected " + expectedRange.toString() + " but found " + foundCount + ".";
//	msg3 = msg2 + "\nSpecification:\n" + spec.toDetailedString() + "\n";

}
}
