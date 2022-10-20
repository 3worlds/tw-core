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

import fr.cnrs.iees.omugi.collections.tables.StringTable;
import au.edu.anu.qgraph.queries.*;
import au.edu.anu.twcore.TextTranslations;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;

/**
 * A Query to test that if a node property takes certain values, then another
 * property must exist. eg, the second property is not really optional: it is
 * not needed in some cases, but it is in others.
 * 
 * @author Jacques Gignoux - 22-06-2018
 * 
 */
//tested OK 5/11/2019
public class RequirePropertyQuery extends QueryAdaptor {
	private String p1;
	private String p2;
	private String[] stringValues;

	public RequirePropertyQuery(StringTable params) {
		super();
		p1 = params.getWithFlatIndex(0);
		p2 = params.getWithFlatIndex(1);
		stringValues = new String[params.size() - 2];
		for (int i = 2; i < params.size(); i++)
			stringValues[i - 2] = params.getWithFlatIndex(i);
	}

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		ReadOnlyDataHolder n = (ReadOnlyDataHolder) input;
		if (n.properties().hasProperty(p1))
			if (n.properties().hasProperty(p2)) {
				boolean ok = false;
				for (int i = 0; i < stringValues.length; i++)
					if (n.properties().getPropertyValue(p2).toString().equals(stringValues[i]))
						ok = true;
				if (!ok) {
					String[] msgs = TextTranslations.getRequirePropertyQuery(p1,p2);
					actionMsg = msgs[0];
					errorMsg = msgs[1];

					// Should throw exception
//					actionMsg = "Remove property '" + p1 + "' by editing '" + Project.getProjectFile().getName()
//							+ "' with a text editor. [ModelMaker programming error!].";// send config to me!
//					errorMsg = "Presence of property '" + p1 + "' is incompatible with value of property '" + p2 + "'.";
				}
			}
		return this;
	}

}
