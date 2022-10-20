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
package au.edu.anu.twcore.archetype.tw;

import java.util.*;

import fr.cnrs.iees.omugi.collections.tables.*;
import au.edu.anu.qgraph.queries.QueryAdaptor;
import fr.cnrs.iees.graph.Specialized;

/**
 * Check that only one string out of many is found in some list
 * 
 * @author gignoux 14/2/2022
 *
 */
public abstract class RequiredLabelQuery extends QueryAdaptor {

	List<String> requiredLabels = new ArrayList<>();
	
	public RequiredLabelQuery(StringTable el) {
		super();
		for (int i=0; i<el.size(); i++)
			requiredLabels.add(el.getWithFlatIndex(i));
	}
	
	public RequiredLabelQuery(String... lab) {
		super();
		for (int i=0; i<lab.length; i++)
			requiredLabels.add(lab[i]);
	}
	
	final int countLabels(Collection<? extends Specialized> labelled) {
		int count=0;
		for (Specialized s:labelled)
			if (requiredLabels.contains(s.classId()))
				count++;
		return count;
	}

}
